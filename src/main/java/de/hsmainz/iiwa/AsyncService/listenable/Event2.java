package de.hsmainz.iiwa.AsyncService.listenable;

import de.hsmainz.iiwa.AsyncService.async.Async;
import de.hsmainz.iiwa.AsyncService.async.AsyncBiFunction;
import de.hsmainz.iiwa.AsyncService.async.AsyncTask;
import de.hsmainz.iiwa.AsyncService.executor.layer.ExecutorLayer;
import de.hsmainz.iiwa.AsyncService.functional.BiConsumer;
import de.hsmainz.iiwa.AsyncService.functional.BiFunction;
import de.hsmainz.iiwa.AsyncService.future.ListenableFuture;

public class Event2<T, U> extends ListenableBase<T> {

    private ExecutorLayer layer;

    public Event2(ExecutorLayer context){
        layer = context;
    }

    public void defer(T valueT, U valueU){
        for(AsyncTask element : get_queue()){
            element.__set__arg_(valueT);
            element.__set__sec__arg_(valueU);
            layer.defer(element);
        }
    }

    public void post(T valueT, U valueU){
        for(AsyncTask element : get_queue()){
            element.__set__arg_(valueT);
            element.__set__sec__arg_(valueU);
            layer.post(element);
        }
    }

    public void dispatch(T valueT, U valueU){
        for(AsyncTask element : get_queue()){
            element.__set__arg_(valueT);
            element.__set__sec__arg_(valueU);
            layer.dispatch(element);
        }
    }

    public void addListener(BiConsumer<T, U> biConsumer) {
        get_queue().add(Async.makeAsync(biConsumer));
    }

    public void addListener(ExecutorLayer layer, BiConsumer<T, U> biConsumer){
        get_queue().add(Async.makeAsync(layer, biConsumer));
    }

    public <R> void addListener(BiFunction<T, U, R> biFunction){
        get_queue().add(Async.makeAsync(biFunction));
    }

    public <R> void addListener(ExecutorLayer layer, BiFunction<T, U, R> biFunction){
        get_queue().add(Async.makeAsync(layer, biFunction));
    }

    public <R> ListenableFuture<R> addListenerThen(AsyncBiFunction<T, U, R> asyncBiFunction){
        get_queue().add(asyncBiFunction);
        return asyncBiFunction.future();
    }

    public <R> ListenableFuture<R> addListenerThen(BiFunction<T, U, R> biFunction){
        AsyncTask bi = Async.makeAsync(biFunction);
        get_queue().add(bi);
        return bi.future();
    }

    public <R> ListenableFuture<R> addListenerThen(ExecutorLayer layer, BiFunction<T, U, R> biFunction){
        AsyncTask bi = Async.makeAsync(layer, biFunction);
        get_queue().add(bi);
        return bi.future();
    }

    public void fire(T in1, U in2){

    }

    @Override
    protected <R> Listenable<R> get_next_listenable(AsyncTask tsk) {
        return tsk.future();
    }
}
