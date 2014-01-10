/* Copyright (c) 2002-2013 Sun Microsystems, Inc. All rights reserved
 *
 * This program is distributed under the terms of
 * the GNU General Public License Version 2. See the LICENSE file
 * at the top of the source tree.
 */
package com.sun.tools.jmake;

import java.util.Random;
import org.junit.Test;
import static org.junit.Assert.*;


public class Base64Test {
    @Test
    public void testEmpty() {
        assertEquals(Base64.encode(new byte[0]).length, 0);
        assertEquals(Base64.decode(new char[0]).length, 0);
    }

    @Test
    public void testRoundTrip() {
        // Use fixed seeds, so tests are repeatable.
        doTestRoundTrip(100, 4, 123);
        doTestRoundTrip(20000, 1000, 456);
        doTestRoundTrip(1000, 10000, 789);
    }

    private void doTestRoundTrip(int numTests, int maxInputSize, int seed) {
        Random rng = new Random(seed);
        for (int i = 0; i < numTests; i++) {
            byte[] bytes = new byte[rng.nextInt(maxInputSize)];
            rng.nextBytes(bytes);
            char[] chars = Base64.encode(bytes);
            byte[] roundtrippedBytes = Base64.decode(chars);
            assertArrayEquals(roundtrippedBytes, bytes);
        }
    }
}