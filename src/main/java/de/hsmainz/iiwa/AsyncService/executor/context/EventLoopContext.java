package de.hsmainz.iiwa.AsyncService.executor.context;

import de.hsmainz.iiwa.AsyncService.async.AsyncTask;
import de.hsmainz.iiwa.AsyncService.executor.layer.ExecutorLayer;
import de.hsmainz.iiwa.AsyncService.executor.layer.ExecutorLayerBase;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class EventLoopContext extends ExecutorLayerBase implements ExecutorContext {

    public EventLoopContext(){
        super(null);
    }

    /**
     * Count of ExecutorWorkGuards associated with this ctx
     */
    private final AtomicInteger work_count = new AtomicInteger(0);

    /**
     * Holds the queued tasks
     */
    private final LinkedBlockingQueue<AsyncTask> queue = new LinkedBlockingQueue<>();

    /**
     * Holds a the threads this executor is running on
     */
    private final ArrayList<Thread> this_threads = new ArrayList<>();

    /**
     *
     */
    private final AtomicBoolean busy = new AtomicBoolean(false);

    @Override
    public void post(AsyncTask tsk) {
        tsk.bindLayer(this);
        queue.add(tsk);
    }

    @Override
    public void dispatch(AsyncTask tsk) {

        tsk.bindLayer(this);

        if(runningInThisContext()){
            tsk.execute();
        } else {
            post(tsk);
        }

    }

    @Override
    public void defer(AsyncTask tsk){
        queue.add(tsk);
    }

    @Override
    public synchronized void run() {

        this_threads.add(Thread.currentThread());

        while(work_count.get() != 0 || !queue.isEmpty()) {

            try {

                busy.set(false);
                AsyncTask t = queue.take();
                busy.set(true);

                t.execute();

            } catch (InterruptedException ex) {
                Thread.currentThread().isInterrupted();
            }


        }

        busy.set(false);

        this_threads.remove(Thread.currentThread());
    }

    public void runOne(){

        this_threads.add(Thread.currentThread());

        AsyncTask t = queue.poll();

        if(t != null){
            t.execute();
        }

        this_threads.remove(Thread.currentThread());
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
        work_count.getAndIncrement();
    }

    @Override
    public void removeWork(ExecutorWorkGuard wrk) {
        Objects.requireNonNull(wrk);
        if(work_count.decrementAndGet() == 0 && queue.isEmpty() && !runningInThisContext()){
            if(!isBusy()){
                synchronized (this_threads) {
                    for (Thread thread : this_threads) {
                        System.out.println("interrupting: " + thread.getId());
                        thread.interrupt();
                    }
                }
            }
        }

    }

    @Override
    public int workCount() {
        return work_count.get();
    }

    public boolean runningInThisContext(){

        synchronized (this_threads){
            for(Thread thread : this_threads){
                if(thread == Thread.currentThread()){
                    return true;
                }
            }
        }
        return false;

    }

    public boolean isRunning(){
        synchronized (this_threads){
            return !this_threads.isEmpty();
        }
    }

    public boolean isBusy(){
        return busy.get();
    }

    public int threadCount(){
        return this_threads.size();
    }



    /* override these methods, because we are the lowest layer */

    @Override
    public ExecutorLayer next_layer() {
        return null;
    }

    @Override
    public ExecutorContext lowest_layer() {
        return this;
    }
}
