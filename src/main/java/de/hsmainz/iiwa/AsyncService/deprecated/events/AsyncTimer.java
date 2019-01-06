package de.hsmainz.iiwa.AsyncService.deprecated.events;

import java.util.TimerTask;
import java.util.UUID;

@Deprecated
public class AsyncTimer extends TimerTask
{
	private AsyncTask asyncTaskToRun;
	private boolean repeat;
	
	private static void debug(String g) {
		//System.out.println("TE: " + g);
	}
	
	private UUID uuid;
	
	public AsyncTimer(AsyncTask asyncTask, boolean rep)
	{
		uuid = UUID.randomUUID();
		asyncTaskToRun = asyncTask;
		repeat = rep;
		asyncTask.attachTimer(this);
	}
	
	public int getId()
	{
		return uuid.hashCode();
	}
	
	public boolean repeats()
	{
		return repeat;
	}
	
	public void repeat(boolean rep)
	{
		repeat = rep;
	}
	
	public int hashCode() {
		return uuid.hashCode();
	}
	
	@Override
	public void run() {

		if(!repeat){

			AsyncService.timer_map.remove(getId());

			debug("removed from map");

		}

		AsyncService.post(asyncTaskToRun);
		debug("AsyncTask posted to Loop");

	}
}
