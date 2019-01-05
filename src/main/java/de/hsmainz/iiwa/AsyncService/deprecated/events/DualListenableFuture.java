package de.hsmainz.iiwa.AsyncService.deprecated.events;

import java.util.concurrent.LinkedBlockingQueue;

import de.hsmainz.iiwa.AsyncService.functional.BiConsumer;
import de.hsmainz.iiwa.AsyncService.functional.BiFunction;
import de.hsmainz.iiwa.AsyncService.functional.Consumer;
import de.hsmainz.iiwa.AsyncService.functional.Function;

/**
 * 
 * @author jonas.ohland
 * @see ListenableFuture
 * @param <T> Type of resource 1 to be available in the future
 * @param <U> Type of resource 2 to be available in the future
 */
public class DualListenableFuture<T,U> {
	private LinkedBlockingQueue<AsyncTask> listeners = new LinkedBlockingQueue<AsyncTask>();
	
	private T result1;
	private U result2;
	
	private AsyncTask asyncTask;
	
	private static void debug(String g) {
		
	}
	
	public void setAsyncTask(AsyncTask e) {
		asyncTask = e;
	}
	
	public DualListenableFuture() {
		
	}
	
	public DualListenableFuture(AsyncTask __e) {
		asyncTask = __e;
	}
	
	void setResults(T __result1, U __result2)
	{
		result1 = __result1;
		result2 = __result2;
	}
	
	public void addListener(Runnable runnable)
	{
		listeners.add(Async.makeAsync(runnable));
	}
	public void addListener(Consumer<T> consumer)
	{
		listeners.add(Async.makeAsync(result1, consumer));
	}
	public void addListener(BiConsumer<T,U> biconsumer)
	{
		listeners.add(Async.makeAsync(result1, result2, biconsumer));
	}
	public <R> ListenableFuture<R> addNextListener(Function<T,R> function)
	{
		AsyncTask e = Async.makeAsync(result1, function);
		listeners.add(e);
		return e.getFuture();
	}
	public <R> ListenableFuture<R> addNextListener(BiFunction<T,U,R> bifunction)
	{
		AsyncTask e = Async.makeAsync(result1, result2, bifunction);
		listeners.add(e);
		return e.getFuture();
	}
	public <R> ListenableFuture<R> then(Function<T,R> function)
	{
		AsyncTask e = Async.makeAsync(result1, function);
		listeners.add(e);
		return e.getFuture();
	}
	public <R> ListenableFuture<R> then(BiFunction<T,U,R> bifunction)
	{
		AsyncTask e = Async.makeAsync(result1, result2, bifunction);
		listeners.add(e);
		return e.getFuture();
	}
	public <R> ListenableFuture<R> run(Function<T,R> function)
	{
		AsyncTask e = Async.makeAsync(result1, function);
		listeners.add(e);
		return e.getFuture();
	}
	public <R> ListenableFuture<R> run(BiFunction<T,U,R> bifunction)
	{
		AsyncTask e = Async.makeAsync(result1, result2, bifunction);
		listeners.add(e);
		return e.getFuture();
	}
	public void fireListeners(T val1, U val2)
	{
		if(!listeners.isEmpty())
		{
			for(AsyncTask element : listeners) {
				
				element.__set__arg_(val1);
				element.__set__sec__arg_(val2);
				
				AsyncService.post(element);
			}
			//listeners.clear();
		}
	}


	
	/**
	 * remove the an asyncTask and its listeners from the future. This should not be called from outside the asyncTask loop.
	 * use the setTimeout method for external timeouts.
	 */
	public void cancel(){
		
		debug("Cancelling Future... ");
		
		if(asyncTask != null) {
			 
			if(asyncTask.hasTimer()) {

				AsyncService.timer_map.remove(asyncTask.getTimer().getId());

				AsyncService.queue.remove(asyncTask);

				asyncTask.getTimer().cancel();

			} else {
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
	
	public void fire(T val1, U val2)
	{
		fireListeners(val1, val2);
	}

	public void fire() { fireListeners(null, null); }
	
	public DualTimeout getTimeout(){
		return new DualTimeout(this);
	}
	
	/**
	 * set Timeout for AsyncTask. After the Timeout Period, the AsyncTask will be atomically removed from the queue
	 * @param delay Timeout time
	 * @return AsyncTaskCancelTimerTask - A TimerTask that can be canceled to cancel the Timeout
	 */
	public AsyncTaskCancelTimerTask setTimeout(long delay) {
		
		AsyncTaskCancelTimerTask ct = new AsyncTaskCancelTimerTask(asyncTask, listeners, null);
		AsyncService.coreTimer.schedule(ct, delay);
		return ct;
	}
}
