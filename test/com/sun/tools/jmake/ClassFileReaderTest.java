package com.sun.tools.jmake;


import java.io.File;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Test;

import static org.junit.Assert.*;


public class ClassFileReaderTest {
    private boolean textFormat = true;

    @Test
    public void testNestedClassDetection() {
        String testRoot = "build/test/nested_class_detection";
        File[] sources = { new File("test/classfilereader/Parent.java") };
        String pcdPath = TestPCDUtils.compile(testRoot, sources, textFormat);
        // It would be nicer to run the ClassFileReader directly, but alas it's too deeply
        // intertwined with ClassInfo and PCDManager.
        PCDContainer pcd = TestPCDUtils.loadPDB(pcdPath, textFormat);
        PCDEntry entry = pcd.pcd.get("classfilereader/Parent");
        Set<String> nestedClasses = new TreeSet<String>(Arrays.asList(entry.oldClassInfo.nestedClasses));
        String[] expectedClassesArray = {
            "StaticChild", "ChildA", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
            "ChildB", "1LocalChildC", "1LocalChildD", "1LocalChildE"
        };
        Set<String> expectedClasses = new TreeSet<String>();
        for (String cls : expectedClassesArray) {
            expectedClasses.add("classfilereader/Parent$" + cls);
        }

        assertEquals(expectedClasses, nestedClasses);
    }

    public static void main(String[] args) {
        new ClassFileReaderTest().testNestedClassDetection();
    }
}
