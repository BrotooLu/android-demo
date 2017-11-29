package com.bro2.timing;

/**
 * Created by Bro2 on 2017/9/6
 */

public final class Timing {

    private static final ThreadLocal<ITimingClerk> sThreadLocal = new ThreadLocal<>();

    public static boolean prepareClerk(ITimingClerk timingClerk) {
        ITimingClerk clerk = sThreadLocal.get();
        if (clerk != null) {
            return false;
        }

        sThreadLocal.set(timingClerk);
        return true;
    }

    public static void enter(Marker marker) {
        ITimingClerk clerk = sThreadLocal.get();
        if (clerk != null) {
            clerk.enter(marker);
        }
    }

    public static void leave(Marker marker) {
        ITimingClerk clerk = sThreadLocal.get();
        if (clerk != null) {
            clerk.leave(marker);
        }
    }

    public static String dump() {
        ITimingClerk clerk = sThreadLocal.get();
        if (clerk != null) {
            return clerk.dump();
        }
        return "";
    }

    public static void clear() {
        ITimingClerk clerk = sThreadLocal.get();
        if (clerk != null) {
            clerk.clear();
        }
    }
}
