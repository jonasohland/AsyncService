package de.hsmainz.iiwa.AsyncService.events;

import de.hsmainz.iiwa.AsyncService.functional.Function;

public class AsyncFunction<T, R> implements AsyncTask {

	private T arg1;
	
	private LazyAllocatedListenableFuture<R> future = new LazyAllocatedListenableFuture<>();
	
	private AsyncTimer timer;
	
	public AsyncFunction(T __arg1, Function<T,R> __func)
	{
		arg1 = __arg1;
		function = __func;
		future.prepare(this);
	}
	
	public AsyncFunction(T __arg1, Function<T,R> __func, ListenableFuture<R> __future)
	{
		arg1 = __arg1;
		function = __func;
		future.attach_if_alloc(__future);
		future.prepare(this);
	}
	
	public AsyncFunction(Function<T,R> __func) {
		function = __func;
		future.prepare(this);
	}

	public Function<T,R> function;

	@Override
	public void execute() {
		R result = function.apply(arg1);
		future.fire(result);
	}

	@SuppressWarnings("unchecked")
	@Override
	public ListenableFuture<R> getFuture() {
		return future.get();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <K> void __set__arg_(K arg) {
		arg1 = (T) arg;
	}

	@Override
	public <L> void __set__sec__arg_(L arg) {
		//empty
	}

    @Override
    public <T> LazyAllocatedListenableFuture<T> getFutureLazy() {
        return null;
    }

    @Override
	public void fire() {
		AsyncService.post(this);
		
	}
	
	public void fire(T input)
	{
		__set__arg_(input);
		AsyncService.post(this);
	}

	@Override
	public AsyncTask copy() {
		return new AsyncFunction<T,R>(arg1, function);
	}

	@Override
	public void attachTimer(AsyncTimer t) {
		timer = t;
	}

	@Override
	public boolean hasTimer() {
		return timer != null;
	}

	@Override
	public AsyncTimer getTimer() {
		return timer;
	}
}
