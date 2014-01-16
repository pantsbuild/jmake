package com.sun.tools.jmake;

import org.junit.Test;

import java.io.File;
import java.io.StringWriter;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import static org.junit.Assert.*;


public class PCDSerializationTest {

    private void doTestRoundTrip(boolean textFormat) {
        String pcdPath = TestPCDUtils.generatePCD(textFormat);
        String roundTripPcdPath = "build/test/generate_test_pcd/scratchpad/test_roundtrip.pcd";
        PCDContainer pcd = TestPCDUtils.loadPDB(pcdPath, textFormat);
        TestPCDUtils.savePDB(pcd, roundTripPcdPath);
        PCDContainer roundtripPcd = TestPCDUtils.loadPDB(roundTripPcdPath, textFormat);
        assertTrue(TestPCDUtils.comparePCDContainers(pcd, roundtripPcd));
    }

    @Test
    public void testRoundTripTextFormat() {
        doTestRoundTrip(true);
    }

    @Test
    public void testRoundTripBinaryFormat() {
        doTestRoundTrip(false);
    }

    @Test
    public void testDependencyMunging() {
        String[][] testDeps = {
            { "java/lang/Object", "[[[@java/lang/Object#", "[C", "C", "byte" },
            { "[C", "[java/lang/Class" }
        };
        String[] expectedMungedDeps = { "java/lang/Object", "java/lang/Class" };
        Hashtable<String,PCDEntry> pcds = new Hashtable<String,PCDEntry>();
        addDeps(pcds, "foo/bar/Baz", testDeps[0]);
        addDeps(pcds, "foo/bar/Baz$", testDeps[1]);

        StringWriter writer = new StringWriter();
        new TextProjectDatabaseWriter().writeProjectDatabase(writer, pcds);
        String output = writer.toString();
        String[] lines = output.split("\n");
        String depsLine = lines[lines.length - 1];
        String[] deps = depsLine.split("\t");
        String src = deps[0];
        assertEquals("/root/foo/bar/Baz.java", src);

        Set<String> expectedMungedDepsSet = new HashSet<String>();
        Collections.addAll(expectedMungedDepsSet, expectedMungedDeps);

        Set<String> actualMungedDepsSet = new HashSet<String>();
        for (int i = 1; i < deps.length; i++) {
            actualMungedDepsSet.add(deps[i]);
        }
        assertEquals(expectedMungedDepsSet, actualMungedDepsSet);
    }

    private void addDeps(Hashtable<String,PCDEntry> pcds, String cls, String[] deps) {
        ClassInfo ci = new ClassInfo();
        ci.cpoolRefsToClasses = deps;
        pcds.put(cls, new PCDEntry(cls, "/root/foo/bar/Baz.java", 0, 0, ci));
    }
}
