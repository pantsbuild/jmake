package com.sun.tools.jmake;

import java.io.File;
import java.io.FilenameFilter;
import java.util.*;


public class TestPCDUtils {
    private TestPCDUtils() {}  // Prevent instantiation.

    /** Generate a PCD by compiling jmake itself.
     * The resulting PCD can then be used, e.g., to test serialization.
     */
    public static String generatePCD(boolean textFormat) {
        return compileJMake(textFormat);
    }

    public static PCDContainer loadPDB(String pdbFile, boolean textFormat) {
        return PCDContainer.load(pdbFile, textFormat);
    }

    public static void savePDB(PCDContainer pcd, String pdbFile, boolean textFormat) {
        new File(pdbFile).getParentFile().mkdirs();
        pcd.storeName = pdbFile;
        pcd.save();
    }

    public static boolean comparePCDContainers(PCDContainer l, PCDContainer r) {
        SortedMap<String, PCDEntry> sortedL = new TreeMap<String, PCDEntry>(l.pcd);
        SortedMap<String, PCDEntry> sortedR = new TreeMap<String, PCDEntry>(r.pcd);
        if (!sortedL.keySet().equals(sortedR.keySet()))
            return false;
        Iterator<PCDEntry> iterL = sortedL.values().iterator();
        Iterator<PCDEntry> iterR = sortedR.values().iterator();
        while (iterL.hasNext()) {
            if (!comparePCDEntry(iterL.next(), iterR.next())) {
                return false;
            }
        }
        return true;
    }

    // Defined here, and not in PCDEntry.equals, so we don't also have to define hashCode.
    // TODO: Emit which fields differ?
    public static boolean comparePCDEntry(PCDEntry l, PCDEntry r) {
        return safeEquals(l.className, r.className)
            && safeEquals(l.javaFileFullPath, r.javaFileFullPath)
            && l.oldClassFileLastModified == r.oldClassFileLastModified
            && l.oldClassFileFingerprint == r.oldClassFileFingerprint
            && compareClassInfo(l.oldClassInfo, r.oldClassInfo)
            ;
    }

    // Defined here, and not in ClassInfo.equals, so we don't also have to define hashCode.
    // TODO: Emit which fields differ?
    public static boolean compareClassInfo(ClassInfo l, ClassInfo r) {
        return safeEquals(l.name, r.name)
            && l.javacTargetRelease == r.javacTargetRelease
            && Arrays.equals(l.cpoolRefsToClasses, r.cpoolRefsToClasses)
            && Arrays.equals(l.isRefClassArray, r.isRefClassArray)
            && Arrays.equals(l.cpoolRefsToFieldClasses, r.cpoolRefsToFieldClasses)
            && Arrays.equals(l.cpoolRefsToFieldNames, r.cpoolRefsToFieldNames)
            && Arrays.equals(l.cpoolRefsToFieldSignatures, r.cpoolRefsToFieldSignatures)
            && Arrays.equals(l.cpoolRefsToMethodClasses, r.cpoolRefsToMethodClasses)
            && Arrays.equals(l.cpoolRefsToMethodNames, r.cpoolRefsToMethodNames)
            && Arrays.equals(l.cpoolRefsToMethodSignatures, r.cpoolRefsToMethodSignatures)
            && l.accessFlags == r.accessFlags
            && l.isNonMemberNestedClass == r.isNonMemberNestedClass
            && safeEquals(l.superName, r.superName)
            && Arrays.equals(l.interfaces, r.interfaces)
            && Arrays.equals(l.fieldNames, r.fieldNames)
            && Arrays.equals(l.fieldSignatures, r.fieldSignatures)
            && Arrays.equals(l.fieldAccessFlags, r.fieldAccessFlags)
            && Arrays.equals(l.primitiveConstantInitValues, r.primitiveConstantInitValues)
            && Arrays.equals(l.methodNames, r.methodNames)
            && Arrays.equals(l.methodSignatures, r.methodSignatures)
            && Arrays.equals(l.methodAccessFlags, r.methodAccessFlags)
            && Arrays.deepEquals(l.checkedExceptions, r.checkedExceptions)
            && Arrays.equals(l.nestedClasses, r.nestedClasses)
            ;
    }

    private static boolean safeEquals(Object l, Object r) {
        return (l == null && r == null) || l.equals(r);
    }

    private static String compileJMake(boolean textFormat) {
        String testRoot = "build/test/generate_test_pcd";
        String pdbFile = testRoot + "/test.pdb";
        new File(pdbFile).delete();  // Ensure a clean compile.
        File[] sources = getAllJMakeSources();
        ArrayList<String> argsList = new ArrayList<String>();
        argsList.add("-d");
        argsList.add(testRoot + "/classes");
        argsList.add("-pdb");
        argsList.add(pdbFile);
        if (textFormat) {
            argsList.add("-pdb-text-format");
        }
        for (File src: sources) {
            argsList.add(src.toString());
        }
        String[] args = argsList.toArray(new String[argsList.size()]);
        Main.main(args);
        return pdbFile;
    }

    private static File[] getAllJMakeSources() {
        // TODO: Make this robust to structural changes under src?
        File sourcesDir = new File("src/com/sun/tools/jmake");
        return sourcesDir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".java");
            }
        });
    }
}
