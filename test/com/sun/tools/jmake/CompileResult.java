package com.sun.tools.jmake;

/**
 * The result of a compilation.
 * The exit code will tell you if it succeeded or not (0 is success)
 * The pdb path points to the resulting pdb file.
 */
public class CompileResult {
    private final int exitCode;
    private final String pdbPath;

    public CompileResult(int exitCode, String pdbPath) {
        this.exitCode = exitCode;
        this.pdbPath = pdbPath;
    }

    public int getExitCode() {
        return exitCode;
    }

    public String getPdbPath() {
        return pdbPath;
    }
}
