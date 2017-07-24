package com.bro2.b2lib.util;

import android.util.Log;

import java.util.HashMap;

import static com.bro2.b2lib.B2LibEnv.DEBUG;
import static com.bro2.b2lib.B2LibEnv.TAG;

/**
 * Created by Bro2 on 2017/7/24
 */

public class RWLock<T> {
    private final Object sRWCheckLock = new Object();
    private final HashMap<T, Integer> sReadFlag = new HashMap<>();
    private final HashMap<T, Boolean> sWriteFlag = new HashMap<>();

    public boolean checkAndSetRead(T key, boolean read) {
        synchronized (sRWCheckLock) {
            if (sWriteFlag.containsKey(key)) {
                return false;
            }

            Integer count = sReadFlag.get(key);
            if (count == null) {
                count = 0;
            }

            if (read) {
                sReadFlag.put(key, count + 1);
            } else {
                int c = count - 1;
                if (c < 0) {
                    if (DEBUG) {
                        Log.e(TAG, "[RWLock.checkAndSetRead] invoke incorrectly");
                    }
                    c = 0;
                }
                if (c == 0) {
                    sReadFlag.remove(key);
                } else {
                    sReadFlag.put(key, count - 1);
                }
            }
            return true;
        }
    }

    public boolean checkAndSetWrite(T key, boolean expected, boolean val) {
        synchronized (sRWCheckLock) {
            Integer read = sReadFlag.get(key);
            if (read != null && read > 0) {
                return false;
            }

            boolean old = sWriteFlag.containsKey(key);
            if (old != expected) {
                return false;
            }

            if (val) {
                sWriteFlag.put(key, null);
            } else {
                sWriteFlag.remove(key);
            }
            return true;
        }
    }

    public void dump() {
        if (DEBUG) {
            Log.d(TAG, "[RWLock.dump] read flags: ");
        }

        for (T k : sReadFlag.keySet()) {
            if (DEBUG) {
                Log.d(TAG, "key: " + k + " count: " + sReadFlag.get(k));
            }
        }

        if (DEBUG) {
            Log.d(TAG, "[RWLock.dump] write flags: ");
        }

        for (T k : sWriteFlag.keySet()) {
            if (DEBUG) {
                Log.d(TAG, "key: " + k + " val: " + sWriteFlag.get(k));
            }
        }
    }
}
