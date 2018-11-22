package de.hsmainz.iiwa.core.events;

import de.hsmainz.iiwa.core.functional.Function;

import de.hsmainz.iiwa.core.future.ListenableFuture;
import de.hsmainz.iiwa.core.future.EventTimer;

public class FunctionEvent<T, R> implements Event {

	private T arg1;
	
	private ListenableFuture<R> future;
	
	private EventTimer timer;
	
	public FunctionEvent(T __arg1, Function<T,R> __func)
	{
		arg1 = __arg1;
		function = __func;
		future = new ListenableFuture<>(this);
	}
	
	public FunctionEvent(T __arg1, Function<T,R> __func, ListenableFuture<R> __future)
	{
		arg1 = __arg1;
		function = __func;
		future = __future;
		future.setEvent(this);
	}
	
	public FunctionEvent(Function<T,R> __func) {
		function = __func;
		future = new ListenableFuture<>(this);
	}

	public Function<T,R> function;

	@Override
	public void execute() {
		R result = function.apply(arg1);
		if(future != null) {
			future.fire(result);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public ListenableFuture<R> getFuture() {
		if(future != null)
			return future;
		else {
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
		//empty
	}

	@Override
	public void fire() {
		AsyncService.post(this);
		
	}
	
	public void fire(T input)
	{
		setArg(input);
		AsyncService.post(this);
	}

	@Override
	public Event copy() {
		return new FunctionEvent<T,R>(arg1, function);
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
