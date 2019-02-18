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
import java.util.Arrays;
import java.util.TreeSet;
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
    private static final long ABORT_TIMEOUT = TimeUnit.MILLISECONDS.toNanos( 2500 );

    /**
     * The target latency between executing two tasks on a single machine.
     *
     * An average tick takes 50ms, and so we ideally need to have handled a couple of events within that window in order
     * to have a perceived low latency.
     */
    private static final long DEFAULT_LATENCY = TimeUnit.MILLISECONDS.toNanos( 50 );

    /**
     * The minimum value that {@link #DEFAULT_LATENCY} can have when scaled.
     *
     * From statistics gathered on SwitchCraft, almost all machines will execute under 15ms, 75% under 1.5ms, with the
     * mean being about 3ms. Most computers shouldn't be too impacted with having such a short period to execute in.
     */
    private static final long DEFAULT_MIN_PERIOD = TimeUnit.MILLISECONDS.toNanos( 5 );

    /**
     * The maximum number of tasks before we have to start scaling latency linearly.
     */
    private static final long LATENCY_MAX_TASKS = DEFAULT_LATENCY / DEFAULT_MIN_PERIOD;

    /**
     * Lock used for modifications to {@link #s_stopped} and {@link #s_threads}
     */
    private static final Object s_stateLock = new Object();

    /**
     * Whether the thread is stopped or should be stopped
     */
    private static volatile boolean s_stopped = false;

    /**
     * The threads tasks execute on
     */
    private static Thread[] s_threads = null;

    /**
     * Currently running executors
     */
    private static ComputerExecutor[] s_executors = null;

    private static long latency;
    private static long minPeriod;

    private static final ReentrantLock computerLock = new ReentrantLock();

    private static final Condition hasWork = computerLock.newCondition();

    /**
     * Active queues to execute
     */
    private static final TreeSet<ComputerExecutor> computerQueue = new TreeSet<>( ( a, b ) -> {
        if( a == b ) return 0; // Should never happen, but let's be consistent here

        long at = a.virtualRuntime, bt = b.virtualRuntime;
        if( at == bt ) return Integer.compare( a.hashCode(), b.hashCode() );
        return Long.compare( at, bt );
    } );

    /**
     * The minimum {@link ComputerExecutor#virtualRuntime} time on the tree.
     */
    private static long minimumVirtualRuntime = 0;

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
                // FIXME: Allow growing/shrinking this while on the fly. Currently this can only be changed when outside
                //  the world.
                s_threads = new Thread[ComputerCraft.computer_threads];
                s_executors = new ComputerExecutor[s_threads.length];
                minimumVirtualRuntime = 0;

                // latency and minPeriod are scaled by 1 + floor(log2(threads)). We can afford to execute tasks for
                // longer when executing on more than one thread.
                long factor = 64 - Long.numberOfLeadingZeros( s_threads.length );
                latency = DEFAULT_LATENCY * factor;
                minPeriod = DEFAULT_MIN_PERIOD * factor;
            }

            for( int i = 0; i < s_threads.length; i++ )
            {
                Thread thread = s_threads[i];
                if( thread == null || !thread.isAlive() )
                {
                    (s_threads[i] = s_ManagerFactory.newThread( new TaskExecutor( i ) )).start();
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
                    if( thread != null && thread.isAlive() ) thread.interrupt();
                }

                Arrays.fill( s_executors, null );
            }
        }

        computerQueue.clear();
    }

    /**
     * Mark a computer as having work, enqueuing it on the thread.
     *
     * @param executor The computer to execute work on.
     */
    static void queue( @Nonnull ComputerExecutor executor )
    {
        computerLock.lock();
        try
        {
            if( executor.onComputerQueue ) throw new IllegalStateException( "Cannot queue already queued executor" );
            executor.onComputerQueue = true;

            updateRuntimes();

            // We're not currently on the queue, so update its current execution time to
            // ensure its at least as high as the minimum.
            long newRuntime = minimumVirtualRuntime;

            if( executor.virtualRuntime == 0 )
            {
                // Slow down new computers a little bit.
                newRuntime += scaledPeriod();
            }
            else
            {
                // Give a small boost to computers which have slept a little.
                newRuntime -= latency / 2;
            }

            executor.virtualRuntime = Math.max( newRuntime, executor.virtualRuntime );

            // Add to the queue, and signal the workers.
            computerQueue.add( executor );
            hasWork.signal();
        }
        finally
        {
            computerLock.unlock();
        }
    }

    /**
     * Update the {@link ComputerExecutor#virtualRuntime}s of all running tasks, and then increment the
     * {@link #minimumVirtualRuntime} of the executor.
     */
    private static void updateRuntimes()
    {
        long minRuntime = Long.MAX_VALUE;

        // If we've a task on the queue, use that as our base time.
        if( !computerQueue.isEmpty() ) minRuntime = computerQueue.first().virtualRuntime;

        // Update all the currently executing tasks
        ComputerExecutor[] executors = s_executors;
        if( executors != null )
        {
            long now = System.nanoTime();
            int tasks = 1 + computerQueue.size();
            for( ComputerExecutor executor : s_executors )
            {
                if( executor != null )
                {
                    // We do two things here: first we update the task's virtual runtime based on when we
                    // last checked, and then we check the minimum.
                    minRuntime = Math.min( minRuntime, executor.virtualRuntime += (now - executor.currentStart) / tasks );
                    executor.currentStart = now;
                }
            }
        }

        if( minRuntime > minimumVirtualRuntime && minRuntime < Long.MAX_VALUE )
        {
            minimumVirtualRuntime = minRuntime;
        }
    }

    /**
     * Re-add this task to the queue
     *
     * @param executor The executor to requeue
     * @param duration The duration of the previous task
     */
    private static void requeue( ComputerExecutor executor, long duration )
    {
        computerLock.lock();
        try
        {
            updateRuntimes();

            // Add to the queue, and signal the workers.
            if( !executor.checkRequeue( duration ) ) return;

            computerQueue.add( executor );
            hasWork.signal();
        }
        finally
        {
            computerLock.unlock();
        }
    }

    /**
     * The scaled period for a single task
     *
     * @return The scaled period for the task
     * @see #DEFAULT_LATENCY
     * @see #DEFAULT_MIN_PERIOD
     * @see #LATENCY_MAX_TASKS
     */
    private static long scaledPeriod()
    {
        // +1 to include the current task
        int count = 1 + computerQueue.size();
        return count < LATENCY_MAX_TASKS ? latency / count : minPeriod;
    }

    /**
     * Responsible for pulling and managing computer tasks. This pulls a task from {@link #computerQueue},
     * creates a new thread using {@link TaskRunner} or reuses a previous one and uses that to execute the task.
     *
     * If the task times out, then it will attempt to interrupt the {@link TaskRunner} instance.
     */
    private static final class TaskExecutor implements Runnable
    {
        private final int id;
        private TaskRunner runner;
        private Thread runnerThread;

        private TaskExecutor( int id )
        {
            this.id = id;
        }

        @Override
        public void run()
        {
            ComputerExecutor[] executors = s_executors;

            try
            {
                while( true )
                {
                    // Wait for some work from the queue
                    ComputerExecutor executor;
                    computerLock.lockInterruptibly();
                    try
                    {
                        while( computerQueue.size() == 0 ) hasWork.await();
                        executor = computerQueue.pollFirst();
                        assert executor != null : "hasWork should ensure we never receive null work";

                        executor.currentStart = System.nanoTime();
                        executors[id] = executor;
                    }
                    finally
                    {
                        computerLock.unlock();
                    }


                    // If threads should be stopped then return
                    synchronized( s_stateLock )
                    {
                        if( s_stopped ) return;
                    }

                    execute( executor );

                    executors[id] = null;
                }
            }
            catch( InterruptedException ignored )
            {
                Thread.currentThread().interrupt();
            }
            finally
            {
                if( runnerThread != null ) runnerThread.interrupt();
                executors[id] = null;
            }
        }

        private void execute( @Nonnull ComputerExecutor executor ) throws InterruptedException
        {
            TaskRunner runner = this.runner;
            if( runnerThread == null || !runnerThread.isAlive() )
            {
                runner = this.runner = new TaskRunner();
                (runnerThread = s_RunnerFactory.newThread( runner )).start();
            }

            runner.lock.lockInterruptibly();

            long start = System.nanoTime();
            try
            {
                // Reset the pause flag and submit to the executor.
                runner.submit( executor );

                long duration = executor.currentDuration;

                while( duration < TIMEOUT )
                {
                    // Run the computer for a single period.
                    if( runner.await( scaledPeriod() ) ) return;

                    duration = executor.currentDuration + (System.nanoTime() - start);

                    // If we've no other tasks to do, we can continue working on this one. Otherwise, try
                    // to suspend. Note, we may end up executing this task straight away, but that's unlikely.
                    if( !computerQueue.isEmpty() )
                    {
                        executor.timeout.pause();
                        break;
                    }
                }

                // Run for any remaining time.
                if( runner.await( TIMEOUT - duration ) ) return;

                // If they overreach that, then attempt to soft abort
                // TODO: We need to handle aborting within this region too.
                executor.timeout.softAbort();
                if( runner.await( ABORT_TIMEOUT ) ) return;

                // Then hard abort
                executor.timeout.hardAbort();
                if( runner.await( ABORT_TIMEOUT ) ) return;

                if( ComputerCraft.logPeripheralErrors ) dumpTimeout( executor, System.nanoTime() - start );

                // Interrupt the thread and clear it + the runner. We'll make a new one next time round.
                runnerThread.interrupt();
                runnerThread = null;
                this.runner = null;
            }
            finally
            {
                long duration = System.nanoTime() - start;
                Tracking.addTaskTiming( executor.getComputer(), duration );

                // Increment the virtual runtime as required, and requeue.
                requeue( executor, duration );

                runner.lock.unlock();
            }
        }

        private void dumpTimeout( ComputerExecutor executor, long time )
        {
            // Print a stack trace of the running executor. Unlikely to yield an awful lot of info, as Lua
            // executes on a different thread, but worth a shot.

            StringBuilder builder = new StringBuilder( "Terminating " )
                .append( "computer #" ).append( executor.getComputer().getID() )
                .append( " due to timeout (running for " ).append( time / 1e9 )
                .append( " seconds, and " ).append( executor.currentDuration )
                .append( " as part of this task)." )
                .append( " This is not a bug, but does mean a computer is evading termination detection. " )
                .append( runnerThread.getName() ).append( " is currently " ).append( runnerThread.getState() );

            Object blocking = LockSupport.getBlocker( runnerThread );
            if( blocking != null ) builder.append( "\n  on " ).append( blocking );

            for( StackTraceElement element : runnerThread.getStackTrace() )
            {
                builder.append( "\n  at " ).append( element );
            }

            ComputerCraft.log.warn( builder.toString() );
        }
    }

    /**
     * Responsible for the actual running of tasks. It waits for the {@link TaskRunner#executorSignal} semaphore to be
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

                    // Execute the task. Note, this must be done outside the lock.
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
                    lock.lock();
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
                ComputerCraft.log.warn( "Computer runner was interrupted", e );
                Thread.currentThread().interrupt();
            }
        }

        void submit( ComputerExecutor task )
        {
            this.executor = task;
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
