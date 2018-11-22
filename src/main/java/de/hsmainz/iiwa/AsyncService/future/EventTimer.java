package de.hsmainz.iiwa.AsyncService.future;

import java.util.TimerTask;
import java.util.UUID;

import de.hsmainz.iiwa.AsyncService.events.AsyncService;
import de.hsmainz.iiwa.AsyncService.events.Event;

public class EventTimer extends TimerTask
{
	private Event eventToRun;
	private boolean repeat;
	
	private static void debug(String g) {
		//System.out.println("TE: " + g);
	}
	
	private UUID uuid;
	
	public EventTimer(Event event, boolean rep)
	{
		uuid = UUID.randomUUID();
		eventToRun = event;
		repeat = rep;
		event.attachTimer(this);
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

		AsyncService.post(eventToRun);
		debug("Event posted to Loop");

	}
}
