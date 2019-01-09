package de.hsmainz.iiwa.AsyncService.utils;

/**
 * Hold 4 objects of different types
 * @param <T> type of the first object
 * @param <U> type of the second object
 * @param <V> type of the third object
 * @param <K> type of the fourth object
 */
public class Quad<T, U, V, K> {

    private T argT;
    private U argU;
    private V argV;
    private K argK;

    private Quad(){};

    /**
     * Construct the Quad with its resources
     * @param first first object
     * @param second second object
     * @param third third object
     * @param fourth fourth object
     */
    public Quad(T first, U second, V third, K fourth){
        argT = first;
        argU = second;
        argV = third;
        argK = fourth;
    }

    /**
     * access the first object
     * @return the first object
     */
    public T first(){
        return argT;
    }

    /**
     * access the second object
     * @return the second object
     */
    public U second(){
        return argU;
    }

    /**
     * access the third object
     * @return the third object
     */
    public V third(){
        return argV;
    }

    /**
     * access the fourth object
     * @return the fourth object
     */
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
