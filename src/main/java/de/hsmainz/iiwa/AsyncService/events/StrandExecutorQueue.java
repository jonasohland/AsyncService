package de.hsmainz.iiwa.AsyncService.events;

import de.hsmainz.iiwa.AsyncService.except.ExecutionRejectedException;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

public class StrandExecutorQueue extends AsyncTaskExecutor{
    private LinkedBlockingQueue<AsyncTask> queue = new LinkedBlockingQueue<>();


    private volatile boolean free = true;

    private synchronized void __next(){

        if(!queue.isEmpty()){

            AsyncTask next_task = queue.remove();

            AsyncService.post(Async.makeAsync(() -> {
                this.__next();
                next_task.execute();
            }));

        }

        if(queue.isEmpty()){
            free = true;
        }


    }

    public synchronized boolean postTask(AsyncTask task) {

        if(free){
            AsyncService.post(()->{
                this.__next();
                task.execute();
            });
            this.free = false;
            return true;
        } else {
            queue.add(task);
            return false;
        }
    }

    @Override
    protected void handle_task(AsyncTask task) {
        postTask(task);
    }
}
