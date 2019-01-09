package de.hsmainz.iiwa.AsyncService.utils;

import de.hsmainz.iiwa.AsyncService.async.Async;
import de.hsmainz.iiwa.AsyncService.async.AsyncTask;
import de.hsmainz.iiwa.AsyncService.except.TaskCancelledException;
import de.hsmainz.iiwa.AsyncService.executor.context.ExecutorContext;
import de.hsmainz.iiwa.AsyncService.executor.context.ExecutorWorkGuard;
import de.hsmainz.iiwa.AsyncService.executor.layer.ExecutorLayer;
import de.hsmainz.iiwa.AsyncService.functional.Consumer;
import de.hsmainz.iiwa.AsyncService.functional.Function;

import java.util.Timer;
import java.util.concurrent.atomic.AtomicInteger;



public class AsyncTimer {

    public AsyncTimer(ExecutorLayer layer){
        lay = layer;
    }

    public AsyncTimerTask schedule(AsyncTask tsk, long time){

        if(work == null){
            work = new ExecutorWorkGuard(lay.lowest_layer());
        }

        if(!work.hasWork()){
            work = new ExecutorWorkGuard(lay.lowest_layer());
        }

        AsyncTimerTask ttsk = new AsyncTimerTask(tsk, this,  lay, false);

        timer.schedule(ttsk, time);
        running_timers.getAndIncrement();

        return ttsk;

    }

    public AsyncTimerTask scheduleAtFixedRate(AsyncTask tsk, long delay, long rate){

        if(work == null){
            work = new ExecutorWorkGuard(lay.lowest_layer());
        }

        if(!work.hasWork()){
            work = new ExecutorWorkGuard(lay.lowest_layer());
        }

        AsyncTimerTask ttsk = new AsyncTimerTask(tsk, this,  lay, true);

        timer.scheduleAtFixedRate(ttsk, 0, rate);

        running_timers.getAndIncrement();

        return ttsk;

    }

    public AsyncTimerTask schedule(Runnable runnable, long delay){
        return schedule(Async.makeAsync(runnable), delay);
    }

    public AsyncTimerTask schedule(Consumer<Completion<TaskCancelledException>> function, long delay){
        return schedule(Async.makeAsync(function), delay);
    }

    public <R> AsyncTimerTask schedule(Function<Completion<TaskCancelledException>, R> function, long delay){
        return schedule(Async.makeAsync(function), delay);
    }

    public AsyncTimerTask scheduleAtFixedRate(Runnable function, long rate){
        return scheduleAtFixedRate(Async.makeAsync(function), 0, rate);
    }

    public AsyncTimerTask scheduleAtFixedRate(Consumer<Completion<TaskCancelledException>> function, long rate){
        return scheduleAtFixedRate(Async.makeAsync(function), 0, rate);
    }

    public <R> AsyncTimerTask scheduleAtFixedRate(Function<Completion<TaskCancelledException>, R> function, long rate){
        return scheduleAtFixedRate(Async.makeAsync(function), 0, rate);
    }

    public AsyncTimerTask scheduleAtFixedRate(Runnable function, long delay, long rate){
        return scheduleAtFixedRate(Async.makeAsync(function), delay, rate);
    }

    public AsyncTimerTask scheduleAtFixedRate(Consumer<Completion<TaskCancelledException>> function, long delay, long rate){
        return scheduleAtFixedRate(Async.makeAsync(function), delay, rate);
    }

    public <R> AsyncTimerTask scheduleAtFixedRate(Function<Completion<TaskCancelledException>, R> function, long delay, long rate){
        return scheduleAtFixedRate(Async.makeAsync(function), delay, rate);
    }

    final AtomicInteger running_timers = new AtomicInteger(0);
    ExecutorWorkGuard work;

    private final Timer timer = new Timer();
    private ExecutorLayer lay;

    public ExecutorContext context(){
        return (ExecutorContext) lay.lowest_layer();
    }

    public ExecutorLayer layer() { return lay; }

}
