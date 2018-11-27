package de.hsmainz.iiwa.AsyncService.test;

import de.hsmainz.iiwa.AsyncService.events.Async;
import de.hsmainz.iiwa.AsyncService.events.AsyncService;
import de.hsmainz.iiwa.AsyncService.threads.ThreadPoolJob;
import de.hsmainz.iiwa.AsyncService.threads.ThreadPoolJobLite;
import de.hsmainz.iiwa.AsyncService.utils.AsyncUpdater;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

class WaitJob extends ThreadPoolJob<Integer> {

    private int wait_time;

    public WaitJob(int _wait_time) {
        wait_time = _wait_time;
    }

    @Override
    public Integer perform() {
        try {
            Thread.sleep(wait_time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return wait_time;
    }
}

class ChainedJob extends ThreadPoolJob<Boolean>{

    public static int num_jobs;

    @Override
    public Boolean perform() {
        num_jobs++;
        if(num_jobs >= 10){
            return false;
        } else {
            ChainedJob j = new ChainedJob();
            j.start();
            return true;
        }
    }
}

class ChildThread extends ThreadPoolJob<Void> {

    @Override
    public Void perform() {

        while(!shouldExit()) {
            // lalala
        }

        return null;
    }
}

class MainThread extends ThreadPoolJob<Void> {

    @Override
    public Void perform() {

        ChildThread c = new ChildThread();

        c.start();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {

        }

        assertTrue(c.isActive());

        c.signalThreadShouldExit();

        try {
            c.join();
        } catch(InterruptedException e) {
            e.printStackTrace();
        }

        assertTrue(!c.isActive());

        return null;
    }
}


class Updateble extends AsyncUpdater<Integer>{

    private volatile Integer sync_int = 0;

    @Override
    public void handleUpdate(Integer input) {
        sync_int = input;
    }

    public int sync(){ return sync_int; }
}

class UpdatedThreadPoolJob extends ThreadPoolJob<Void> {

    private Updateble uppi = new Updateble();

    @Override
    public Void perform() {
        System.out.println("start yield");
        while(!(uppi.sync() == 44)){
            Thread.yield();
        }
        System.out.println("stop yield");

        return null;
    }

    Updateble getUppi() {
        return uppi;
    }
}



public class ThreadPoolTests {

    public int exits = 0;

    public void increase_exit_count() {
        exits++;
    }

    @Test
    public void basic() {

        AsyncService.init();

        exits = 0;

        for(int i = 0; i < 16; i++){
            WaitJob w = new WaitJob(100 + (int) (Math.random() * 100));

            w.onFinish.addListener(() -> { increase_exit_count(); } );

            w.start();
        }



        AsyncService.run();
        AsyncService.exit();

        assertEquals(16, exits);

    }

    @Test
    public void thread_chain() {

        AsyncService.init();

        ChainedJob job = new ChainedJob();

        job.start();

        AsyncService.run();
        AsyncService.exit();

        assertEquals(10, ChainedJob.num_jobs);
    }

    @Test
    public void should_exit_test() {

        AsyncService.init();

        MainThread m = new MainThread();

        m.start();

        AsyncService.run();
        AsyncService.exit();

        assertTrue(!m.isActive());

    }

    @Test
    public void post_before_start() {

        WaitJob w = new WaitJob(100);
        w.start();

        w.onFinish.addListener((i) -> { System.out.println(i); });

        AsyncService.init();


        AsyncService.run();

        AsyncService.exit();

    }

    private int inner_job_return = 0;

    @Test
    public void factories_test() {

        AsyncService.init();

        ThreadPoolJobLite job = ThreadPoolJobLite.makeJob(() -> {

            System.out.println("Hello! ");

            ThreadPoolJob<Integer> inner_job = ThreadPoolJob.makeJob(() -> { return 44; });

            inner_job.onFinish.addListener((k) -> { inner_job_return = k; });

            inner_job.start();

        });

        job.start();

        AsyncService.run();
        System.out.println("AsyncTask Loop returned");
        AsyncService.exit();

        assertEquals(44, inner_job_return);

    }


    boolean success_lite = false;

    @Test
    public void LiteTest(){

        AsyncService.init();

        ThreadPoolJobLite job = ThreadPoolJobLite.makeJob(() -> {
            success_lite = true;
        });

        job.start();

        AsyncService.run();
        AsyncService.exit();

        assertTrue(success_lite);

    }

    @Test
    public void finished_test() throws InterruptedException {

        AsyncService.init();

        ThreadPoolJob<Integer> job = ThreadPoolJob.makeJob(() -> { return 15; });

        ThreadPoolJobLite job_lite = new ThreadPoolJobLite(() -> { System.out.println("hello there"); });

        assertFalse(job.isFinished());
        assertFalse(job_lite.isFinished());

        job.start();
        job_lite.start();

        AsyncService.run();
        AsyncService.exit();

        assertTrue(job.isFinished());
        assertTrue(job_lite.isFinished());

        assertEquals((int) job.get(), 15);

    }

    @Test
    public void updatable_test() {

        AsyncService.init();

        UpdatedThreadPoolJob job = new UpdatedThreadPoolJob();

        job.start();

        AsyncService.schedule(Async.makeAsync(() -> {

            System.out.println("posting update");

            job.getUppi().triggerUpdate(44);

        }), 500);

        AsyncService.run();
        AsyncService.exit();


    }

}
