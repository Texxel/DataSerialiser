package com.github.texxel;

import junit.framework.AssertionFailedError;
import junit.framework.ComparisonFailure;
import org.junit.Assert;

import java.util.*;

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

    private static void failCollection( String message, Collection expected, Collection actual ) {
        throw new ComparisonFailure(message,
                expected == null ? null : Arrays.toString(expected.toArray(new Object[1])),
                actual == null ? null : Arrays.toString(actual.toArray(new Object[1])));
    }

    public static <T> void assertListEquals( List<? extends T> expected, List<? extends T> actual ) {
        if (expected == actual)
            return;
        assertArrayEquals(expected.toArray(), actual.toArray());
    }

    public static <T, K> void assertMapEquals(Map<? extends T, ? extends K> expected, Map<? extends T, ? extends K> actual) {
        if (expected == actual)
            return;
        if (expected == null || actual == null)
            throw failMap("", expected, actual);

        for (Map.Entry<? extends T, ? extends K> entry : expected.entrySet()) {
            K expectedValue = entry.getValue();
            K actualValue = actual.get(entry.getKey());
            if (actualValue == expectedValue)
                continue;
            if (actualValue == null || expectedValue == null )
                throw failMap("Key wrong at '" + entry.getKey() + "'", expected, actual );
            if (!actualValue.equals(expectedValue))
                throw failMap("Key wrong at '" + entry.getKey() + "'", expected, actual );
        }

        for (Map.Entry<? extends T, ? extends K> entry : actual.entrySet()) {
            K actualValue = entry.getValue();
            K expectedValue = expected.get(entry.getKey());
            if (actualValue == expectedValue)
                continue;
            if (actualValue == null || expectedValue == null || !expectedValue.equals(actualValue))
                throw failMap("Key wrong at " + entry.getKey(), expected, actual );
        }
    }

    private static ComparisonFailure failMap(String message, Map expected, Map actual) {
        throw new ComparisonFailure(message,
                expected == null ? "null" : expected.toString(),
                actual == null ? "null" : actual.toString());
    }

}
