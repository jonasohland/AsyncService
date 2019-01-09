package de.hsmainz.iiwa.AsyncService.executor.layer;

import de.hsmainz.iiwa.AsyncService.async.Async;
import de.hsmainz.iiwa.AsyncService.async.AsyncTask;
import de.hsmainz.iiwa.AsyncService.listenable.Listenable;
import de.hsmainz.iiwa.AsyncService.executor.context.ExecutorContext;
import de.hsmainz.iiwa.AsyncService.functional.*;

public abstract class ExecutorLayerBase implements ExecutorLayer {

    private ExecutorLayer nextLayer;

    public ExecutorLayerBase(ExecutorLayer next_layer){
        nextLayer = next_layer;
    }

    public void post(Runnable runnable){
        post(Async.makeAsync(runnable));
    }

    public <T> void post(Supplier<T> supplier){
        post(Async.makeAsync(supplier));
    }

    public <T> Listenable<T> postThen(Supplier<T> supplier){
        AsyncTask tsk = Async.makeAsync(supplier);
        post(tsk);
        return tsk.future();
    }

    public <T> void post(Consumer<T> consumer){
        post(Async.makeAsync(consumer));
    }

    public <T, R> void post(Function<T, R> function){
        post(Async.makeAsync(function));
    }

    public <T, R> Listenable<R> postThen(Function<T, R> function){
        AsyncTask tsk = Async.makeAsync(function);
        post(tsk);
        return tsk.future();
    }

    public <T, U> void post(BiConsumer<T, U> biConsumer){
        post(Async.makeAsync(biConsumer));
    }

    public <T, U, R> void post(BiFunction<T, U, R> biFunction) {
        post(Async.makeAsync(biFunction));
    }

    public <T, U, R> Listenable<R> postThen(BiFunction<T, U, R> function){
        AsyncTask tsk = Async.makeAsync(function);
        post(tsk);
        return tsk.future();
    }

    public void dispatch(Runnable runnable){
        dispatch(Async.makeAsync(runnable));
    }

    public <T> void dispatch(Supplier<T> supplier){
        dispatch(Async.makeAsync(supplier));
    }

    public <T> Listenable<T> dispatchThen(Supplier<T> supplier){
        AsyncTask tsk = Async.makeAsync(supplier);
        dispatch(tsk);
        return tsk.future();
    }

    public <T> void dispatch(Consumer<T> consumer){
        dispatch(Async.makeAsync(consumer));
    }

    public <T, R> void dispatch(Function<T, R> function){
        dispatch(Async.makeAsync(function));
    }

    public <T, R> Listenable<R> dispatchThen(Function<T, R> function){
        AsyncTask tsk = Async.makeAsync(function);
        dispatch(tsk);
        return tsk.future();
    }

    public <T, U> void dispatch(BiConsumer<T, U> biConsumer){
        dispatch(Async.makeAsync(biConsumer));
    }

    public <T, U, R> void dispatch(BiFunction<T, U, R> biFunction) {
        dispatch(Async.makeAsync(biFunction));
    }

    public <T, U, R> Listenable<R> dispatchThen(BiFunction<T, U, R> function){
        AsyncTask tsk = Async.makeAsync(function);
        dispatch(tsk);
        return tsk.future();
    }

    public void defer(Runnable runnable){
        defer(Async.makeAsync(runnable));
    }

    public <T> void defer(Supplier<T> supplier){
        defer(Async.makeAsync(supplier));
    }

    public <T> Listenable<T> deferThen(Supplier<T> supplier){
        AsyncTask tsk = Async.makeAsync(supplier);
        defer(tsk);
        return tsk.future();
    }

    public <T> void defer(Consumer<T> consumer){
        defer(Async.makeAsync(consumer));
    }

    public <T, R> void defer(Function<T, R> function){
        defer(Async.makeAsync(function));
    }

    public <T, R> Listenable<R> deferThen(Function<T, R> function){
        AsyncTask tsk = Async.makeAsync(function);
        defer(tsk);
        return tsk.future();
    }

    public <T, U> void defer(BiConsumer<T, U> biConsumer){
        defer(Async.makeAsync(biConsumer));
    }

    public <T, U, R> void defer(BiFunction<T, U, R> biFunction) {
        defer(Async.makeAsync(biFunction));
    }

    public <T, U, R> Listenable<R> deferThen(BiFunction<T, U, R> function){
        AsyncTask tsk = Async.makeAsync(function);
        defer(tsk);
        return tsk.future();
    }

    @Override
    public abstract void post(AsyncTask t);

    @Override
    public abstract void defer(AsyncTask t);

    @Override
    public abstract void dispatch(AsyncTask t);

    public ExecutorLayer next_layer(){
        return nextLayer;
    }

    public ExecutorContext lowest_layer(){
        return next_layer().lowest_layer();
    }
}
