package de.hsmainz.iiwa.AsyncService.executor;

import de.hsmainz.iiwa.AsyncService.except.TaskCancelledException;
import de.hsmainz.iiwa.AsyncService.utils.Completion;

import java.util.TimerTask;

public class AsyncTimerTask extends TimerTask {

    private AsyncTask tsk;
    private ExecutionLayer lay;
    private AsyncTimer tm;

    public AsyncTimerTask(AsyncTask task, AsyncTimer timer, ExecutionLayer layer){
        tsk = task;
        tm = timer;
        lay = layer;
    }

    @Override
    public void run() {

        tsk.__set__arg_(new Completion<TaskCancelledException>());

        if(Async.getLayer(tsk) != null){
            tsk.fire();
        } else {
            lay.dispatch(tsk);
        }

        if(tm.running_timers.decrementAndGet() == 0){
            tm.work.reset();
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

    public ExecutionLayer layer(){
        return lay;
    }

    public AsyncTimer timer(){
        return tm;
    }

    public AsyncTask task(){
        return tsk;
    }
}