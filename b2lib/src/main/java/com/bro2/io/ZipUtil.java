package com.bro2.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

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

}
