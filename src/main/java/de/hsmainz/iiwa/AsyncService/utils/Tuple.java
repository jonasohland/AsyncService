package de.hsmainz.iiwa.AsyncService.utils;

public class Tuple {
    public static <T, U> Pair<T, U> makeTuple(T first, U second){
        return new Pair<T, U>(first, second);
    }

    public static <T, U, V> Triplet<T, U, V> makeTuple(T first, U second, V third){
        return new Triplet<T, U, V>(first, second, third);
    }

    public static <T, U, V, K> Quad<T, U, V, K> makeTuple(T first, U second, V third, K fourth){
        return new Quad<T, U, V, K>(first, second, third, fourth);
    }
}
