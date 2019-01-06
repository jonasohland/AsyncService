package de.hsmainz.iiwa.AsyncService.executor;

import de.hsmainz.iiwa.AsyncService.functional.Consumer;
import de.hsmainz.iiwa.AsyncService.functional.Function;
import de.hsmainz.iiwa.AsyncService.functional.Supplier;

import java.util.concurrent.LinkedBlockingQueue;

/**
 *  
 * @author jonas.ohland
 *
 * @param <T> Type of Object which will be available in the future.
 */
public class ListenableFuture<T> extends ListenableBase<T> {
	
	private AsyncTask tsk;
	private T result;

	ListenableFuture(){}

	ListenableFuture(AsyncTask task){
		tsk = task;
	}

	public AsyncTask task(){
		return tsk;
	}

	public <R> ListenableFuture<R> addListenerAnd(Function<T, R> function) {
		AsyncFunction<T, R> async_func = Async.makeAsync(function);
		get_queue().add(async_func);
		return async_func.future();
	}

	public <R> ListenableFuture<R> addListenerAnd(ExecutionContext ctx, Function<T, R> function) {
		AsyncFunction<T, R> async_func = Async.makeAsync(ctx, function);
		get_queue().add(async_func);
		return async_func.future();
	}

	/**
	 * Set this Futures Resource and fire all listeners associated to this Future.
	 * @param value the result object which is fired.
	 */
	public void fire(T value) {

		if(!get_queue().isEmpty()) {

			if(tsk != null && tsk.context() != null) {

				for (AsyncTask element : get_queue()) {

					if(element.context() == null){
						element.bindContext(tsk.context());
					}

					element.__set__arg_(value);

					element.context().post(element);

				}
			} else {
				execute_listeners(value);
			}
		}
	}

	/**
	 * Set this Futures Resource and fire all listeners associated to this Future.
	 */
	public void fire() {

		if(!get_queue().isEmpty()) {

			if(Async.getContext(tsk) != null) {

				for (AsyncTask element : get_queue()) {

					if(element.context() == null){
						element.bindContext(tsk.context());
					}

					element.context().post(element);

				}
			} else {
				execute_listeners(null);
			}
		}
	}

	private void execute_listeners(T value){
		for (AsyncTask element : get_queue()) {
			element.__set__arg_(value);
			element.fire();
		}
	}

	@Override
	protected <R> Listenable<R> get_next_listenable(AsyncTask tsk) {
		return tsk.future();
	}
}
