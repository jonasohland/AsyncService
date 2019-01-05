package de.hsmainz.iiwa.AsyncService.executor;

import de.hsmainz.iiwa.AsyncService.functional.Function;

public abstract class ExecutorWrapperBase {
    public abstract AsyncTask handleTask(AsyncTask tsk);

    public AsyncTask wrap(Runnable runnable) {
        return handleTask(Async.makeAsync(runnable));
    }

    public <T, R> AsyncTask wrap(Function<T, R> function){
        return handleTask(Async.makeAsync(function));
    }
}
