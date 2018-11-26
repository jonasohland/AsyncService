package de.hsmainz.iiwa.AsyncService.events;

import de.hsmainz.iiwa.AsyncService.except.ExecutionRejectedException;

public class StrandExecutor {

    private volatile boolean free = true;

    public void __set_free(){
        free = true;
    }

    public synchronized void post(AsyncTask task) throws ExecutionRejectedException{

        if(free){
            AsyncService.post(()->{
                this.__set_free();
                task.execute();
            });
            this.free = false;
        } else throw new ExecutionRejectedException();
    }
}
