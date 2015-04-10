/* Copyright (c) 2002-2008 Sun Microsystems, Inc. All rights reserved
 *
 * This program is distributed under the terms of
 * the GNU General Public License Version 2. See the LICENSE file
 * at the top of the source tree.
 */
package org.pantsbuild.jmake;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.channels.FileChannel;

import org.junit.Before;
import org.junit.Test;

public class DepFileTest {

    public DepFileTest() {
    }

    @Before
    public void setUp() throws Exception {
        new File("build/test/classes/Foo.class").delete();
        new File("build/test/classes/Bar.class").delete();
        new File("build/test/testdepfile.pdb").delete();
    }

    @Test
    public void testDepFileNew() {
        System.out.println("jmake with depfile, new file");

        withExtraClass();
    }

    @Test
    /*
     * Test dependency file handling. First we test without an extra class, then
     * with, then without.
     */
    public void testDepFileExisting() {
        withoutExtraClass();

        withExtraClass();

        /* remove the extra class and make sure that we detect htis */
        withoutExtraClass();

    }

    private void withExtraClass() {
        writeDepfile(new File("build/test/depfile"),
                "../../../test/testcase2/Foo.java", "testcase2/Foo.class",
                "../../../test/testcase2/Foo.java", "testcase2/Bar.class"
                );

        copyFile("test/testcase2/orig-files/with-extra", "test/testcase2/Foo.java");
        String[] args = {
                "-pdb", "build/test/testdepfile.pdb",
                "-pdb-text-format",
                "-depfile", "build/test/depfile",
                "-d", "build/test/classes",
                "test/testcase2/Foo.java"
                };
        Main.main(args);
        PCDContainer container = PCDContainer.load("build/test/testdepfile.pdb", true);
        PCDEntry pcde = container.pcd.get("testcase2/Foo");
        assertNotNull(pcde);
        PCDEntry pcde2 = container.pcd.get("testcase2/Bar");
        assertNotNull(pcde2);
    }

    private void withoutExtraClass() {
        writeDepfile(new File("build/test/depfile"),
                "../../../test/testcase2/Foo.java", "testcase2/Foo.class");

        copyFile("test/testcase2/orig-files/without-extra", "test/testcase2/Foo.java");
        String[] args = {
                "-pdb", "build/test/testdepfile.pdb",
                "-pdb-text-format",
                "-depfile", "build/test/depfile",
                "-d", "build/test/classes",
                "test/testcase2/Foo.java"
                };
        Main.main(args);
        PCDContainer container = PCDContainer.load("build/test/testdepfile.pdb", true);
        PCDEntry pcde = container.pcd.get("testcase2/Foo");
        assertNotNull(pcde);
        PCDEntry pcde2 = container.pcd.get("testcase2/Bar");
        assertNull(pcde2);
    }

    private static void copyFile(String source, String dest) {
        FileChannel sourceChannel = null;
        FileChannel destChannel = null;
        try {
            sourceChannel = new FileInputStream(new File(source)).getChannel();
            destChannel = new FileOutputStream(new File(dest)).getChannel();
            destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                sourceChannel.close();
                destChannel.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void writeDepfile(File file, String... deps) {
        try {
            PrintWriter writer = new PrintWriter(new FileOutputStream(file));
            for (int i = 0; i < deps.length; i += 2) {
                String java = deps[i];
                String cls = deps[i + 1];
                writer.println(java + " -> " + cls);
            }
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
