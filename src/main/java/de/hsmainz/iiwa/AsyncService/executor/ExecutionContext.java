package de.hsmainz.iiwa.AsyncService.executor;

public interface ExecutionContext extends ExecutionLayer {

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
