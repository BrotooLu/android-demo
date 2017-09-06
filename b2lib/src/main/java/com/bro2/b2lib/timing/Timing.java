package com.bro2.b2lib.timing;

/**
 * Created by Bro2 on 2017/9/6
 */

public final class Timing {

    private static final ThreadLocal<ITimingClerks> sThreadLocal = new ThreadLocal<>();

    public static boolean prepareClerks(ITimingClerks timingClerks) {
        ITimingClerks clerks = sThreadLocal.get();
        if (clerks != null) {
            return false;
        }

        sThreadLocal.set(timingClerks);
        return true;
    }

    public static ITimingClerks getTimingClerks() {
        return sThreadLocal.get();
    }

}
