package de.hsmainz.iiwa.AsyncService.executor;

import de.hsmainz.iiwa.AsyncService.functional.BiConsumer;
import de.hsmainz.iiwa.AsyncService.functional.BiFunction;

public class DualListenableFuture<T, U> extends ListenableBase<T> {

    private AsyncTask tsk;
    private T resultT;
    private U resultU;


    public void addListener(BiConsumer<T, U> biConsumer){
        get_queue().add(Async.makeAsync(biConsumer));
    }

    public void addListener(ExecutionContext ctx, BiConsumer<T, U> biConsumer){
        get_queue().add(Async.makeAsync(biConsumer));
    }

    public <R> ListenableFuture<R> addListenerAnd(BiConsumer<T, U> biConsumer){
        AsyncTask bi = Async.makeAsync(biConsumer);
        get_queue().add(bi);
        return bi.future();
    }

    public <R> ListenableFuture<R> addListenerAnd(ExecutionContext ctx, BiConsumer<T, U> biConsumer){
        AsyncTask bi = Async.makeAsync(ctx, biConsumer);
        get_queue().add(bi);
        return bi.future();
    }

    public <R> void addListener(BiFunction<T, U, R> biFunction){
        get_queue().add(Async.makeAsync(biFunction));
    }

    public <R> void addListener(ExecutionContext ctx, BiFunction<T, U, R> biFunction){
        get_queue().add(Async.makeAsync(ctx, biFunction));
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

    public <R> ListenableFuture<R> addListenerThen(ExecutionContext ctx, BiFunction<T, U, R> biFunction){
        AsyncTask bi = Async.makeAsync(ctx, biFunction);
        get_queue().add(bi);
        return bi.future();
    }

    @Override
    protected <R> Listenable<R> get_next_listenable(AsyncTask tsk) {
        return tsk.future();
    }
}
