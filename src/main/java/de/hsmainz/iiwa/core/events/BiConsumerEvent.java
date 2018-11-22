package de.hsmainz.iiwa.core.events;

import de.hsmainz.iiwa.core.functional.BiConsumer;

import de.hsmainz.iiwa.core.future.ListenableFuture;
import de.hsmainz.iiwa.core.future.EventTimer;

public class BiConsumerEvent<Tin1, Tin2> implements Event{

	private BiConsumer<Tin1, Tin2> function;
	private ListenableFuture<Void> future;
	
	private EventTimer timer;
	
	private Tin1 arg1;
	private Tin2 arg2;
	
	public BiConsumerEvent(Tin1 __in1, Tin2 __in2, BiConsumer<Tin1, Tin2> __function)
	{
		function = __function;
		arg1 = __in1;
		arg2 = __in2;
	}
	
	public BiConsumerEvent(BiConsumer<Tin1, Tin2> __function)
	{
		function = __function;
	}
	
	@Override
	public void execute() {
		function.accept(arg1, arg2);
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
		arg1 = (Tin1) arg;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <L> void setSecondArg(L arg) {
		arg2 = (Tin2) arg;
	}

	@Override
	public void fire() {
		AsyncService.post(this);
		if(future != null){
			future.fire();
		}
	}
	
	public void fire(Tin1 __arg1, Tin2 __arg2) {
		arg1 = __arg1;
		arg2 = __arg2;
		AsyncService.post(this);
		if(future != null){
			future.fire();
		}
	}

	@Override
	public Event copy() {
		return new BiConsumerEvent<Tin1, Tin2>(arg1, arg2, function);
	}


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
