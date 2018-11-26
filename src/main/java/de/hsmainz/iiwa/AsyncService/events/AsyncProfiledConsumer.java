package de.hsmainz.iiwa.AsyncService.events;

import de.hsmainz.iiwa.AsyncService.functional.Consumer;

public class AsyncProfiledConsumer<T> implements AsyncTask {

	private AsyncTimer tm;
	
	private ListenableFuture<Long> future;
	private Consumer<T> function;
	private T input;
	
	long start_time;
	long end_time;
	
	public AsyncProfiledConsumer(Consumer<T> __func){
		function = __func;
	}
	
	public AsyncProfiledConsumer(T in, Consumer<T> __func){
		function = __func;
		input = in;
	}
	
	
	@Override
	public void execute() {
		start_time = System.nanoTime();
		function.accept(input);
		end_time = System.nanoTime();

		if(future != null)
			future.fire(end_time - start_time);
	}

	@Override
	public void fire() {
		AsyncService.post(this);
	}
	
	public void fire(T in) {
		input = in;
		AsyncService.post(this);
	}

	@Override
	public AsyncTask copy() {
		return null;
	}

	@Override
	public void attachTimer(AsyncTimer t) {
		tm = t;
		
	}

	@Override
	public boolean hasTimer() {
		return tm != null;
	}

	@Override
	public AsyncTimer getTimer() {
		return tm;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <K> void __set__arg_(K arg) {
		input = (T) arg;
		
	}

	@Override
	public <L> void __set__sec__arg_(L arg) {
	}

	@Override
	public LazyAllocatedListenableFuture<Long> getFutureLazy() {
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ListenableFuture<Long> getFuture() {
		if(future != null){
			return future;
		}
		else {
			future = new ListenableFuture<>(this);
			return future;
		}
	}
}
