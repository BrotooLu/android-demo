package com.bro2.io;

import android.util.Log;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static com.bro2.b2lib.B2LibEnv.DEBUG;
import static com.bro2.b2lib.B2LibEnv.TAG_PREFIX;

/**
 * Created by Brotoo on 2017/11/29
 */

public final class IOUtil {
    public static final int DEFAULT_STREAM_BUFFER_SIZE = 10 * 1024;

    private static final String TAG = TAG_PREFIX + "io";


    private IOUtil() {
    }

    public static void closeSilently(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception e) {
                if (DEBUG) {
                    Log.e(TAG, "closeCloseable", e);
                }
            }
        }
    }

    public static void copyStreamWithClose(InputStream src, OutputStream dest) throws IOException {
        try {
            int read;
            byte[] buffer = new byte[1024 * 4];
            while ((read = src.read(buffer, 0, buffer.length)) != -1) {
                dest.write(buffer, 0, read);
            }
        } finally {
            closeSilently(dest);
            closeSilently(src);
        }
    }

    public static int readStreamAtMost(InputStream inputStream, byte[] buffer) throws IOException {
        int remain = buffer.length;
        int start = 0;

        while (remain > 0) {
            int read = inputStream.read(buffer, start, remain);
            if (read == -1) {
                break;
            }
            start += read;
            remain -= read;
        }

        return start;
    }
}
