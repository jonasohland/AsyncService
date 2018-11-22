package de.hsmainz.iiwa.testing.unit.core;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.hsmainz.iiwa.core.events.AsyncService;
import de.hsmainz.iiwa.core.events.ProfiledEvent;

public class ProfiledEventTest {
	
	private long profile_result;
	
	@Test
	public void perform(){
		
		AsyncService.init();

		ProfiledEvent<Integer> event = new ProfiledEvent<>((i) -> {
			try {
				// System.out.println("Hello!");
				Thread.sleep(i);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});
		
		event.getFuture().addListener((i) -> profile_result = i);
		
		event.fire(10);
		
		AsyncService.run();
		AsyncService.exit();

		System.out.println("result: " + profile_result / 1000000);
		
		assertTrue(profile_result > 0);
	}
}
