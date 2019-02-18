/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2019. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */

package dan200.computercraft.core.computer;

import dan200.computercraft.ComputerCraft;
import dan200.computercraft.api.filesystem.IMount;
import dan200.computercraft.api.filesystem.IWritableMount;
import dan200.computercraft.api.lua.ILuaAPI;
import dan200.computercraft.api.lua.ILuaAPIFactory;
import dan200.computercraft.core.apis.*;
import dan200.computercraft.core.filesystem.FileSystem;
import dan200.computercraft.core.filesystem.FileSystemException;
import dan200.computercraft.core.lua.CobaltLuaMachine;
import dan200.computercraft.core.lua.ILuaMachine;
import dan200.computercraft.core.terminal.Terminal;
import dan200.computercraft.shared.util.IoUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

import static dan200.computercraft.core.computer.Computer.ExecutorTask;

/**
 * The {@link ComputerExecutor} class handles the main "lifecycle" of a computer: turning it on and off, running
 * events and executing any arbitrary tasks.
 */
final class ComputerExecutor
{
    private static final int QUEUE_LIMIT = 256;

    private static IMount romMount;
    private static final Object romMountLock = new Object();

    private final Computer computer;
    private final List<ILuaAPI> apis = new ArrayList<>();

    private FileSystem fileSystem;

    private ILuaMachine machine;

    private volatile boolean isOn = false;
    private final ReentrantLock isOnLock = new ReentrantLock();

    /**
     * A lock used for any changes to {@link #taskQueue} or {@link #onComputerQueue}. This will be used on the main
     * thread, so locks should be kept as brief as possible.
     */
    private final Object queueLock = new Object();
    volatile boolean onComputerQueue = false;
    private final Queue<ExecutorTask> taskQueue = new ArrayBlockingQueue<>( QUEUE_LIMIT );

    /**
     * The command that {@link #runCommand()} should execute on the computer thread.
     *
     * One sets the command with {@link #queueStart()} and {@link #queueStop(boolean, boolean)}. Neither of these will
     * queue a new event if there is an existing one in the queue.
     *
     * Note, if command is not {@code null}, then some command is scheduled to be exeucted. Otherwise it is not
     * currently in the queue (or is currently being executed).
     */
    private volatile StateCommand command;

    /**
     * Whetehr this executor has been closed, and will no longer accept any incoming commands or events.
     *
     * @see #queueStop(boolean, boolean)
     */
    private boolean closed;

    private IWritableMount rootMount;

    /**
     * {@code true} when inside {@link #work()}. We use this to ensure we're only doing one bit of work at one time.
     */
    private final AtomicBoolean isExecuting = new AtomicBoolean( false );

    ComputerExecutor( Computer computer )
    {
        // Ensure the computer thread is running as required.
        ComputerThread.start();

        this.computer = computer;

        Environment environment = computer.getEnvironment();

        // Add all default APIs to the loaded list.
        apis.add( new TermAPI( environment ) );
        apis.add( new RedstoneAPI( environment ) );
        apis.add( new FSAPI( environment ) );
        apis.add( new PeripheralAPI( environment ) );
        apis.add( new OSAPI( environment ) );
        if( ComputerCraft.http_enable ) apis.add( new HTTPAPI( environment ) );

        // Load in the externally registered APIs.
        for( ILuaAPIFactory factory : ApiFactories.getAll() )
        {
            ComputerSystem system = new ComputerSystem( environment );
            ILuaAPI api = factory.create( system );
            if( api != null ) apis.add( new ApiWrapper( api, system ) );
        }
    }

    boolean isOn()
    {
        return isOn;
    }

    FileSystem getFileSystem()
    {
        return fileSystem;
    }

    Computer getComputer()
    {
        return computer;
    }

    void addApi( ILuaAPI api )
    {
        apis.add( api );
    }

    /**
     * Abort the current Lua machine
     *
     * @param hard Whether this is a hard abort. Namely, if this should be terminated with no hope of recovery.
     */
    void abort( boolean hard )
    {
        ILuaMachine machine = this.machine;
        if( machine == null ) return;

        if( hard )
        {
            this.machine.hardAbort( "Too long without yielding" );
        }
        else
        {
            this.machine.softAbort( "Too long without yielding" );
        }
    }

    /**
     * Schedule this computer to be started if not already on.
     */
    void queueStart()
    {
        synchronized( queueLock )
        {
            // We should only schedule a start if we're not currently on and there's turn on.
            if( closed || isOn || this.command != null ) return;

            command = StateCommand.TURN_ON;
            taskQueue.offer( this::runCommand );
            enqueue();
        }
    }

    /**
     * Schedule this computer to be stopped if not already on.
     *
     * @param reboot Reboot the computer after stopping
     * @param close  Close the computer after stopping.
     * @see #closed
     */
    void queueStop( boolean reboot, boolean close )
    {
        synchronized( queueLock )
        {
            if( closed ) return;
            this.closed = close;

            StateCommand newCommand = reboot ? StateCommand.REBOOT : StateCommand.SHUTDOWN;

            // We should only schedule a stop if we're currently on and there's no shutdown pending.
            if( !isOn || command != null )
            {
                // If we're closing, set the command just in case.
                if( close ) command = newCommand;
                return;
            }

            command = newCommand;
            taskQueue.offer( this::runCommand );
            enqueue();
        }
    }

    /**
     * Queue an event if the computer is on
     *
     * @param event The event's name
     * @param args  The event's arguments
     */
    void queueEvent( @Nonnull String event, @Nullable Object[] args )
    {
        // Events should not be queued if we're not on
        if( !isOn ) return;
        synchronized( queueLock )
        {
            // And if we've got some command in the pipeline, then don't queue events - they'll
            // probably be disposed of anyway.
            if( closed || command != null ) return;

            taskQueue.offer( () -> {
                if( !isOn ) return;
                machine.handleEvent( event, args );
                if( machine.isFinished() )
                {
                    displayFailure( "Error resuming bios.lua" );
                    shutdown();
                }
            } );
            enqueue();
        }
    }

    /**
     * Queue an arbitrary task to execute on the computer thread.
     *
     * @param task The task to execute
     */
    void queueTask( @Nonnull ExecutorTask task )
    {
        synchronized( queueLock )
        {
            taskQueue.offer( task );
            enqueue();
        }
    }

    /**
     * Add this executor to the {@link ComputerThread} if not already there.
     */
    private void enqueue()
    {
        synchronized( queueLock )
        {
            if( onComputerQueue ) return;
            onComputerQueue = true;
            ComputerThread.queue( this );
        }
    }

    /**
     * Re-add this executor to the {@link ComputerThread}, or remove it if we have no more work.
     */
    void requeue()
    {
        synchronized( queueLock )
        {
            if( taskQueue.isEmpty() )
            {
                onComputerQueue = false;
            }
            else
            {
                ComputerThread.queue( this );
            }
        }
    }

    /**
     * Update the internals of the executor.
     */
    void tick()
    {
        if( isOn && isOnLock.tryLock() )
        {
            // This horrific structure means we don't try to update APIs while the state is being changed
            // (and so they may be running startup/shutdown).
            // We use tryLock here, as it has minimal delay, and it doesn't matter if we miss an advance at the
            // beginning or end of a computer's lifetime.
            try
            {
                if( isOn )
                {
                    // Advance our APIs.
                    for( ILuaAPI api : apis ) api.update();
                }
            }
            finally
            {
                isOnLock.unlock();
            }
        }
    }

    private IMount getRomMount()
    {
        if( romMount != null ) return romMount;

        synchronized( romMountLock )
        {
            if( romMount != null ) return romMount;
            return romMount = computer.getComputerEnvironment().createResourceMount( "computercraft", "lua/rom" );
        }
    }

    private FileSystem createFileSystem()
    {
        if( rootMount == null )
        {
            rootMount = computer.getComputerEnvironment().createSaveDirMount(
                "computer/" + computer.assignID(),
                computer.getComputerEnvironment().getComputerSpaceLimit()
            );
        }

        FileSystem filesystem = null;
        try
        {
            filesystem = new FileSystem( "hdd", rootMount );

            IMount romMount = getRomMount();
            if( romMount == null )
            {
                displayFailure( "Cannot mount rom" );
                return null;
            }

            filesystem.mount( "rom", "rom", romMount );
            return filesystem;
        }
        catch( FileSystemException e )
        {
            if( filesystem != null ) filesystem.close();
            ComputerCraft.log.error( "Cannot mount computer filesystem", e );

            displayFailure( "Cannot mount computer system" );
            return null;
        }
    }

    private ILuaMachine createLuaMachine()
    {
        // Load the bios resource
        InputStream biosStream = null;
        try
        {
            biosStream = computer.getComputerEnvironment().createResourceFile( "computercraft", "lua/bios.lua" );
        }
        catch( Exception ignored )
        {
        }

        if( biosStream == null )
        {
            displayFailure( "Error loading bios.lua" );
            return null;
        }

        // Create the lua machine
        ILuaMachine machine = new CobaltLuaMachine( computer );

        // Add the APIs
        for( ILuaAPI api : apis ) machine.addAPI( api );

        // Start the machine running the bios resource
        machine.loadBios( biosStream );
        IoUtil.closeQuietly( biosStream );

        if( machine.isFinished() )
        {
            machine.close();
            displayFailure( "Error starting bios.lua" );
            return null;
        }

        return machine;
    }

    private synchronized void turnOn()
    {
        // Reset some old state
        computer.getTerminal().reset();
        taskQueue.clear();

        // Init filesystem
        FileSystem fileSystem = this.fileSystem = createFileSystem();
        if( fileSystem == null )
        {
            shutdown();
            return;
        }

        // Init APIs
        for( ILuaAPI api : apis ) api.startup();

        // Init lua
        ILuaMachine machine = this.machine = createLuaMachine();
        if( machine == null )
        {
            shutdown();
            return;
        }

        // Start a new state
        isOn = true;
        computer.markChanged();

        machine.handleEvent( null, null );
    }

    private void shutdown()
    {
        isOn = false;
        taskQueue.clear();

        // Shutdown Lua machine
        if( machine != null )
        {
            machine.close();
            machine = null;
        }

        // Shutdown our APIs
        for( ILuaAPI api : apis ) api.shutdown();

        // Unload filesystem
        if( fileSystem != null )
        {
            fileSystem.close();
            fileSystem = null;
        }

        computer.getEnvironment().resetOutput();
        computer.markChanged();
    }

    private void runCommand() throws InterruptedException
    {
        StateCommand command;
        synchronized( queueLock )
        {
            command = this.command;
            this.command = null;
        }

        isOnLock.lockInterruptibly();
        try
        {
            switch( command )
            {
                case TURN_ON:
                    if( isOn ) return;
                    turnOn();
                    break;

                case SHUTDOWN:
                    if( !isOn ) return;
                    computer.getTerminal().reset();
                    shutdown();
                    break;

                case REBOOT:
                    if( !isOn ) return;
                    computer.getTerminal().reset();
                    shutdown();

                    computer.turnOn();
                    break;
            }
        }
        finally
        {
            isOnLock.unlock();
        }
    }

    void work() throws InterruptedException
    {
        if( isExecuting.getAndSet( true ) )
        {
            throw new IllegalStateException( "Multiple threads running on computer the same time" );
        }

        try
        {
            ExecutorTask task = taskQueue.poll();
            if( task != null ) task.run();
        }
        finally
        {
            isExecuting.set( false );
        }
    }

    private void displayFailure( String message )
    {
        Terminal terminal = computer.getTerminal();
        terminal.reset();
        terminal.write( message );
        terminal.setCursorPos( 0, 1 );
        terminal.write( "ComputerCraft may be installed incorrectly" );
    }

    private enum StateCommand
    {
        TURN_ON,
        SHUTDOWN,
        REBOOT,
    }

}
