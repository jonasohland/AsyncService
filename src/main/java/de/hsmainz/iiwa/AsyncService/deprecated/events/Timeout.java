package de.hsmainz.iiwa.AsyncService.deprecated.events;

@Deprecated
public class Timeout implements AsyncTask {
	
	LazyAllocatedListenableFuture<?> future;
	
	public <T> Timeout(LazyAllocatedListenableFuture<T> __future){
		future = __future;
	}

	@Override
	public void attachTimer(AsyncTimer arg0) {
		
	}

	@Override
	public AsyncTask copy() {
		return new Timeout(future);
	}

	@Override
	public void execute() {
		future.get().cancel();
	}

	@Override
	public void fire() {
		AsyncService.post(this);
		
	}

	@Override
	public <T> ListenableFuture<T> getFuture() {
		return null;
	}

	@Override
	public <T> LazyAllocatedListenableFuture<T> getFutureLazy() {
		return null;
	}

	@Override
	public AsyncTimer getTimer() {
		return null;
	}

	@Override
	public boolean hasTimer() {
		return false;
	}

	@Override
	public <K> void __set__arg_(K arg0) {
		
	}

	@Override
	public <L> void __set__sec__arg_(L arg0) {
		
	}

}
