package de.hsmainz.iiwa.AsyncService.executor;

import de.hsmainz.iiwa.AsyncService.executor.Async;
import de.hsmainz.iiwa.AsyncService.executor.AsyncTask;

import java.util.concurrent.LinkedBlockingQueue;


public class StrandExecutorQueue extends ExecutionLayerBase {

    private LinkedBlockingQueue<AsyncTask> queue = new LinkedBlockingQueue<>();


    private ExecutionLayer ctx;

    private volatile boolean free = true;

    public StrandExecutorQueue(ExecutionLayer layer){
        super(layer);
        ctx = layer;
    }

    private synchronized void __next(){

        if(!queue.isEmpty()){

            AsyncTask next_task = queue.remove();

            next_layer().post(Async.makeAsync(() -> {
                this.__next();
                next_task.execute();
            }));

        }

        if(queue.isEmpty()){
            free = true;
        }
    }

    public LinkedBlockingQueue<AsyncTask> queue(){
        return queue;
    }

    private synchronized boolean postTask(AsyncTask task) {

        if(free){
            next_layer().post(Async.makeAsync(()->{
                this.__next();
                task.execute();
            }));
            this.free = false;
            return true;
        } else {
            queue.add(task);
            return false;
        }
    }

    private synchronized boolean deferTask(AsyncTask task) {

        if(free){
            next_layer().defer(Async.makeAsync(()->{
                this.__next();
                task.execute();
            }));
            this.free = false;
            return true;
        } else {
            queue.add(task);
            return false;
        }
    }

    protected void handle_task(AsyncTask task) {
        postTask(task);
    }

    @Override
    public void post(AsyncTask t) {
        if(t.layer() == null){
            t.bindLayer(this);
        }
        postTask(t);
    }

    @Override
    public void defer(AsyncTask t) {
        deferTask(t);
    }

    @Override
    public void dispatch(AsyncTask t) {
        if(t.layer() == null){
            t.bindLayer(this);
        }
        postTask(t);
    }
}
