package de.hsmainz.iiwa.AsyncService.utils;

import de.hsmainz.iiwa.AsyncService.async.Async;
import de.hsmainz.iiwa.AsyncService.async.AsyncTask;
import de.hsmainz.iiwa.AsyncService.except.ExecutionRejectedException;
import de.hsmainz.iiwa.AsyncService.executor.layer.ExecutorLayer;

import java.util.concurrent.LinkedBlockingDeque;

/**
 * This ExecutorLayer ensures that only one Task at a time will pass through this layer.
 * The next tasks will be queued and executed when the active task finished running. This class
 * provides two different layers for the queue front and back.
 */
public class StrandExecutorDeque {

    private final LinkedBlockingDeque<AsyncTask> queue = new LinkedBlockingDeque<AsyncTask>();

    private StrandExecutorDequeFront front;
    private StrandExecutorDequeBack back;

    private volatile boolean free = true;

    private ExecutorLayer next_layer;


    /**
     * Construct the layer with the next layer to sit on
     * @param nextlayer the next layer
     */
    public StrandExecutorDeque(ExecutorLayer nextlayer){
        next_layer = nextlayer;
        front = new StrandExecutorDequeFront(nextlayer, this);
        back = new StrandExecutorDequeBack(nextlayer, this);
    }

    /**
     * access the queue front
     * @return an ExecutorLayer that represents the queue front entry.
     * @see ExecutorLayer
     * @see StrandExecutorDequeFront
     */
    public StrandExecutorDequeFront front(){
        return front;
    }

    /**
     * access the queue front
     * @return an ExecutorLayer that represents the queue front entry.
     * @see ExecutorLayer
     * @see StrandExecutorDequeFront
     */
    public StrandExecutorDequeBack back(){
        return back;
    }

    boolean is_free(){
        return free;
    }

    void setFree(boolean is_free){
        free = is_free;
    }

    LinkedBlockingDeque<AsyncTask> get(){
        return queue;
    }

    synchronized void __next(){

        if(!queue.isEmpty()){

            AsyncTask next_task = queue.remove();

            next_layer.post(Async.makeAsync(() -> {
                this.__next();
                next_task.execute();
            }));

        }

        if(queue.isEmpty()){
            free = true;
        }
    }


}
