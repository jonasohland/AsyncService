package de.hsmainz.iiwa.AsyncService.deprecated.events;



import de.hsmainz.iiwa.AsyncService.functional.Supplier;

public class AsyncSupplier<T> implements AsyncTask {

	T result;
	
	private LazyAllocatedListenableFuture<T> future = new LazyAllocatedListenableFuture<>();
	private Supplier<T> function;
	
	public AsyncSupplier(Supplier<T> __function)
	{
		function = __function;
	}
	
	public AsyncSupplier(Supplier<T> __function, ListenableFuture<T> __future)
	{
		function = __function;
		future.attach(__future);
		future.get().setAsyncTask(this);
	}

	@Override
	public void execute() {
		result = function.get();
		future.fire(result);
	}

	@Override
	public <K> void __set__arg_(K arg) {
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public ListenableFuture<T> getFuture() {
		return future.get();
	}

	@Override
	public <L> void __set__sec__arg_(L arg) {
	}

	@Override
	public LazyAllocatedListenableFuture<T> getFutureLazy() {
		return future;
	}

	@Override
	public void fire() {
		AsyncService.post(this);
		
	}

	@Override
	public AsyncTask copy() {
		return new AsyncSupplier<T>(function);
	}
	
	private AsyncTimer timer;

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
