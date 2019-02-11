package de.hsmainz.iiwa.AsyncService.utils;

public class Tuple {
    /**
     * Make a Pair of 2 things
     * @param first first thing
     * @param second second thing
     * @param <T> type of first thing
     * @param <U> type of second thing
     * @return the pair
     */
    public static <T, U> Pair<T, U> makeTuple(T first, U second){
        return new Pair<T, U>(first, second);
    }

    /**
     * Make a Triplet of 3 things
     * @param first first thing
     * @param second second thing
     * @param third third thing
     * @param <T> type of first thing
     * @param <U> type of second thing
     * @param <V> type of third thing
     * @return the triplet
     */
    public static <T, U, V> Triplet<T, U, V> makeTuple(T first, U second, V third){
        return new Triplet<T, U, V>(first, second, third);
    }

    /**
     * Make a Quad of 4 things
     * @param first first thing
     * @param second second thing
     * @param third third thing
     * @param fourth fourth thing
     * @param <T> type of first thing
     * @param <U> type of second thing
     * @param <V> type of third thing
     * @param <K> type of fourth thing
     * @return the quad
     */
    public static <T, U, V, K> Quad<T, U, V, K> makeTuple(T first, U second, V third, K fourth){
        return new Quad<T, U, V, K>(first, second, third, fourth);
    }

    public static <T, U> void tie(Pair<T, U> pair, T first, U second){
        first = pair.first();
        second = pair.second();
    }
}
