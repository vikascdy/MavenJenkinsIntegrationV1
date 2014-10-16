// -----------------------------------------------------------------------------
//  Copyright (c) Edifecs Inc. All Rights Reserved.
//
// This software is the confidential and proprietary information of Edifecs Inc.
// ("Confidential Information").  You shall not disclose such Confidential
// Information and shall use it only in accordance with the terms of the license
// agreement you entered into with Edifecs.
//
// EDIFECS MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
// SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED
// WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, OR
// NON-INFRINGEMENT. EDIFECS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY
// LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR
// ITS DERIVATIVES.
// -----------------------------------------------------------------------------
package com.edifecs.core.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;


/**
 * Assists in Zipping and unzipping files.
 *
 * @author willclem
 */
public class ZIPHelper {

    private static final int BUFFER = 2048;

    /**
     * Unzip the given zip file stream to the specified directory.
     *
     * @param is          InputStream of the ZipFile to unzip
     * @param destination Destination of the unzipped file
     */
    public final void unzipFiles(InputStream is, String destination) {
        try {
            ZipInputStream zis = new ZipInputStream(new BufferedInputStream(is));

            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                int count;
                byte[] data = new byte[BUFFER];

                // Make the destination directory path if not already built.
                File file;
                if (entry.isDirectory()) {
                    file = new File(destination + entry.getName());
                    file.mkdirs();
                } else {
                    file = new File(new File(destination + entry.getName()).getParent());
                    file.mkdirs();

                    // write the files to the disk
                    FileOutputStream fos = new FileOutputStream(destination + entry.getName());
                    BufferedOutputStream dest = new BufferedOutputStream(fos, BUFFER);
                    while ((count = zis.read(data, 0, BUFFER)) != -1) {
                        dest.write(data, 0, count);
                    }
                    dest.flush();
                    dest.close();
                }
            }
            zis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Create a zip archive from a folder, recursively using default zip options.
     *
     * @param path           initial folder where to add files, if empty or null will include directly into zip file
     * @param sourceFolder   the folder to archive
     * @param destinationZip destination file
     */
    public final void createZipRecurse(String path, File sourceFolder, String destinationZip) throws IOException {
        ZipOutputStream zip;
        FileOutputStream fileWriter;

        fileWriter = new FileOutputStream(destinationZip);
        zip = new ZipOutputStream(fileWriter);

        String root = path;
        if (path == null) {
            root = "";
        }
        zipFolderRecurse(root, sourceFolder, zip);

        zip.flush();
        zip.close();
    }

    /**
     * Add a directory to a zip file.
     *
     * @param path      initial folder where to add files, if empty will include directly into zip file
     * @param srcFolder the folder to archive
     * @param zip       zip archive stream
     */
    public void zipFolderRecurse(String path, File srcFolder, ZipOutputStream zip) throws IOException {
        File[] files = srcFolder.listFiles();
        if (files == null) {
            throw new IOException("There wasn't any file found in " + srcFolder + "dirctory");
        } else {
            for (File file : files) {
                if (file.isDirectory()) {
                    zipFolderRecurse(path + "/" + file.getName(), file, zip);
                } else {
                    addFile(path, file, zip);
                }
            }
        }
    }

    /**
     * Add a file to a zip archive.
     *
     * @param pathToFile path to file inside zip
     * @param file       the file to add
     * @param zip        zip archive stream
     */
    public void addFile(String pathToFile, File file, ZipOutputStream zip) throws IOException {
        byte[] buf = new byte[BUFFER];
        int len;
        FileInputStream in = new FileInputStream(file);
        zip.putNextEntry(new ZipEntry(pathToFile + "/" + file.getName()));
        while ((len = in.read(buf)) > 0) {
            zip.write(buf, 0, len);
        }
        // if not closed, the file can't be deleted
        in.close();
    }

}
