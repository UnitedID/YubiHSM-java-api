package org.unitedid.yhsm.utility;

import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertEquals;

public class IntRangeTest {

    @Test
    public void testIntRange() {
        List<Integer> arrayResult = new ArrayList<Integer>();
        List<Integer> arrayTest = new ArrayList<Integer>();
        arrayTest.add(0);
        arrayTest.add(1);
        arrayTest.add(2);

        for (int i : new IntRange(3)) {
            arrayResult.add(i);
        }
        assertEquals(arrayResult, arrayTest);
    }

    @Test
    public void testIntRangeWidthMinMax() {
        List<Integer> arrayResult = new ArrayList<Integer>();
        List<Integer> arrayTest = new ArrayList<Integer>();
        arrayTest.add(5);
        arrayTest.add(6);
        arrayTest.add(7);

        for (int i : new IntRange(5, 8)) {
            arrayResult.add(i);
        }
        assertEquals(arrayResult, arrayTest);
    }

    @Test
    public void testIntRangeWidthMinMaxSteps() {
        List<Integer> arrayResult = new ArrayList<Integer>();
        List<Integer> arrayTest = new ArrayList<Integer>();
        arrayTest.add(0);
        arrayTest.add(3);
        arrayTest.add(6);

        for (int i : new IntRange(0, 3, 3)) {
            arrayResult.add(i);
        }
        assertEquals(arrayResult, arrayTest);
    }

    @Test
    public void testIntRangeNegativeValue() {
        List<Integer> arrayResult = new ArrayList<Integer>();
        List<Integer> arrayTest = new ArrayList<Integer>();
        arrayTest.add(-1);
        arrayTest.add(0);
        arrayTest.add(1);

        for (int i : new IntRange(-1, 2)) {
            arrayResult.add(i);
        }
        assertEquals(arrayResult, arrayTest);
    }

}
