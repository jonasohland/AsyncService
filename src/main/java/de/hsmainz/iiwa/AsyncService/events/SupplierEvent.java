package de.hsmainz.iiwa.AsyncService.events;



import de.hsmainz.iiwa.AsyncService.functional.Supplier;
import de.hsmainz.iiwa.AsyncService.future.ListenableFuture;
import de.hsmainz.iiwa.AsyncService.future.EventTimer;

public class SupplierEvent<T> implements Event {
	
	private ListenableFuture<T> future;
	private Supplier<T> function;
	
	public SupplierEvent(Supplier<T> __function)
	{
		function = __function;
	}
	
	public SupplierEvent(Supplier<T> __function, ListenableFuture<T> __future)
	{
		function = __function;
		future = __future;
		future.setEvent(this);
	}

	@Override
	public void execute() {
		T result = function.get();
		if(future != null) {
			future.fire(result);
		}
	}

	@Override
	public <K> void setArg(K arg) {
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public ListenableFuture<T> getFuture() {
		if(future != null){
			return future;
		}
		else {
			future = new ListenableFuture<>(this);
			return future;
		}
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
		return new SupplierEvent<T>(function);
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
