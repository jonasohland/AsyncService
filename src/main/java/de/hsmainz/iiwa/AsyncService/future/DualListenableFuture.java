package de.hsmainz.iiwa.AsyncService.future;

import de.hsmainz.iiwa.AsyncService.async.Async;
import de.hsmainz.iiwa.AsyncService.async.AsyncBiFunction;
import de.hsmainz.iiwa.AsyncService.async.AsyncTask;
import de.hsmainz.iiwa.AsyncService.executor.layer.ExecutorLayer;
import de.hsmainz.iiwa.AsyncService.listenable.Listenable;
import de.hsmainz.iiwa.AsyncService.listenable.ListenableBase;
import de.hsmainz.iiwa.AsyncService.functional.BiConsumer;
import de.hsmainz.iiwa.AsyncService.functional.BiFunction;

public class DualListenableFuture<T, U> extends ListenableBase<T> {

    private AsyncTask tsk;
    private T resultT;
    private U resultU;


    public void addListener(BiConsumer<T, U> biConsumer){
        get_queue().add(Async.makeAsync(biConsumer));
    }

    public void addListener(ExecutorLayer layer, BiConsumer<T, U> biConsumer){
        get_queue().add(Async.makeAsync(biConsumer));
    }

    public <R> ListenableFuture<R> addListenerAnd(BiConsumer<T, U> biConsumer){
        AsyncTask bi = Async.makeAsync(biConsumer);
        get_queue().add(bi);
        return bi.future();
    }

    public <R> ListenableFuture<R> addListenerAnd(ExecutorLayer layer, BiConsumer<T, U> biConsumer){
        AsyncTask bi = Async.makeAsync(layer, biConsumer);
        get_queue().add(bi);
        return bi.future();
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

    @Override
    protected <R> Listenable<R> get_next_listenable(AsyncTask tsk) {
        return tsk.future();
    }

    /**
     * Set this Futures Resource and fire all listeners associated to this Future.
     * @param value1 the result object which is fired.
     */
    public void fire(T value1, U value2) {

        if(!get_queue().isEmpty()) {

            if(tsk != null && tsk.layer() != null) {

                for (AsyncTask element : get_queue()) {

                    if(element.layer() == null){
                        element.bind(tsk.layer());
                    }

                    element.__set__arg_(value1);
                    element.__set__sec__arg_(value2);

                    element.layer().post(element);

                }
            } else {
                execute_listeners(value1, value2);
            }
        }
    }

    /**
     * Set this Futures Resource and fire all listeners associated to this Future.
     */
    public void fire() {

        if(!get_queue().isEmpty()) {

            if(Async.getLayer(tsk) != null) {

                for (AsyncTask element : get_queue()) {

                    if(element.layer() == null){
                        element.bind(tsk.layer());
                    }

                    element.layer().post(element);

                }
            } else {
                execute_listeners(null, null);
            }
        }
    }

    private void execute_listeners(T value1, U value2){
        for (AsyncTask element : get_queue()) {
            element.__set__arg_(value1);
            element.__set__sec__arg_(value2);
            element.fire();
        }
    }
}
