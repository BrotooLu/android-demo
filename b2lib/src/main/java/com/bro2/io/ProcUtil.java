package com.bro2.io;

import android.text.TextUtils;
import android.util.Pair;
import android.util.SparseArray;

import com.bro2.b2lib.B2LibEnv;

import java.io.FileInputStream;

/**
 * Created by Brotoo on 2017/12/6
 */

public final class ProcUtil {
    private ProcUtil() {
    }

    private static final int CAPACITY_PROCESS = 10;
    private static final int CAPACITY_THREAD = 30;

    private static final SparseArray<String> sProcessName = new SparseArray<>(CAPACITY_PROCESS);
    private static final SparseArray<String> sThreadName = new SparseArray<>(CAPACITY_THREAD);

    public static Pair<String, String> getProcessName(int pid, int tid) {
        FileInputStream fis = null;
        String pName = null;
        String tName = null;
        try {
            pName = sProcessName.get(pid);
            if (TextUtils.isEmpty(pName)) {
                String cmdLine = String.format("/proc/%d/cmdline", pid);
                fis = new FileInputStream(cmdLine);
                byte[] buffer = new byte[1024];
                int read = fis.read(buffer);
                for (int i = 0; i < read; i++) {
                    if (buffer[i] == 0) {
                        pName = new String(buffer, 0, i);

                        if (sProcessName.size() > CAPACITY_PROCESS) {
                            sProcessName.clear();
                        }

                        sProcessName.append(pid, pName);
                        break;
                    }
                }
            }
        } catch (Throwable e) {
            if (B2LibEnv.DEBUG) {
                throw new RuntimeException(e);
            }
        } finally {
            IOUtil.closeSilently(fis);
        }

        try {
            tName = sThreadName.get(tid);
            if (TextUtils.isEmpty(tName)) {
                String statPath = String.format("/proc/%d/task/%d/stat", pid, tid);
                fis = new FileInputStream(statPath);
                byte[] buffer = new byte[1024];
                int read = fis.read(buffer);

                int start = -1;
                for (int i = 0; i < read; i++) {
                    if (buffer[i] == ' ') {
                        if (start == -1) {
                            start = i;
                        } else {
                            tName = new String(buffer, start + 2, i - start - 3);

                            if (sThreadName.size() > CAPACITY_THREAD) {
                                sThreadName.clear();
                            }

                            sThreadName.append(tid, tName);
                            break;
                        }
                    }
                }
            }
        } catch (Throwable e) {
            if (B2LibEnv.DEBUG) {
                throw new RuntimeException(e);
            }
        } finally {
            IOUtil.closeSilently(fis);
        }

        return new Pair<>(pName, tName);
    }
}
