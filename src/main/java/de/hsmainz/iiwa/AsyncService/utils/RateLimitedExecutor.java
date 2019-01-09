package de.hsmainz.iiwa.AsyncService.utils;

import de.hsmainz.iiwa.AsyncService.async.AsyncTask;
import de.hsmainz.iiwa.AsyncService.executor.context.ExecutorWorkGuard;
import de.hsmainz.iiwa.AsyncService.executor.layer.ExecutorLayer;
import de.hsmainz.iiwa.AsyncService.executor.layer.ExecutorLayerBase;

import java.util.Timer;
import java.util.TimerTask;

class ResetLimiterTask extends TimerTask {

    private RateLimitedExecutor rate_limiter;

    ResetLimiterTask(RateLimitedExecutor executor){
        rate_limiter = executor;
    }

    @Override
    public void run() {
        rate_limiter.__next();
    }
}

public class RateLimitedExecutor extends ExecutorLayerBase {

    private RateLimitedExecutor(ExecutorLayer nextlayer){
        super(nextlayer);
    }

    private long rate;

    private volatile AsyncTask last;
    private volatile AsyncTask current;

    private volatile boolean blocked;

    private ExecutorWorkGuard work;

    private final Timer timer = new Timer();


    public RateLimitedExecutor(ExecutorLayer layer, long exec_rate){
        super(layer);
        rate = exec_rate;
    }

    synchronized void __next(){
        if(last != null){

            current = last;
            last = null;

            next_layer().defer(current);
            schedule_timeout();
        } else {
            blocked = false;
            work.reset();
        }
    }

    public void post(AsyncTask t){
        if(t.layer() == null){
            t.bindLayer(this);
        }
        postTask(t);
    }

    @Override
    public void defer(AsyncTask t) {
        deferTask(t);
    }

    @Override
    public void dispatch(AsyncTask t) {
        if(t.layer() == null){
            t.bindLayer(this);
        }
        postTask(t);
    }

    private void schedule_timeout(){
        timer.schedule(new ResetLimiterTask(this), rate);
    }

    private synchronized void postTask(AsyncTask task) {
        if(!blocked){
            next_layer().post(task);
            schedule_timeout();
            blocked = true;
            work = new ExecutorWorkGuard(lowest_layer());
        } else {
            last = task;
        }
    }

    private synchronized void deferTask(AsyncTask task) {
        if(!blocked){
            next_layer().defer(task);
            schedule_timeout();
            blocked = true;
            work = new ExecutorWorkGuard(lowest_layer());
        } else {
            last = task;
        }
    }

}
