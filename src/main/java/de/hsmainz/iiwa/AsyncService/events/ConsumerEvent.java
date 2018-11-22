package de.hsmainz.iiwa.AsyncService.events;

import de.hsmainz.iiwa.AsyncService.functional.Consumer;

import de.hsmainz.iiwa.AsyncService.future.ListenableFuture;
import de.hsmainz.iiwa.AsyncService.future.EventTimer;

public class ConsumerEvent<T> implements Event {

	private Consumer<T> function;
	private ListenableFuture<Void> future;
	private T arg1;
	
	public ConsumerEvent(T __in1, Consumer<T> __function)
	{
		function = __function;
		arg1 = __in1;
	}
	
	public ConsumerEvent(Consumer<T> __function)
	{
		function = __function;
	}

	
	@Override
	public void execute() {
		function.accept(arg1);
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

	@SuppressWarnings("unchecked")
	@Override
	public <K> void setArg(K arg) {
		arg1 = (T) arg;
	}

	@Override
	public <L> void setSecondArg(L arg) {
		
	}

	@Override
	public void fire() {
		AsyncService.post(this);
		if(future != null) {
			future.fire();
		}
	}
	
	public void fire(T input) {
		setArg(input);
		AsyncService.post(this);
		if(future != null) {
			future.fire();
		}
	}

	@Override
	public Event copy() {
		return new ConsumerEvent<T>(arg1, function);
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
