package com.bro2.io;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * Created by Brotoo on 2017/11/29
 */

public final class ZipUtil {
    private ZipUtil() {
    }

    public interface IUnzipCallback {
        void onUnzip(ZipEntry entry, InputStream stream);
    }

    public static void unzip(File file, Comparator<ZipEntry> sort, IUnzipCallback callback) throws IOException {
        ZipFile zipFile = new ZipFile(file);
        ArrayList<ZipEntry> entries = new ArrayList<>();

        Enumeration<? extends ZipEntry> entryEnumeration = zipFile.entries();
        while (entryEnumeration.hasMoreElements()) {
            entries.add(entryEnumeration.nextElement());
        }

        if (sort != null) {
            Collections.sort(entries, sort);
        }

        for (ZipEntry entry : entries) {
            InputStream stream = zipFile.getInputStream(entry);
            callback.onUnzip(entry, stream);
        }
    }


    private static void zip(String canonicalRootPath, File file, ZipOutputStream zos) throws IOException {
        if (file == null) {
            return;
        }

        if (file.isFile()) {
            String subPath = file.getCanonicalPath();
            int index = subPath.indexOf(canonicalRootPath);
            if (index != -1) {
                subPath = subPath.substring(canonicalRootPath.length() + File.separator.length());
            }

            ZipEntry entry = new ZipEntry(subPath);
            zos.putNextEntry(entry);

            BufferedInputStream bis = null;
            try {
                bis = new BufferedInputStream(new FileInputStream(file));
                IOUtil.copy(bis, zos);
            } finally {
                IOUtil.closeSilently(bis);
                zos.closeEntry();
            }
        } else {
            for (File child : file.listFiles()) {
                zip(canonicalRootPath, child, zos);
            }
        }
    }

    public static boolean zip(File src, File dest) throws IOException {
        if (dest == null || src == null || !src.exists()) {
            return false;
        }

        ZipOutputStream zos = null;
        try {
            if (src.isDirectory() && dest.getParentFile().equals(src)) {
                throw new IOException("recursive error");
            }

            dest.getParentFile().mkdirs();

            zos = new ZipOutputStream(new CheckedOutputStream(new FileOutputStream(dest), new CRC32()));
            zip(src.isFile() ? src.getParentFile().getCanonicalPath() : src.getCanonicalPath(), src, zos);
            zos.flush();

            return true;
        } finally {
            IOUtil.closeSilently(zos);
        }
    }


    public static boolean unzip(File src, File dest) throws IOException {
        if (dest == null || src == null || !src.exists() || !src.isFile()) {
            return false;
        }

        if (!dest.exists() && !dest.mkdirs()) {
            return false;
        }

        ZipFile zipFile = new ZipFile(src);
        try {
            Enumeration<? extends ZipEntry> zipEntries = zipFile.entries();

            String rootPath = dest.getCanonicalPath();

            while (zipEntries.hasMoreElements()) {
                ZipEntry zipEntry = zipEntries.nextElement();
                if (zipEntry != null) {
                    File file = new File(dest, zipEntry.getName());
                    if (!file.getCanonicalPath().startsWith(rootPath)) {
                        throw new IOException("potential coverage");
                    }

                    File parent = file.getParentFile();
                    if (!parent.exists()) {
                        parent.mkdirs();
                    }

                    if (zipEntry.isDirectory()) {
                        file.mkdirs();
                    } else {
                        InputStream is = null;
                        FileOutputStream os = null;
                        try {
                            is = zipFile.getInputStream(zipEntry);
                            os = new FileOutputStream(file);
                            IOUtil.copy(is, os);
                        } finally {
                            IOUtil.closeSilently(os);
                            IOUtil.closeSilently(is);
                        }
                    }
                }
            }
        } finally {
            zipFile.close();
        }

        return true;
    }


}
