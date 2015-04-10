/*
 * Copyright (c) 2001 Sun Microsystems, Inc.
 * All rights reserved.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 *
 */
package org.pantsbuild.jmake.ant;

import org.pantsbuild.jmake.Main;

import org.pantsbuild.jmake.Utils;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;

import org.apache.tools.ant.taskdefs.Javac;

import java.io.File;
import java.util.ArrayList;
import java.lang.reflect.Method;
import java.util.List;

/**
 * This class is used when <b>jmake</b> is invoked from within Ant.<P>
 *
 * Task to selectively compile Java source files using JavaMake.
 * This task is in fact a slightly modified version of the core Javac task, and
 * completely substitutes it. It recognizes all of the parameters that the Javac
 * task takes plus the following additional optional arguments:
 *
 * <ul>
 * <li> pdbFilename - specifies the non-default path and name for the project database file
 * (by default this file will be called "jmake.pdb" and placed in the current directory).
 * If one wants to use the same non-default project database file everywhere, an optional
 * <code>jmake.pdb.filename</code> global property can be used to set it.
 *
 * <li> projClassPath - a classpath for sourceless project classes (currently only <code>JAR</code>s
 * are allowed on this class path). When a Java compiler is invoked, it is passed the class path
 * which is the user class path and project class path merged.
 *
 * <li> failOnDependentJar - specifies that jmake should fail if it detects a dependency
 * of a class in a <code>JAR</code> on a class that has a <code>.java</code> source. Default
 * behaviour is to issue a warning.
 *
 * <li> noWarnOnDependentJar - specifies that jmake should not produce an error or warning
 * in the above case. Use this option with care.
 * </ul>
 *
 * @author <a href="mailto:mikhail.dmitriev@sun.com">Misha Dmitriev</a>
 */
public class JavaMake extends Javac {

    private static final String FAIL_MSG =
            "Compilation invoked by jmake failed, messages should have been provided.";
    private String pdbFilename;
    private boolean failOnDependentJar = false;
    private boolean noWarnOnDependentJar = false;
    /** List of all source file names stored as full paths, for this project. */
    protected List<String> sourceFileNameList;
    /** Class path for all JARs included in this project. */
    protected Path projClasspath;

    public String getPdbFilename() {
        return pdbFilename;
    }

    public void setPdbFilename(String v) {
        this.pdbFilename = v;
    }

    /**
     * Set the project classpath to be used for this compilation.
     *
     * @param classpath an Ant Path object containing the project classpath.
     */
    public void setProjClasspath(Path classpath) {
        if (projClasspath == null) {
            projClasspath = classpath;
        } else {
            projClasspath.append(classpath);
        }
    }

    /** Gets the project classpath to be used for this compilation. */
    public Path getProjClasspath() {
        return projClasspath;
    }

    /** Adds a path to the project classpath. */
    public Path createProjClasspath() {
        if (projClasspath == null) {
            projClasspath = new Path(getProject());
        }
        return projClasspath.createPath();
    }

    /** Adds a reference to a project classpath defined elsewhere. */
    public void setProjClasspathRef(Reference r) {
        createProjClasspath().setRefid(r);
    }

    public boolean getFailOnDependentJar() {
        return failOnDependentJar;
    }

    public void setFailOnDependentJar(boolean v) throws BuildException {
        if (v && noWarnOnDependentJar) {
            throw new BuildException("failOnDependentJar and noWarnOnDependentJar attributes can't be set to true together.");
        } else {
            failOnDependentJar = v;
        }
    }

    public boolean getNoWarnOnDependentJar() {
        return noWarnOnDependentJar;
    }

    public void setNoWarnOnDependentJar(boolean v) throws BuildException {
        if (failOnDependentJar) {
            throw new BuildException("failOnDependentJar and noWarnOnDependentJar attributes can't be set to true together.");
        } else {
            noWarnOnDependentJar = v;
        }
    }

    /** Clear the list of source file names */
    public void resetSourceFileNameList() {
        // Should we use Ant for a project smaller than that? :-)
        sourceFileNameList = new ArrayList<String>(100);
    }

    /** Return the list of source file names as an array */
    public String[] sourceFileNameListArray() {
        return sourceFileNameList.toArray(new String[sourceFileNameList.size()]);
    }

    /** Executes the task. */
    public void execute() throws BuildException {
        // We keep the .java files in the String array, unlike the parent class Javac that
        // keeps them in a File array. To pass files as Strings is a requirement of the jmake
        // public API, which would be too hard to change. So, we manage our own sourceFileNameList,
        // and we need to initialize it here.
        resetSourceFileNameList();
        super.execute();
    }

    /**
     * Extract all .java files from the given file list, prepend their names with srcDir
     * name, and add to sourceFileNameList. The second parameter is not used at all, and
     * is provided only for compatibility with the same method defined in the parent
     * class.
     */
    protected void scanDir(File srcDir, File destDir, String fileNames[]) {
        String srcDirName = srcDir.getAbsolutePath();
        if (srcDirName != null && (!"".equals(srcDirName)) &&
                !srcDirName.endsWith(File.separator)) {
            srcDirName += File.separator;
        }
        for (int i = 0; i < fileNames.length; i++) {
            if (fileNames[i].endsWith(".java")) {
                sourceFileNameList.add(srcDirName + fileNames[i]);
            }
        }
    }

    /**
     * This method is called by execute() in the superclass, and it invokes a jmake
     * public API method. The latter, in turn, calls compileSourceFiles(String files[]),
     * perhaps several times, passing it a list of files that are updated and/or need
     * to be recompiled because of potentially broken dependencies.
     */
    protected void compile() {
        if (sourceFileNameList.size() == 0) {
            return;
        }

        File destDir = getDestdir();

        // Create a method object for method "compileSourceFiles" to call back
        Class<?> thisClass = this.getClass();
        Method compileSourceFilesMethod;
        try {
          Class<?>[] args = new Class<?>[]{String[].class};

          compileSourceFilesMethod = thisClass.getDeclaredMethod(
                    "compileSourceFiles", args);
        } catch (Exception e) {
            throw new BuildException(e.getMessage() + "\n Should not happen!");
        }

        // Get the non-default project database name, if it has been supplied
        String pdbFileName = getPdbFilename();
        if (pdbFileName == null) {
            pdbFileName = getProject().getProperty("jmake.pdb.filename");
        }
        File pdbFile =
                pdbFileName != null ? getProject().resolveFile(pdbFileName) : null;

        Main jmake = new Main();
        try {
            Path compilePath = getClasspath();
            if (compilePath != null) {
                Main.setClassPath(compilePath.toString());
            }
            Path path = getProjClasspath();
            if (path != null) {
                Main.setProjectClassPath(path.toString());
            }
            if (compilePath == null) {
                compilePath = path;
            } else if (path != null) {
                compilePath.append(path);
            }
            setClasspath(compilePath);
            path = getBootclasspath();
            if (path != null) {
                Main.setBootClassPath(path.toString());
            }
            path = getExtdirs();
            if (path != null) {
                Main.setExtDirs(path.toString());
            }
        } catch (Exception e) {
            /* Should not happen - Ant has already checked paths */
            Utils.ignore(e);
        }
        if (failOnDependentJar || noWarnOnDependentJar) {
            jmake.setResponseOnDependentJar(failOnDependentJar ? Main.DEPJAR_ERROR
                    : Main.DEPJAR_NOWARNORERROR);
        }

        // Initialize (or re-initialize) jmake's internal "out" and "err" stream variables - important
        // in situations when e.g. an IDE that launches the Ant script may redefine System.out and System.err
        // in between Ant invocations
        Main.setOutputStreams(System.out, System.out, System.err);

        int res =
                jmake.mainExternalControlled(
                sourceFileNameListArray(), destDir != null ? destDir.getAbsolutePath()
                : null,
                pdbFile != null ? pdbFile.getAbsolutePath() : null, this, compileSourceFilesMethod);
        if (res != 0) {
            if (failOnError) {
                throw new BuildException(FAIL_MSG, getLocation());
            } else {
                log(FAIL_MSG, Project.MSG_ERR);
            }
        }
    }

    /** Compile the given source files using all attributes set for this task. */
    public int compileSourceFiles(String files[]) {
        // We received a list of full path file names. Convert them into java.io.File
        // objects that Ant compilerAdapters consume
        resetFileLists();
        compileList = new File[files.length];
        for (int i = 0; i < files.length; i++) {
            compileList[i] = new File(files[i]);
        }

        int result;
        boolean savedFailOnError = failOnError;
        failOnError = true;  // To guarantee that super.compile() throws a
        // BuildException if there is a compilation error
        try {
            super.compile();   // Deals with compilerAdapters, etc.
            result = 0;
        } catch (BuildException e) {
            Utils.ignore(e);
            log(FAIL_MSG, Project.MSG_ERR);
            result = -1;
        }

        failOnError = savedFailOnError;
        return result;
    }
}
