package de.hsmainz.iiwa.AsyncService.deprecated.events;

import de.hsmainz.iiwa.AsyncService.functional.*;

public abstract class AsyncTaskExecutor {

    protected abstract void handle_task(AsyncTask task);

    public void post(AsyncTask task){
        handle_task(task);
    }

    public void post(Runnable runnable) {
        handle_task(Async.makeAsync(runnable));
    }

    public <T> void post(Consumer<T> consumer) {
        handle_task(Async.makeAsync(consumer));
    }

    public <T> void post(T in1, Consumer<T> consumer) {
        handle_task(Async.makeAsync(in1, consumer));
    }

    public <T> void post(Supplier<T> supplier){
        handle_task(Async.makeAsync(supplier));
    }

    public <T, R> void post(Function<T, R> function){
        handle_task(Async.makeAsync(function));
    }

    public <T, R> void post(T in, Function<T, R> function){
        handle_task(Async.makeAsync(in, function));
    }

    public <T, U> void post(BiConsumer<T, U> biconsumer){
        handle_task(Async.makeAsync(biconsumer));
    }

    public <T, U> void post(T in1, U in2, BiConsumer<T, U> biconsumer){
        handle_task(Async.makeAsync(in1, in2, biconsumer));
    }

    public <T, U, R> void post(BiFunction<T, U, R> bifunction){
        handle_task(Async.makeAsync(bifunction));
    }

    public <T, U, R> void post(T in1, U in2, BiFunction<T, U, R> bifunction){
        handle_task(Async.makeAsync(in1, in2, bifunction));
    }
}
