/* Copyright (c) 2002-2008 Sun Microsystems, Inc. All rights reserved
 *
 * This program is distributed under the terms of
 * the GNU General Public License Version 2. See the LICENSE file
 * at the top of the source tree.
 */
package org.pantsbuild.jmake;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

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
    public void testVersion() {
        System.out.println("jmake -version");
        String[] args = {"-version"};
        Main.main(args);
        assertEquals(true, true);
    }

    @Test
    public void testHelp() {
        System.out.println("jmake -help");
        String[] args = {"-help"};
        Main.main(args);
        assertEquals(true, true);
    }

}
