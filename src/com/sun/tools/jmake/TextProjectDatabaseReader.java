/* Copyright (c) 2002-2013 Sun Microsystems, Inc. All rights reserved
 *
 * This program is distributed under the terms of
 * the GNU General Public License Version 2. See the LICENSE file
 * at the top of the source tree.
 */
package com.sun.tools.jmake;

import java.io.*;
import java.util.Hashtable;


public class TextProjectDatabaseReader {
    public Hashtable<String,PCDEntry> readProjectDatabaseFromFile(File infile) {
        try {
            BufferedReader in =
                new BufferedReader(new InputStreamReader(new FileInputStream(infile), "UTF-8"));
            try {
                return readProjectDatabase(in);
            } finally {
                in.close();
            }
        } catch (FileNotFoundException e) {
            throw new PrivateException(e);
        } catch (UnsupportedEncodingException e) {
            throw new PrivateException(e);
        } catch (IOException e) {
            throw new PrivateException(e);
        }
    }

    public Hashtable<String,PCDEntry> readProjectDatabase(BufferedReader in) {
        Hashtable<String,PCDEntry> pcd = new Hashtable<String, PCDEntry>();
        try {
            String line = in.readLine();
            while (line != null) {
                String[] parts = line.split("\t");
                if (parts.length != 5) {
                    throw new PrivateException(new IllegalArgumentException("Invalid line: " + line));
                }
                String className = parts[0];
                String javaFullFilePath = parts[1];
                long oldClassFileLastModified = Long.parseLong(parts[2]);
                long oldClassFileFingerprint = Long.parseLong(parts[3]);
                ClassInfo ci = classInfoFromBase64(parts[4]);
                PCDEntry entry = new PCDEntry(className, javaFullFilePath, oldClassFileLastModified,
                    oldClassFileFingerprint, ci);
                pcd.put(entry.className, entry);
            }
        } catch (IOException e) {
            throw new PrivateException(e);
        }
        return pcd;
    }

    private ClassInfo classInfoFromBase64(String s) {
        try {
            byte[] bytes = Base64.decode(s.toCharArray());
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
            return (ClassInfo)ois.readObject();
        } catch (IOException e) {
            throw new PrivateException(e);
        } catch (ClassNotFoundException e) {
            throw new PrivateException(e);
        }
    }
}
