package de.hsmainz.iiwa.AsyncService.deprecated.events;

import java.util.concurrent.LinkedBlockingQueue;

import de.hsmainz.iiwa.AsyncService.functional.Consumer;
import de.hsmainz.iiwa.AsyncService.functional.Function;

/**
 *  
 * @author jonas.ohland
 *
 * @param <T> Type of Object which will be available in the future.
 */
@Deprecated
public class ListenableFuture<T>{
	
	private AsyncTask asyncTask;
	private LinkedBlockingQueue<AsyncTask> listeners = new LinkedBlockingQueue<AsyncTask>();
	private T result;
	
	/**
	 * Adds the asyncTask to the Listenable which calls all listeners.
	 * @param e Adds an asyncTask to the ListanableFuture object.
	 */
	public void setAsyncTask(AsyncTask e) {
		asyncTask = e;
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
	 * Constructor with asyncTask.
	 * @param e AsyncTask to associate with
	 */
	public ListenableFuture(AsyncTask e){
		asyncTask = e;
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
		listeners.add(Async.makeAsync(runnable));
	}
	
	/**
	 * Add a listener to the listener queue.
	 * @param consumer A consumer object to add.
	 */
	public void addListener(Consumer<T> consumer)
	{
		listeners.add(Async.makeAsync(result, consumer));
	}
	
	/**
	 * Add a listener to the listener queue.
	 * @param consumer A AsyncConsumer object to add.
	 */
	public void addListener(AsyncConsumer<T> consumer)
	{
		listeners.add(consumer);
	}
	
	/**
	 * Add a listener to the listener queue which not returns a listable future.
	 * @param runnable a runnable object to add.
	 */
	public void last(Runnable runnable)
	{
		listeners.add(Async.makeAsync(runnable));
	}
	
	/**
	 * Add a listener to the listener queue which not returns a listenable future.
	 * @param consumer A consumer object to add.
	 */
	public void last(Consumer<T> consumer)
	{
		listeners.add(Async.makeAsync(result, consumer));
	}
	
	/**
	 * Add a listener to the listener queue.
	 * @param consumer A AsyncConsumer object to add.
	 */
	public void last(AsyncConsumer<T> consumer)
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
		AsyncTask e = Async.makeAsync(result, function);
		listeners.add(e);
		return e.getFuture();
	}
	
	/**
	 * Add a chained listener to the future. 
	 * @param function A AsyncFunction object to add.
	 * @param <R> Return Type of function.
	 * @return The ListenableFuture object.
	 */
	public <R> ListenableFuture<R> addNextListener(AsyncFunction<T,R> function)
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
		AsyncTask e = Async.makeAsync(result, function);
		listeners.add(e);
		return e.getFuture();
	}
	
	/**
	 * Same as addNextListener function. 
	 * @param function A AsyncFunction object to add.
	 * @return The ListenableFuture object.
	 * @param <R> ReturnType of function
	 * @see #addNextListener(Function)	 
	 * */
	public <R> ListenableFuture<R> then(AsyncFunction<T,R> function)
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
			
			for(AsyncTask element : listeners) {

				element.__set__arg_(value);

				AsyncService.post(element);

			}
		}
	}
	
	/**
	 * Fire all listeners associated to this Future
	 */
	public void fire() {
		
		if(!listeners.isEmpty()) {
			
			for(AsyncTask element : listeners) {
				AsyncService.post(element);
			}
			//listeners.clear();
		}
	}
	
	/**
	 * Cancel the timer for this Future and remove its AsyncTask from the AsyncService.
	 */
	public void cancel(){
		
		debug("Cancelling Future... ");
		
		if(asyncTask != null) {
			 
			if(asyncTask.hasTimer()) {
				
				debug("Future has Timer " + asyncTask.getTimer().getId());
				
				if(AsyncService.timer_map.containsKey(asyncTask.getTimer().getId())){
					debug("Timer AsyncTask was found in Map");
				} else {
					debug("Timer AsyncTask was not found in Map");
				}
				
				AsyncService.timer_map.remove(asyncTask.getTimer().getId());
				
				AsyncService.queue.remove(asyncTask);
				
				debug("removing Timer... ");
				
				if(asyncTask.getTimer().cancel()) {
					debug("successful");
				} else {
					debug("failed");
				}
			}
			else {
				AsyncService.queue.remove(asyncTask);
			}
		} 
		
		if(!listeners.isEmpty()) {
			for(AsyncTask listener : listeners) {
				AsyncService.queue.remove(listener);
			}
		}

		AsyncService.iterate_loop_if_waiting();
	}
	
	/**
	 * Returns a Timeout object that can be used to cancel this Future.
	 * @return A timeout object associated to this Future. 
	 * @see AsyncService#schedule(AsyncTask, long)
	 */
	public Timeout getTimeout(){
		LazyAllocatedListenableFuture<T> lazy_f = new LazyAllocatedListenableFuture<T>();
		lazy_f.attach(this);
		return new Timeout(lazy_f);
	}
	
	/**
	 * Set a timeout Timeout for AsyncTask. After the Timeout Period, the AsyncTask will be atomically removed from the queue.
	 * @param delay Timeout time
	 * @return AsyncTaskCancelTimerTask - A TimerTask that can be canceled to cancel the Timeout
	 */
	public AsyncTaskCancelTimerTask setTimeout(long delay) {
		AsyncTaskCancelTimerTask ct = new AsyncTaskCancelTimerTask(asyncTask, listeners, null);
		AsyncService.coreTimer.schedule(ct, delay);
		return ct;
	}
}
