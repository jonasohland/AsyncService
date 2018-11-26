package de.hsmainz.iiwa.AsyncService.threads;


public interface Executable {
    public ThreadPoolHandle getHandle();
    public Runnable getRunnable();
    public void start();
    public boolean isActive();
    public boolean isFinished();
    public void join() throws InterruptedException;
}