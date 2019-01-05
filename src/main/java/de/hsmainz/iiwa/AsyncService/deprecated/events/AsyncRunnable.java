package de.hsmainz.iiwa.AsyncService.deprecated.events;

import de.hsmainz.iiwa.AsyncService.executor.ExecutionContext;

public class AsyncRunnable implements AsyncTask {
	
	public AsyncRunnable(Runnable __function)
	{
		this.function = __function;
		future.prepare(this);
	}

	public <T> AsyncRunnable(Runnable __function, ListenableFuture<T> __future) {
		future.prepare(this);
		future.attach_if_alloc((ListenableFuture<Void>) __future);
	}

	private LazyAllocatedListenableFuture<Void> future = new LazyAllocatedListenableFuture<>();
	
	private Runnable function;

	private ExecutionContext exec;
	
	@Override
	public void execute() {
		function.run();
		future.fire();
	}

	@Override
	public ListenableFuture<Void> getFuture() {
		return future.get();
	}

	@Override
	public <K> void __set__arg_(K arg) {
		
	}

	@Override
	public <L> void __set__sec__arg_(L arg) {
		
	}

	@Override
	public LazyAllocatedListenableFuture<Void> getFutureLazy() {
		return future;
	}

	@Override
	public void fire() {

		AsyncService.post(this);

	}

	@Override
	public AsyncTask copy() {
		return new AsyncRunnable(function);
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
