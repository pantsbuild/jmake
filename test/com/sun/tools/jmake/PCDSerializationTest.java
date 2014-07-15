package com.sun.tools.jmake;

import java.io.StringWriter;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


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
        Map<String,PCDEntry> pcds = new LinkedHashMap<String,PCDEntry>();
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

        Set<String> expectedMungedDepsSet = new LinkedHashSet<String>();
        Collections.addAll(expectedMungedDepsSet, expectedMungedDeps);

        Set<String> actualMungedDepsSet = new LinkedHashSet<String>();
        for (int i = 1; i < deps.length; i++) {
            actualMungedDepsSet.add(deps[i]);
        }
        assertEquals(expectedMungedDepsSet, actualMungedDepsSet);
    }

    private void addDeps(Map<String,PCDEntry> pcds, String cls, String[] deps) {
        ClassInfo ci = new ClassInfo();
        ci.cpoolRefsToClasses = deps;
        pcds.put(cls, new PCDEntry(cls, "/root/foo/bar/Baz.java", 0, 0, ci));
    }
}
