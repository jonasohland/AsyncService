package de.hsmainz.iiwa.core.utils;

import de.hsmainz.iiwa.core.events.AsyncService;
import de.hsmainz.iiwa.core.events.Events;
import de.hsmainz.iiwa.core.functional.Consumer;

/**
 * Classes that need to be updated by other Classes that dont have time to execute longer update-Methods, can derive from this
 * @author Jonas Ohland
 * @param <T> the Type of data to send with the update
 */
public abstract class AsyncUpdater<T> {
	
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
		AsyncService.post(Events.makeEvent(input, new Consumer<T>() {
			@Override
			public void accept(T __input) {
				AsyncUpdater.this.handleUpdate(__input);
			}
		}));
	}
}
