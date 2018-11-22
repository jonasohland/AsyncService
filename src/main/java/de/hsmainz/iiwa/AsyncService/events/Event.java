package de.hsmainz.iiwa.AsyncService.events;

import de.hsmainz.iiwa.AsyncService.future.ListenableFuture;
import de.hsmainz.iiwa.AsyncService.future.EventTimer;

public interface Event {
	
	/**
	 * Executes the event immediately. This should only be called by the AsyncService.
	 */
	public void execute();
	
	/**
	 * Post the event to the EventLoop queue.  
	 */
	public void fire();
	
	/**
	 * Returns a separate copy of this Event. 
	 * @return the new Event
	 */
	public Event copy();
	
	/**
	 * Attaches a Timer to the Event to schedule the Event.
	 * @param t EventTimer to attach. 
	 * @see EventTimer
	 */
	public void attachTimer(EventTimer t);
	
	/**
	 * Checks if a Timer exist. 
	 * @return True if the Timer exists. 
	 */
	public boolean hasTimer();
	
	/**
	 * Get the attached Timer. 
	 * @return Returns the EventTimer.
	 */
	public EventTimer getTimer();
	
	/**
	 * DO NOT USE THIS METHOD. FOR INTERNAL USE ONLY
	 * @param <K> Argument Type
	 * @param arg Argument to set
	 */
	public <K> void setArg(K arg);
	
	/**
	 * DO NOT USE THIS METHOD. FOR INTERNAL USE ONLY
	 * @param <L> Argument Type
	 * @param arg Argument to set
	 */
	public <L> void setSecondArg(L arg);
	
	/**
	 * Returns the generic ListenableFuture 
	 * @return Returns the ListenableFuture Object
	 * @param <T> the Type of Resource that will be available in the Future
	 * @see ListenableFuture
	 */
	public <T> ListenableFuture<T> getFuture();
	
	public int hashCode();
	
}
