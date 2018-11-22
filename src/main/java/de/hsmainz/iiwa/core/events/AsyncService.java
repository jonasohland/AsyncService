package de.hsmainz.iiwa.core.events;

import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicBoolean;

import de.hsmainz.iiwa.core.functional.*;
import de.hsmainz.iiwa.core.future.ListenableFuture;
import de.hsmainz.iiwa.core.future.EventTimer;
import de.hsmainz.iiwa.core.threads.ThreadPool;


/**
 * <p>
 * The AsyncService is the central executor for any asynchronous operation in the program. Asynchronous operations can be
 * queued by calling the post() method with a lambda or an Event. The async service will execute the given operation some
 * time in the future inside the AsyncService.run() method.
 *</p><p>
 * The AsyncService.run() method must be used to perform the async operations and should be used to execute almost all parts of the program.
 * Its perfectly legal and good practice to trigger async operations inside async operations.
 *</p>
 * <pre>{@code
 *     AsyncService.post(() -> {
 *
 *     		System.out.println("triggering operation");
 *
 *     		AsyncService.post(() -> {
 *     			System.out.println("operation triggered");
 *     		});
 *     })
 * }</pre>
 */
public class AsyncService {

	/**
	 * The Event Queue
	 */
	public static LinkedBlockingDeque<Event> queue = new LinkedBlockingDeque<Event>();
	
	/**
	 * The Map where the active timers are stored
	 */
	public static ConcurrentHashMap<Integer, EventTimer> timer_map = new ConcurrentHashMap<Integer, EventTimer>(5);
	
	public static Timer coreTimer;
	
	private static boolean exit = false;

	private static AtomicBoolean busy = new AtomicBoolean();

	private static volatile boolean running;
	
	private static void debug(String g)
	{
		// System.out.println("CEL: " + g);
	}

	/**
	 * Checks if there is something to do.
	 */
	private static void exitCheck()
	{
		debug("Performing exit check...");
		
		debug("Timer Map isEmpty(): " + timer_map.isEmpty());
		debug("queue.peek() == null: " + (queue.peek() == null));
		debug("ThreadPool.hasRunningThreads: " + ThreadPool.hasRunningThreads());
		
		if(queue.peek() == null && timer_map.isEmpty() && !ThreadPool.hasRunningThreads()) {
			debug("Setting exit flag...");
			exit = true;
		}
	}
	
	/**
	 * Reset and initialize the timer and the queue
	 */
	public static void init() {
		busy.set(true);
		coreTimer = new Timer();
		queue.clear();
	}
	
	/**
	 * clean up the eventLoop timer thread. This will not clean the queue.
	 */
	public static void exit() {

		ThreadPool.shutdown();

		coreTimer.cancel();
		timer_map.clear();
	}
	
	/**
	 * Run the Event Loop.
	 * This function will block until the event Loop exits (no more active Timers/ThreadPool-threads and empty event queue)
	 */
	public static void run()
	{
		exit = false;

		ThreadPool.startPool();

		exitCheck();

		running = true;

		while(!exit) {

			busy.set(false);

			Event[] nextEvents = null;
			Event nextEvent = null;
			
			try {

				if(queue.size() <= 2){



					nextEvent = queue.take();

					busy.set(true);

					nextEvent.execute();



				} else {

					Event[] e_arr = new Event[0];

					nextEvents = queue.toArray(e_arr);

					queue.clear();

					busy.set(true);

					for (Event e : nextEvents) {



						e.execute();

					}

				}

			} catch(InterruptedException e) {

				System.out.println("Event Loop was interrupted");

				exit();

				break;

			}

			exitCheck();
		}

		running = false;
		busy.set(false);

		debug("Exit Event Loop... ");
	}

	/**
	 * Checks if the service is working inside the run() method.
	 * @return
	 */
	public static boolean isRunning() {
		return running;
	}

	public static boolean isWaiting() {
		return !busy.get();
	}
	
	/**
	 * Execute a Runnable asynchronously. 
	 * @param command The Runnable to execute. 
	 */
	public void execute(Runnable command) {
		post(Events.makeEvent(command));
	}
	
	/**
	 * Call a Supplier asynchronously.
	 * @param sup a Supplier event.
	 * @param <T> The Type of resource that will be available in the Future
	 * @return The ListenableFuture object.
	 */
	public <T> ListenableFuture<T> callAsync(Supplier<T> sup) {
		SupplierEvent<T> e = new SupplierEvent<T>(sup);
		AsyncService.post(e);
		return e.getFuture();
	}
	
	/**
	 * Post event to the EventQueue.
	 * @param newEvent The Event to add.
	 */
	public static void post(Event newEvent)
	{
		queue.add(newEvent);
	}

	
	/**
	 * Place an Event at the beginning if the EventQueue.
	 * @param newEvent The Event to add.
	 */
	public static void postNext(Event newEvent)
	{
		queue.push(newEvent);
	}

	/**
	 * Generate a RunnableEvent and post it to the Queue
	 * @param runnable the runnable to perform inside the EventLoop
	 * @see RunnableEvent
	 */
	public static void post(Runnable runnable) {
		Event r_event = Events.makeEvent(runnable);
		post(r_event);
	}

	/**
	 * Generate a SupplierEvent and post it to the Queue
	 * @param supplier the Supplier to perform inside the EventLoop
	 * @see SupplierEvent
	 * @param <T> supplier return type
	 */
	public static <T> void post(Supplier<T> supplier) {
		Event r_event = Events.makeEvent(supplier);
		post(r_event);
	}

	/**
	 * Generate a ConsumerEvent and post it to the Queue
	 * @param consumer the Consumer to perform inside the EventLoop
	 * @see SupplierEvent
	 */
	public static <T> void post(T input, Consumer<T> consumer) {
		Event r_event = Events.makeEvent(input, consumer);
		post(r_event);
	}


	/**
	 * Generate a ConsumerEvent and post it to the Queue
	 * @param biconsumer the BiConsumer to perform inside the EventLoop
	 * @see SupplierEvent
	 */
	public static <T,U> void post(T input1, U input2, BiConsumer<T,U> biconsumer) {
		Event r_event = Events.makeEvent(input1, input2, biconsumer);
		post(r_event);
	}

	/**
	 * Generate a FunctionEvent and post it to the Queue
	 * @param function the Function to perform inside the EventLoop
	 * @param <T> function input type
	 * @param <U> function return type
	 * @see FunctionEvent
	 */
	public static <T, U> void post(T input, Function<T,U> function) {
		Event f_event = Events.makeEvent(input, function);
		post(f_event);
	}

	/**
	 * Generate a BiFunctionEvent and post it to the Queue
	 * @param bifunction the BiFunction to perform inside the EventLoop
	 * @param <T> function input type
	 * @param <U> function input type
	 * @param <R> function return type
	 * @see BiFunctionEvent
	 */
	public static <T,U,R> void post(T input1, U input2, BiFunction<T, U, R> bifunction) {
		Event bf_event = Events.makeEvent(input1, input2, bifunction);
		post(bf_event);
	}
	
	/**
	 * Schedule an Event for later execution with a given delay. 
	 * @param newEvent The Event to add.
	 * @param delay delay time. 
	 * @param <T> The return type of the Event if it returns.
	 * @return The ListenableFuture object associated with the Event.
	 */
	public static <T> ListenableFuture<T> schedule(Event newEvent, long delay)
	{
		EventTimer tm_e = new EventTimer(newEvent, false);
		
		timer_map.put(tm_e.getId(), tm_e);
		
		coreTimer.schedule(tm_e, delay);

		return newEvent.getFuture();
	}
	
	/**
	 * Schedule an Event for repeated execution with a given interval. 
	 * @param newEvent The Event to add. 
	 * @param interval Interval time. 
	 * @param <T> The return type of the Event if it returns.
	 * @return The ListenabelFuture object. 
	 */
	public static <T> ListenableFuture<T> scheduleInterval(Event newEvent, long interval)
	{
		EventTimer tm_e = new EventTimer(newEvent, true);
		timer_map.put(tm_e.getId(), tm_e);
		
		coreTimer.schedule(tm_e, 0, interval);
		
		return newEvent.getFuture();
	}



	
	/**
	 * set the exit flag to exit the EventLoop after the next Iteration
	 * @param __exit should exit
	 */
	public static synchronized void setExit(boolean __exit) {
		exit = __exit;
	}
}
