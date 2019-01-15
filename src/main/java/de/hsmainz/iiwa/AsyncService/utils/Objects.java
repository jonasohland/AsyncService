package de.hsmainz.iiwa.AsyncService.utils;

public class Objects {
    public static void requireNonNull(Object o){
        if(o == null)
            throw new NullPointerException();
    }

    public static void requireNonNull(Object o, String s){
        if(o == null)
            throw new NullPointerException(s);
    }
}
