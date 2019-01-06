package de.hsmainz.iiwa.AsyncService.executor;

import de.hsmainz.iiwa.AsyncService.functional.BiConsumer;
import de.hsmainz.iiwa.AsyncService.functional.Consumer;
import de.hsmainz.iiwa.AsyncService.functional.Function;
import de.hsmainz.iiwa.AsyncService.functional.Supplier;
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
     * Make an AsyncRunnable from a Runnable object and bind it to a Context
     * @param ctx A context to bind the AsyncRunnable to
     * @param runnable A Runnable to make the AsyncRunnable from
     * @return the newly created AsyncRunnable
     */
    public static AsyncRunnable makeAsync(ExecutionContext ctx, Runnable runnable){
        return new AsyncRunnable(ctx, runnable);
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
     * Make an AsyncSupplier from a Supplier object and bind it to a Context
     * @param ctx A context to bind the AsyncSupplier to
     * @param supplier A Supplier to make the AsyncRunnable from
     * @return the newly created AsyncSupplier
     */
    public static <T> AsyncSupplier<T> makeAsync(ExecutionContext ctx, Supplier<T> supplier){
        return new AsyncSupplier<T>(ctx, supplier);
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
     * Make an AsyncConsumer from a Runnable object and bind it to a Context
     * @param ctx A context to bind the AsyncConsumer to
     * @param consumer A Consumer to make the AsyncRunnable from
     * @return the newly created AsyncConsumer
     */
    public static <T> AsyncConsumer<T> makeAsync(ExecutionContext ctx, Consumer<T> consumer){
        return new AsyncConsumer<T>(ctx, consumer);
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
     * @param ctx
     * @param func
     * @param <T>
     * @param <R>
     * @return
     */
    public static <T, R> AsyncFunction<T, R> makeAsync(ExecutionContext ctx, Function<T, R> func){
        return new AsyncFunction<T, R>(ctx, func);
    }


    // ------------ biconsumer


    public static <T, U> AsyncBiConsumer<T, U> makeAsync(BiConsumer<T, U> biConsumer){
        return new AsyncBiConsumer<T, U>(biConsumer);
    }

    public static <T, U> AsyncBiConsumer<T, U> makeAsync(ExecutionContext ctx, BiConsumer<T, U> biConsumer){
        return new AsyncBiConsumer<T, U>(ctx, biConsumer);
    }


    /* -------------------------------------------------------------------------------------------------------- */
    /*                                             Async.invoke()                                               */
    /* -------------------------------------------------------------------------------------------------------- */

    /**
     * Request an ExecutionContext to invoke the given Runnable object
     * @param ctx An ExecutionContext to request an invocation
     * @param runnable A Runnable object request to be invoked
     */
    public static void invoke(ExecutionContext ctx, Runnable runnable){
        ctx.post(makeAsync(runnable));
    }

    /**
     * Request an ExecutionContext to invoke the given Consumer object
     * @param ctx An ExecutionContext to request an invocation
     * @param consumer A Consumer object request to be invoked
     */
    public static <T> void invoke(ExecutionContext ctx, Consumer<T> consumer){
        ctx.post(makeAsync(consumer));
    }

    /**
     * Request an ExecutionContext to invoke the given Runnable object
     * @param ctx An ExecutionContext to request an invocation
     * @param supplier A Runnable object request to be invoked
     */
    public static <T> void invoke(ExecutionContext ctx, Supplier<T> supplier){
        ctx.post(makeAsync(supplier));
    }


    public static <T, R>  ListenableFuture<R> invokeAnd(ExecutionContext ctx, Function<T, R> func){
        AsyncFunction<T, R> function = new AsyncFunction<>(ctx, func);
        ctx.post(function);
        return function.future();
    }

    public static <T>  ListenableFuture<T> invokeAnd(ExecutionContext ctx, Supplier<T> sup){
        AsyncSupplier<T> supplier = new AsyncSupplier<T>(ctx, sup);
        ctx.post(supplier);
        return supplier.future();
    }

    public static <T, R>  ListenableFuture<R> deferAnd(ExecutionContext ctx, Function<T, R> func){
        AsyncFunction<T, R> function = new AsyncFunction<>(func);
        ctx.defer(function);
        return function.future();
    }

    public static <T>  ListenableFuture<T> deferAnd(ExecutionContext ctx, Supplier<T> sup){
        AsyncSupplier<T> supplier = new AsyncSupplier<T>(sup);
        ctx.defer(supplier);
        return supplier.future();
    }


    /* -------------------------------------------------------------------------------------------------------- */
    /*                                             Async.getContext()                                           */
    /* -------------------------------------------------------------------------------------------------------- */

    /**
     * Get the Context from an AsyncTask
     * @param tsk An AsyncTask to get the Context from
     * @return the ExecutionContext
     */
    public static ExecutionContext getContext(AsyncTask tsk){
        if(tsk != null){
            return tsk.context();
        } else {
            return null;
        }
    }

    /**
     * Get the Context from an ListenableFuture. The Context can only be deduced if
     * the future is associated with an AsyncTask.
     * @param future A ListenableFuture to get the Context from
     * @return the ExecutionContext
     */
    public static <T> ExecutionContext getContext(ListenableFuture<T> future){
        if(future.task() != null){
            return future.task().context();
        } else {
            return null;
        }
    }

    /**
     *
     * @param ttsk
     * @return
     */
    public static ExecutionContext getContext(AsyncTimerTask ttsk){
        return ttsk.context();
    }

    /**
     *
     * @param timer
     * @return
     */
    public static ExecutionContext getContext(AsyncTimer timer){
        return timer.context();
    }

    /**
     *
     * @param work
     * @return
     */
    public static ExecutionContext getContext(ExecutorWorkGuard work){
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



}