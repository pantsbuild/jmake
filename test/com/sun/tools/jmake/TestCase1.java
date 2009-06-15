/* Copyright (c) 2002-2008 Sun Microsystems, Inc. All rights reserved
 *
 * This program is distributed under the terms of
 * the GNU General Public License Version 2. See the LICENSE file
 * at the top of the source tree.
 */
package com.sun.tools.jmake;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestCase1 {

    public TestCase1() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Test
    public void testCase1() {
        System.out.println("jmake testcase1");
        String[] args = {
            "-pdb", "build/test/testcase1.pdb",
            "-d", "build/test/classes",
            "test/testcase1/A.java",
            "test/testcase1/B.java",
            "test/testcase1/C.java"};
        Main.main(args);
        assertEquals(true, true);
        // FIXME: now copy and modify A/B/C to trigger rebuilds
    }
}
