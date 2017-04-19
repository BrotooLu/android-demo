package com.bro2.watchdog;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;

public class MainActivity extends Activity {
    static final String TAG = "MainActivity";

    private static class H extends Handler {
        public H(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try {
                Log.d(TAG, "handleMessage start: " + SystemClock.uptimeMillis());
                Thread.sleep(1000);
                Log.d(TAG, "handleMessage end: " + SystemClock.uptimeMillis());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static class SleepRunnable implements Runnable {
        private String mTag;
        private long mWaitMax;

        public SleepRunnable(String tag, long waitMax) {
            mTag = tag;
            mWaitMax = waitMax;
        }

        @Override
        public void run() {
            Log.d(TAG, mTag + " work start in thread: " + Thread.currentThread().getId());
            try {
                Thread.sleep(mWaitMax);
            } catch (InterruptedException e) {
                Log.e(TAG, mTag, e);
            }
            Log.d(TAG, mTag + " work end");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void invoke(View view) {
//        HandlerThread thread = new HandlerThread("ht");
//        thread.start();
//        H ht = new H(thread.getLooper());
//        Log.d(TAG, "invoke Handler start: " + SystemClock.uptimeMillis());
//        SynchronizeInvoker.invoke(new SleepRunnable("Handler Thread", 5000), ht, -1);
//        Log.d(TAG, "invoke Handler end: " + SystemClock.uptimeMillis());

        Log.d(TAG, "invoke New start: " + SystemClock.uptimeMillis());
        SynchronizeInvoker.invoke(new SleepRunnable("New Thread", 500), "New Thread", 1000);
        Log.d(TAG, "invoke New end: " + SystemClock.uptimeMillis());
    }
}
