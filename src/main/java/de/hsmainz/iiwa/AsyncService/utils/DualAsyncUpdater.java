package de.hsmainz.iiwa.AsyncService.utils;

import de.hsmainz.iiwa.AsyncService.executor.Async;
import de.hsmainz.iiwa.AsyncService.executor.ExecutionLayer;
import de.hsmainz.iiwa.AsyncService.functional.BiConsumer;

/**
 * Classes that need to be updated by other Classes that don't have time to execute longer update-Methods, can derive from this
 * @author Jonas Ohland
 *
 * @param <T> Type of the first input to update with.
 * @param <U> Type of the first input to update with.
 */
public abstract class DualAsyncUpdater<T, U> {

	private ExecutionLayer layer;

	protected DualAsyncUpdater(ExecutionLayer _layer){
		layer = _layer;
	}
	
	/**
	 * implemented to handle the incoming update
	 * @param __input0 first input to update with
	 * @param __input1 second input to update with
	 */
	public abstract void handleUpdate(T __input0, U __input1);
	
	/**
	 * Called by the updating class to Trigger the update
	 * @param __input0 first input to update with
	 * @param __input1 second input to update with
	 */
	public void triggerUpdate(T __input0, U __input1) {
		layer.post(Async.makeAsync(() -> handleUpdate(__input0, __input1)));
	}
}