package de.hsmainz.iiwa.AsyncService.executor;

import de.hsmainz.iiwa.AsyncService.except.ExecutionRejectedException;
import de.hsmainz.iiwa.AsyncService.functional.*;

public class StrandExecutor {

    private ExecutionLayer next_layer;

    public StrandExecutor(ExecutionLayer layer){
        next_layer = layer;
    }

    private volatile boolean free = true;

    private synchronized void __set_free(){
        free = true;
    }

    public synchronized void handle_task(AsyncTask task) throws ExecutionRejectedException{

        if(free){
            next_layer.post(Async.makeAsync(()->{
                this.__set_free();
                task.execute();
            }));
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

    public <T> void post(Supplier<T> supplier) throws ExecutionRejectedException {
        handle_task(Async.makeAsync(supplier));
    }

    public <T, R> void post(Function<T, R> function) throws ExecutionRejectedException {
        handle_task(Async.makeAsync(function));
    }


    public <T, U> void post(BiConsumer<T, U> biconsumer) throws ExecutionRejectedException {
        handle_task(Async.makeAsync(biconsumer));
    }


    public <T, U, R> void post(BiFunction<T, U, R> bifunction) throws ExecutionRejectedException {
        handle_task(Async.makeAsync(bifunction));
    }

}
