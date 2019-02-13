package de.hsmainz.iiwa.AsyncService.async;

import de.hsmainz.iiwa.AsyncService.executor.layer.ExecutorLayer;
import de.hsmainz.iiwa.AsyncService.future.ListenableFuture;
import de.hsmainz.iiwa.AsyncService.functional.Supplier;

public class AsyncSupplier <T> implements AsyncTask {

    private ExecutorLayer exec;
    private T input;
    private Supplier<T> func;
    private ListenableFuture<T> future;

    public AsyncSupplier(Supplier<T> function){
        func = function;
    }

    public AsyncSupplier(ExecutorLayer ctx, Supplier<T> function){
        func = function;
        exec = ctx;
    }

    public AsyncSupplier(T in, Supplier<T> function){
        input = in;
        func = function;
    }

    public AsyncSupplier(T in, Supplier<T> function, ExecutorLayer ctx){
        input = in;
        func = function;
        exec = ctx;
    }

    @Override
    public void execute() {
        T result = func.get();
        if(future != null){
            future.fire(result);
        }
    }

    @Override
    public void bind(ExecutorLayer layer) {
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

    @Override
    public AsyncTask copy() throws CloneNotSupportedException {
        return null;
    }

    @Override
    public <K> void __set__arg_(K arg) {
        input = (T) arg;
    }

    @Override
    public <L> void __set__sec__arg_(L arg) { }

    @Override
    public ListenableFuture<T> future() {
        if(future == null){
            future = new ListenableFuture<T>(this);
        }
        return future;
    }
}
