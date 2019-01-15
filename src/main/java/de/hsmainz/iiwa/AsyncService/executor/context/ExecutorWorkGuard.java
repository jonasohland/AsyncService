package de.hsmainz.iiwa.AsyncService.executor.context;

import de.hsmainz.iiwa.AsyncService.utils.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class ExecutorWorkGuard {

    private ExecutorContext ctx;
    private final AtomicBoolean work = new AtomicBoolean();


    public ExecutorWorkGuard(ExecutorContext _ctx){

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

    public ExecutorContext context(){
        return ctx;
    }

}
