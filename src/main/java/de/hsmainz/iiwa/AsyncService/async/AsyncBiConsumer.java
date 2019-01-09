package de.hsmainz.iiwa.AsyncService.async;

import de.hsmainz.iiwa.AsyncService.executor.layer.ExecutorLayer;
import de.hsmainz.iiwa.AsyncService.future.ListenableFuture;
import de.hsmainz.iiwa.AsyncService.functional.BiConsumer;

public class AsyncBiConsumer <T, U> implements AsyncTask {

    private ExecutorLayer exec;
    private T inputT;
    private U inputU;
    private BiConsumer<T, U> biConsumer;
    private ListenableFuture<Object> future;

    public AsyncBiConsumer(BiConsumer<T, U> biconsumer){
        biConsumer = biconsumer;
    }

    public AsyncBiConsumer(ExecutorLayer ctx, BiConsumer<T, U> biconsumer){
        biConsumer = biconsumer;
        exec = ctx;
    }

    public AsyncBiConsumer(T in, BiConsumer<T, U> biConsumer){
        inputT = in;
        biConsumer = biConsumer;
    }

    public AsyncBiConsumer(T inT, U inU, BiConsumer<T, U> biConsumer){
        inputT = inT;
        inputU = inU;
        biConsumer = biConsumer;
    }

    public AsyncBiConsumer(T in, BiConsumer<T, U> biconsumer, ExecutorLayer ctx){
        inputT = in;
        biConsumer = biconsumer;
        exec = ctx;
    }

    public AsyncBiConsumer(T inT, U inU, BiConsumer<T, U> biconsumer, ExecutorLayer ctx){
        inputT = inT;
        inputU = inU;
        biConsumer = biconsumer;
        exec = ctx;
    }

    @Override
    public void execute() {
        biConsumer.accept(inputT, inputU);
        if(future != null){
            future.fire();
        }
    }

    @Override
    public void bindLayer(ExecutorLayer layer) {
        exec = layer;
    }

    @Override
    public ExecutorLayer layer() {
        return exec;
    }

    @Override
    public void fire() {
        if(exec != null){
            exec.post(this);
        } else {
            execute();
        }
    }

    public void fire(T arg) {

        __set__arg_(arg);

        if(exec != null){
            exec.post(this);
        } else {
            execute();
        }
    }

    public void fire(T argT, U argU) {

        __set__arg_(argT);
        __set__sec__arg_(argU);

        if(exec != null){
            exec.post(this);
        } else {
            execute();
        }
    }

    @Override
    public AsyncTask copy() throws CloneNotSupportedException {
        return null;
    }

    @Override
    public <K> void __set__arg_(K arg) {
        inputT = (T) arg;
    }

    @Override
    public <L> void __set__sec__arg_(L arg) { inputU = (U) arg; }

    @Override
    public ListenableFuture<Object> future() {
        if(future == null){
            future = new ListenableFuture<Object>(this);
        }
        return future;
    }
}
