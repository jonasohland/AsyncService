package de.hsmainz.iiwa.AsyncService.test;

import de.hsmainz.iiwa.AsyncService.events.*;
import org.junit.Test;

import de.hsmainz.iiwa.AsyncService.events.AsyncService;
import de.hsmainz.iiwa.AsyncService.events.DualListenableFuture;
import de.hsmainz.iiwa.AsyncService.events.ListenableFuture;

import static org.junit.Assert.*;

public class TimersTest {
	
	private int success_count = 0;
	
	@Test
	public void perform() {
		
		AsyncService.init();
		
		AsyncRunnable e = new AsyncRunnable( () -> success_count++);
		
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
		
		dual_future.setTimeout(1);


		
		AsyncService.post(Async.makeAsync(() -> {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}));

		AsyncService.post(Async.makeAsync(() -> {
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
		
		AsyncBiFunction<Integer, Integer, Integer> event = new AsyncBiFunction<Integer, Integer, Integer>((g, h) -> {
			success = false;
			return g; 
		});
		
		dual_cancel_future = event.getFuture();
		
		AsyncService.schedule(event, 100000);
		
		AsyncService.schedule(Async.makeAsync(() -> {
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
		

		AsyncTask wait = Async.makeAsync(() -> {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
			}
		});

		AsyncTask asyncTask = Async.makeAsync(() -> { fail_1 = true; });
		

		AsyncService.post(wait);

		AsyncService.schedule(asyncTask, 200);
		asyncTask.getFuture().setTimeout(100);
		
		AsyncService.run();
		AsyncService.exit();

		assertTrue(!fail_1);
		
	}
}
