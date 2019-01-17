package de.hsmainz.iiwa.AsyncService.test.unit;

import de.hsmainz.iiwa.AsyncService.utils.Pair;
import de.hsmainz.iiwa.AsyncService.utils.Quad;
import de.hsmainz.iiwa.AsyncService.utils.Triplet;
import org.junit.Assert;
import org.junit.Test;

public class UtilTest {

    @Test
    public void tuple_test(){

        Pair<Integer, Integer> int_pair = new Pair<>(1, 2);
        Triplet<Integer, Integer, Integer> int_triplet = new Triplet<>(1, 2,3);
        Quad<Integer, Integer, Integer, Integer> int_quad = new Quad<>(1,2,3,4);

        Pair<Integer, Integer> int_pair_2 = new Pair<>(1, 2);
        Triplet<Integer, Integer, Integer> int_triplet_2 = new Triplet<>(1, 2,3);
        Quad<Integer, Integer, Integer, Integer> int_quad_2 = new Quad<>(1,2,3,4);


        Assert.assertEquals(int_pair.first(), int_pair_2.first());
        Assert.assertEquals(int_pair.second(), int_pair_2.second());

        Assert.assertEquals(int_triplet.first(), int_triplet_2.first());
        Assert.assertEquals(int_triplet.second(), int_triplet_2.second());
        Assert.assertEquals(int_triplet.third(), int_triplet_2.third());



        Assert.assertEquals(int_quad.first(), int_quad_2.first());
        Assert.assertEquals(int_quad.second(), int_quad_2.second());
        Assert.assertEquals(int_quad.third(), int_quad_2.third());
        Assert.assertEquals(int_quad.fourth(), int_quad_2.fourth());

        // Assert.assertEquals(int_pair, int_pair_2);
        // Assert.assertEquals(int_triplet, int_triplet_2);
        Assert.assertEquals(int_quad, int_quad_2);

    }
}
