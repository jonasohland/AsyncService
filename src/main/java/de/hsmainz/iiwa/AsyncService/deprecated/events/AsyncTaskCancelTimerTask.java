package de.hsmainz.iiwa.AsyncService.deprecated.events;

import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingQueue;

import de.hsmainz.iiwa.AsyncService.threads.ThreadPoolJob;

/**
 * 
 * @author jonas.ohland
 *
 */
@Deprecated
public class AsyncTaskCancelTimerTask extends TimerTask {

	private AsyncTask asyncTask;
	private LinkedBlockingQueue<AsyncTask> listeners;
	private ThreadPoolJob job;
	
	AsyncTaskCancelTimerTask(AsyncTask e, LinkedBlockingQueue<AsyncTask> l, ThreadPoolJob j) {
		asyncTask = e;
		listeners = l;
	}
	
	@Override
	public void run() {
		
		if(asyncTask != null) {

			//remove the timer if one was attached
			if(asyncTask.hasTimer() && AsyncService.timer_map.containsKey(asyncTask.getTimer().getId())) {
				
				AsyncService.timer_map.remove(asyncTask.getTimer().getId());
				asyncTask.getTimer().cancel();
				
			}
			
			//remove the asyncTask from the queue
			AsyncService.queue.remove(asyncTask);
			
			//this may be called while the loop is waiting for an asyncTask so we need to
			//iterate Loop to exit 
			
			AsyncService.iterate_loop_if_waiting();
		}

		if(listeners != null) {
			listeners.clear();
		}

		if(job != null) {
			job.signalThreadShouldExit();
		}
	}
}
