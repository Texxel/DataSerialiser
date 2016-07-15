package com.github.texxel;

import junit.framework.ComparisonFailure;
import org.junit.Assert;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class AssertHelpers extends Assert {

    public static <T> void assertSetEquals( Set<? extends T> expected, Set<? extends T> actual ) {
        if (expected == actual)
            return;
        if (expected == null || actual == null )
            failCollection("", actual, expected);
        for (T thing : expected) {
            if (!actual.contains(thing))
                failCollection("missing " + thing, expected, actual);
        }
        for (T thing : actual) {
            if (!expected.contains(thing))
                failCollection("containes " + thing, expected, actual);
        }
    }

    public static <T> void assertListEquals( List<? extends T> expected, List<? extends T> actual ) {
        if (expected == actual)
            return;
        assertArrayEquals(expected.toArray(), actual.toArray());
    }

    private static void failCollection( String message, Collection expected, Collection actual ) {
        throw new ComparisonFailure(message,
                expected == null ? null : Arrays.toString(expected.toArray(new Object[1])),
                actual == null ? null : Arrays.toString(actual.toArray(new Object[1])));
    }

}
