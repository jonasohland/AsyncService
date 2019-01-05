package de.hsmainz.iiwa.AsyncService.executor;

public interface ExecutionContext {

    /**
     *
     * @param tsk
     */
    public void post(AsyncTask tsk);

    /**
     *
     * @param tsk
     */
    public void dispatch(AsyncTask tsk);

    /**
     *
     */
    public void defer(AsyncTask tsk);

    /**
     *
     */
    public void run();

    /**
     *
     */
    public void stop();

    /**
     *
     */
    public void reset();

    void registerWork(ExecutorWorkGuard wrk);
    void removeWork(ExecutorWorkGuard wrk);
    int workCount();



}
