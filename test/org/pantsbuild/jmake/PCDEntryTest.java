/* Copyright (c) 2015 Twitter, Inc. All rights reserved
 *
 * This program is distributed under the terms of
 * the GNU General Public License Version 2. See the LICENSE file
 * at the top of the source tree.
 */
package org.pantsbuild.jmake;


import org.junit.Test;

import static org.junit.Assert.*;


public class PCDEntryTest {
    private PCDEntry createPCDEntry(String className, String filename) {
        return new PCDEntry(className,
                            filename,
                            filename.replace(".java", ".class"),
                            1000000000000L,
                            1000000000000L,
                            null);

    }

    @Test
    public void testIsPackagePrivateClass() {
        assertEquals(false, createPCDEntry("foo/bar/Foo", "/tmp/foo/bar/Foo.java").isPackagePrivateClass());
        assertEquals(false, createPCDEntry("foo/bar/Foo$1", "/tmp/foo/bar/Foo.java").isPackagePrivateClass());
        assertEquals(true, createPCDEntry("foo/bar/Bar", "/tmp/foo/bar/Foo.java").isPackagePrivateClass());
    }

    public static void main(String[] args) {
        new PCDEntryTest().testIsPackagePrivateClass();
    }
}
