/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2019. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */

package dan200.computercraft.core.computer;

import dan200.computercraft.ComputerCraft;
import dan200.computercraft.core.tracking.Tracking;
import dan200.computercraft.shared.util.ThreadUtils;

import javax.annotation.Nonnull;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.locks.LockSupport;

public class ComputerThread
{
    /**
     * Lock used for modifications to the object
     */
    private static final Object s_stateLock = new Object();

    /**
     * Active queues to execute
     */
    private static final BlockingQueue<ComputerExecutor> computersActive = new LinkedBlockingQueue<>();

    /**
     * Whether the thread is stopped or should be stopped
     */
    private static boolean s_stopped = false;

    /**
     * The thread tasks execute on
     */
    private static Thread[] s_threads = null;

    private static final ThreadFactory s_ManagerFactory = ThreadUtils.factory( "Computer-Manager" );
    private static final ThreadFactory s_RunnerFactory = ThreadUtils.factory( "Computer-Runner" );

    /**
     * Start the computer thread
     */
    public static void start()
    {
        synchronized( s_stateLock )
        {
            s_stopped = false;
            if( s_threads == null || s_threads.length != ComputerCraft.computer_threads )
            {
                s_threads = new Thread[ComputerCraft.computer_threads];
            }

            for( int i = 0; i < s_threads.length; i++ )
            {
                Thread thread = s_threads[i];
                if( thread == null || !thread.isAlive() )
                {
                    (s_threads[i] = s_ManagerFactory.newThread( new TaskExecutor() )).start();
                }
            }
        }
    }

    /**
     * Attempt to stop the computer thread
     */
    public static void stop()
    {
        synchronized( s_stateLock )
        {
            if( s_threads != null )
            {
                s_stopped = true;
                for( Thread thread : s_threads )
                {
                    if( thread != null && thread.isAlive() )
                    {
                        thread.interrupt();
                    }
                }
            }
        }

        computersActive.clear();
    }

    /**
     * Mark a computer as having work, enqueuing it on the thread.
     *
     * @param computer The computer to execute work on.
     */
    static void queue( @Nonnull ComputerExecutor computer )
    {
        if( !computer.onComputerQueue ) throw new IllegalStateException( "Computer must be on queue" );
        computersActive.add( computer );
    }

    /**
     * Responsible for pulling and managing computer tasks. This pulls a task from {@link #computersActive},
     * creates a new thread using {@link TaskRunner} or reuses a previous one and uses that to execute the task.
     *
     * If the task times out, then it will attempt to interrupt the {@link TaskRunner} instance.
     */
    private static final class TaskExecutor implements Runnable
    {
        private TaskRunner runner;
        private Thread thread;

        @Override
        public void run()
        {
            try
            {
                while( true )
                {
                    // Wait for an active queue to execute
                    ComputerExecutor queue = computersActive.take();

                    // If threads should be stopped then return
                    synchronized( s_stateLock )
                    {
                        if( s_stopped ) return;
                    }

                    execute( queue );
                }
            }
            catch( InterruptedException ignored )
            {
                Thread.currentThread().interrupt();
            }
        }

        private void execute( @Nonnull ComputerExecutor executor ) throws InterruptedException
        {
            if( thread == null || !thread.isAlive() )
            {
                runner = new TaskRunner();
                (thread = s_RunnerFactory.newThread( runner )).start();
            }

            long start = System.nanoTime();

            // Execute the task
            runner.submit( executor );

            try
            {
                // We allow tasks to run for 7 seconds by default
                if( runner.await( 7000 ) ) return;

                // If they overreach that, then attempt to soft abort
                executor.abort( false );
                if( runner.await( 1500 ) ) return;

                // Then hard abort
                executor.abort( true );
                if( runner.await( 1500 ) ) return;

                if( ComputerCraft.logPeripheralErrors )
                {
                    // Print a stack trace of the running executor. Unlikely to yield an awful lot of info, as Lua
                    // executes on a different thread, but worth a shot.

                    long time = System.nanoTime() - start;
                    StringBuilder builder = new StringBuilder( "Terminating " )
                        .append( "computer #" ).append( executor.getComputer().getID() )
                        .append( " due to timeout (running for " ).append( time / 1e9 )
                        .append( " seconds). This is NOT a bug, but may mean a computer is misbehaving. " )
                        .append( thread.getName() ).append( " is currently " ).append( thread.getState() );

                    Object blocking = LockSupport.getBlocker( thread );
                    if( blocking != null ) builder.append( "\n  on " ).append( blocking );

                    for( StackTraceElement element : thread.getStackTrace() )
                    {
                        builder.append( "\n  at " ).append( element );
                    }

                    ComputerCraft.log.warn( builder.toString() );
                }

                // Interrupt the thread
                thread.interrupt();
                thread = null;
                runner = null;
            }
            finally
            {
                long stop = System.nanoTime();
                Tracking.addTaskTiming( executor.getComputer(), stop - start );

                executor.requeue();
            }
        }
    }

    /**
     * Responsible for the actual running of tasks. It waits for the {@link TaskRunner#input} semaphore to be
     * triggered, consumes a task and then triggers {@link TaskRunner#finished}.
     */
    private static final class TaskRunner implements Runnable
    {
        private final Semaphore input = new Semaphore();
        private final Semaphore finished = new Semaphore();
        private ComputerExecutor task;

        @Override
        public void run()
        {
            try
            {
                while( true )
                {
                    input.await();
                    try
                    {
                        task.work();
                    }
                    catch( RuntimeException e )
                    {
                        ComputerCraft.log.error( "Error running task.", e );
                    }
                    task = null;
                    finished.signal();
                }
            }
            catch( InterruptedException e )
            {
                ComputerCraft.log.error( "Error running task.", e );
                Thread.currentThread().interrupt();
            }
        }

        void submit( ComputerExecutor task )
        {
            this.task = task;
            input.signal();
        }

        boolean await( long timeout ) throws InterruptedException
        {
            return finished.await( timeout );
        }
    }

    /**
     * A simple method to allow awaiting/providing a signal.
     *
     * Java does provide similar classes, but I only needed something simple.
     */
    private static final class Semaphore
    {
        private volatile boolean state = false;

        synchronized void signal()
        {
            state = true;
            notify();
        }

        synchronized void await() throws InterruptedException
        {
            while( !state ) wait();
            state = false;
        }

        synchronized boolean await( long timeout ) throws InterruptedException
        {
            if( !state )
            {
                wait( timeout );
                if( !state ) return false;
            }
            state = false;
            return true;
        }
    }
}
