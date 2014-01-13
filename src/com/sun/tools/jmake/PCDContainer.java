/* Copyright (c) 2002-2008 Sun Microsystems, Inc. All rights reserved
 *
 * This program is distributed under the terms of
 * the GNU General Public License Version 2. See the LICENSE file
 * at the top of the source tree.
 */
package com.sun.tools.jmake;

import java.io.IOException;
import java.io.File;
import java.io.FileOutputStream;

import java.util.Hashtable;

/**
 * This class is a persistent container for the Project Class Directory, that can
 * read and write itself from/to disk.
 *
 * @author Misha Dmitriev
 * @date 12 November 2001
 */
public class PCDContainer {

    /** The data structure (currently Hashtable) for PCD, that maps class name to
    record containing information about the class */
    Hashtable<String,PCDEntry> pcd;
    String storeName;

    private PCDContainer(Hashtable<String,PCDEntry> pcd, String storeName) {
        this.storeName = storeName;
        this.pcd = pcd;
    }

    public static PCDContainer load(String storeName) {
        return doLoad(storeName, false);
    }

    public static PCDContainer loadFromText(String storeName) {
        return doLoad(storeName, true);
    }

    private static PCDContainer doLoad(String storeName, boolean fromText) {
        if (storeName == null) {
            storeName = Main.DEFAULT_STORE_NAME;
        }
        File storeFile = Utils.checkFileForName(storeName);
        if (storeFile != null) {
            Utils.printInfoMessageNoEOL("Opening project database...  ");
            Hashtable<String,PCDEntry> pcd;
            if (fromText) {
                pcd = new TextProjectDatabaseReader().readProjectDatabaseFromFile(storeFile);
            } else {
                byte buf[] = Utils.readFileIntoBuffer(storeFile);
                pcd = new ProjectDatabaseReader().readProjectDatabase(buf, storeName);
            }
            PCDContainer pcdc = new PCDContainer(pcd, storeName);
            Utils.printInfoMessage("Done.");
            return pcdc;
        }
        return new PCDContainer(null, storeName);
    }

    public void save() {
        doSave(false);
    }

    public void saveToText() {
        doSave(true);
    }

    private void doSave(boolean toText) {
        Utils.printInfoMessageNoEOL("Writing project database...  ");
        try {
            if (toText) {
                new TextProjectDatabaseWriter().writeProjectDatabaseToFile(new File(storeName), pcd);
            } else {
                byte[] buf = new ProjectDatabaseWriter().writeProjectDatabase(pcd);
                FileOutputStream out = new FileOutputStream(storeName);
                out.write(buf);
                out.close();
            }
            Utils.printInfoMessage("Done.");
        } catch (IOException e) {
            throw new PrivateException(e);
        }
    }
}
