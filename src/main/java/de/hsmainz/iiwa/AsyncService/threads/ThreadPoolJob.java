package de.hsmainz.iiwa.AsyncService.threads;

import de.hsmainz.iiwa.AsyncService.events.AsyncService;
import de.hsmainz.iiwa.AsyncService.functional.Supplier;
import de.hsmainz.iiwa.AsyncService.events.ListenableFuture;

public abstract class ThreadPoolJob<T> implements Executable {

    private ThreadPoolHandle handle;

    private T result;

    private volatile boolean should_exit;

    private volatile boolean is_active;

    private volatile boolean is_finished = false;

    private Thread th;

    /**
     * This listenable future will provide the result of the computation or whatever
     */
    public ListenableFuture<T> onFinish = new ListenableFuture<T>();

    /**
     * This AsyncTask will be fired at Thread start and it will have the executing java.lang.Thread as argument
     */
    public ListenableFuture<Void> onStart = new ListenableFuture<Void>();

    public ListenableFuture<Void> onShouldExit = new ListenableFuture<Void>();

    public ThreadPoolJob() {

        handle = new ThreadPoolHandle(this);

    }

    /**
     * execute this job immediately
     */
    private void execute() {

        ThreadPool.thread_log.info("ThreadPoolJob started, id: " + handle.getId());

        th = Thread.currentThread();

        set_active(true);

        onStart.fire();

        result = perform();


        set_active(false);

        ThreadPool.thread_pool_map.remove(this.handle.getId());
        onFinish.fire(result);

        set_finished(true);

        AsyncService.iterate_loop_if_waiting();

        ThreadPool.thread_log.info("ThreadPoolJob finished, id: " + handle.getId());

    }

    /**
     * get a runnable that can be used to execute this Job
     * @return a runnable that will execute this job
     */
    public Runnable getRunnable() {
        return () -> execute();
    }

    /**
     * Get the associated ThreadPoolHandle
     * @return the ThreadPoolHandle associated with this ThreadPoolJob
     * @see ThreadPoolHandle
     */
    public ThreadPoolHandle getHandle() {
        return handle;
    }


    /**
     * determine if this ThreadPoolJob is running
     * @return true if it is, false if not
     */
    public synchronized boolean isActive() {
        return is_active;
    }

    /**
     * determine if this ThreadPoolJob is finished
     * @return true if it is, false if not
     */
    public synchronized boolean isFinished() {
        return is_finished;
    }


    private synchronized void set_active(boolean active) {
        is_active = active;
    }
    private synchronized void set_finished(boolean finished) {
        is_finished = finished;
    }


    /**
     * start this job as soon as a thread is ready for executing it.
     */
    public void start() {
        if(ThreadPool.executor != null)
            ThreadPool.submit(this);
        else {
            ThreadPool.postJob(this);
        }
    }


    /**
     * Check if this Job was signaled to exit soon
     * @return true if signaled, false if not
     */
    protected synchronized boolean shouldExit() {
        return should_exit;
    }

    /**
     * tell a ThreadPoolJob to exit. The job can listen to this by calling the shouldExit() method
     */
    public synchronized void signalThreadShouldExit() {
        should_exit = true;
        onShouldExit.fire();
    }

    public synchronized void interrupt() {
        th.interrupt();
    }

    /**
     * Wait for a Jobs execution to finish.
     */
    public void join() throws InterruptedException{
        while(isActive()){
            if(Thread.interrupted()){
                throw new InterruptedException();
            }
        }
    }

    public T get() throws InterruptedException{
        while(isActive()){
            if(Thread.interrupted()){
                throw new InterruptedException();
            }
        }
        return result;
    }


    /**
     * This method must be implemented to perform the actual job
     * @return the result to emit via onFinish
     */
    public abstract T perform();

    public static ThreadPoolJob makeJob(Runnable r) {
        return new ThreadPoolJob<Void>() {
            @Override
            public Void perform() {
                r.run();
                return null;
            }
        };
    }

    public static <T> ThreadPoolJob<T> makeJob(Supplier<T> s) {
        return new ThreadPoolJob<T>() {
            @Override
            public T perform() {
                return s.get();
            }
        };
    }
}
