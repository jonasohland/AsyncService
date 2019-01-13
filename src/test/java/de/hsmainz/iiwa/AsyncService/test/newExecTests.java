package de.hsmainz.iiwa.AsyncService.test;

import de.hsmainz.iiwa.AsyncService.async.Async;
import de.hsmainz.iiwa.AsyncService.async.AsyncFunction;
import de.hsmainz.iiwa.AsyncService.async.AsyncRunnable;
import de.hsmainz.iiwa.AsyncService.except.ExecutionRejectedException;
import de.hsmainz.iiwa.AsyncService.except.TaskCancelledException;
import de.hsmainz.iiwa.AsyncService.executor.context.EventLoopContext;
import de.hsmainz.iiwa.AsyncService.executor.context.ExecutorContext;
import de.hsmainz.iiwa.AsyncService.executor.context.ExecutorWorkGuard;
import de.hsmainz.iiwa.AsyncService.executor.context.InstantExectorContext;
import de.hsmainz.iiwa.AsyncService.listenable.Event;
import de.hsmainz.iiwa.AsyncService.listenable.Event2;
import de.hsmainz.iiwa.AsyncService.utils.*;
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

        EventLoopContext ctx = new EventLoopContext();

        ctx.post(Async.makeAsync(() -> {

            test = test + "a";

            ctx.post(Async.makeAsync(() -> {
                test = test + "d";
            }));


            ctx.post(Async.makeAsync(() -> {
                th_c_inside = ctx.threadCount();
            }));

            ctx.dispatch(Async.makeAsync(() -> {
                test = test + "b";
            }));

        }));

        ctx.dispatch(Async.makeAsync(() -> {
            test = test + "c";
        }));



        ctx.run();

        th_c_outside = ctx.threadCount();

        Assert.assertEquals("abcd", test);
        Assert.assertEquals(0, th_c_outside);
        Assert.assertEquals(1, th_c_inside);

    }

    @Test
    public void bind_ctx_test(){

        EventLoopContext ctx = new EventLoopContext();

        AsyncRunnable runnable = new AsyncRunnable(ctx, () -> System.out.println("1"));

        AsyncRunnable runnable1 = new AsyncRunnable(() -> System.out.println("2"));

        runnable1.bindLayer(ctx);

        runnable.fire();
        runnable1.fire();

        Async.invoke(ctx, () -> System.out.println("3"));

        ctx.runOne();

        System.out.println("next");

        ctx.run();


    }

    @Test
    public void future_test(){
        EventLoopContext ctx = new EventLoopContext();

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

        EventLoopContext ctx = new EventLoopContext();
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

        EventLoopContext ctx = new EventLoopContext();

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

        EventLoopContext ctx = new EventLoopContext();
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
        }, 1000); */

        ctx.run();

    }

    @Test
    public void multi_exec_test(){

        EventLoopContext ctx_1 = new EventLoopContext();
        EventLoopContext ctx_2 = new EventLoopContext();


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

        EventLoopContext ctx_1 = new EventLoopContext();
        EventLoopContext ctx_2 = new EventLoopContext();

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

        EventLoopContext ctx = new EventLoopContext();
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
        EventLoopContext ctx = new EventLoopContext();

        RateLimitedExecutor ratelim = new RateLimitedExecutor(ctx, 200);

        StrandExecutorQueue queue = new StrandExecutorQueue(ratelim);

        RateLimitedExecutor ratelim2 = new RateLimitedExecutor(queue, 1000);

        StrandExecutorQueue queue2 = new StrandExecutorQueue(ratelim2);

        Async.invokeAnd(queue2, () -> {  System.out.println("999"); return 1000; })
                .addListenerThen((Integer i) -> { System.out.println(i); return ++i; } )
                .addListenerThen((Integer k) -> { System.out.println(k); return ++k; })
                .addListener((Integer f) -> { System.out.println(f); return f; });

        ctx.run();

        Async.printLayerTrace(queue2);
    }

    @Test
    public void filesystem_example(){

        EventLoopContext ctx = new EventLoopContext();

        RateLimitedExecutor limiter = new RateLimitedExecutor(ctx, 1000);
        StrandExecutorQueue queue = new StrandExecutorQueue(limiter);

        Async.invokeAnd(queue, () -> {
            System.out.println("creating file object");
            return new File(System.getProperty("user.dir") + "/test.txt");
        }).addListenerThen((File f) -> {
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
        }).addListenerThen((Result<File, IOException> res) -> {
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

        EventLoopContext ctx = new EventLoopContext();

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

    @Test
    public void event2_test(){
        EventLoopContext ctx = new EventLoopContext();

        Event2<String, String> event = new Event2<>(ctx);

        event.addListenerThen((String str1, String str2) -> {System.out.println("got: " + str1 + str2); return str2;})
                .addListenerThen((String str) -> { System.out.println("Second str: " + str); return str; })
                .addListener((String stra) -> { System.out.println("3rd Method: " + stra); });

        event.post("Hello ", "World!");

        ctx.run();
    }

    @Test
    public void rate_limiter_test(){

        EventLoopContext ctx = new EventLoopContext();
        RateLimitedExecutor rateLimitedExecutor = new RateLimitedExecutor(ctx, 1000);

        rateLimitedExecutor.post(Async.makeAsync(() -> System.out.println("stuff 1") ));
        rateLimitedExecutor.post(Async.makeAsync(() -> System.out.println("stuff 2") ));
        rateLimitedExecutor.post(Async.makeAsync(() -> System.out.println("stuff 3") ));
        rateLimitedExecutor.post(Async.makeAsync(() -> System.out.println("stuff 4") ));

        ctx.run();


    }

    @Test
    public void multi_layer_test(){

        EventLoopContext ctx = new EventLoopContext();
        RateLimitedExecutor rate_limiter = new RateLimitedExecutor(ctx, 220);
        AsyncTimer tm = new AsyncTimer(rate_limiter);

        tm.schedule(Async.makeAsync(() -> System.out.println("hellloo!!!! 1")), 100);
        tm.schedule(Async.makeAsync(() -> System.out.println("hellloo!!!! 2")), 200);
        tm.schedule(Async.makeAsync(() -> System.out.println("hellloo!!!! 3")), 300);
        tm.schedule(Async.makeAsync(() -> System.out.println("hellloo!!!! 4")), 400);
        tm.schedule(Async.makeAsync(() -> System.out.println("hellloo!!!! 5")), 500);
        tm.schedule(Async.makeAsync(() -> System.out.println("hellloo!!!! 6")), 1200);

        ctx.run();

        Assert.assertEquals(ctx, rate_limiter.lowest_layer());

    }

    @Test
    public void multi_layer_test_2(){

        EventLoopContext ctx = new EventLoopContext();

        RateLimitedExecutor ratelim = new RateLimitedExecutor(ctx, 1000);

        StrandExecutorDeque queue = new StrandExecutorDeque(ratelim);

        queue.back().dispatch(() -> System.out.println("Hello! 1"));
        queue.back().dispatchThen(() -> {System.out.println("Hello! 2"); return 44; })
                .addListener(queue.front(), (Integer i) -> {
                    System.out.println("got: " + i);
                });
        queue.back().dispatch(() -> System.out.println("Hello! 3"));
        queue.back().dispatch(() -> System.out.println("Hello! 4"));

        ctx.run();


        Assert.assertEquals(ctx, queue.back().lowest_layer());
        Assert.assertEquals(ctx, queue.front().lowest_layer());
    }

    @Test
    public void counter_test(){

        EventLoopContext ctx = new EventLoopContext();
        CountingExecutor counter = new CountingExecutor(ctx);


        AsyncTimer timer = new AsyncTimer(counter);
        AsyncTimer timer2 = new AsyncTimer(counter);

        timer.schedule(Async.makeAsync(() -> {}), 1000);
        timer.schedule(Async.makeAsync(() -> {}), 1000);
        timer.schedule(Async.makeAsync(() -> {}), 0);
        timer.schedule(Async.makeAsync(() -> {}), 0);
        timer.schedule(Async.makeAsync(() -> {}), 0);
        timer2.schedule(Async.makeAsync(() -> {}), 1000);
        timer2.schedule(Async.makeAsync(() -> {}), 1000);
        timer2.schedule(Async.makeAsync(() -> {}), 0);
        timer2.schedule(Async.makeAsync(() -> {}), 0);
        timer2.schedule(Async.makeAsync(() -> {}), 0);

        ctx.run();

        System.out.println("counted: " + counter.count());

        Assert.assertEquals(ctx, counter.lowest_layer());
        Assert.assertEquals(10, (long) counter.count());

    }

    @Test
    public void profiler_test(){
        EventLoopContext ctx = new EventLoopContext();
        Profiler profiler = new Profiler();

        ProfilerChannel c = profiler.newChannel("Test1", ctx);
        ProfilerChannel a = profiler.newChannel("Test2", ctx);

        for(int i = 0; i < 100; i++){
            c.post(() -> {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }

        for(int i = 0; i < 100; i++){
            a.post(() -> {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }

        ctx.run();

        profiler.printStats();


    }

    @Test
    public void trace_test(){

        Profiler profiler = new Profiler();

        ExecutorContext ctx = new EventLoopContext();

        ProfilerChannel channel = profiler.newChannel("TestChannel", ctx);

        CountingExecutor counter = new CountingExecutor(channel);
        RateLimitedExecutor rate_limiter = new RateLimitedExecutor(counter, 1000);
        StrandExecutorDeque deque = new StrandExecutorDeque(rate_limiter);

        AsyncTimer t = new AsyncTimer(deque.back());

        t.schedule(Async.makeAsync(() -> { System.out.println("Hello!");
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }), 3);
        t.schedule(Async.makeAsync(() -> { System.out.println("Hello!"); }), 5);
        t.schedule(Async.makeAsync(() -> { System.out.println("Hello!"); }), 10);
        t.schedule(Async.makeAsync(() -> { System.out.println("Hello!"); }), 20);
        t.schedule(Async.makeAsync(() -> { System.out.println("Hello!"); }), 30);

        t.schedule(Async.makeAsync(() -> {
            System.out.println("Hello!");
        }), 3);

        ctx.run();

        profiler.printStats();

        Async.printLayerTrace(deque.front());

        Assert.assertEquals(4, Async.traceLayers(deque.front()));

    }

    @Test
    public void schedule_fixed_rate_test(){
        ExecutorContext ctx = new EventLoopContext();
        AsyncTimer timer = new AsyncTimer(ctx);

        AsyncTimerTask blub = timer.scheduleAtFixedRate((Completion<TaskCancelledException> com) -> {
            if(!com.failed()){
                System.out.println("blub");
            } else {
                System.out.println("end");
            }
        }, 1000);

        timer.schedule(blub::cancel, 3200);

        ctx.run();
    }

    @Test
    public void instant_test(){
        ExecutorContext instant_exec = new InstantExectorContext();
        Async.invoke(instant_exec, () -> System.out.println("hey!"));
    }
}
