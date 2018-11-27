package de.hsmainz.iiwa.AsyncService.events;

import de.hsmainz.iiwa.AsyncService.except.ExecutionRejectedException;
import de.hsmainz.iiwa.AsyncService.functional.*;

public class StrandExecutor {

    private volatile boolean free = true;

    public void __set_free(){
        free = true;
    }

    public synchronized void handle_task(AsyncTask task) throws ExecutionRejectedException{

        if(free){
            AsyncService.post(()->{
                this.__set_free();
                task.execute();
            });
            this.free = false;
        } else throw new ExecutionRejectedException();
    }

    public void post(AsyncTask task) throws ExecutionRejectedException {
        handle_task(task);
    }

    public void post(Runnable runnable) throws ExecutionRejectedException {
        handle_task(Async.makeAsync(runnable));
    }

    public <T> void post(Consumer<T> consumer) throws ExecutionRejectedException {
        handle_task(Async.makeAsync(consumer));
    }

    public <T> void post(T in1, Consumer<T> consumer) throws ExecutionRejectedException {
        handle_task(Async.makeAsync(in1, consumer));
    }

    public <T> void post(Supplier<T> supplier) throws ExecutionRejectedException {
        handle_task(Async.makeAsync(supplier));
    }

    public <T, R> void post(Function<T, R> function) throws ExecutionRejectedException {
        handle_task(Async.makeAsync(function));
    }

    public <T, R> void post(T in, Function<T, R> function) throws ExecutionRejectedException {
        handle_task(Async.makeAsync(in, function));
    }

    public <T, U> void post(BiConsumer<T, U> biconsumer) throws ExecutionRejectedException {
        handle_task(Async.makeAsync(biconsumer));
    }

    public <T, U> void post(T in1, U in2, BiConsumer<T, U> biconsumer) throws ExecutionRejectedException {
        handle_task(Async.makeAsync(in1, in2, biconsumer));
    }

    public <T, U, R> void post(BiFunction<T, U, R> bifunction) throws ExecutionRejectedException {
        handle_task(Async.makeAsync(bifunction));
    }

    public <T, U, R> void post(T in1, U in2, BiFunction<T, U, R> bifunction) throws ExecutionRejectedException {
        handle_task(Async.makeAsync(in1, in2, bifunction));
    }


}
