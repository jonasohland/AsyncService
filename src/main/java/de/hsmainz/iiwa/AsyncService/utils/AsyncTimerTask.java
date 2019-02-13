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
    private boolean exit = false;

    public AsyncTimerTask(AsyncTask task, AsyncTimer timer, ExecutorLayer layer, boolean repeat){
        tsk = task;
        tm = timer;
        lay = layer;
        this.repeat = repeat;
    }

    @Override
    public void run() {


        tsk.__set__arg_((exit) ? new Completion<TaskCancelledException>(new TaskCancelledException())
                : new Completion<TaskCancelledException>());

        if(Async.getLayer(tsk) != null){
            tsk.fire();
        } else {
            lay.dispatch(tsk);
        }

        if(!repeat) {

            super.cancel();

            if (tm.running_timers.decrementAndGet() == 0) {
                tm.work.reset();
            }
        }
    }

    /**
     * cancel the timer task. This will immediately invoke the invoke the task with a failed Completion.
     * The task will never run again after this call.
     * @return true if the task was cancelled, false if it was not scheduled for execution.
     */
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

    /**
     * Stop a repeated task from running. The task will run one more time with a failed completion. If this is not
     * a repeated task, the task will be invoked at the scheduled time with a failed Completion
     */
    public void stop(){
        repeat = false;
        exit = true;
    }

    /**
     * set the timing of a repeating task. This will silently cancel and reschedule the task internally.
     * this AsyncTimerTask will be invalid after this.
     * @param newRepeatTime the new AsyncTimerTask with the new repeatTime
     */
    public AsyncTimerTask newRepeatTime(long newRepeatTime){

        if(!repeat)
            throw new IllegalStateException("You cannot set the repeat-time of a non-repeating task");

        super.cancel();

        AsyncTimerTask newTask = tm.scheduleAtFixedRate(tsk, newRepeatTime, newRepeatTime);

        timer().running_timers.decrementAndGet();

        return newTask;

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