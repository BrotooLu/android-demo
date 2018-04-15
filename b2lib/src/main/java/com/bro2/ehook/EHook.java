package com.bro2.ehook;

/**
 * Created by Brotoo on 2018/4/14.
 */
public final class EHook {
    static {
        System.loadLibrary("ehook");
    }

    public interface Handler {

        void onHook(long id, Object... args);

    }

    public static long registerHandler(String lib, String sym, Handler handler) {
        return 0;
    }

    private native void registerHandler(long id, String lib, String sym);

    private static Object dispatchHook(long id, Object... args) {
        return null;
    }
}
