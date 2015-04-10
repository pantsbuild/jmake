/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pantsbuild.jmake;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ohair
 */
public class UtilsTest {

    public UtilsTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }


    @Test
    public void testIgnore() {
        System.out.println("ignore");
        Exception e = null;
        Utils.ignore(e);
        assertEquals(true,true);
    }



}
