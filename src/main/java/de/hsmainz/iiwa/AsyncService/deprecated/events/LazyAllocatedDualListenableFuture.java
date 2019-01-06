package de.hsmainz.iiwa.AsyncService.deprecated.events;

@Deprecated
public class LazyAllocatedDualListenableFuture<T, U> {

    private DualListenableFuture<T, U> future;
    AsyncTask e;

    public LazyAllocatedDualListenableFuture() {}

    public LazyAllocatedDualListenableFuture(AsyncTask ev) {
        e = ev;
    }

    void prepare(AsyncTask ev){
        e = ev;
        if(isAlloc())
            future.setAsyncTask(ev);
    }

    public DualListenableFuture<T, U> get() {
        alloc();
        return future;
    }

    public DualListenableFuture<T, U> weak_get() {
        return future;
    }

    public void fire(T val, U val2) {
        alloc();
        future.fire(val, val2);
    }

    public void fire() {
        alloc();
        future.fire();
    }

    public void weak_fire() {
        if(isAlloc()){
            future.fire();
        }
    }

    public void weak_fire(T value, U value2){
        if(isAlloc()) {
            future.fire(value, value2);
        }
    }

    public void alloc() {
        if(future != null)
            if(e != null)
                future = new DualListenableFuture<T, U>(e);
            else
                future = new DualListenableFuture<T, U>();
    }

    public boolean isAlloc() {
        return future != null;
    }

    void attach(DualListenableFuture<T, U> _future){
        future = _future;
        if(e != null) {
            future.setAsyncTask(e);
        }
    }

    void attach_if_alloc(DualListenableFuture<T, U> _future) {
        if(_future != null){
            attach(_future);
        }
    }
}
