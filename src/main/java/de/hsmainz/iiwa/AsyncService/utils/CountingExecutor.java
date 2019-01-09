package de.hsmainz.iiwa.AsyncService.utils;

import de.hsmainz.iiwa.AsyncService.async.AsyncTask;
import de.hsmainz.iiwa.AsyncService.executor.layer.ExecutorLayer;
import de.hsmainz.iiwa.AsyncService.executor.layer.ExecutorLayerBase;

public class CountingExecutor extends ExecutorLayerBase {

    private static volatile Integer counter = 0;

    public CountingExecutor(ExecutorLayer nextLayer){
        super(nextLayer);

    }

    @Override
    public void post(AsyncTask t) {
        synchronized (counter){
            counter++;
            next_layer().post(t);
        }

    }

    @Override
    public void defer(AsyncTask t) {
        synchronized (counter){
            counter++;
            next_layer().defer(t);
        }
    }

    @Override
    public void dispatch(AsyncTask t) {
        synchronized (counter){
            counter++;
            next_layer().dispatch(t);
        }
    }

    public void reset(){
        synchronized (counter){
            counter = 0;
        }
    }

    public Integer count(){
        synchronized (counter){
            return counter;
        }
    }
}
