package de.hsmainz.iiwa.AsyncService.deprecated.events;

@Deprecated
public abstract class CatchingAsyncRunnable implements AsyncTask {



    private Throwable throwie;
    private LazyAllocatedListenableFuture<Throwable> future = new LazyAllocatedListenableFuture<>();
    private AsyncTimer timer;

    public CatchingAsyncRunnable(){
        future.prepare(this);
    }

    @Override
    public void execute() {
        try {
            test();
        } catch (Throwable e) {
            throwie = e;
        }

        future.fire(throwie);
    }

    public abstract void test() throws Throwable;

    @Override
    public void fire() {
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

    }

    @Override
    public <L> void __set__sec__arg_(L arg) {

    }

    @SuppressWarnings("unchecked")
    @Override
    public ListenableFuture<Throwable> getFuture() {
        return future.get();
    }

    @Override
    public LazyAllocatedListenableFuture<Throwable> getFutureLazy() {
        return future;
    }
}
