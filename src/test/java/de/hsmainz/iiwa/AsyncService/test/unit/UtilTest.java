package de.hsmainz.iiwa.AsyncService.test.unit;

import de.hsmainz.iiwa.AsyncService.utils.*;
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

        Assert.assertEquals(int_pair, int_pair_2);
        Assert.assertEquals(int_triplet, int_triplet_2);
        Assert.assertEquals(int_quad, int_quad_2);

        Assert.assertNotNull(int_pair.first());
        Assert.assertNotNull(int_pair.second());

        Assert.assertNotNull(int_triplet.first());
        Assert.assertNotNull(int_triplet.second());
        Assert.assertNotNull(int_triplet.third());

        Assert.assertNotNull(int_quad.first());
        Assert.assertNotNull(int_quad.second());
        Assert.assertNotNull(int_quad.third());
        Assert.assertNotNull(int_quad.fourth());


    }

    @Test
    public void completion_test(){

        Completion<Exception> comp_1 = new Completion<>();
        Completion<Exception> comp_2 = new Completion<>();
        Completion<Exception> comp_3 = new Completion<>(new Exception());

        Assert.assertEquals(comp_1, comp_2);
        Assert.assertNotEquals(comp_1, comp_3);
        Assert.assertNotNull(comp_3.getException());
    }

    @Test
    public void result_test(){

        Result<Integer, Exception> res_1 = new Result<>(3);
        Result<Integer, Exception> res_2 = new Result<>(3);

        Result<Integer, Exception> res_3 = new Result<>(5);

        Result<Integer, Exception> res_f_1 = new Result<>(new Exception());
        Result<Integer, Exception> res_f_2 = new Result<>(new Exception(), 6);

        Assert.assertNotNull(res_1.get());

        Assert.assertEquals(res_1, res_2);
        Assert.assertNotEquals(res_1, res_3);

        Assert.assertFalse(res_1.failed());

        Assert.assertTrue(res_f_1.failed());
        Assert.assertTrue(res_f_2.failed());
        Assert.assertTrue(res_f_2.hasResult());

        Assert.assertNotNull(res_f_2.get());

    }


}
