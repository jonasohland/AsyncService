package de.hsmainz.iiwa.core.events;

import de.hsmainz.iiwa.core.future.DualListenableFuture;
import de.hsmainz.iiwa.core.future.ListenableFuture;
import de.hsmainz.iiwa.core.future.EventTimer;

public class DualTimeout implements Event {
	
	DualListenableFuture<?,?> future;
	
	public <T,U> DualTimeout(DualListenableFuture<T,U> __future){
		future = __future;
	}

	@Override
	public void attachTimer(EventTimer arg0) {
		
	}

	@Override
	public Event copy() {
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
	public EventTimer getTimer() {
		return null;
	}

	@Override
	public boolean hasTimer() {
		return false;
	}

	@Override
	public <K> void setArg(K arg0) {
		
	}

	@Override
	public <L> void setSecondArg(L arg0) {
		
	}

}
