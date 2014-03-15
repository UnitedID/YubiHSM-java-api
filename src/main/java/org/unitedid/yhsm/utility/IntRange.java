package org.unitedid.yhsm.utility;

import java.util.Iterator;
import java.util.NoSuchElementException;

/** <code>IntRange</code> implements an Iterable class to print integer ranges */
public class IntRange implements Iterable<Integer> {

    private int limit;
    private int steps = 1;
    private int current = 0;

    /**
     * Constructs a new IntRange using the specified number as both the minimum and maximum in this range.
     *
     * @param limit a number
     */
    public IntRange(int limit) {
        this.limit = limit;
    }

    /**
     * Constructs a new IntRange using the specified minimum and maximum numbers.
     *
     * @param min a minimum number (starting value)
     * @param max a maximum number
     */
    public IntRange(int min, int max) {
        this.current = min;
        this.limit = max;
    }

    /**
     * Constructs a new IntRange using the specified minimum and maximum numbers, and allow changing the step amount.
     *
     * @param min a minimum number (starting value)
     * @param max a maximum number
     * @param steps the amount to increase between each iteration
     */
    public IntRange(int min, int max, int steps) {
        this.current = min;
        this.limit = max;
        this.steps = steps;
    }

    @Override
    public Iterator<Integer> iterator() {
        return new Iterator<Integer>() {
            final private int max = limit;
            @Override
            public boolean hasNext() {
                return current < max;
            }

            @Override
            public Integer next() {
                if (hasNext()) {
                    return current++*steps;
                } else {
                    throw new NoSuchElementException("End of range");
                }
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Removing values from IntRange() is not possible");
            }
        };
    }
}
