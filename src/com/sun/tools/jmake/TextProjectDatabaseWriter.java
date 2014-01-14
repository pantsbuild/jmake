/* Copyright (c) 2002-2013 Sun Microsystems, Inc. All rights reserved
 *
 * This program is distributed under the terms of
 * the GNU General Public License Version 2. See the LICENSE file
 * at the top of the source tree.
 */
package com.sun.tools.jmake;

import java.io.*;
import java.util.Enumeration;
import java.util.Hashtable;


/**
 * This class implements writing a text stream representing a project database.
 *
 * @see com.sun.tools.jmake.TextProjectDatabaseReader for details.
 *
 * @author  Benjy Weinberger
 * @date 13 January 2013
 */
public class TextProjectDatabaseWriter {
    private ByteArrayOutputStream baos = new ByteArrayOutputStream();  // Reusable temp buffer.

    public void writeProjectDatabaseToFile(File outfile, Hashtable<String, PCDEntry> pcd) {
        try {
            Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outfile), "UTF-8"));
            try {
                writeProjectDatabase(out, pcd);
            } finally {
                out.close();
            }
        } catch (FileNotFoundException e) {
            throw new PrivateException(e);
        } catch (UnsupportedEncodingException e) {
            throw new PrivateException(e);
        } catch (IOException e) {
            throw new PrivateException(e);
        }
    }

	public void writeProjectDatabase(Writer out, Hashtable<String,PCDEntry> pcd) {
        Enumeration<PCDEntry> entries = pcd.elements();
        while (entries.hasMoreElements()) {
            PCDEntry entry = entries.nextElement();
            writePCDEntry(out, entry);
        }
	}

	private void writePCDEntry(Writer out, PCDEntry entry) {
        try {
            out.write(entry.className);
            out.write('\t');
            out.write(entry.javaFileFullPath);
            out.write('\t');
            out.write(Long.toString(entry.oldClassFileLastModified));
            out.write('\t');
            out.write(Long.toString(entry.oldClassFileFingerprint));
            out.write('\t');
            out.write(classInfoToBase64(entry.oldClassInfo));
            out.write('\n');
        } catch (IOException e) {
            throw new PrivateException(e);
        }
	}

	private char[] classInfoToBase64(ClassInfo ci) {
        baos.reset();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(ci);
            oos.close();
        } catch (IOException e) {
            throw new PrivateException(e);
        }
        return Base64.encode(baos.toByteArray());
	}
}
