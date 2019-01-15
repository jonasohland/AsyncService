package de.hsmainz.iiwa.AsyncService.executor.context;

import de.hsmainz.iiwa.AsyncService.async.AsyncTask;
import de.hsmainz.iiwa.AsyncService.executor.layer.ExecutorLayerBase;

import de.hsmainz.iiwa.AsyncService.utils.Objects;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class EventLoopContextSingleThread extends ExecutorLayerBase implements ExecutorContext {

    private final AtomicInteger work_count = new AtomicInteger();

    private final LinkedBlockingQueue<AsyncTask> queue = new LinkedBlockingQueue<AsyncTask>();

    private volatile Thread worker_thread;

    private final AtomicBoolean is_busy = new AtomicBoolean(false);


    public EventLoopContextSingleThread(){
        super(null);
    }

    @Override
    public void run() {

        is_busy.set(false);
        worker_thread = Thread.currentThread();


        while(work_count.get() != 0 || !queue.isEmpty()){


            try {


                AsyncTask t = queue.take();
                is_busy.set(true);

                t.execute();


            } catch (InterruptedException e) {

                worker_thread = null;
                is_busy.set(false);
                return;
            }

            is_busy.set(false);

        }

        worker_thread = null;

    }

    @Override
    public void stop() {

    }

    @Override
    public void reset() {

    }

    @Override
    public void registerWork(ExecutorWorkGuard wrk) {
        Objects.requireNonNull(wrk);
        work_count.incrementAndGet();
    }

    @Override
    public void removeWork(ExecutorWorkGuard wrk) {

        Objects.requireNonNull(wrk);

        if(work_count.decrementAndGet() == 0 && queue.isEmpty()){
            if(!is_busy.get()){
                worker_thread.interrupt();
            }
        }
    }

    @Override
    public int workCount() {
        return work_count.get();
    }

    @Override
    public void post(AsyncTask t) {
        t.bindLayer(this);
        queue.add(t);
    }

    @Override
    public void defer(AsyncTask t) {
        queue.add(t);
    }

    @Override
    public void dispatch(AsyncTask t) {

        t.bindLayer(this);

        if (runningInThisContext()){
            t.execute();
        } else {
            defer(t);
        }
    }

    @Override
    public ExecutorContext lowest_layer(){
        return this;
    }


    public boolean runningInThisContext(){
        return Thread.currentThread() == worker_thread;
    }
}
