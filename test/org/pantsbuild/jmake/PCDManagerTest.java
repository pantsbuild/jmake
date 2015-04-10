package org.pantsbuild.jmake;


import java.io.File;

import org.junit.Test;

import static org.junit.Assert.*;


public class PCDManagerTest {

    @Test
    public void testMissingPackageInfoClassDoesNotFailBuild() {
        String testRoot = "build/test/packageinfo";
        File[] sources = { new File("test/packageinfo/package-info.java") };
        CompileResult result = TestPCDUtils.compile(testRoot, sources, true);

        assertEquals(0, result.getExitCode());
    }

    public static void main(String[] args) {
        new PCDManagerTest().testMissingPackageInfoClassDoesNotFailBuild();
    }
}
