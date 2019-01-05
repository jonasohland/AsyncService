package de.hsmainz.iiwa.AsyncService.executor;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class ExecutorWorkGuard {

    private ExecutionContext ctx;
    private final AtomicBoolean work = new AtomicBoolean();


    public ExecutorWorkGuard(ExecutionContext _ctx){

        Objects.requireNonNull(_ctx);

        ctx = _ctx;

        work.set(true);

        ctx.registerWork(this);
    }



    public synchronized boolean hasWork(){
        return work.get();
    }

    public synchronized void reset(){
        ctx.removeWork(this);
        work.set(false);
    }

    public ExecutionContext context(){
        return ctx;
    }

}
