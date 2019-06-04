package com.bro2.b2lib;

import com.bro2.io.FileUtil;
import com.bro2.io.ZipUtil;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

/**
 * Created by Brotoo on 2019-06-04
 */
public class ZipTest {
    private File destZip = new File("src/test/zip/zip.zip");
    private File srcZipDir = new File("src/test/zip/zip");
    private File srcZipFile = new File("src/test/zip/single.txt");

    private File srcUnzip = new File("src/test/zip/unzip.zip");
    private File destUnzip = new File("src/test/zip/unzip");

    @Before
    public void setUp() {
        if (destZip.exists()) {
            assertTrue(destZip.delete());
        }

        if (destUnzip.exists()) {
            FileUtil.deleteFileIterate(destUnzip);
        }
        assertFalse(destUnzip.exists());
    }

    @Test
    public void zipDirTest() throws IOException {
        assertTrue(ZipUtil.zip(srcZipDir, destZip));
    }

    @Test
    public void zipFileTest() throws IOException {
        assertTrue(ZipUtil.zip(srcZipFile, destZip));
    }

    @Test
    public void unzipTest() throws IOException {
        assertTrue(ZipUtil.unzip(srcUnzip, destUnzip));
    }

}
