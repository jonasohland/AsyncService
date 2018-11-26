package de.hsmainz.iiwa.AsyncService.events;

import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicBoolean;

import de.hsmainz.iiwa.AsyncService.functional.*;
import de.hsmainz.iiwa.AsyncService.threads.ThreadPool;


/**
 * <p>
 * The AsyncService is the central executor for any asynchronous operation in the program. Asynchronous operations can be
 * queued by calling the post() method with a lambda or an AsyncTask. The async service will execute the given operation some
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
	 * The AsyncTask Queue
	 */
	public static LinkedBlockingDeque<AsyncTask> queue = new LinkedBlockingDeque<AsyncTask>();
	
	/**
	 * The Map where the active timers are stored
	 */
	static ConcurrentHashMap<Integer, AsyncTimer> timer_map = new ConcurrentHashMap<Integer, AsyncTimer>(5);
	
	static Timer coreTimer;
	
	private static boolean exit = false;

	private static AtomicBoolean busy = new AtomicBoolean();

	static volatile boolean running;

	private static Thread service_thread;
	
	static void debug(String g)
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
	 * Run the AsyncTask Loop.
	 * This function will block until the event Loop exits (no more active Timers/ThreadPool-threads and empty event queue)
	 */
	public static void run()
	{
		exit = false;

		ThreadPool.startPool();

		service_thread = Thread.currentThread();

		exitCheck();

		running = true;

		while(!exit) {

			busy.set(false);

			AsyncTask[] nextAsyncTasks = null;
			AsyncTask nextAsyncTask = null;
			
			try {

				if(queue.size() <= 2){



					nextAsyncTask = queue.take();

					busy.set(true);

					nextAsyncTask.execute();



				} else {

					AsyncTask[] e_arr = new AsyncTask[0];

					nextAsyncTasks = queue.toArray(e_arr);

					queue.clear();

					busy.set(true);

					for (AsyncTask e : nextAsyncTasks) {



						e.execute();

					}

				}

				if(Thread.interrupted()){
					System.out.println("cleared interrupt state");
				}

			} catch(InterruptedException e) {

				System.out.println(" ---  event loop interrupted --- ");

			}

			exitCheck();
		}

		running = false;
		busy.set(false);

		debug("Exit AsyncTask Loop... ");
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
		post(Async.makeAsync(command));
	}
	
	/**
	 * Call a Supplier asynchronously.
	 * @param sup a Supplier event.
	 * @param <T> The Type of resource that will be available in the Future
	 * @return The ListenableFuture object.
	 */
	public <T> ListenableFuture<T> callAsync(Supplier<T> sup) {
		AsyncSupplier<T> e = new AsyncSupplier<T>(sup);
		AsyncService.post(e);
		return e.getFuture();
	}
	
	/**
	 * Post event to the EventQueue.
	 * @param newAsyncTask The AsyncTask to add.
	 */
	public static void post(AsyncTask newAsyncTask)
	{
		queue.add(newAsyncTask);
	}

	
	/**
	 * Place an AsyncTask at the beginning if the EventQueue.
	 * @param newAsyncTask The AsyncTask to add.
	 */
	public static void postNext(AsyncTask newAsyncTask)
	{
		queue.push(newAsyncTask);
	}

	/**
	 * Generate a AsyncRunnable and post it to the Queue
	 * @param runnable the runnable to perform inside the EventLoop
	 * @see AsyncRunnable
	 */
	public static void post(Runnable runnable) {
		AsyncTask r_asyncTask = Async.makeAsync(runnable);
		post(r_asyncTask);
	}

	/**
	 * Generate a AsyncSupplier and post it to the Queue
	 * @param supplier the Supplier to perform inside the EventLoop
	 * @see AsyncSupplier
	 * @param <T> supplier return type
	 */
	public static <T> void post(Supplier<T> supplier) {
		AsyncTask r_asyncTask = Async.makeAsync(supplier);
		post(r_asyncTask);
	}

	/**
	 * Generate a AsyncConsumer and post it to the Queue
	 * @param consumer the Consumer to perform inside the EventLoop
	 * @see AsyncSupplier
	 */
	public static <T> void post(T input, Consumer<T> consumer) {
		AsyncTask r_asyncTask = Async.makeAsync(input, consumer);
		post(r_asyncTask);
	}


	/**
	 * Generate a AsyncConsumer and post it to the Queue
	 * @param biconsumer the BiConsumer to perform inside the EventLoop
	 * @see AsyncSupplier
	 */
	public static <T,U> void post(T input1, U input2, BiConsumer<T,U> biconsumer) {
		AsyncTask r_asyncTask = Async.makeAsync(input1, input2, biconsumer);
		post(r_asyncTask);
	}

	/**
	 * Generate a AsyncFunction and post it to the Queue
	 * @param function the Function to perform inside the EventLoop
	 * @param <T> function input type
	 * @param <U> function return type
	 * @see AsyncFunction
	 */
	public static <T, U> void post(T input, Function<T,U> function) {
		AsyncTask f_asyncTask = Async.makeAsync(input, function);
		post(f_asyncTask);
	}

	/**
	 * Generate a AsyncBiFunction and post it to the Queue
	 * @param bifunction the BiFunction to perform inside the EventLoop
	 * @param <T> function input type
	 * @param <U> function input type
	 * @param <R> function return type
	 * @see AsyncBiFunction
	 */
	public static <T,U,R> void post(T input1, U input2, BiFunction<T, U, R> bifunction) {
		AsyncTask bf_asyncTask = Async.makeAsync(input1, input2, bifunction);
		post(bf_asyncTask);
	}
	
	/**
	 * Schedule an AsyncTask for later execution with a given delay.
	 * @param newAsyncTask The AsyncTask to add.
	 * @param delay delay time. 
	 * @param <T> The return type of the AsyncTask if it returns.
	 * @return The ListenableFuture object associated with the AsyncTask.
	 */
	public static <T> LazyAllocatedListenableFuture<T> schedule(AsyncTask newAsyncTask, long delay)
	{
		AsyncTimer tm_e = new AsyncTimer(newAsyncTask, false);
		
		timer_map.put(tm_e.getId(), tm_e);
		
		coreTimer.schedule(tm_e, delay);

		return newAsyncTask.getFutureLazy();
	}
	
	/**
	 * Schedule an AsyncTask for repeated execution with a given interval.
	 * @param newAsyncTask The AsyncTask to add.
	 * @param interval Interval time. 
	 * @param <T> The return type of the AsyncTask if it returns.
	 * @return The ListenabelFuture object. 
	 */
	public static <T> LazyAllocatedListenableFuture<T> scheduleInterval(AsyncTask newAsyncTask, long interval)
	{
		AsyncTimer tm_e = new AsyncTimer(newAsyncTask, true);
		timer_map.put(tm_e.getId(), tm_e);
		
		coreTimer.schedule(tm_e, 0, interval);
		
		return newAsyncTask.getFutureLazy();
	}



	
	/**
	 * set the exit flag to exit the EventLoop after the next Iteration
	 * @param __exit should exit
	 */
	public static synchronized void __set__exit(boolean __exit) {
		exit = __exit;
	}

	private static AsyncRunnable dummyEvent = new AsyncRunnable(() -> {});

	public static void iterate_loop_if_waiting() {
		/* if(isWaiting()){
			post(dummyEvent);
		} */

		service_thread.interrupt();

	}
}
