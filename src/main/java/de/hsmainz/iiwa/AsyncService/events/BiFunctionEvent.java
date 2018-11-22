package de.hsmainz.iiwa.AsyncService.events;

import de.hsmainz.iiwa.AsyncService.functional.BiFunction;

import de.hsmainz.iiwa.AsyncService.future.ListenableFuture;
import de.hsmainz.iiwa.AsyncService.future.EventTimer;

public class BiFunctionEvent<Tin1, Tin2, Tout> implements Event {
	
	private Tin1 arg1;
	private Tin2 arg2;
	
	private ListenableFuture<Tout> future;

	public BiFunction<Tin1,Tin2,Tout> function;
	
	private EventTimer timer;
	
	public BiFunctionEvent(Tin1 __arg1, Tin2 __arg2, BiFunction<Tin1, Tin2, Tout> __func)
	{
		arg1 = __arg1;
		arg2 = __arg2;
		function = __func;
	}
	
	public BiFunctionEvent(BiFunction<Tin1, Tin2, Tout> __func){
		function = __func;
	}
	
	public BiFunctionEvent(BiFunction<Tin1, Tin2, Tout> __func, ListenableFuture<Tout> __future){
		function = __func;
		future = __future;
		future.setEvent(this);
	}


	@Override
	public void execute() {
		Tout result = function.apply(arg1, arg2);
		if(future != null)
			future.fire(result);
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public ListenableFuture<Tout> getFuture() {
		if(future != null) {
			return future;
		}
		else {
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
	}
	
	public void fire(Tin1 in1, Tin2 in2) {
		
		setArg(in1);
		setSecondArg(in2);
		
		AsyncService.post(this);
		
	}
	
	@Override
	public Event copy() {
		return new BiFunctionEvent<Tin1, Tin2, Tout>(arg1, arg2, function);
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
