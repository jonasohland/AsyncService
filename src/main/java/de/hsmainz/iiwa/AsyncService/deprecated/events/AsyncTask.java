package de.hsmainz.iiwa.AsyncService.deprecated.events;

public interface AsyncTask {
	
	/**
	 * Executes the event immediately. This should only be called by the AsyncService.
	 */
	public void execute();
	
	/**
	 * Post the event to the EventLoop queue.  
	 */
	public void fire();
	
	/**
	 * Returns a separate copy of this AsyncTask.
	 * @return the new AsyncTask
	 */
	public AsyncTask copy() throws CloneNotSupportedException;
	
	/**
	 * Attaches a Timer to the AsyncTask to schedule the AsyncTask.
	 * @param t AsyncTimer to attach.
	 * @see AsyncTimer
	 */
	public void attachTimer(AsyncTimer t);
	
	/**
	 * Checks if a Timer exist. 
	 * @return True if the Timer exists. 
	 */
	public boolean hasTimer();
	
	/**
	 * Get the attached Timer. 
	 * @return Returns the AsyncTimer.
	 */
	public AsyncTimer getTimer();
	
	/**
	 * DO NOT USE THIS METHOD. FOR INTERNAL USE ONLY
	 * @param <K> Argument Type
	 * @param arg Argument to set
	 */
	public <K> void __set__arg_(K arg);
	
	/**
	 * DO NOT USE THIS METHOD. FOR INTERNAL USE ONLY
	 * @param <L> Argument Type
	 * @param arg Argument to set
	 */
	public <L> void __set__sec__arg_(L arg);
	
	/**
	 * Returns the generic ListenableFuture 
	 * @return Returns the ListenableFuture Object
	 * @param <T> the Type of Resource that will be available in the Future
	 * @see ListenableFuture
	 */
	public <T> ListenableFuture<T> getFuture();

	public <T> LazyAllocatedListenableFuture<T> getFutureLazy();
	
	public int hashCode();
	
}
