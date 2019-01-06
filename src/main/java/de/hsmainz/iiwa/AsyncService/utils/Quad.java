package de.hsmainz.iiwa.AsyncService.utils;

public class Quad<T, U, V, K> {

    private T argT;
    private U argU;
    private V argV;
    private K argK;

    private Quad(){};

    public Quad(T first, U second, V third, K fourth){
        argT = first;
        argU = second;
        argV = third;
        argK = fourth;
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

    public K fourth(){
        return argK;
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
                if (argK != null ? !argK.equals(q.fourth()) : q.fourth() != null) return false;
                return true;

            } else {
                return false;
            }
        }
    }
}
