package de.hsmainz.iiwa.AsyncService.events;

import de.hsmainz.iiwa.AsyncService.future.ListenableFuture;
import de.hsmainz.iiwa.AsyncService.future.EventTimer;

public class RunnableEvent implements Event{
	
	public RunnableEvent(Runnable __function)
	{
		this.function = __function;
	}

	private ListenableFuture<Void> future;
	
	private Runnable function;
	
	@Override
	public void execute() {
		function.run();

		if(future != null){
			future.fire();
		}
	}

	@Override
	public ListenableFuture<Void> getFuture() {
		if(future != null) {
			return future;
		} else {
			future = new ListenableFuture<>(this);
			return future;
		}
	}

	@Override
	public <K> void setArg(K arg) {
		
	}

	@Override
	public <L> void setSecondArg(L arg) {
		
	}

	@Override
	public void fire() {

		AsyncService.post(this);

	}

	@Override
	public Event copy() {
		return new RunnableEvent(function);
	}

	private EventTimer timer;
	
	@Override
	public void attachTimer(EventTimer t) {
		timer = t;
	}

	@Override
	public boolean hasTimer() {
		return timer != null;
	}

	@Override
	public EventTimer getTimer() {
		return timer;
	}
}
