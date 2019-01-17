package de.hsmainz.iiwa.AsyncService.utils;

/**
 * Holds 3 objects of different types
 * @param <T> first element type
 * @param <U> second element type
 * @param <V> third element type
 */
public class Triplet<T, U, V> {
    private T argT;
    private U argU;
    private V argV;

    private Triplet(){}

    /**
     * construct the triplet with with its contents
     * @param first first element
     * @param second second element
     * @param third third element
     */
    public Triplet(T first, U second, V third){
        argT = first;
        argU = second;
        argV = third;
    }

    /**
     * access the first element
     * @return
     */
    public T first(){
        return argT;
    }

    /**
     * access the second element
     * @return
     */
    public U second(){
        return argU;
    }

    /**
     * access the third element
     * @return
     */
    public V third(){
        return argV;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this) return true;
        else {
            if(obj instanceof Pair){
                Triplet q = (Triplet) obj;

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
