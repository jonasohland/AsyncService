package de.hsmainz.iiwa.AsyncService.future;

import de.hsmainz.iiwa.AsyncService.async.Async;
import de.hsmainz.iiwa.AsyncService.async.AsyncTask;
import de.hsmainz.iiwa.AsyncService.listenable.Listenable;
import de.hsmainz.iiwa.AsyncService.listenable.ListenableBase;

/**
 *  
 * @author jonas.ohland
 *
 * @param <T> Type of Object which will be available in the future.
 */
public class ListenableFuture<T> extends ListenableBase<T> {
	
	private AsyncTask tsk;
	private T result;

	public ListenableFuture(){}

	public ListenableFuture(AsyncTask task){
		tsk = task;
	}

	public AsyncTask task(){
		return tsk;
	}

	/**
	 * Set this Futures Resource and fire all listeners associated to this Future.
	 * @param value the result object which is fired.
	 */
	public void fire(T value) {

		if(!get_queue().isEmpty()) {

			if(tsk != null && tsk.layer() != null) {

				for (AsyncTask element : get_queue()) {

					if(element.layer() == null){
						element.bind(tsk.layer());
					}

					element.__set__arg_(value);

					element.layer().post(element);

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

			if(Async.getLayer(tsk) != null) {

				for (AsyncTask element : get_queue()) {

					if(element.layer() == null){
						element.bind(tsk.layer());
					}

					element.layer().post(element);

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
