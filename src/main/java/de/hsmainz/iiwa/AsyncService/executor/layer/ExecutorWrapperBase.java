package de.hsmainz.iiwa.AsyncService.executor.layer;

import de.hsmainz.iiwa.AsyncService.async.Async;
import de.hsmainz.iiwa.AsyncService.async.AsyncTask;
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
