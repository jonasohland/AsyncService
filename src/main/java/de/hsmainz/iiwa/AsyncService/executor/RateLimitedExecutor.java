package de.hsmainz.iiwa.AsyncService.executor;

import de.hsmainz.iiwa.AsyncService.executor.AsyncTask;

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

public class RateLimitedExecutor {

    private RateLimitedExecutor(){}

    private long rate;

    private volatile AsyncTask last;
    private volatile AsyncTask current;

    private volatile boolean blocked;

    private ExecutorWorkGuard work;

    private ExecutionContext ctx;

    private final Timer timer = new Timer();


    public RateLimitedExecutor(ExecutionContext context, long exec_rate){
        ctx = context;
        rate = exec_rate;
    }

    synchronized void __next(){
        if(last != null){

            current = last;
            last = null;

            ctx.defer(current);
            schedule_timeout();
        } else {
            blocked = false;
            work.reset();
        }
    }

    public void post(AsyncTask t){
        postTask(t);
    }

    private void schedule_timeout(){
        timer.schedule(new ResetLimiterTask(this), rate);
    }

    protected void handle_task(AsyncTask task) {
        postTask(task);
    }

    private synchronized void postTask(AsyncTask task) {
        if(!blocked){
            ctx.post(task);
            schedule_timeout();
            blocked = true;
            work = new ExecutorWorkGuard(ctx);
        } else {
            last = task;
        }
    }

}
