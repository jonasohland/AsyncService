package de.hsmainz.iiwa.AsyncService.executor;


import de.hsmainz.iiwa.AsyncService.functional.BiFunction;

public class AsyncBiFunction <T, U, R> implements AsyncTask {

    private ExecutionLayer exec;
    private T inputT;
    private U inputU;
    private BiFunction<T, U, R> biFunction;
    private ListenableFuture<R> future;

    public AsyncBiFunction(BiFunction<T, U, R> biFunction){
        this.biFunction = biFunction;
    }

    public AsyncBiFunction(ExecutionLayer ctx, BiFunction<T, U, R> biFunction){
        this.biFunction = biFunction;
        exec = ctx;
    }

    public AsyncBiFunction(T in, BiFunction<T, U, R> biFunction){
        inputT = in;
        this.biFunction = biFunction;
    }

    public AsyncBiFunction(T inT, U inU, BiFunction<T, U, R> biFunction){
        inputT = inT;
        inputU = inU;
        this.biFunction = biFunction;
    }

    public AsyncBiFunction(T in, BiFunction<T, U, R> biFunction, ExecutionLayer ctx){
        inputT = in;
        this.biFunction = biFunction;
        exec = ctx;
    }

    public AsyncBiFunction(T inT, U inU, BiFunction<T, U, R> biFunction, ExecutionLayer ctx){
        inputT = inT;
        inputU = inU;
        this.biFunction = biFunction;
        exec = ctx;
    }

    @Override
    public void execute() {
        R result = biFunction.apply(inputT, inputU);
        if(future != null){
            future.fire(result);
        }
    }

    @Override
    public void bindLayer(ExecutionLayer layer) {
        exec = layer;
    }

    @Override
    public ExecutionLayer layer() {
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
    public ListenableFuture<R> future() {
        if(future == null){
            future = new ListenableFuture<R>(this);
        }
        return future;
    }
}
