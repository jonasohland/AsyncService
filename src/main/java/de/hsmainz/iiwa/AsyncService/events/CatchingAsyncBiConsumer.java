package de.hsmainz.iiwa.AsyncService.events;

public abstract class CatchingAsyncBiConsumer<T, U> implements AsyncTask {

    private Throwable throwie;
    private LazyAllocatedListenableFuture<Throwable> future = new LazyAllocatedListenableFuture<>();
    private AsyncTimer timer;

    private T _input;
    private U _input2;

    public CatchingAsyncBiConsumer() {
        future.prepare(this);
    }


    @Override
    public void execute() {
        try {
            test(_input, _input2);
        } catch (Throwable e) {
            throwie = e;
        }

        future.fire(throwie);
    }

    public abstract void test(T firstInput, U secondInput) throws Throwable;

    @Override
    public void fire() {
        AsyncService.post(this);
    }

    public void fire(T __input) {
        __set__arg_(__input);
        AsyncService.post(this);
    }

    @Override
    public AsyncTask copy() throws CloneNotSupportedException{
        throw new CloneNotSupportedException();
    }

    @Override
    public void attachTimer(AsyncTimer t) {
        timer = t;
    }

    @Override
    public boolean hasTimer() {
        return timer != null;
    }

    @Override
    public AsyncTimer getTimer() {
        return timer;
    }

    @Override
    public <K> void __set__arg_(K arg) {
        _input = (T) arg;
    }

    @Override
    public <L> void __set__sec__arg_(L arg) {
        _input2 = (U) arg;
    }

    @SuppressWarnings("unchecked")
    @Override
    public ListenableFuture<Throwable> getFuture() {
        return future.get();
    }

    public LazyAllocatedListenableFuture<Throwable> getFutureLazy() {
        return future;
    }
}
