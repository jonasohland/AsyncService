package de.hsmainz.iiwa.core.events;

import de.hsmainz.iiwa.core.future.ListenableFuture;
import de.hsmainz.iiwa.core.future.EventTimer;
import de.hsmainz.iiwa.core.functional.Consumer;

public class ProfiledEvent<T> implements Event {

	private EventTimer tm;
	
	private ListenableFuture<Long> future;
	private Consumer<T> function;
	private T input;
	
	long start_time;
	long end_time;
	
	public ProfiledEvent(Consumer<T> __func){
		function = __func;
	}
	
	public ProfiledEvent(T in, Consumer<T> __func){
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
	public Event copy() {
		return null;
	}

	@Override
	public void attachTimer(EventTimer t) {
		tm = t;
		
	}

	@Override
	public boolean hasTimer() {
		return tm != null;
	}

	@Override
	public EventTimer getTimer() {
		return tm;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <K> void setArg(K arg) {
		input = (T) arg;
		
	}

	@Override
	public <L> void setSecondArg(L arg) {
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
