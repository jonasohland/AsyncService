package de.hsmainz.iiwa.AsyncService.executor;

public class StrandExecutorDequeFront extends ExecutionLayerBase {

    StrandExecutorDeque queue;

    public StrandExecutorDequeFront(ExecutionLayer layer, StrandExecutorDeque q){
        super(layer);
        queue = q;
    }

    @Override
    public void post(AsyncTask t) {
        if(t.layer() != null){
            t.bindLayer(this);
        }
        post_task(t);
    }

    @Override
    public void defer(AsyncTask t) {
        post_task(t);
    }

    @Override
    public void dispatch(AsyncTask t) {
        if(t.layer() != null){
            t.bindLayer(this);
        }
        post_task(t);
    }
    private void post_task(AsyncTask t){

        synchronized (queue){

            if(queue.is_free()){
                next_layer().post(Async.makeAsync(()->{
                    queue.__next();
                    t.execute();
                }));
                queue.setFree(false);
            } else {
                queue.get().addFirst(t);
            }
        }
    }
}
