package de.hsmainz.iiwa.core.threads;

import de.hsmainz.iiwa.core.events.AsyncService;

public class ThreadPoolJobLite implements Executable {

    private ThreadPoolHandle handle;

    private Thread th;

    private volatile boolean should_exit;

    private volatile boolean is_active;

    private volatile boolean is_finished = false;

    private Runnable runnable;


    public ThreadPoolJobLite() {

        handle = new ThreadPoolHandle(this);

    }

    public ThreadPoolJobLite(Runnable __runnable){
        runnable = __runnable;
        handle = new ThreadPoolHandle(this);
    }

    /**
     * get a runnable that can be used to execute this Job
     * @return a runnable that will execute this job
     */
    public Runnable getRunnable() {
        return () -> {

            ThreadPool.thread_log.info("ThreadPoolJob started, id: " + handle.getId());

            th = Thread.currentThread();

            set_active(true);

            runnable.run();

            set_active(false);

            ThreadPool.thread_pool_map.remove(this.handle.getId());

            set_finished(true);

            if(AsyncService.isWaiting()) {
                AsyncService.post(()->{});
            }

            ThreadPool.thread_log.info("ThreadPoolJob finished, id: " + handle.getId());
        };
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
     * determine if this ThreadPoolJob is running
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

    public Thread getThread() {
        return th;
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
    }

    /**
     * Wait for a Jobs execution to finish.
     */
    public void join() throws InterruptedException {
        while(isActive()){
            if(Thread.interrupted()){
                throw new InterruptedException();
            }
        }
    }

    public static ThreadPoolJobLite makeJob(Runnable runnable) {
        return new ThreadPoolJobLite(runnable);
    }
}
