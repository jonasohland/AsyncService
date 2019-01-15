package de.hsmainz.iiwa.AsyncService.utils;

import de.hsmainz.iiwa.AsyncService.async.Async;
import de.hsmainz.iiwa.AsyncService.async.AsyncTask;
import de.hsmainz.iiwa.AsyncService.except.ExecutionRejectedException;
import de.hsmainz.iiwa.AsyncService.executor.layer.ExecutorLayer;
import de.hsmainz.iiwa.AsyncService.functional.*;

public class StrandExecutor {

    private ExecutorLayer next_layer;

    /**
     * Construct the StrandExecutor with its next_layer
     * @param layer the next_layer
     */
    public StrandExecutor(ExecutorLayer layer){
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

    /**
     * Post an AsyncTask to this strand. If this strand is blocked, it will throw an ExecutionRejectedException
     * @param task the task to be posted to the next_layer
     * @throws ExecutionRejectedException will be thrown if the strand is blocked by another execution
     */
    public void post(AsyncTask task) throws ExecutionRejectedException {
        handle_task(task);
    }

    /**
     * Post an AsyncTask to this strand. If this strand is blocked, it will throw an ExecutionRejectedException
     * @param runnable the runnable to be converted to a AsyncTask that will be posted to the next_layer
     * @throws ExecutionRejectedException will be thrown if the strand is blocked by another execution
     */
    public void post(Runnable runnable) throws ExecutionRejectedException {
        handle_task(Async.makeAsync(runnable));
    }

    /**
     * Post an AsyncTask to this strand. If this strand is blocked, it will throw an ExecutionRejectedException
     * @param consumer the consumer to be converted to a AsyncTask that will be posted to the next_layer
     * @throws ExecutionRejectedException will be thrown if the strand is blocked by another execution
     */
    public <T> void post(Consumer<T> consumer) throws ExecutionRejectedException {
        handle_task(Async.makeAsync(consumer));
    }

    /**
     * Post an AsyncTask to this strand. If this strand is blocked, it will throw an ExecutionRejectedException
     * @param supplier the supplier to be converted to a AsyncTask that will be posted to the next_layer
     * @throws ExecutionRejectedException will be thrown if the strand is blocked by another execution
     */
    public <T> void post(Supplier<T> supplier) throws ExecutionRejectedException {
        handle_task(Async.makeAsync(supplier));
    }

    /**
     * Post an AsyncTask to this strand. If this strand is blocked, it will throw an ExecutionRejectedException
     * @param function the function to be converted to a AsyncTask that will be posted to the next_layer
     * @throws ExecutionRejectedException will be thrown if the strand is blocked by another execution
     */
    public <T, R> void post(Function<T, R> function) throws ExecutionRejectedException {
        handle_task(Async.makeAsync(function));
    }

    /**
     * Post an AsyncTask to this strand. If this strand is blocked, it will throw an ExecutionRejectedException
     * @param biconsumer the biconsumer to be converted to a AsyncTask that will be posted to the next_layer
     * @throws ExecutionRejectedException will be thrown if the strand is blocked by another execution
     */
    public <T, U> void post(BiConsumer<T, U> biconsumer) throws ExecutionRejectedException {
        handle_task(Async.makeAsync(biconsumer));
    }

    /**
     * Post an AsyncTask to this strand. If this strand is blocked, it will throw an ExecutionRejectedException
     * @param bifunction the bifunction to be converted to a AsyncTask that will be posted to the next_layer
     * @throws ExecutionRejectedException will be thrown if the strand is blocked by another execution
     */
    public <T, U, R> void post(BiFunction<T, U, R> bifunction) throws ExecutionRejectedException {
        handle_task(Async.makeAsync(bifunction));
    }

}
