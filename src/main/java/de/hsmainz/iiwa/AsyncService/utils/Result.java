package de.hsmainz.iiwa.AsyncService.utils;

public class Result<E extends Exception> {


    private E exception;

    public Result(){
        is_failed = false;
    }

    public Result(E e){
        exception = e;
    }

    private boolean is_failed;

    public boolean failed(){
        return is_failed;
    }

    public E getException(){
        return exception;
    }
}
