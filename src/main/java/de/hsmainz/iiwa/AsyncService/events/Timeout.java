package de.hsmainz.iiwa.AsyncService.events;

import de.hsmainz.iiwa.AsyncService.future.ListenableFuture;
import de.hsmainz.iiwa.AsyncService.future.EventTimer;

public class Timeout implements Event {
	
	ListenableFuture<?> future;
	
	public <T> Timeout(ListenableFuture<T> __future){
		future = __future;
	}

	@Override
	public void attachTimer(EventTimer arg0) {
		
	}

	@Override
	public Event copy() {
		return new Timeout(future);
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
		return null;
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
