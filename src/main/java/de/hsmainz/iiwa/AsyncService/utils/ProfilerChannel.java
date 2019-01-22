package de.hsmainz.iiwa.AsyncService.utils;

import de.hsmainz.iiwa.AsyncService.async.Async;
import de.hsmainz.iiwa.AsyncService.async.AsyncTask;
import de.hsmainz.iiwa.AsyncService.executor.layer.ExecutorLayer;
import de.hsmainz.iiwa.AsyncService.executor.layer.ExecutorLayerBase;

public class ProfilerChannel extends ExecutorLayerBase {

    private final Object mutex = new Object();

    private volatile long[] samples;

    private volatile int avg_sample_size = 0;
    private volatile int avg_sample_index = 0;
    private volatile int avg_sample_max_size;

    private volatile long counter = 0;
    private volatile long max = Long.MIN_VALUE;
    private volatile long min = Long.MAX_VALUE;

    private String name;

    ProfilerChannel(ExecutorLayer nextLayer, String name){
        super(nextLayer);
        samples = new long[1024];
        avg_sample_max_size = 1024;
        this.name = name;
    }

    ProfilerChannel(ExecutorLayer nextLayer, int avg_mem_size){
        super(nextLayer);
        samples = new long[avg_mem_size];
        avg_sample_max_size = avg_mem_size;
    }

    @Override
    public void post(AsyncTask t) {
        next_layer().post(Async.makeAsync(() -> {
            long start = System.nanoTime();
            t.execute();
            long end = System.nanoTime();
            report(end - start);
        }));
    }

    @Override
    public void defer(AsyncTask t) {
        next_layer().defer(Async.makeAsync(() -> {
            long start = System.nanoTime();
            t.execute();
            long end = System.nanoTime();
            report(end - start);
        }));
    }

    @Override
    public void dispatch(AsyncTask t) {
        next_layer().dispatch(Async.makeAsync(() -> {
            long start = System.nanoTime();
            t.execute();
            long end = System.nanoTime();
            report(end - start);
        }));
    }

    private void report(long time){
        synchronized (mutex){

            samples[avg_sample_index] = time;

            if(avg_sample_size != avg_sample_max_size){
                avg_sample_size = avg_sample_index + 1;
            }

            counter++;
            avg_sample_index++;

            if(avg_sample_index >= avg_sample_max_size){
                avg_sample_index = 0;
            }

            if(time > max){
                max = time;
            }

            if(time < min){
                min = time;
            }
        }
    }




    public Duration max(){
        synchronized (mutex){
            return Duration.ofNanos(max);
        }
    }

    public Duration min(){
        synchronized (mutex){
            return Duration.ofNanos(min);
        }
    }

    public Duration average(){
        synchronized (mutex){

            long avg = 0;

            for(long sample : samples){
                avg += sample;
            }

            return Duration.ofNanos(avg / avg_sample_size);
        }
    }

    public long count(){
        synchronized (mutex){
            return counter;
        }
    }

    public String name(){
        return name;
    }

}
