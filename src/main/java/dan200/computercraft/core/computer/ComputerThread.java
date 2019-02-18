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
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

public class ComputerThread
{
    /**
     * The total time a task is allowed to run before aborting.
     */
    private static final long TIMEOUT = TimeUnit.MILLISECONDS.toNanos( 7000 );

    /**
     * The time the task is allowed to run after each abort.
     */
    private static final long ABORT_TIMEOUT = TimeUnit.MILLISECONDS.toNanos( 1500 );

    private static final int QUEUE_LIMIT = 256;

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
            TaskRunner runner = this.runner;
            if( thread == null || !thread.isAlive() )
            {
                runner = this.runner = new TaskRunner();
                (thread = s_RunnerFactory.newThread( runner )).start();
            }


            runner.lock.lockInterruptibly();
            long start = System.nanoTime();

            try
            {
                // Execute the task
                runner.submit( executor );

                // If we ran within our time period, then just exit
                if( runner.await( TIMEOUT ) ) return;

                // Attempt to soft then hard abort
                executor.timeout.softAbort();
                if( runner.await( ABORT_TIMEOUT ) ) return;

                executor.timeout.hardAbort();
                if( runner.await( ABORT_TIMEOUT ) ) return;

                if( ComputerCraft.logPeripheralErrors )
                {
                    long time = System.nanoTime() - start;
                    StringBuilder builder = new StringBuilder()
                        .append( "Terminating computer #" ).append( executor.getComputer().getID() )
                        .append( " due to timeout (running for " ).append( time / 1e9 )
                        .append( " seconds). This is NOT a bug, but may mean a computer is misbehaving. " )
                        .append( thread.getName() )
                        .append( " is currently " )
                        .append( thread.getState() );
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
                this.runner = null;
            }
            finally
            {
                long stop = System.nanoTime();
                Tracking.addTaskTiming( executor.getComputer(), stop - start );

                runner.lock.unlock();

                executor.requeue();
            }
        }
    }

    /**
     * Responsible for the actual running of tasks. It waits for the {@link TaskRunner#executorSignal} condition to be
     * triggered, consumes a task and then triggers {@link TaskRunner#finishedSignal}.
     */
    private static final class TaskRunner implements Runnable
    {
        private final ReentrantLock lock = new ReentrantLock();

        private volatile ComputerExecutor executor;
        private final Condition executorSignal = lock.newCondition();

        private volatile boolean finished;
        private final Condition finishedSignal = lock.newCondition();

        @Override
        public void run()
        {
            try
            {
                while( true )
                {
                    // Pull the task from the queue.
                    lock.lockInterruptibly();
                    try
                    {
                        if( executor == null ) executorSignal.await();
                    }
                    finally
                    {
                        lock.unlock();
                    }

                    // Execute the task. Note, we must not be holding the lock at this point
                    try
                    {
                        executor.work();
                    }
                    catch( RuntimeException e )
                    {
                        ComputerCraft.log.error( "Error running task.", e );
                    }

                    executor = null;

                    // And mark the task as finished.
                    lock.lockInterruptibly();
                    try
                    {
                        finished = true;
                        finishedSignal.signal();
                    }
                    finally
                    {
                        lock.unlock();
                    }
                }
            }
            catch( InterruptedException e )
            {
                ComputerCraft.log.error( "Error running task.", e );
                Thread.currentThread().interrupt();
            }
        }

        void submit( ComputerExecutor executor )
        {
            this.executor = executor;
            executorSignal.signal();
        }

        boolean await( long timeout ) throws InterruptedException
        {
            if( !finished && finishedSignal.awaitNanos( timeout ) <= 0 ) return false;
            finished = false;
            return true;
        }
    }
}
