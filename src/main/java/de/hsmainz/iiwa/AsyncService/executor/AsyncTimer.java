package de.hsmainz.iiwa.AsyncService.executor;

import de.hsmainz.iiwa.AsyncService.except.TaskCancelledException;
import de.hsmainz.iiwa.AsyncService.functional.Function;
import de.hsmainz.iiwa.AsyncService.utils.Completion;

import java.util.Timer;
import java.util.concurrent.atomic.AtomicInteger;



public class AsyncTimer {

    public AsyncTimer(ExecutionContext context){
        ctx = context;
    }

    public AsyncTimerTask schedule(AsyncTask tsk, long time){

        if(work == null){
            work = new ExecutorWorkGuard(ctx);
        }

        if(!work.hasWork()){
            work = new ExecutorWorkGuard(ctx);
        }

        AsyncTimerTask ttsk = new AsyncTimerTask(tsk, this,  ctx);

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
    private ExecutionContext ctx;

    public ExecutionContext context(){
        return ctx;
    }

}
