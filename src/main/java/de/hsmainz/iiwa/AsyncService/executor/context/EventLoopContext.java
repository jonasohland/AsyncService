package de.hsmainz.iiwa.AsyncService.executor.context;

import de.hsmainz.iiwa.AsyncService.async.AsyncTask;
import de.hsmainz.iiwa.AsyncService.executor.layer.ExecutorLayer;
import de.hsmainz.iiwa.AsyncService.executor.layer.ExecutorLayerBase;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;


class EventLoopThreadWrapper {

    private Thread t;

    private EventLoopThreadWrapper(){}

    public EventLoopThreadWrapper(Thread th){
        Objects.requireNonNull(th, "The Thread object must not be null");
        t = th;
    }

    private final AtomicBoolean busy = new AtomicBoolean(false);


    public void setBusy(boolean is_busy){
        busy.set(is_busy);
    }

    public boolean isBusy(){
        return busy.get();
    }

    public Thread thread(){
        synchronized (t){
            return t;
        }
    }

}


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
    private final ArrayList<EventLoopThreadWrapper> this_threads = new ArrayList<>();

    /**
     *
     */
    private final AtomicBoolean busy = new AtomicBoolean(false);

    private volatile AtomicBoolean is_exit_procedure = new AtomicBoolean(false);

    private final AtomicBoolean multithread_init = new AtomicBoolean(false);

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
            defer(tsk);
        }

    }

    @Override
    public void defer(AsyncTask tsk){
        queue.add(tsk);
    }

    @Override
    public void run() {

        EventLoopThreadWrapper stack_local_thread = new EventLoopThreadWrapper(Thread.currentThread());

        synchronized (this_threads){

            if(this_threads.size() == 0){
                if(queue.isEmpty() && work_count.get() == 0){

                    System.out.println("no work to do");

                    return;
                }
            }

            this_threads.add(stack_local_thread);
        }

        do {

            // enter multi-thread synchronized state

            do {
                // enter thread owned state

                try {

                    // get new element from queue and execute

                    stack_local_thread.setBusy(false);
                    AsyncTask t = queue.take();
                    stack_local_thread.setBusy(true);

                    t.execute();


                } catch (InterruptedException ex) {

                    //Thread was interrupted in its waiting state

                    Thread.currentThread().isInterrupted();

                    synchronized (this_threads){

                        //remove self from ThreadPool and exit run()

                        this_threads.remove(stack_local_thread);
                        return;
                    }

                }

                //iterate if work is present

            } while(work_count.get() != 0 || !queue.isEmpty());

            //if there are other busy threads, reenter waiting state

            stack_local_thread.setBusy(false);

        } while(!allThreadsWaiting());

        if(!is_exit_procedure.getAndSet(true)){
            synchronized (this_threads){

                this_threads.remove(stack_local_thread);

                for (EventLoopThreadWrapper thread : this_threads) {

                    if(!thread.isBusy()){
                        thread.thread().interrupt();
                    }
                }

                System.out.println("context exit");
            }
        }
    }

    public void runOne(){

        this_threads.add(new EventLoopThreadWrapper(Thread.currentThread()));

        AsyncTask t = queue.poll();

        if(t != null){
            t.execute();
        }

        this_threads.remove(new EventLoopThreadWrapper(Thread.currentThread()));
    }

    public void runMultiThread(int threads) throws InterruptedException {


        multithread_init.set(true);


        for(int i = 0; i < threads; i++){
            new Thread(this::run).start();
        }

        while(this_threads.size() != threads){
            if (Thread.currentThread().isInterrupted()){
                throw new InterruptedException("Interrupted while waiting for Thread startup");
            }
        }


        synchronized (multithread_init){
            multithread_init.set(false);
            multithread_init.notifyAll();
        }

    }

    public void joinThreads() throws InterruptedException {

        if(multithread_init.get()){
            multithread_init.wait();
            if (multithread_init.get()){
                System.out.println("init failed");
                return;
            }
        }

        ArrayList<EventLoopThreadWrapper> threadcopy;

        synchronized (this_threads){
            threadcopy = new ArrayList<>(this_threads);
        }

        for(EventLoopThreadWrapper th : threadcopy){
            th.thread().join();
        }
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

        if(work_count.decrementAndGet() == 0 && queue.isEmpty()){

            synchronized (this_threads) {

                System.out.println("exiting from " + this_threads.size() + " threads");

                for (EventLoopThreadWrapper thread : this_threads) {

                    if(!thread.isBusy()){
                        System.out.println("interrupting thread in waiting state: " + thread.thread().getId());
                        thread.thread().interrupt();
                    }
                }
            }
        }
    }

    public int waitingThreadCount(){
        int ret_v = 0;
        for(EventLoopThreadWrapper thread : this_threads){
            if(!thread.isBusy()){
                ret_v++;
            }
        }
        return ret_v;
    }

    private boolean allThreadsWaiting(){
        synchronized (this_threads){
            return waitingThreadCount() == this_threads.size();
        }
    }

    @Override
    public int workCount() {
        return work_count.get();
    }

    public boolean runningInThisContext(){

        synchronized (this_threads){
            for(EventLoopThreadWrapper thread : this_threads){
                if(thread.thread() == Thread.currentThread()){
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
