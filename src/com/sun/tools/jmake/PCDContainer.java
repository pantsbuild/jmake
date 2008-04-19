/* Copyright (c) 2002-2008 Sun Microsystems, Inc. All rights reserved
 *
 * This program is distributed under the terms of
 * the GNU General Public License Version 2. See the LICENSE file
 * at the top of the source tree.
 */

package com.sun.tools.jmake;

import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;
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
  Hashtable pcd;
  
  String storeName;
  
  private PCDContainer(Hashtable pcd, String storeName) {
    this.storeName = storeName;
    this.pcd = pcd;
  }
  
  
  public static PCDContainer load(String storeName) {
    if (storeName == null) storeName = Main.DEFAULT_STORE_NAME;
    File storeFile = Utils.checkFileForName(storeName);
    if (storeFile != null) {
      Utils.printInfoMessageNoEOL("Opening project database...  ");
      byte buf[] = Utils.readFileIntoBuffer(storeFile);
      Hashtable pcd = new ProjectDatabaseReader().readProjectDatabase(buf, storeName);
      PCDContainer pcdc = new PCDContainer(pcd, storeName);
      Utils.printInfoMessage("Done.");
      return pcdc;
    }
    return new PCDContainer(null, storeName);
  }

  
  public void save() {
    Utils.printInfoMessageNoEOL("Writing project database...  ");
    try {
      byte[] buf = new ProjectDatabaseWriter().writeProjectDatabase(pcd);
      FileOutputStream out = new FileOutputStream(storeName);
      out.write(buf);
      out.close();
      Utils.printInfoMessage("Done.");
    } catch (IOException e) {
      throw new PrivateException(e);
    }
  }

}
