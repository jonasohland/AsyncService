package de.hsmainz.iiwa.AsyncService.events;

import de.hsmainz.iiwa.AsyncService.functional.BiConsumer;

public class AsyncBiConsumer<Tin1, Tin2> implements AsyncTask {

	private BiConsumer<Tin1, Tin2> function;
	private LazyAllocatedListenableFuture<Void> future = new LazyAllocatedListenableFuture<>();
	
	private AsyncTimer timer;
	
	private Tin1 arg1;
	private Tin2 arg2;
	
	public AsyncBiConsumer(Tin1 __in1, Tin2 __in2, BiConsumer<Tin1, Tin2> __function)
	{
		function = __function;
		arg1 = __in1;
		arg2 = __in2;
		future.prepare(this);
	}
	
	public AsyncBiConsumer(BiConsumer<Tin1, Tin2> __function)
	{
		function = __function;
		future.prepare(this);
	}
	
	@Override
	public void execute() {
		function.accept(arg1, arg2);
	}


	@Override
	public ListenableFuture<Void> getFuture() {
		return future.get();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <K> void __set__arg_(K arg) {
		arg1 = (Tin1) arg;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <L> void __set__sec__arg_(L arg) {
		arg2 = (Tin2) arg;
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
	
	public void fire(Tin1 __arg1, Tin2 __arg2) {
		arg1 = __arg1;
		arg2 = __arg2;
		AsyncService.post(this);
		future.fire();
	}

	@Override
	public AsyncTask copy() {
		return new AsyncBiConsumer<Tin1, Tin2>(arg1, arg2, function);
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
