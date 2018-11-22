package de.hsmainz.iiwa.core.future;

import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingQueue;

import de.hsmainz.iiwa.core.events.AsyncService;
import de.hsmainz.iiwa.core.events.Event;
import de.hsmainz.iiwa.core.events.Events;
import de.hsmainz.iiwa.core.threads.ThreadPoolJob;

/**
 * 
 * @author jonas.ohland
 *
 */
public class EventCancelTask extends TimerTask {

	private Event event;
	private LinkedBlockingQueue<Event> listeners;
	private ThreadPoolJob job;
	
	EventCancelTask(Event e, LinkedBlockingQueue<Event> l, ThreadPoolJob j) {
		event = e;
		listeners = l;
	}
	
	@Override
	public void run() {
		
		if(event != null) {

			//remove the timer if one was attached
			if(event.hasTimer() && AsyncService.timer_map.containsKey(event.getTimer().getId())) {
				
				AsyncService.timer_map.remove(event.getTimer().getId());
				event.getTimer().cancel();
				
			}
			
			//remove the event from the queue
			AsyncService.queue.remove(event);
			
			//this may be called while the loop is waiting for an event so we need to 
			//iterate Loop to exit 
			
			if(AsyncService.isWaiting()) {
				AsyncService.post(Events.makeEvent(() -> {}));
			}
		}

		if(listeners != null) {
			listeners.clear();
		}

		if(job != null) {
			job.signalThreadShouldExit();
		}
	}
}
