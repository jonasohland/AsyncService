package de.hsmainz.iiwa.AsyncService.executor.context;

import de.hsmainz.iiwa.AsyncService.async.AsyncTask;
import de.hsmainz.iiwa.AsyncService.executor.layer.ExecutorLayer;

public class InstantExectorContext implements ExecutorContext {
    @Override
    public void run() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void reset() {

    }

    @Override
    public void registerWork(ExecutorWorkGuard wrk) {

    }

    @Override
    public void removeWork(ExecutorWorkGuard wrk) {

    }

    @Override
    public int workCount() {
        return 0;
    }

    @Override
    public void post(AsyncTask t) {
        t.execute();
    }

    @Override
    public void defer(AsyncTask t) {
        t.execute();
    }

    @Override
    public void dispatch(AsyncTask t) {
        t.execute();
    }

    @Override
    public ExecutorLayer next_layer() {
        return null;
    }

    @Override
    public ExecutorContext lowest_layer() {
        return this;
    }
}
