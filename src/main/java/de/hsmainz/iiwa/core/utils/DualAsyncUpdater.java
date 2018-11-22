package de.hsmainz.iiwa.core.utils;

import de.hsmainz.iiwa.core.events.AsyncService;
import de.hsmainz.iiwa.core.events.Events;
import de.hsmainz.iiwa.core.functional.BiConsumer;

/**
 * Classes that need to be updated by other Classes that don't have time to execute longer update-Methods, can derive from this
 * @author Jonas Ohland
 *
 * @param <T> Type of the first input to update with.
 * @param <U> Type of the first input to update with.
 */
public abstract class DualAsyncUpdater<T, U> {
	
	
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
	public void triggerUpdate(T __input0, U __input1)
	{
		AsyncService.post(Events.makeEvent(__input0, __input1, new BiConsumer<T, U>() {
			public void accept(T __input0, U __input1) {
				DualAsyncUpdater.this.handleUpdate(__input0, __input1);
			}
		}));
	}
}
