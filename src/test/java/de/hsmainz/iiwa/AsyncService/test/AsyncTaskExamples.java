package de.hsmainz.iiwa.AsyncService.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import de.hsmainz.iiwa.AsyncService.events.*;
import org.junit.Test;

import de.hsmainz.iiwa.AsyncService.events.AsyncService;
import de.hsmainz.iiwa.AsyncService.utils.AsyncUpdater;

public class AsyncTaskExamples {
	private boolean success = false;
	
	@Test
	public void basicPostToLoop() throws InterruptedException
	{
		AsyncService.init();
		
		success = false;
		
		AsyncTask e = Async.makeAsync(() -> success = true);
		
		AsyncService.post(e);
		AsyncService.run();
		AsyncService.exit();

		assertTrue(success);

		System.out.println("basic postTask ran");
		
	}
	
	private int event_counter = 0;
	
	@Test
	public void eventTypes() throws InterruptedException
	{
		AsyncService.init();
		
		AsyncRunnable asyncRunnable = new AsyncRunnable(() -> event_counter++);
		
		asyncRunnable.fire();
		
		AsyncConsumer<Integer> asyncConsumer = new AsyncConsumer<Integer>((i) -> event_counter+=i);
		
		asyncConsumer.fire(1);
		
		AsyncSupplier<Integer> asyncSupplier = new AsyncSupplier<Integer>(() -> { event_counter++; return 0; });
		
		asyncSupplier.fire();
		
		AsyncFunction<Integer, Integer> asyncFunction = new AsyncFunction<Integer, Integer>((x) -> { event_counter+=x ; return 0; });
		
		asyncFunction.fire(1);
		
		AsyncBiConsumer<Integer, Integer> asyncBiConsumer = new AsyncBiConsumer<Integer, Integer>((j, k) -> event_counter+=j-k);
		
		asyncBiConsumer.fire(2, 1);
		
		AsyncBiFunction<Integer,Integer,Integer> asyncBiFunction = new AsyncBiFunction<Integer,Integer,Integer>((y, z) -> { event_counter+= y - z; return 6; });
		
		asyncBiFunction.fire(2, 1);
		
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
		
		AsyncSupplier<Integer> sup = new AsyncSupplier<Integer>(() -> { return 5; });
		
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
		
		AsyncRunnable runnable = new AsyncRunnable(() ->  done_event = true);
		
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

			AsyncRunnable runnable = new AsyncRunnable(() -> {
				exec_count++;
			});

			AsyncService.schedule(runnable, wait_time);

		}

		AsyncService.run();
		AsyncService.exit();

		assertEquals(20, exec_count);
	}
	
	private LazyAllocatedListenableFuture<Object> repeating_future;
	private int repeat_counter = 0;
	
	@Test
	public void interval_test() throws InterruptedException
	{
		AsyncService.init();
		
		AsyncRunnable runnable = new AsyncRunnable(() -> repeat_counter++);
		
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