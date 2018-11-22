package de.hsmainz.iiwa.testing.unit.core;

import de.hsmainz.iiwa.AsyncService.events.*;
import org.junit.Test;

import de.hsmainz.iiwa.AsyncService.events.AsyncService;
import de.hsmainz.iiwa.AsyncService.future.DualListenableFuture;
import de.hsmainz.iiwa.AsyncService.future.ListenableFuture;

import static org.junit.Assert.*;

public class TimersTest {
	
	private int success_count = 0;
	
	@Test
	public void perform() {
		
		AsyncService.init();
		
		RunnableEvent e = new RunnableEvent( () -> success_count++);
		
		//schedule for periodical execution
		AsyncService.scheduleInterval(e, 50);
		
		//remove scheduled event in 60 ms (after 2 iterations)
		AsyncService.schedule(e.getFuture().getTimeout(), 200);
		
		AsyncService.run();

		assertTrue( success_count > 1);
	}
	
	private DualListenableFuture<String, Integer> dual_future = new DualListenableFuture<>();
	
	private boolean test_failed = false;
	
	@Test
	public void perform_dual_future() throws InterruptedException {
		
		AsyncService.init();
		
		dual_future.addListener((k, x) -> test_failed = true);
		
		dual_future.setTimeout(5);


		
		AsyncService.post(Events.makeEvent(() -> {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}));

		AsyncService.post(Events.makeEvent(() -> {
			dual_future.fireListeners("k", 18);
		}));
		

		
		AsyncService.run();
		
		AsyncService.exit();
		
		assertFalse(test_failed);
		
	} 
	
	private ListenableFuture<Integer> dual_cancel_future;
	
	private boolean success = true;
	
	@Test
	public void perform_timed_cancel()
	{
		AsyncService.init();
		
		BiFunctionEvent<Integer, Integer, Integer> event = new BiFunctionEvent<Integer, Integer, Integer>((g,h) -> { 
			success = false;
			return g; 
		});
		
		dual_cancel_future = event.getFuture();
		
		AsyncService.schedule(event, 100000);
		
		AsyncService.schedule(Events.makeEvent(() -> {
			dual_cancel_future.cancel();
			System.out.println("it happened");
		}), 20);
		
		AsyncService.run();
		
		assertTrue(success);
		
	}

	private boolean fail_1 = false;

	@Test
	public void automatedTimeoutEvent() {

		AsyncService.init();
		

		Event wait = Events.makeEvent(() -> {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});

		Event event = Events.makeEvent(() -> { fail_1 = true; });
		

		AsyncService.post(wait);

		AsyncService.schedule(event, 200);
		event.getFuture().setTimeout(100);
		
		AsyncService.run();
		AsyncService.exit();

		assertTrue(!fail_1);
		
	}
}
