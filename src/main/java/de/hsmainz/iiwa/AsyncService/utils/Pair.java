package de.hsmainz.iiwa.AsyncService.utils;

public class Pair<T, U> {

    private T argT;
    private U argU;

    private Pair(){}

    public Pair(T first, U second){
        argT = first;
        argU = second;
    }

    public T first(){
        return argT;
    }

    public U second(){
        return argU;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this) return true;
        else {
            if(obj instanceof Pair){
                Pair q = (Pair) obj;

                if (argT != null ? !argT.equals(q.first()) : q.first() != null) return false;
                if (argU != null ? !argU.equals(q.second()) : q.second() != null) return false;
                return true;

            } else {
                return false;
            }
        }
    }


}
