package de.hsmainz.iiwa.AsyncService.utils;

import de.hsmainz.iiwa.AsyncService.async.Async;
import de.hsmainz.iiwa.AsyncService.executor.layer.ExecutorLayer;

/**
 * Classes that need to be updated by other Classes that dont have time to execute longer update-Methods, can derive from this
 * @author Jonas Ohland
 * @param <T> the Type of data to send with the update
 */
public abstract class AsyncUpdater<T> {

	private ExecutorLayer layer;

	protected AsyncUpdater(ExecutorLayer _layer){
		layer = _layer;
	}
	
	/**
	 * implemented to handle the incoming update
	 * @param input The input to the update
	 * @author Jonas Ohland
	 */
	public abstract void handleUpdate(T input);
	
	
	/**
	 * Called by the updating class to Trigger the update
	 * @param input The input to the update
	 * @author Jonas Ohland
	 */
	public void triggerUpdate(T input)
	{
		layer.post(Async.makeAsync(() -> handleUpdate(input)));
	}
}
