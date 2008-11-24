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

public class MainTest {

    public MainTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Test
    public void testMain() {
        System.out.println("main");
        String[] args = { "-help" };
        Main.main(args);
    }

}