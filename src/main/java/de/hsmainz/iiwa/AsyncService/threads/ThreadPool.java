package de.hsmainz.iiwa.AsyncService.threads;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Deprecated
public class ThreadPool {


    static ArrayList<Executable> init_list;

    /**
     * This map contains all currently queued or executed jobs
     */
    static final ConcurrentHashMap<Integer, ThreadPoolHandle> thread_pool_map =
                new ConcurrentHashMap<Integer, ThreadPoolHandle>(8);

    /**
     * The central job queue
     */
    static final LinkedBlockingDeque<Runnable> job_queue = new LinkedBlockingDeque<>();

    /**
     * The executor that is working at the AsyncService
     */
    static ThreadPoolExecutor executor;


    /**
     * keeps track of how many jobs have been created by now. (Donald Trump likes that)
     */
    private static long total_num_jobs;


    /**
     * Get the amount of currently active Threads
     * @return the number of active Threads
     */
    public static int getRunningThreads(){
        return executor.getActiveCount();
    }

    /**
     * Determine if The Thread pool has any work to do
     * @return true if there are any jobs queued or executed, false otherwise
     */
    public static boolean hasRunningThreads(){
        // System.out.println("checking for size. map: " + thread_pool_map.size() + " queue: " + job_queue.size());
        return !thread_pool_map.isEmpty() || !job_queue.isEmpty();
    }

    /**
     * This method will be called when a new ThreadPoolHandle is created
     * @return the total number of Jobs in this program
     */
    static synchronized long registerNewThread() {
        total_num_jobs++;
        return total_num_jobs;
    }

    /**
     * Submit a new Job to the ThreadPool
     * @param job the job to submit
     */
    static void submit(Executable job){
        thread_pool_map.put(job.getHandle().getId(), job.getHandle());
        executor.submit(job.getRunnable());
    }

    /**
     * Check if a job is running in the ThreadPool
     * @param handle to the ThreadPoolJob
     * @return true if job is beeing executed, false otherwise
     */
    static boolean isRunningThread(ThreadPoolHandle handle) {
        return thread_pool_map.get(handle.getId()) != null;
    }

    /**
     * Queue a Job for later execution. This may be called before the ThreadPool Executor is running
     * @param job the job to submit
     */
    static void postJob(Executable job) {
        if(init_list != null)
            init_list.add(job);
        else {
            init_list = new ArrayList<>();
            init_list.add(job);
        }
    }

    public static void shutdown() {
        executor.shutdown();
        try {
            executor.awaitTermination(5000, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            System.out.println("--- forced shutdown of threadpool ---");
        }

        executor = null;

    }

    /**
     * Create an Executable from a runnable. Start the Executable with Executable.start().
     * @param runnable the runnable to run inside the executable
     * @return the Executable
     * @see Executable::start();
     */
    public static Executable fromRunnable(Runnable runnable){
        return new ThreadPoolJobLite(runnable);
    }

    /**
     * Start the ThreadPool Executor. This must be called before submitting any Jobs to the ThreadPool
     */
    public static void startPool() {
        executor = new ThreadPoolExecutor(8, 8, 10000, TimeUnit.SECONDS, job_queue);
        executor.allowCoreThreadTimeOut(false);

        if(init_list != null) {
            for(Executable job : init_list) {
                submit(job);
            }
        }
    }

    public static void startPool(int poolSize) {
        executor = new ThreadPoolExecutor(poolSize, poolSize, 10000, TimeUnit.SECONDS, job_queue);
        executor.allowCoreThreadTimeOut(false);

        if(init_list != null) {
            for(Executable job : init_list) {
                submit(job);
            }
        }
    }
}
