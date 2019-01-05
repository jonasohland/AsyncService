package de.hsmainz.iiwa.AsyncService.deprecated.events;

import de.hsmainz.iiwa.AsyncService.functional.Consumer;

public class AsyncConsumer<T> implements AsyncTask {

	private Consumer<T> function;
	private LazyAllocatedListenableFuture<Void> future = new LazyAllocatedListenableFuture<>();
	private T arg1;
	
	public AsyncConsumer(T __in1, Consumer<T> __function)
	{
		function = __function;
		arg1 = __in1;
		future.prepare(this);
	}
	
	public AsyncConsumer(Consumer<T> __function)
	{
		function = __function;
		future.prepare(this);

	}

	
	@Override
	public void execute() {
		function.accept(arg1);
	}

	@Override
	public ListenableFuture<Void> getFuture() {
		return future.get();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <K> void __set__arg_(K arg) {
		arg1 = (T) arg;
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
		future.fire();
	}
	
	public void fire(T input) {
		__set__arg_(input);
		AsyncService.post(this);
		future.fire();
	}

	@Override
	public AsyncTask copy() {
		return new AsyncConsumer<T>(arg1, function);
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
