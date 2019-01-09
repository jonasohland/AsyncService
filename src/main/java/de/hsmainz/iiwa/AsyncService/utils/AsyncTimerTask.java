package de.hsmainz.iiwa.AsyncService.utils;

import de.hsmainz.iiwa.AsyncService.async.Async;
import de.hsmainz.iiwa.AsyncService.async.AsyncTask;
import de.hsmainz.iiwa.AsyncService.except.TaskCancelledException;
import de.hsmainz.iiwa.AsyncService.executor.layer.ExecutorLayer;

import java.util.TimerTask;

public class AsyncTimerTask extends TimerTask {

    private AsyncTask tsk;
    private ExecutorLayer lay;
    private AsyncTimer tm;
    private boolean repeat = false;

    public AsyncTimerTask(AsyncTask task, AsyncTimer timer, ExecutorLayer layer, boolean repeat){
        tsk = task;
        tm = timer;
        lay = layer;
        this.repeat = repeat;
    }

    @Override
    public void run() {

        tsk.__set__arg_(new Completion<TaskCancelledException>());

        if(Async.getLayer(tsk) != null){
            tsk.fire();
        } else {
            lay.dispatch(tsk);
        }

        if(!repeat) {
            if (tm.running_timers.decrementAndGet() == 0) {
                tm.work.reset();
            }
        }
    }

    public boolean cancel(){

        boolean ret = super.cancel();

        tsk.__set__arg_(new Completion<TaskCancelledException>(new TaskCancelledException()));

        if(Async.getLayer(tsk) != null){
            tsk.fire();
        } else {
            lay.dispatch(tsk);
        }

        if(tm.running_timers.decrementAndGet() == 0){
            tm.work.reset();
        }

        return ret;
    }

    public ExecutorLayer layer(){
        return lay;
    }

    public AsyncTimer timer(){
        return tm;
    }

    public AsyncTask task(){
        return tsk;
    }
}