package de.hsmainz.iiwa.AsyncService.executor;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicBoolean;

public class StrandExecutorDeque {

    private final LinkedBlockingDeque<AsyncTask> queue = new LinkedBlockingDeque<AsyncTask>();

    private StrandExecutorDequeFront front;
    private StrandExecutorDequeBack back;

    private volatile boolean free = true;

    private ExecutionLayer next_layer;


    public StrandExecutorDeque(ExecutionLayer nextlayer){
        next_layer = nextlayer;
        front = new StrandExecutorDequeFront(nextlayer, this);
        back = new StrandExecutorDequeBack(nextlayer, this);
    }

    public StrandExecutorDequeFront front(){
        return front;
    }

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
