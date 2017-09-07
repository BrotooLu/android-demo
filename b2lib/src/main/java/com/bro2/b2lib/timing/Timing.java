package com.bro2.b2lib.timing;

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

    public static ITimingClerk getTimingClerk() {
        return sThreadLocal.get();
    }

}
