package de.hsmainz.iiwa.AsyncService.events;

public abstract class AsyncTaskExecutor {

    protected abstract void handle_task(AsyncTask task);

    public void post(){

    }
}
