package de.hsmainz.iiwa.AsyncService.deprecated.events;

public abstract class CatchingAsyncFunction<T, R> implements AsyncTask {

    private Throwable throwie;
    private LazyAllocatedListenableFuture<R> future = new LazyAllocatedListenableFuture<>(this);
    private LazyAllocatedDualListenableFuture<R, Throwable> except_future = new LazyAllocatedDualListenableFuture<>(this );
    private AsyncTimer timer;

    private T _input;

    @Override
    public void execute() {
        R result = null;
        try {
            result = test(_input);
        } catch (Throwable e) {
            throwie = e;
        }

        if(except_future != null)
            except_future.fire(result, throwie);

        future.fire();
    }

    public abstract R test(T input) throws Throwable;

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

    }

    @SuppressWarnings("unchecked")
    @Override
    public ListenableFuture<R> getFuture() {
        return future.get();
    }

    public DualListenableFuture<R, Throwable> getExceptionFuture() {
        return except_future.get();
    }

    @Override
    public LazyAllocatedListenableFuture<R> getFutureLazy() {
        return future;
    }
}
