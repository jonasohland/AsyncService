package de.hsmainz.iiwa.AsyncService.utils;

import javafx.util.Pair;

public class Triplet<T, U, V> {
    private T argT;
    private U argU;
    private V argV;

    private Triplet(){}

    public Triplet(T first, U second, V third){
        argT = first;
        argU = second;
        argV = third;
    }

    public T first(){
        return argT;
    }

    public U second(){
        return argU;
    }

    public V third(){
        return argV;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this) return true;
        else {
            if(obj instanceof Quad){
                Quad q = (Quad) obj;

                if (argT != null ? !argT.equals(q.first()) : q.first() != null) return false;
                if (argU != null ? !argU.equals(q.second()) : q.second() != null) return false;
                if (argV != null ? !argV.equals(q.third()) : q.third() != null) return false;
                return true;

            } else {
                return false;
            }
        }
    }
}
