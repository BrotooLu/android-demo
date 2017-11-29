package com.bro2.io;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Brotoo on 2017/11/29
 */

public final class FileUtil {
    private FileUtil() {
    }

    public static String readFile(File file) throws IOException {
        InputStreamReader reader = null;
        char[] buffer;
        String rtn;
        int n;
        try {
            reader = new InputStreamReader(new BufferedInputStream(new FileInputStream(file)));
            int size = (int) file.length();
            if (size > IOUtil.DEFAULT_STREAM_BUFFER_SIZE) {
                buffer = new char[1024 * 4];
                StringBuilder result = new StringBuilder(size / 2);
                while (-1 != (n = reader.read(buffer))) {
                    result.append(buffer, 0, n);
                }
                rtn = result.toString();
            } else {
                buffer = new char[size];
                n = reader.read(buffer);
                rtn = new String(buffer, 0, n);
            }
        } finally {
            IOUtil.closeSilently(reader);
        }
        return rtn;
    }

    public static long getFileSizeIterate(File file) {
        if (!file.exists()) {
            return 0;
        }

        if (file.isFile()) {
            return file.length();
        }

        File[] files = file.listFiles();
        if (files == null || files.length < 1) {
            return 0;
        }

        long size = 0;
        for (File child : files) {
            size += getFileSizeIterate(child);
        }
        return size;
    }

    public static long deleteFileIterate(File file) {
        if (!file.exists()) {
            return 0;
        }

        if (file.isFile()) {
            long len = file.length();
            return file.delete() ? len : 0;
        } else {
            File[] files = file.listFiles();
            if (files == null || files.length < 1) {
                return 0;
            }

            long len = 0;
            for (File child : files) {
                len += deleteFileIterate(child);
            }

            file.delete();
            return len;
        }
    }

    public static void writeFile(File file, String str) throws IOException {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            fos.write(str.getBytes());
            fos.flush();
        } finally {
            IOUtil.closeSilently(fos);
        }
    }
}
