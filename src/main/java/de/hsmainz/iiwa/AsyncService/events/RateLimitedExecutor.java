package de.hsmainz.iiwa.AsyncService.events;

import java.util.TimerTask;

class ResetLimiterTask extends TimerTask {

    private RateLimitedExecutor rate_limiter;

    public ResetLimiterTask(RateLimitedExecutor executor){
        rate_limiter = executor;
    }

    @Override
    public void run() {
        rate_limiter.__next();
    }
}

public class RateLimitedExecutor extends AsyncTaskExecutor {

    private RateLimitedExecutor(){}

    private long rate;

    private volatile AsyncTask last;
    private volatile AsyncTask current;

    private volatile boolean blocked;


    public RateLimitedExecutor(long exec_rate){
        rate = exec_rate;
    }

    synchronized void __next(){
        if(last != null){

            current = last;
            last = null;

            AsyncService.post(current);
            schedule_timeout();
        } else {
            blocked = false;
        }
    }

    private void schedule_timeout(){
        AsyncService.coreTimer.schedule(new ResetLimiterTask(this), rate);
    }


    @Override
    protected void handle_task(AsyncTask task) {
        postTask(task);
    }

    public synchronized void postTask(AsyncTask task) {
        if(!blocked){
            AsyncService.post(task);
            schedule_timeout();
            blocked = true;
        } else {
            last = task;
        }
    }

}
