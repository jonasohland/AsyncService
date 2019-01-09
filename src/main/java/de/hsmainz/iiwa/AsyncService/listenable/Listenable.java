package de.hsmainz.iiwa.AsyncService.listenable;

import de.hsmainz.iiwa.AsyncService.async.AsyncTask;
import de.hsmainz.iiwa.AsyncService.executor.layer.ExecutorLayer;
import de.hsmainz.iiwa.AsyncService.functional.Consumer;
import de.hsmainz.iiwa.AsyncService.functional.Function;

public interface Listenable<T> {

    public void addListener(AsyncTask tsk);

    public void addListener(Runnable runnable);
    public void addListener(ExecutorLayer ctx, Runnable runnable);

    public void addListener(Consumer<T> consumer);
    public void addListener(ExecutorLayer ctx, Consumer<T> consumer);

    public <R> void addListener(Function<T, R> function);
    public <R> void addListener(ExecutorLayer ctx, Function<T, R> function);

    public <R> Listenable<R> addListenerThen(Function<T, R> function);

}
