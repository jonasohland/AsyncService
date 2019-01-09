package de.hsmainz.iiwa.AsyncService.executor.context;

import de.hsmainz.iiwa.AsyncService.executor.layer.ExecutorLayer;

public interface ExecutorContext extends ExecutorLayer {

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
