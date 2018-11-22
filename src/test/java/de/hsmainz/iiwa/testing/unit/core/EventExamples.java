package de.hsmainz.iiwa.testing.unit.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import de.hsmainz.iiwa.core.events.*;
import org.junit.Test;

import de.hsmainz.iiwa.core.events.AsyncService;
import de.hsmainz.iiwa.core.future.ListenableFuture;
import de.hsmainz.iiwa.core.utils.AsyncUpdater;

public class EventExamples {
	private boolean success = false;
	
	@Test
	public void basicPostToLoop() throws InterruptedException
	{
		AsyncService.init();
		
		success = false;
		
		Event e = Events.makeEvent(() -> success = true);
		
		AsyncService.post(e);
		AsyncService.run();
		AsyncService.exit();

		assertTrue(success);

		System.out.println("basic post ran");
		
	}
	
	private int event_counter = 0;
	
	@Test
	public void eventTypes() throws InterruptedException
	{
		AsyncService.init();
		
		RunnableEvent runnableEvent = new RunnableEvent(() -> event_counter++);
		
		runnableEvent.fire();
		
		ConsumerEvent<Integer> consumerEvent = new ConsumerEvent<Integer>((i) -> event_counter+=i);
		
		consumerEvent.fire(1);
		
		SupplierEvent<Integer> supplierEvent = new SupplierEvent<Integer>(() -> { event_counter++; return 0; });
		
		supplierEvent.fire();
		
		FunctionEvent<Integer, Integer> functionEvent = new FunctionEvent<Integer, Integer>((x) -> { event_counter+=x ; return 0; });
		
		functionEvent.fire(1);
		
		BiConsumerEvent<Integer, Integer> biConsumerEvent = new BiConsumerEvent<Integer, Integer>((j,k) -> event_counter+=j-k);
		
		biConsumerEvent.fire(2, 1);
		
		BiFunctionEvent<Integer,Integer,Integer> biFunctionEvent = new BiFunctionEvent<Integer,Integer,Integer>((y, z) -> { event_counter+= y - z; return 6; });
		
		biFunctionEvent.fire(2, 1);
		
		AsyncService.run();

		AsyncService.exit();
		
		assertEquals(event_counter, 6);
		
	}
	
	private int listener_check = 0;
	private int listener_check_2 = 0;
	
	@Test
	public void listeners() throws InterruptedException
	
	{
		AsyncService.init();
		
		SupplierEvent<Integer> sup = new SupplierEvent<Integer>(() -> { return 5; });
		
		sup.getFuture().addNextListener((i) -> { listener_check = i; return i+1; } ).addListener((t) -> listener_check_2 = t);
		
		sup.fire();
		
		AsyncService.run();
		AsyncService.exit();
		
		assertTrue( listener_check == 5 && listener_check_2 == 6 );
		
	}
	
	private boolean done_event = false;
	private boolean done_listener = false;
	
	@Test
	public void schedule_test() throws InterruptedException
	{
		AsyncService.init();
		
		RunnableEvent runnable = new RunnableEvent(() ->  done_event = true);
		
		runnable.getFuture().addListener(() -> done_listener = true);
		
		AsyncService.schedule(runnable, 50);

		Thread.sleep(100);
		
		AsyncService.run();
		AsyncService.exit();
		
		assertTrue(done_event);
		assertTrue(done_listener);
	}

	private int exec_count = 0;

	@Test
	public void schedule_many_test()
	{
		AsyncService.init();

		for(int i = 0; i < 20; i++){

			int wait_time = (int) ( Math.random() * 1000 );

			RunnableEvent runnable = new RunnableEvent(() -> {
				exec_count++;
			});

			AsyncService.schedule(runnable, wait_time);

		}

		AsyncService.run();
		AsyncService.exit();

		assertEquals(20, exec_count);
	}
	
	private ListenableFuture<Object> repeating_future;
	private int repeat_counter = 0;
	
	@Test
	public void interval_test() throws InterruptedException
	{
		AsyncService.init();
		
		RunnableEvent runnable = new RunnableEvent(() -> repeat_counter++);
		
		repeating_future  = AsyncService.scheduleInterval(runnable, 100);
		
		AsyncService.schedule(new Timeout(repeating_future), 1000);
		
		AsyncService.run();
		AsyncService.exit();
		
		assertTrue(repeat_counter > 1);
	}
	
	@Test
	public void asyncupdater_test() throws InterruptedException
	{
		class TestUpdater extends AsyncUpdater<Integer> 
		{
			private boolean was_updated;
			
			@Override
			public void handleUpdate(Integer input) {
				if(input == 4) {
					was_updated = true;
				}
			}
			
			private boolean success(){
				return was_updated;
			}
			
		}
		
		AsyncService.init();
		
		TestUpdater test_updater = new TestUpdater();
		
		test_updater.triggerUpdate(4);
		
		AsyncService.run();
		AsyncService.exit();
		
		assertTrue(test_updater.success());
		
	}

	@Test
	public void postMany() {

		AsyncService.init();

		long start_time_post = System.nanoTime();
		for(int i = 0; i < 1000000; i++){
			AsyncService.post(() -> { });
		}
		long end_time_post = System.nanoTime();

		System.out.println("posting took: " + (end_time_post - start_time_post) / 1000000 + " ms" );

		long start_time = System.nanoTime();
		AsyncService.run();
		long end_time = System.nanoTime();

		System.out.println("exec took: " + (end_time - start_time) / 1000000 + " ms" );

		AsyncService.exit();

	}
	@Test
	public void postMany_andMore() {

		AsyncService.init();

		class Poster implements Runnable {

			@Override
			public void run() {
				for(int i = 0; i < 1000; i++) {
					AsyncService.post(() -> {});
				}
			}
		}

		long start_time_post = System.nanoTime();

		for(int z = 0; z < 1000; z++) {
			AsyncService.post(new Poster());
		}

		long end_time_post = System.nanoTime();

		System.out.println("posting took: " + (end_time_post - start_time_post) / 1000000 + " ms" );

		long start_time = System.nanoTime();
		AsyncService.run();
		long end_time = System.nanoTime();

		System.out.println("exec took: " + (end_time - start_time) / 1000000 + " ms" );

		AsyncService.exit();


	}
}