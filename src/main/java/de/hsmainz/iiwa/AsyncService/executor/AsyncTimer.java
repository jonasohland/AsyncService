package de.hsmainz.iiwa.AsyncService.executor;

import de.hsmainz.iiwa.AsyncService.except.TaskCancelledException;
import de.hsmainz.iiwa.AsyncService.functional.Function;
import de.hsmainz.iiwa.AsyncService.utils.Completion;

import java.util.Timer;
import java.util.concurrent.atomic.AtomicInteger;



public class AsyncTimer {

    public AsyncTimer(ExecutionLayer layer){
        lay = layer;
    }

    public AsyncTimerTask schedule(AsyncTask tsk, long time){

        if(work == null){
            work = new ExecutorWorkGuard((ExecutionContext) lay.lowest_layer());
        }

        if(!work.hasWork()){
            work = new ExecutorWorkGuard((ExecutionContext) lay.lowest_layer());
        }

        AsyncTimerTask ttsk = new AsyncTimerTask(tsk, this,  lay);

        timer.schedule(ttsk, time);
        running_timers.getAndIncrement();

        return ttsk;

    }

    public <R> AsyncTimerTask schedule(Function<Completion<TaskCancelledException>, R> function, long delay){
        return schedule(Async.makeAsync(function), delay);
    }

    final AtomicInteger running_timers = new AtomicInteger(0);
    ExecutorWorkGuard work;

    private final Timer timer = new Timer();
    private ExecutionLayer lay;

    public ExecutionContext context(){
        return (ExecutionContext) lay.lowest_layer();
    }

    public ExecutionLayer layer() { return lay; }

}
