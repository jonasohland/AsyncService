package de.hsmainz.iiwa.AsyncService.test.unit;

import de.hsmainz.iiwa.AsyncService.async.*;
import de.hsmainz.iiwa.AsyncService.executor.context.EventLoopContext;
import de.hsmainz.iiwa.AsyncService.executor.context.ExecutorContext;
import de.hsmainz.iiwa.AsyncService.executor.context.InPlaceExecutorContext;
import org.junit.Assert;
import org.junit.Test;

public class AsyncBase {

    private boolean runnable_invoked;
    private boolean consumer_invoked;
    private boolean supplier_invoked;
    private boolean function_invoked;
    private boolean biconsumer_invoked;
    private boolean bifunction_invoked;

    private void reset_tests(){
        runnable_invoked = false;
        consumer_invoked = false;
        supplier_invoked = false;
        function_invoked = false;
        biconsumer_invoked = false;
        bifunction_invoked = false;
    }

    private void assert_invokations(){
        Assert.assertTrue("runnable was not invoked", runnable_invoked);
        Assert.assertTrue("consumer was not invoked", consumer_invoked);
        Assert.assertTrue("supplier was not invoked", supplier_invoked);
        Assert.assertTrue("function was not invoked", function_invoked);
        Assert.assertTrue("biconsumer was not invoked", biconsumer_invoked);
        Assert.assertTrue("bifuntion was not invoked", bifunction_invoked);
    }

    private final AsyncTask async_runnable = new AsyncRunnable(() ->
            runnable_invoked = true);

    private final AsyncTask async_consumer = new AsyncConsumer<Integer>((Integer i) ->
            consumer_invoked = true);

    private final AsyncTask async_supplier = new AsyncSupplier<Integer>(() -> {
        supplier_invoked = true; return 0; });

    private final AsyncTask async_function = new AsyncFunction<Integer, Integer>( (Integer x) -> {
        function_invoked = true; return x; });

    private final AsyncTask async_biconsumer = new AsyncBiConsumer<Integer, Integer>((Integer b, Integer o) ->
            biconsumer_invoked = true);

    private final AsyncTask async_bifunction = new AsyncBiFunction<Integer, Integer, Integer>(
            (Integer x, Integer y) -> {
                bifunction_invoked = true; return 0; });



    @Test
    public void asyncs_from_functional(){

        AsyncTask as_runnable = Async.makeAsync(() -> { });
        AsyncTask as_consumer = Async.makeAsync((Integer i) -> { });
        AsyncTask as_supplier = Async.makeAsync(() -> {return 0;});
        AsyncTask as_function = Async.makeAsync((Integer k) ->{ return k; });
        AsyncTask as_biconsumer = Async.makeAsync((Integer g, Integer b) -> {});
        AsyncTask as_bifunction = Async.makeAsync((Integer y, Integer x) -> { return x + y; });

        Assert.assertTrue(as_runnable instanceof AsyncRunnable);
        Assert.assertTrue(as_consumer instanceof AsyncConsumer);
        Assert.assertTrue(as_supplier instanceof AsyncSupplier);
        Assert.assertTrue(as_function instanceof AsyncFunction);
        Assert.assertTrue(as_biconsumer instanceof AsyncBiConsumer);
        Assert.assertTrue(as_bifunction instanceof AsyncBiFunction);

    }

    @Test
    public void async_invoke(){

        ExecutorContext in_place = new InPlaceExecutorContext();

        reset_tests();

        Async.invoke(in_place, async_runnable);
        Async.invoke(in_place, async_consumer);
        Async.invoke(in_place, async_supplier);
        Async.invoke(in_place, async_function);
        Async.invoke(in_place, async_biconsumer);
        Async.invoke(in_place, async_bifunction);

        assert_invokations();
    }


    @Test
    public void test_thread_exit(){

        EventLoopContext ctx = new EventLoopContext();

        /* Async.invoke(ctx, () -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }); */

        for(int i = 0; i < 3; i++){
            new Thread(() -> {
                ctx.run();
                System.out.println("exit");
            }).start();

        }
        ctx.run();
        System.out.println("exit");
    }

}