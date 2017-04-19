package com.bro2.watchdog;

import android.os.Handler;

import java.util.ArrayList;

/**
 * Created by bro2 on 17-4-17.
 */

public class WatchDog implements Runnable {
    private static final WatchDog mInstance = new WatchDog();

    public interface Monitor {
        void monitor();

        void onBlocked();
    }

    public WatchDog getInstance() {
        return mInstance;
    }

    public WatchDog() {

    }

    public final class HandlerChecker implements Runnable {
        private final Handler mHandler;
        private final String mName;
        private final long mWaitMax;
        private final ArrayList<Monitor> mMonitors = new ArrayList<>();
        private long mStartTime;
        private Monitor mCurrentMonitor;

        public HandlerChecker(Handler handler, String name, long waitMax) {
            mHandler = handler;
            mName = name;
            mWaitMax = waitMax;
        }

        public void addMonitor(Monitor monitor) {
//            mMonitors.add(monitor);
        }

        public void scheduleCheck() {
//            mStartTime = SystemClock.uptimeMillis();
        }

        public boolean isOverdueLocked() {
            return false;
        }

        public String describleBlockState() {
            return null;
        }

        @Override
        public void run() {

        }
    }

    @Override
    public void run() {

    }
}
