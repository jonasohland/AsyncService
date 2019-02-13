package de.hsmainz.iiwa.AsyncService.executor.layer;

import de.hsmainz.iiwa.AsyncService.async.AsyncTask;
import de.hsmainz.iiwa.AsyncService.executor.context.ExecutorContext;

public interface ExecutorLayer {

    /**
     * request the execution layer to process the given AsyncTask object.
     * the post() method will take ownership of the AsyncTask and its listeners will also be invoked through this
     * layer if no other layer is specified for them.
     * @param t The AsyncTask object to process
     */
    public void post(AsyncTask t);

    /**
     * request the execution layer to process the given AsyncTask object.
     * the defer() method wont take ownership of the AsyncTask and its listeners will be invoked in place if no
     * other executor was specified
     * @param t The AsyncTask object to process
     */
    public void defer(AsyncTask t);

    /**
     * request the execution layer to process the given AsyncTask object.
     * the dispatch method will invoke the AsyncTask in place if it is beeing called inside a thread that is currently
     * executing the lowest layers run() method. If not, it will be queued for later execution just like it would be
     * by the post() method
     * @param t The AsyncTask Object to process
     */
    public void dispatch(AsyncTask t);

    /**
     * Get the next layer in this execution chain
     * @return the layer under this ExecutorLayer
     */
    ExecutorLayer next_layer();


    /**
     * Get the ExecutorContext that represents the lowest layer in this execution chain
     * @return the lowest layer
     */
    ExecutorContext context();
}
