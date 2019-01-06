package de.hsmainz.iiwa.AsyncService.executor;

import de.hsmainz.iiwa.AsyncService.functional.BiConsumer;

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

    @Override
    protected <R> Listenable<R> get_next_listenable(AsyncTask tsk) {
        return tsk.future();
    }
}
