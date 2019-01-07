package de.hsmainz.iiwa.AsyncService.executor;

public class Event<T> extends ListenableBase<T> {

    private ExecutionLayer lay;

    public Event(ExecutionLayer layer){
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