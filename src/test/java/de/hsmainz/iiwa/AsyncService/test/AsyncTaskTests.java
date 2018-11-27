package de.hsmainz.iiwa.AsyncService.test;

import de.hsmainz.iiwa.AsyncService.events.*;
import de.hsmainz.iiwa.AsyncService.except.ExecutionRejectedException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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

    private boolean catched_redundant_exec_1 = false;
    private boolean catched_redundant_exec_2 = false;
    private boolean legal_exec_1 = false;
    private boolean legal_exec_2 = false;

    @Test
    public void strand_test() {

        AsyncService.init();

        StrandExecutor executor = new StrandExecutor();

        try{
            executor.post(Async.makeAsync(() -> {

                legal_exec_1 = true;

                try {

                    executor.post(Async.makeAsync(() -> { legal_exec_2 = true; }));


                } catch(ExecutionRejectedException ex) {

                }

                try {

                    executor.post(Async.makeAsync(() -> {}));


                } catch(ExecutionRejectedException ex) {
                    catched_redundant_exec_2 = true;
                }


            }));

        } catch (ExecutionRejectedException ex){
        }

        try{
            executor.post(Async.makeAsync(() -> { System.out.println("hello"); }));
        } catch (ExecutionRejectedException ex){
            catched_redundant_exec_1 = true;
        }

        AsyncService.run();
        AsyncService.exit();

        assertTrue(legal_exec_1);
        assertTrue(legal_exec_2);
        assertTrue(catched_redundant_exec_1);
        assertTrue(catched_redundant_exec_2);

    }

    @Test
    public void strand_queue() {

        AsyncService.init();

        StrandExecutorQueue strand = new StrandExecutorQueue();

        for(int i = 0; i < 3; i++){
            boolean res = strand.post(Async.makeAsync(() -> {

                System.out.println("Something happened! ");

                strand.post(Async.makeAsync(() -> {

                    System.out.println("something happened on the inside");

                    strand.post(Async.makeAsync(() -> {

                        System.out.println("something happened even further inside");

                    }));
                }));
            }));
        }

        AsyncService.post(() -> System.out.println("something else happened"));

        AsyncService.run();
        AsyncService.exit();

    }
}
