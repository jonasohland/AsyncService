package de.hsmainz.iiwa.AsyncService.async;

import de.hsmainz.iiwa.AsyncService.executor.context.ExecutorWorkGuard;
import de.hsmainz.iiwa.AsyncService.executor.layer.ExecutorLayer;
import de.hsmainz.iiwa.AsyncService.functional.*;
import de.hsmainz.iiwa.AsyncService.future.ListenableFuture;
import de.hsmainz.iiwa.AsyncService.utils.AsyncTimer;
import de.hsmainz.iiwa.AsyncService.utils.AsyncTimerTask;
import de.hsmainz.iiwa.AsyncService.utils.Completion;

public class Async {


    /* -------------------------------------------------------------------------------------------------------- */
    /*                                             Async.makeAsync()                                            */
    /* -------------------------------------------------------------------------------------------------------- */

    // ------------ runnable

    /**
     * Make an AsyncRunnable from a Runnable object
     * @param runnable a Runnable object to wrap
     * @return the newly created AsyncRunnable
     */
    public static AsyncRunnable makeAsync(Runnable runnable){
        return new AsyncRunnable(runnable);
    }

    /**
     * Make an AsyncRunnable from a Runnable object and bind it to a layer
     * @param layer A layer to bind the AsyncRunnable to
     * @param runnable A Runnable to make the AsyncRunnable from
     * @return the newly created AsyncRunnable
     */
    public static AsyncRunnable makeAsync(ExecutorLayer layer, Runnable runnable){
        return new AsyncRunnable(layer, runnable);
    }

    // ------------ supplier

    /**
     * Make an AsyncSupplier from a Supplier object
     * @param supplier A Supplier to make the AsyncSupplier from
     * @return the newly created AsyncSupplier
     */
    public static <T> AsyncSupplier<T> makeAsync(Supplier<T> supplier){
        return new AsyncSupplier<T>(supplier);
    }

    /**
     * Make an AsyncSupplier from a Supplier object and bind it to a layer
     * @param layer A layer to bind the AsyncSupplier to
     * @param supplier A Supplier to make the AsyncRunnable from
     * @return the newly created AsyncSupplier
     */
    public static <T> AsyncSupplier<T> makeAsync(ExecutorLayer layer, Supplier<T> supplier){
        return new AsyncSupplier<T>(layer, supplier);
    }

    // ------------ consumer

    /**
     * Make an AsyncConsumer from a Runnable object
     * @param consumer A Consumer to make the AsyncConsumer from
     * @return the newly created AsyncConsumer
     */
    public static <T> AsyncConsumer<T> makeAsync(Consumer<T> consumer){
        return new AsyncConsumer<T>(consumer);
    }

    /**
     * Make an AsyncConsumer from a Runnable object and bind it to a layer
     * @param layer A layer to bind the AsyncConsumer to
     * @param consumer A Consumer to make the AsyncRunnable from
     * @return the newly created AsyncConsumer
     */
    public static <T> AsyncConsumer<T> makeAsync(ExecutorLayer layer, Consumer<T> consumer){
        return new AsyncConsumer<T>(layer, consumer);
    }

    // ------------ function

    /**
     *
     * @param func
     * @param <T>
     * @param <R>
     * @return
     */
    public static <T, R> AsyncFunction<T, R> makeAsync(Function<T, R> func){
        return new AsyncFunction<>(func);
    }

    /**
     *
     * @param layer
     * @param func
     * @param <T>
     * @param <R>
     * @return
     */
    public static <T, R> AsyncFunction<T, R> makeAsync(ExecutorLayer layer, Function<T, R> func){
        return new AsyncFunction<T, R>(layer, func);
    }


    // ------------ biconsumer


    public static <T, U> AsyncBiConsumer<T, U> makeAsync(BiConsumer<T, U> biConsumer){
        return new AsyncBiConsumer<T, U>(biConsumer);
    }

    public static <T, U> AsyncBiConsumer<T, U> makeAsync(ExecutorLayer layer, BiConsumer<T, U> biConsumer){
        return new AsyncBiConsumer<T, U>(layer, biConsumer);
    }

    // ------------ bifunction


    public static <T, U, R> AsyncBiFunction<T, U, R> makeAsync(BiFunction<T, U, R> biFunction){
        return new AsyncBiFunction<T, U, R>(biFunction);
    }

    public static <T, U, R> AsyncBiFunction<T, U, R> makeAsync(ExecutorLayer layer, BiFunction<T, U, R> biFunction){
        return new AsyncBiFunction<T, U, R>(layer, biFunction);
    }


    /* -------------------------------------------------------------------------------------------------------- */
    /*                                             Async.invoke()                                               */
    /* -------------------------------------------------------------------------------------------------------- */

    /**
     * Request an ExecutorLayer to invoke the given Runnable object
     * @param layer An ExecutorLayer to request an invocation
     * @param runnable A Runnable object request to be invoked
     */
    public static void invoke(ExecutorLayer layer, Runnable runnable){
        layer.post(makeAsync(runnable));
    }

    /**
     * Request an ExecutorLayer to invoke the given Consumer object
     * @param layer An ExecutorLayer to request an invocation
     * @param consumer A Consumer object request to be invoked
     */
    public static <T> void invoke(ExecutorLayer layer, Consumer<T> consumer){
        layer.post(makeAsync(consumer));
    }

    /**
     * Request an ExecutorLayer to invoke the given Runnable object
     * @param layer An ExecutorLayer to request an invocation
     * @param supplier A Runnable object request to be invoked
     */
    public static <T> void invoke(ExecutorLayer layer, Supplier<T> supplier){
        layer.post(makeAsync(supplier));
    }

    public static <T, R> void invoke(ExecutorLayer layer, Function<T, R> function) {
        layer.post(makeAsync(function));
    }

    public static <T, U> void invoke(ExecutorLayer layer, BiConsumer<T, U> biConsumer){
        layer.post(makeAsync(biConsumer));
    }

    public static <T, U, R> void invoke(ExecutorLayer layer, BiFunction<T, U, R> biFunction){
        layer.post(makeAsync(biFunction));
    }

    public static void invoke(ExecutorLayer layer, AsyncTask t){
        layer.post(t);
    }

    /* -------------------------------------------------------------------------------------------------------- */
    /*                                             Async.invokeAnd()                                            */
    /* -------------------------------------------------------------------------------------------------------- */

    public static <T> ListenableFuture<T> invokeAnd(ExecutorLayer layer, Supplier<T> sup){
        AsyncSupplier<T> supplier = new AsyncSupplier<T>(layer, sup);
        layer.post(supplier);
        return supplier.future();
    }

    public static <T, R>  ListenableFuture<R> invokeAnd(ExecutorLayer layer, Function<T, R> func){
        AsyncFunction<T, R> function = new AsyncFunction<>(layer, func);
        layer.post(function);
        return function.future();
    }

    public static <T, U, R>  ListenableFuture<R> invokeAnd(ExecutorLayer layer, BiFunction<T, U, R> func){
        AsyncBiFunction<T, U, R> function = new AsyncBiFunction<>(layer, func);
        layer.post(function);
        return function.future();
    }

    public static <T> ListenableFuture<T> deferAnd(ExecutorLayer layer, Supplier<T> sup){
        AsyncSupplier<T> supplier = new AsyncSupplier<T>(sup);
        layer.defer(supplier);
        return supplier.future();
    }

    public static <T, R>  ListenableFuture<R> deferAnd(ExecutorLayer layer, Function<T, R> func){
        AsyncFunction<T, R> function = new AsyncFunction<>(func);
        layer.defer(function);
        return function.future();
    }

    public static <T, U, R>  ListenableFuture<R> deferAnd(ExecutorLayer layer, BiFunction<T, U, R> bifunc){
        AsyncBiFunction<T, U, R> function = new AsyncBiFunction<>(bifunc);
        layer.defer(function);
        return function.future();
    }




    /* -------------------------------------------------------------------------------------------------------- */
    /*                                             Async.getLayer()                                           */
    /* -------------------------------------------------------------------------------------------------------- */

    /**
     * Get the layer from an AsyncTask
     * @param tsk An AsyncTask to get the layer from
     * @return the ExecutorLayer
     */
    public static ExecutorLayer getLayer(AsyncTask tsk){
        if(tsk != null){
            return tsk.layer();
        } else {
            return null;
        }
    }

    /**
     * Get the layer from an ListenableFuture. The layer can only be deduced if
     * the future is associated with an AsyncTask.
     * @param future A ListenableFuture to get the layer from
     * @return the ExecutorLayer
     */
    public static <T> ExecutorLayer getLayer(ListenableFuture<T> future){
        if(future.task() != null){
            return future.task().layer();
        } else {
            return null;
        }
    }

    /**
     *
     * @param ttsk
     * @return
     */
    public static ExecutorLayer getLayer(AsyncTimerTask ttsk){
        return ttsk.layer();
    }

    /**
     *
     * @param timer
     * @return
     */
    public static ExecutorLayer getLayer(AsyncTimer timer){
        return timer.context();
    }

    /**
     *
     * @param work
     * @return
     */
    public static ExecutorLayer getLayer(ExecutorWorkGuard work){
        return work.context();
    }

    public static <E extends Exception> Completion<E> tryAndGetResult(Runnable r){
        try{
            r.run();
            return new Completion<E>();
        } catch(Exception e){
            return new Completion<E>((E) e);
        }
    }

    public static int traceLayers(ExecutorLayer layer){

        ExecutorLayer l = layer;
        int tracer = 0;

        while(!l.equals(l.context())){
            tracer++;
            l = l.next_layer();

            if(tracer >= 1000){
                break;
            }
        }

        return tracer;

    }

    public static void printLayerTrace(ExecutorLayer layer){
        ExecutorLayer l = layer;
        int tracer = 0;

        System.out.println("------------- begin layer trace -------------");

        System.out.println("Layer " + tracer + ": " + l.getClass());

        while(!l.equals(l.context())){
            tracer++;
            l = l.next_layer();

            System.out.println("Layer " + tracer + ": " + l.getClass());

            if(tracer >= 1000){
                break;
            }
        }

        System.out.println("-------------- end layer trace --------------");
    }



}