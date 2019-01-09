package de.hsmainz.iiwa.AsyncService.listenable;

import de.hsmainz.iiwa.AsyncService.async.AsyncTask;
import de.hsmainz.iiwa.AsyncService.executor.layer.ExecutorLayer;

public class Event<T> extends ListenableBase<T> {

    private ExecutorLayer lay;

    public Event(ExecutorLayer layer){
        lay = layer;
    }

    public void defer(T value){
        for(AsyncTask element : get_queue()){
            element.__set__arg_(value);
            lay.defer(element);
        }
    }

    public void post(T value){
        for(AsyncTask element : get_queue()){
            element.__set__arg_(value);
            lay.post(element);
        }
    }

    public void dispatch(T value){
        for(AsyncTask element : get_queue()){
            element.__set__arg_(value);
            lay.dispatch(element);
        }
    }

    @Override
    protected <R> Listenable<R> get_next_listenable(AsyncTask tsk) {
        return tsk.future();
    }
}