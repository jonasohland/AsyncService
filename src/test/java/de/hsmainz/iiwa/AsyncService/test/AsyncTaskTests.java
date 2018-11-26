package de.hsmainz.iiwa.AsyncService.test;

import de.hsmainz.iiwa.AsyncService.events.*;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AsyncTaskTests {

    private int counter = 0;

    @Test
    public void async_make_factories() {

        counter = 0;

        AsyncService.init();

        AsyncTask runnable = Async.makeAsync(() -> { System.out.println("Hello from Runnable!"); counter++; });
        AsyncTask consumer = Async.makeAsync((i) -> { System.out.println("Hello from Consumer!"); counter++; });
        AsyncTask supplier = Async.makeAsync(() -> { System.out.println("Hello from Supplier!"); counter++; return 3; });
        AsyncTask function = Async.makeAsync(() -> { System.out.println("Hello from Function!"); counter++; });
        AsyncTask biconsumer = Async.makeAsync((k,v) -> { System.out.println("Hello from BiConsumer!"); counter++; });
        AsyncTask bifunction = Async.makeAsync((l,j) -> { System.out.println("Hello from BiFunction!"); counter++; return 40; });

        AsyncService.post(runnable);
        AsyncService.post(consumer);
        AsyncService.post(supplier);
        AsyncService.post(function);
        AsyncService.post(bifunction);
        AsyncService.post(biconsumer);

        AsyncService.run();
        AsyncService.exit();

        assertEquals(6, counter);


    }

    @Test
    public void async_task_fire() {

        counter = 0;

        AsyncService.init();

        AsyncTask runnable = Async.makeAsync(() -> { System.out.println("Hello from Runnable!"); counter++; });
        AsyncTask consumer = Async.makeAsync((i) -> { System.out.println("Hello from Consumer!"); counter++; });
        AsyncTask supplier = Async.makeAsync(() -> { System.out.println("Hello from Supplier!"); counter++; return 3; });
        AsyncTask function = Async.makeAsync(() -> { System.out.println("Hello from Function!"); counter++; });
        AsyncTask biconsumer = Async.makeAsync((k,v) -> { System.out.println("Hello from BiConsumer!"); counter++; });
        AsyncTask bifunction = Async.makeAsync((l,j) -> { System.out.println("Hello from BiFunction!"); counter++; return 40; });

        runnable.fire();
        consumer.fire();
        supplier.fire();
        function.fire();
        biconsumer.fire();
        bifunction.fire();

        AsyncService.run();
        AsyncService.exit();

        assertEquals(6, counter);


    }

    private int assertion_int = 0;

    @Test
    public void basic_listenables() {

        AsyncService.init();

        AsyncSupplier<Integer> supplier = new AsyncSupplier<>(() -> { return 55; });

        supplier.getFuture().addListener((i) -> { assertion_int = i; });

        supplier.fire();

        AsyncService.run();
        AsyncService.exit();

        assertEquals(assertion_int, 55);

    }

    Throwable throwie;

    @Test
    public void catch_test() {



        AsyncService.init();

        CatchingAsyncRunnable catchie = new CatchingAsyncRunnable() {
            @Override
            public void test() throws Throwable {
                throw new ClassCircularityError();
            }
        };

        catchie.getFuture().addListener((_throwie) -> {
            if(_throwie != null)
                throwie = _throwie;
            else
                System.out.println("throwie was null");
        });

        AsyncService.post(catchie);

        AsyncService.run();

        AsyncService.exit();

        assertEquals(throwie.toString(), (new ClassCircularityError()).toString());

    }
}
