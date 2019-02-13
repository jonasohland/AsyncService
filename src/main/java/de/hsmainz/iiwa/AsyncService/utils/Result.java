package de.hsmainz.iiwa.AsyncService.utils;

/**
 * Represents the result of an asynchronous operation
 * @param <T> type of the result resource
 * @param <E> type of the exception that my have been thrown while de resource was produced
 */
public class Result<T, E extends Exception> {


    private E exception;
    private T res;

    /**
     * Construct the result with its resource.
     * A Result constructed this way represents result of an
     * successful operation and Result.failed() will return false
     * @param resource the resource to bind to this result
     */
    public Result(T resource){
        res = resource;
        is_failed = false;
    }

    /**
     * Construct the Result with an Exception
     * A Result constructed this way represents a failure and Result.failed() will return true
     * @param e the Exception to bind to this Result
     */
    public Result(E e){
        exception = e;
        is_failed = true;
    }

    /**
     * Construct the Result with an Exception and a resource.
     * A Result constructed this way will represent and operation that produced an output but failed.
     * @param e the Exception to bind to this Result
     * @param resource the resource to bind to this Result
     */
    public Result(E e, T resource){
        exception = e;
        res = resource;
        is_failed = true;
    }


    private boolean is_failed;

    /**
     * check if the operation failed
     * @return true if it failed, false otherwise
     */
    public boolean failed(){
        return is_failed;
    }

    /**
     * get the resource bound to this Result
     * @return the resource
     */
    public T get(){
        return res;
    }

    /**
     * check if the Result has a resource bound to it
     * @return true if has a resource bound to it, false otherwise
     */
    public boolean hasResult(){
        return res != null;
    }


    /**
     * get the Exception bound to this Result
     * @return the Exception bound to this Result
     */
    public E exception(){
        return exception;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this) return true;
        else {
            if (obj instanceof Result) {

                Result other = (Result) obj;

                if (exception != null ? !exception.equals(other.exception()) : other.exception() != null)
                    return false;
                return res != null ? res.equals(other.get()) : other.get() == null;

            } else return false;
        }
    }
}
