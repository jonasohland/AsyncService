package de.hsmainz.iiwa.AsyncService.utils;

import de.hsmainz.iiwa.AsyncService.async.Async;
import de.hsmainz.iiwa.AsyncService.async.AsyncTask;
import de.hsmainz.iiwa.AsyncService.executor.layer.ExecutorLayer;
import de.hsmainz.iiwa.AsyncService.executor.layer.ExecutorLayerBase;

import java.util.concurrent.LinkedBlockingQueue;


/**
 * This ExecutorLayer ensures that only one Task at a time will pass through this layer.
 * The next tasks will be queued and executed when the active task finished running.
 */
public class StrandExecutorQueue extends ExecutorLayerBase {

    private LinkedBlockingQueue<AsyncTask> queue = new LinkedBlockingQueue<>();


    private ExecutorLayer ctx;

    private volatile boolean free = true;

    /**
     * Construct the layer with the next layer to sit on
     * @param layer the next layer
     */
    public StrandExecutorQueue(ExecutorLayer layer){
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

    /**
     * access the internal queue
     * @return the StrandExecutorQueues internal queue
     */
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

    @Override
    public void post(AsyncTask t) {
        if(t.layer() == null){
            t.bind(this);
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
            t.bind(this);
        }
        postTask(t);
    }
}
