package it.mikeslab.identity.util;

import lombok.experimental.UtilityClass;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@UtilityClass
public class ZipUtil {

    private static final int BUFFER_SIZE = 4096;

    /**
     * Extracts a zip file specified by the zipFilePath to a directory specified by
     * destDirectory (will be created if it does not exist)
     * @param zipFile The zip file
     * @param destDir The destination directory
     * @throws IOException
     */
    public void unzip(File zipFile, File destDir) throws IOException {

        if (!destDir.exists()) {
            destDir.mkdir();
        }

        ZipInputStream zipIn = new ZipInputStream(Files.newInputStream(Paths.get(zipFile.getAbsolutePath())));
        ZipEntry entry = zipIn.getNextEntry();

        // iterates over entries in the zip file
        while (entry != null) {

            String filePath = destDir + File.separator + entry.getName();

            if (!entry.isDirectory()) {
                // if the entry is a file, extracts it
                extractFile(zipIn, filePath);

            } else {
                // if the entry is a directory, make the directory
                File dir = new File(filePath);
                dir.mkdirs();
            }
            zipIn.closeEntry();
            entry = zipIn.getNextEntry();
        }

        zipIn.close();
    }



    /**
     * Extracts a zip entry (file entry)
     * @param zipIn The ZipInputStream
     * @param filePath The path to write the file
     * @throws IOException
     */
    private void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(Files.newOutputStream(Paths.get(filePath)));
        byte[] bytesIn = new byte[BUFFER_SIZE];
        int read = 0;

        while ((read = zipIn.read(bytesIn)) != -1) {
            bos.write(bytesIn, 0, read);
        }

        bos.close();
    }



}
