package de.hsmainz.iiwa.AsyncService.future;

import java.util.concurrent.LinkedBlockingQueue;

import de.hsmainz.iiwa.AsyncService.events.AsyncService;
import de.hsmainz.iiwa.AsyncService.functional.BiConsumer;
import de.hsmainz.iiwa.AsyncService.functional.BiFunction;
import de.hsmainz.iiwa.AsyncService.functional.Consumer;
import de.hsmainz.iiwa.AsyncService.functional.Function;

import de.hsmainz.iiwa.AsyncService.events.DualTimeout;
import de.hsmainz.iiwa.AsyncService.events.Event;
import de.hsmainz.iiwa.AsyncService.events.Events;

/**
 * 
 * @author jonas.ohland
 * @see ListenableFuture
 * @param <T> Type of resource 1 to be available in the future
 * @param <U> Type of resource 2 to be available in the future
 */
public class DualListenableFuture<T,U> {
	private LinkedBlockingQueue<Event> listeners = new LinkedBlockingQueue<Event>();
	
	private T result1;
	private U result2;
	
	private Event event;
	
	private static void debug(String g) {
		
	}
	
	public void setEvent(Event e) {
		event = e;
	}
	
	public DualListenableFuture() {
		
	}
	
	public DualListenableFuture(Event __e) {
		event = __e;
	}
	
	void setResults(T __result1, U __result2)
	{
		result1 = __result1;
		result2 = __result2;
	}
	
	public void addListener(Runnable runnable)
	{
		listeners.add(Events.makeEvent(runnable));
	}
	public void addListener(Consumer<T> consumer)
	{
		listeners.add(Events.makeEvent(result1, consumer));
	}
	public void addListener(BiConsumer<T,U> biconsumer)
	{
		listeners.add(Events.makeEvent(result1, result2, biconsumer));
	}
	public <R> ListenableFuture<R> addNextListener(Function<T,R> function)
	{
		Event e = Events.makeEvent(result1, function);
		listeners.add(e);
		return e.getFuture();
	}
	public <R> ListenableFuture<R> addNextListener(BiFunction<T,U,R> bifunction)
	{
		Event e = Events.makeEvent(result1, result2, bifunction);
		listeners.add(e);
		return e.getFuture();
	}
	public <R> ListenableFuture<R> then(Function<T,R> function)
	{
		Event e = Events.makeEvent(result1, function);
		listeners.add(e);
		return e.getFuture();
	}
	public <R> ListenableFuture<R> then(BiFunction<T,U,R> bifunction)
	{
		Event e = Events.makeEvent(result1, result2, bifunction);
		listeners.add(e);
		return e.getFuture();
	}
	public <R> ListenableFuture<R> run(Function<T,R> function)
	{
		Event e = Events.makeEvent(result1, function);
		listeners.add(e);
		return e.getFuture();
	}
	public <R> ListenableFuture<R> run(BiFunction<T,U,R> bifunction)
	{
		Event e = Events.makeEvent(result1, result2, bifunction);
		listeners.add(e);
		return e.getFuture();
	}
	public void fireListeners(T val1, U val2)
	{
		if(!listeners.isEmpty())
		{
			for(Event element : listeners) {
				
				element.setArg(val1);
				element.setSecondArg(val2);
				
				AsyncService.post(element);
			}
			//listeners.clear();
		}
	}
	
	/**
	 * remove the an event and its listeners from the future. This should not be called from outside the event loop. 
	 * use the setTimeout method for external timeouts.
	 */
	public void cancel(){
		
		debug("Cancelling Future... ");
		
		if(event != null) {
			 
			if(event.hasTimer()) {

				AsyncService.timer_map.remove(event.getTimer().getId());

				AsyncService.queue.remove(event);

				event.getTimer().cancel();

			} else {
				AsyncService.queue.remove(event);
			}
		} 
		
		if(!listeners.isEmpty()) {
			for(Event listener : listeners) {
				AsyncService.queue.remove(listener);
			}
		}

		if(AsyncService.isWaiting()){
			AsyncService.post(()->{});
		}
	}
	
	public void fire(T val1, U val2)
	{
		fireListeners(val1, val2);
	}
	
	public DualTimeout getTimeout(){
		return new DualTimeout(this);
	}
	
	/**
	 * set Timeout for Event. After the Timeout Period, the Event will be atomically removed from the queue
	 * @param delay Timeout time
	 * @return EventCancelTask - A TimerTask that can be canceled to cancel the Timeout
	 */
	public EventCancelTask setTimeout(long delay) {
		
		EventCancelTask ct = new EventCancelTask(event, listeners, null);
		AsyncService.coreTimer.schedule(ct, delay);
		return ct;
	}
}
