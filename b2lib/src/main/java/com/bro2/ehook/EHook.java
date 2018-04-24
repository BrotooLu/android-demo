package com.bro2.ehook;

import android.text.TextUtils;

/**
 * Created by Brotoo on 2018/4/14.
 */
public final class EHook {
    static {
        System.loadLibrary("ehook");
    }

    public static class Target {
        public final String lib;
        public final String sym;

        public Target(String lib, String sym) {
            this.lib = lib;
            this.sym = sym;
        }

        @Override
        public int hashCode() {
            int h = lib == null ? 0 : lib.hashCode();
            h += 31 * (sym == null ? 0 : sym.hashCode());
            return h;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }

            if (!(obj instanceof Target)) {
                return false;
            }

            Target o = (Target) obj;
            return TextUtils.equals(lib, o.lib) && TextUtils.equals(sym, o.sym);
        }
    }

    public interface Handler {

        void onHook(Target target, Object... args);

    }

    public static long registerHandler(Target target, Handler handler) {
        return 0;
    }

    private native long registerHandler(Target target);

    private static Object dispatchHook(long id, Object... args) {
        return null;
    }
}
