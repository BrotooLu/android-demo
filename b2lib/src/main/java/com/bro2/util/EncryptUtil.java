package com.bro2.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Brotoo on 2017/12/11
 */

public final class EncryptUtil {
    private EncryptUtil() {
    }

    private static final char[] sHexChar = {'0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public static String md5(String content) throws NoSuchAlgorithmException, IOException {
        ByteArrayInputStream stream = new ByteArrayInputStream(content.getBytes());
        return md5(stream);
    }

    public static String md5(InputStream stream) throws NoSuchAlgorithmException, IOException {
        MessageDigest digest = MessageDigest.getInstance("MD5");
        byte[] buffer = new byte[1024 * 4];
        int read;

        while ((read = stream.read(buffer)) != -1) {
            digest.update(buffer, 0, read);
        }

        return toHexString(digest.digest());
    }

    public static String toHexString(byte bytes[]) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(sHexChar[(b & 0xf0) >>> 4]);
            sb.append(sHexChar[b & 0xf]);
        }
        return sb.toString();
    }
}
