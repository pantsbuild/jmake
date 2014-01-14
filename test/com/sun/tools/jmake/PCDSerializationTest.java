package com.sun.tools.jmake;

import org.junit.Test;

import java.io.File;

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
}
