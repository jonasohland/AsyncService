package de.hsmainz.iiwa.AsyncService.events;

public class DualTimeout implements AsyncTask {
	
	DualListenableFuture<?,?> future;
	
	public <T,U> DualTimeout(DualListenableFuture<T,U> __future){
		future = __future;
	}

	@Override
	public void attachTimer(AsyncTimer arg0) {
		
	}

	@Override
	public AsyncTask copy() {
		return new DualTimeout(future);
	}

	@Override
	public void execute() {
		future.cancel();
	}

	@Override
	public void fire() {
		AsyncService.post(this);
		
	}

	@Override
	public <T> ListenableFuture<T> getFuture() {
		throw new NullPointerException();
	}

	@Override
	public <T> LazyAllocatedListenableFuture<T> getFutureLazy() {
		throw new NullPointerException();
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
