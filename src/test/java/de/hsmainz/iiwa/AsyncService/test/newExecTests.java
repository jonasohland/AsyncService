package de.hsmainz.iiwa.AsyncService.test;

import de.hsmainz.iiwa.AsyncService.except.ExecutionRejectedException;
import de.hsmainz.iiwa.AsyncService.except.TaskCancelledException;
import de.hsmainz.iiwa.AsyncService.executor.*;
import de.hsmainz.iiwa.AsyncService.utils.Completion;
import de.hsmainz.iiwa.AsyncService.utils.Result;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.util.Timer;
import java.util.TimerTask;

public class newExecTests {

    private long thread_id(){
        return Thread.currentThread().getId();
    }

    String test = new String("");
    int th_c_inside = 4;
    int th_c_outside = 4;

    @Test
    public void basic_test(){

        EventLoopExecutor exec = new EventLoopExecutor();

        exec.post(Async.makeAsync(() -> {

            test = test + "a";

            exec.post(Async.makeAsync(() -> {
                test = test + "d";
            }));


            exec.post(Async.makeAsync(() -> {
                th_c_inside = exec.threadCount();
            }));

            exec.dispatch(Async.makeAsync(() -> {
                test = test + "b";
            }));

        }));

        exec.dispatch(Async.makeAsync(() -> {
            test = test + "c";
        }));



        exec.run();

        th_c_outside = exec.threadCount();

        Assert.assertEquals("abcd", test);
        Assert.assertEquals(0, th_c_outside);
        Assert.assertEquals(1, th_c_inside);

    }

    @Test
    public void bind_ctx_test(){

        EventLoopExecutor exec = new EventLoopExecutor();

        AsyncRunnable runnable = new AsyncRunnable(exec, () -> System.out.println("1"));

        AsyncRunnable runnable1 = new AsyncRunnable(() -> System.out.println("2"));

        runnable1.bindContext(exec);

        runnable.fire();
        runnable1.fire();

        Async.invoke(exec, () -> System.out.println("3"));

        exec.runOne();

        System.out.println("next");

        exec.run();


    }

    @Test
    public void future_test(){
        EventLoopExecutor ctx = new EventLoopExecutor();

        AsyncFunction<Integer, Integer> func = Async.makeAsync((Integer i) -> {
            System.out.println("stuff");
            return 999;
        });

        func.future().addListener((Integer i) -> {

            if(ctx.runningInThisContext()){
                System.out.println("yas");
            }

            System.out.println(i);
            return i;
        });

        ctx.defer(func);

        ctx.run();

    }

    @Test
    public void timer_test(){

        EventLoopExecutor ctx = new EventLoopExecutor();
        AsyncTimer timer = new AsyncTimer(ctx);
        AsyncTimer timer2 = new AsyncTimer(ctx);

        timer.schedule(Async.makeAsync(() -> System.out.println("get")), 100);
        timer.schedule(Async.makeAsync(() -> System.out.println("get")), 200);
        timer.schedule(Async.makeAsync(() -> System.out.println("got")), 300);
        timer.schedule(Async.makeAsync(() -> System.out.println("got")), 400);

        timer.schedule(Async.makeAsync(() -> System.out.println("work count: " + ctx.workCount())), 275);

        timer2.schedule(Async.makeAsync(() -> System.out.println("get")), 150);
        timer2.schedule(Async.makeAsync(() -> System.out.println("get")), 250);
        timer2.schedule(Async.makeAsync(() -> System.out.println("got")), 350);
        timer2.schedule(Async.makeAsync(() -> System.out.println("got")), 450);

        ctx.run();

    }

    @Test
    public void strand_test(){

        Timer timer = new Timer();

        EventLoopExecutor ctx = new EventLoopExecutor();

        AsyncTimer at = new AsyncTimer(ctx);

        StrandExecutor strand = new StrandExecutor(ctx);

        TimerTask t = new TimerTask() {
            @Override
            public void run() {
                try {
                    strand.post(() -> {
                        System.out.println("Hello!");
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    });
                } catch(ExecutionRejectedException e){
                    System.out.println(".");
                }
            }
        };

        timer.scheduleAtFixedRate(t, 100, 100);

        at.schedule((Completion<TaskCancelledException> result) -> {

            if(result.failed()){
                result.getException().printStackTrace();
                return 0;
            }
            System.out.println("exit...");
            t.cancel();

            return 0; }, 3000);

        ctx.run();

    }

    @Test
    public void timer_cancel_test(){

        EventLoopExecutor ctx = new EventLoopExecutor();
        AsyncTimer t = new AsyncTimer(ctx);

        Timer jt = new Timer();

        AsyncTimerTask task = t.schedule((Completion<TaskCancelledException> res) -> {
            if(res.failed()){
                System.out.println("cancelled");
            } else {
                System.out.println("not cancelled");
            }
            return 0;
        }, 2000);


        // Async.invoke(ctx, task::cancel);

        // t.schedule(Async.makeAsync(task::cancel), 500);


        /* jt.schedule(new TimerTask() {
            @Override
            public void run() {
                task.cancel();
            }
        }, 1000);*/

        ctx.run();

    }

    @Test
    public void multi_exec_test(){

        EventLoopExecutor ctx_1 = new EventLoopExecutor();
        EventLoopExecutor ctx_2 = new EventLoopExecutor();


        AsyncFunction<Integer, Integer> starter = Async.makeAsync(ctx_1, (Integer i) -> {
            System.out.println("starter: " + i);
            return i;
        });

        AsyncFunction<Integer, Integer> listener = Async.makeAsync(ctx_2, (Integer i) -> {
            System.out.println("listener: " + i);
            return i;
        });

        starter.future().addListener(listener);

        starter.fire(3333);

        ctx_1.run();

        System.out.println("~~~~~~~~~~~~~~");

        ctx_2.run();
    }

    @Test
    public void timer_multi_ctx(){

        EventLoopExecutor ctx_1 = new EventLoopExecutor();
        EventLoopExecutor ctx_2 = new EventLoopExecutor();

        AsyncTimer timer = new AsyncTimer(ctx_1);


        timer.schedule(Async.makeAsync(() -> System.out.println("context 1")), 100);
        timer.schedule(Async.makeAsync(ctx_2, () -> System.out.println("context 2")), 50);

        System.out.println("1:");

        ctx_1.run();

        System.out.println("~~~~~~~~~~~~~~");
        System.out.println("2:");

        ctx_2.run();

    }

    @Test
    public void interrupt_to_exit_test(){

        EventLoopExecutor ctx = new EventLoopExecutor();
        Timer timer = new Timer();

        ExecutorWorkGuard guard = new ExecutorWorkGuard(ctx);

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                guard.reset();
            }
        }, 1000);

        ctx.run();

    }

    @Test
    public void invoke_and_test(){
        EventLoopExecutor ctx = new EventLoopExecutor();

        Async.deferAnd(ctx, () -> {  System.out.println("999"); return 1000; })
                .addListenerThen((Integer i) -> { System.out.println(i); return ++i; } )
                .addListenerThen((Integer k) -> { System.out.println(k); return ++k; })
                .addListener((Integer f) -> { System.out.println(f); return f; });

        ctx.run();
    }

    @Test
    public void filesystem_example(){

        EventLoopExecutor ctx = new EventLoopExecutor();

        Async.invokeAnd(ctx, () -> {
            System.out.println("creating file object");
            return new File(System.getProperty("user.dir") + "/test.txt");
        }).addListenerAnd((File f) -> {
            System.out.println("creating: " + f.toString());
            try {
                if (f.createNewFile()) {
                    return new Result<File, IOException>(f);
                } else {
                    return new Result<File, IOException>(new FileAlreadyExistsException("File already exists"), f);
                }
            } catch (IOException e) {
                return new Result<File, IOException>(e);
            }
        }).addListenerAnd((Result<File, IOException> res) -> {
            if (res.failed()) {
                System.out.println("creation failed: " + res.getException().getMessage());
                if (res.hasResult()) {
                    System.out.println("but file could be opened");
                    if (res.get().delete()) {
                        return new Completion<IOException>();
                    } else {
                        return new Completion<>(new IOException("could not be deleted"));
                    }
                } else {
                    System.out.println("and file could not be opened");
                    return new Completion<>(new IOException("file was not opened"));
                }
            }
            System.out.println("File created!");
            if (res.get().delete()) {
                return new Completion<IOException>();
            } else {
                return new Completion<>(new IOException("could not be deleted"));
            }
        }).addListener((Completion<IOException> res) -> {
            if (res.failed()) {
                System.out.println("operation not completed: " + res.getException().getMessage());
            } else {
                System.out.println("File deleted.");
            }
        });

        ctx.run();
    }

    @Test
    public void event_test(){

        EventLoopExecutor ctx = new EventLoopExecutor();

        Event<String> onThing = new Event<>(ctx);

        onThing.addListenerThen((String thing) -> {
            System.out.println("invoked with: " + thing);
            return thing;
        }).addListener((String bla) -> {
            System.out.println("received: " + bla);
        });

        Async.invoke(ctx, () -> { onThing.dispatch("Hello!"); });

        ctx.run();
    }

}
