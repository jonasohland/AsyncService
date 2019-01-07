package de.hsmainz.iiwa.AsyncService.executor;

import de.hsmainz.iiwa.AsyncService.functional.Consumer;

public class AsyncConsumer <T> implements AsyncTask {

    private ExecutionLayer exec;
    private T input;
    private Consumer<T> func;
    private ListenableFuture<Object> future;

    public AsyncConsumer(Consumer<T> function){
        func = function;
    }

    public AsyncConsumer(ExecutionLayer ctx, Consumer<T> function){
        func = function;
        exec = ctx;
    }

    public AsyncConsumer(T in, Consumer<T> function){
        input = in;
        func = function;
    }

    public AsyncConsumer(T in, Consumer<T> function, ExecutionLayer ctx){
        input = in;
        func = function;
        exec = ctx;
    }

    @Override
    public void execute() {
        func.accept(input);
        if(future != null){
            future.fire();
        }
    }

    @Override
    public void bindLayer(ExecutionLayer ctx) {
        exec = ctx;
    }

    @Override
    public ExecutionLayer layer(){
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
    public ListenableFuture<Object> future() {
        if(future == null){
            future = new ListenableFuture<Object>(this);
        }
        return future;
    }
}
