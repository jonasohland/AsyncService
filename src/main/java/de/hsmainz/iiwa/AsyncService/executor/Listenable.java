package de.hsmainz.iiwa.AsyncService.executor;

import de.hsmainz.iiwa.AsyncService.functional.Consumer;
import de.hsmainz.iiwa.AsyncService.functional.Function;

public interface Listenable<T> {

    public void addListener(AsyncTask tsk);

    public void addListener(Runnable runnable);
    public void addListener(ExecutionContext ctx, Runnable runnable);

    public void addListener(Consumer<T> consumer);
    public void addListener(ExecutionContext ctx, Consumer<T> consumer);

    public <R> void addListener(Function<T, R> function);
    public <R> void addListener(ExecutionContext ctx, Function<T, R> function);

    public <R> Listenable<R> addListenerThen(Function<T, R> function);

}
