package de.hsmainz.iiwa.AsyncService.deprecated.events;

@Deprecated
public class LazyAllocatedListenableFuture<T> {

    private ListenableFuture<T> future;
    AsyncTask e;

    public LazyAllocatedListenableFuture(AsyncTask ev) {
        e = ev;
    }
    public LazyAllocatedListenableFuture() {

    }

    public void prepare(AsyncTask _e) {
        e = _e;
        if(isAlloc())
            future.setAsyncTask(_e);
    }

    public ListenableFuture<T> get() {
        alloc();
        return future;
    }

    public ListenableFuture<T> weak_get() {
        return future;
    }

    public ListenableFuture<T> get_unsafe() {
        return future;
    }

    public void fire(T val) {
        if(isAlloc())
            future.fire(val);
    }

    public void fire() {
        if(isAlloc())
            future.fire();
    }

    public void alloc() {
        if(future == null) {

            if (e != null)
                future = new ListenableFuture<T>(e);
            else
                future = new ListenableFuture<T>();
        }
    }

    public boolean isAlloc() {
        return future != null;
    }

    void attach(ListenableFuture<T> _future){

        future = _future;

        if(e != null){
            future.setAsyncTask(e);
        }
    }

    void attach_if_alloc(ListenableFuture<T> _future) {
        if(_future != null){

            future = _future;

            if(e != null)
                future.setAsyncTask(e);

        } else {
            // nothing
        }
    }
}
