package de.hsmainz.iiwa.AsyncService.utils;

public class Result<T, E extends Exception> {


    private E exception;
    private T res;

    public Result(T result){
        res = result;
        is_failed = false;
    }

    public Result(E e){
        exception = e;
        is_failed = true;
    }

    public Result(E e, T result){
        exception = e;
        res = result;
        is_failed = true;
    }


    private boolean is_failed;

    public boolean failed(){
        return is_failed;
    }

    public T get(){
        return res;
    }

    public boolean hasResult(){
        return res != null;
    }


    public E getException(){
        return exception;
    }
}
