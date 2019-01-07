package de.hsmainz.iiwa.AsyncService.executor;

import de.hsmainz.iiwa.AsyncService.functional.Consumer;
import de.hsmainz.iiwa.AsyncService.functional.Function;

import java.util.concurrent.LinkedBlockingQueue;

public abstract class ListenableBase<T> implements Listenable<T> {

    protected final LinkedBlockingQueue<AsyncTask> queue = new LinkedBlockingQueue<>();

    abstract protected <R> Listenable <R> get_next_listenable(AsyncTask tsk);

    protected LinkedBlockingQueue<AsyncTask> get_queue(){
        return queue;
    }

    @Override
    public void addListener(AsyncTask tsk){
        queue.add(tsk);
    }

    @Override
    public void addListener(Runnable runnable) {
        queue.add(Async.makeAsync(runnable));
    }

    @Override
    public void addListener(ExecutionLayer ctx, Runnable runnable) {
        queue.add(Async.makeAsync(ctx, runnable));
    }

    @Override
    public void addListener(Consumer<T> consumer) {
        queue.add(Async.makeAsync(consumer));
    }

    @Override
    public void addListener(ExecutionLayer ctx, Consumer<T> consumer) {
        queue.add(Async.makeAsync(ctx, consumer));
    }

    @Override
    public <R> void addListener(Function<T, R> function) {
        queue.add(Async.makeAsync(function));
    }

    @Override
    public <R> void addListener(ExecutionLayer ctx, Function<T, R> function) {
        queue.add(Async.makeAsync(ctx, function));
    }

    public <R> Listenable<R> addListenerThen(Function<T, R> function){
        AsyncFunction<T, R> async_function = Async.makeAsync(function);
        queue.add(async_function);
        return get_next_listenable(async_function);
    }
}
