package de.hsmainz.iiwa.AsyncService.utils;

public class Completion<E extends Exception> {


    private E exception;

    public Completion(){
        is_failed = false;
    }

    public Completion(E e){
        exception = e;
        is_failed = true;
    }

    private boolean is_failed;

    public boolean failed(){
        return is_failed;
    }

    public E getException(){
        return exception;
    }
}
