package de.hsmainz.iiwa.AsyncService.future;

import java.util.concurrent.LinkedBlockingQueue;

import de.hsmainz.iiwa.AsyncService.events.*;
import de.hsmainz.iiwa.AsyncService.functional.Consumer;
import de.hsmainz.iiwa.AsyncService.functional.Function;
import de.hsmainz.iiwa.AsyncService.events.AsyncService;

/**
 *  
 * @author jonas.ohland
 *
 * @param <T> Type of Object which will be available in the future.
 */
public class ListenableFuture<T>{
	
	private Event event;
	private LinkedBlockingQueue<Event> listeners = new LinkedBlockingQueue<Event>();
	private T result;
	
	/**
	 * Adds the event to the Listenable which calls all listeners.
	 * @param e Adds an event to the ListanableFuture object.
	 */
	public void setEvent(Event e) {
		event = e;
	}
	
	/**
	 * Internal debug function. 
	 * @param g debug message
	 */
	private static void debug(String g) {
		// System.out.println("FT: " + g);
	}
	
	/**
	 * Default constructor
	 */
	public ListenableFuture() {
	}
	
	/**
	 * Constructor with event. 
	 * @param e Event to associate with
	 */
	public ListenableFuture(Event e){
		event = e;
	}
	
	/**
	 * The result object which holds the values which will be available in the future. Can be set by fire or by this method.
	 * @param __result The result which will be fired. 
	 */
	public void setResult(T __result){
		result = __result;
	}
	
	/**
	 * Add a listener to the listener queue.
	 * @param runnable a runnable object to add.
	 */
	public void addListener(Runnable runnable)
	{
		listeners.add(Events.makeEvent(runnable));
	}
	
	/**
	 * Add a listener to the listener queue.
	 * @param consumer A consumer object to add.
	 */
	public void addListener(Consumer<T> consumer)
	{
		listeners.add(Events.makeEvent(result, consumer));
	}
	
	/**
	 * Add a listener to the listener queue.
	 * @param consumer A ConsumerEvent object to add.
	 */
	public void addListener(ConsumerEvent<T> consumer)
	{
		listeners.add(consumer);
	}
	
	/**
	 * Add a listener to the listener queue which not returns a listable future.
	 * @param runnable a runnable object to add.
	 */
	public void last(Runnable runnable)
	{
		listeners.add(Events.makeEvent(runnable));
	}
	
	/**
	 * Add a listener to the listener queue which not returns a listenable future.
	 * @param consumer A consumer object to add.
	 */
	public void last(Consumer<T> consumer)
	{
		listeners.add(Events.makeEvent(result, consumer));
	}
	
	/**
	 * Add a listener to the listener queue.
	 * @param consumer A ConsumerEvent object to add.
	 */
	public void last(ConsumerEvent<T> consumer)
	{
		listeners.add(consumer);
	}
	
	/**
	 * Add a chained listener to the future. 
	 * @param function A Function object to add.
	 * @param <R> Return Type of function
	 * @return The ListenableFuture object.
	 */
	public <R> ListenableFuture<R> addNextListener(Function<T,R> function)
	{
		Event e = Events.makeEvent(result, function);
		listeners.add(e);
		return e.getFuture();
	}
	
	/**
	 * Add a chained listener to the future. 
	 * @param function A FunctionEvent object to add.
	 * @param <R> Return Type of function.
	 * @return The ListenableFuture object.
	 */
	public <R> ListenableFuture<R> addNextListener(FunctionEvent<T,R> function)
	{
		listeners.add(function);
		return function.getFuture();
	}
	
	/**
	 * Same as addNextListener function. 
	 * @param function A Function object to add.
	 * @return The ListenableFuture object.
	 * @param <R> ReturnType of function
	 * @see #addNextListener(Function)	 
	 * */
	public <R> ListenableFuture<R> then(Function<T,R> function)
	{
		Event e = Events.makeEvent(result, function);
		listeners.add(e);
		return e.getFuture();
	}
	
	/**
	 * Same as addNextListener function. 
	 * @param function A FunctionEvent object to add.
	 * @return The ListenableFuture object.
	 * @param <R> ReturnType of function
	 * @see #addNextListener(Function)	 
	 * */
	public <R> ListenableFuture<R> then(FunctionEvent<T,R> function)
	{
		listeners.add(function);
		return function.getFuture();
	}
	
	

	/**
	 * Set this Futures Resource and fire all listeners associated to this Future. 
	 * @param value the result object which is fired. 
	 */
	public void fire(T value) {
		
		if(!listeners.isEmpty()) {
			
			for(Event element : listeners) {
				
				element.setArg(value);
				AsyncService.post(element);
			}
			//listeners.clear();
		}
	}
	
	/**
	 * Fire all listeners associated to this Future
	 */
	public void fire() {
		
		if(!listeners.isEmpty()) {
			
			for(Event element : listeners) {
				AsyncService.post(element);
			}
			//listeners.clear();
		}
	}
	
	/**
	 * Cancel the timer for this Future and remove its Event from the AsyncService.
	 */
	public void cancel(){
		
		debug("Cancelling Future... ");
		
		if(event != null) {
			 
			if(event.hasTimer()) {
				
				debug("Future has Timer " + event.getTimer().getId());
				
				if(AsyncService.timer_map.containsKey(event.getTimer().getId())){
					debug("Timer Event was found in Map");
				} else {
					debug("Timer Event was not found in Map");
				}
				
				AsyncService.timer_map.remove(event.getTimer().getId());
				
				AsyncService.queue.remove(event);
				
				debug("removing Timer... ");
				
				if(event.getTimer().cancel()) {
					debug("successful");
				} else {
					debug("failed");
				}
			}
			else {
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
	
	/**
	 * Returns a Timeout object that can be used to cancel this Future.
	 * @return A timeout object associated to this Future. 
	 * @see AsyncService#schedule(Event, long)
	 */
	public Timeout getTimeout(){
		return new Timeout(this);
	}
	
	/**
	 * Set a timeout Timeout for Event. After the Timeout Period, the Event will be atomically removed from the queue. 
	 * @param delay Timeout time
	 * @return EventCancelTask - A TimerTask that can be canceled to cancel the Timeout
	 */
	public EventCancelTask setTimeout(long delay) {
		EventCancelTask ct = new EventCancelTask(event, listeners, null);
		AsyncService.coreTimer.schedule(ct, delay);
		return ct;
	}
}
