package com.bro2.demo;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static com.bro2.demo.DemoEnv.DEBUG;
import static com.bro2.demo.DemoEnv.TAG;

public class MiPipeActivity extends Activity {

    private static final int TASK_NUMBER = 4;

    private static class WriteRunnable implements Runnable {

        interface ICallback {
            void onData(int id, byte[] buffer, int len);

            void onFinish(int id);
        }

        final int id;
        final ICallback callback;

        private WriteRunnable(ICallback callback, int id) {
            this.callback = callback;
            this.id = id;
        }

        @Override
        public void run() {
            try {
                if (DEBUG) {
                    Log.d(TAG, "[WriteRunnable.run] start write:" + id + ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                }
                Thread.sleep((long) (Math.random() * 1000));
                for (int i = 0; i < 10; ++i) {
                    byte[] buffer = ("write " + i).getBytes();
                    callback.onData(id, buffer, buffer.length);
                }
                callback.onFinish(id);
                if (DEBUG) {
                    Log.d(TAG, "[WriteRunnable.run] write done:" + id + "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
                }
            } catch (Exception e) {
                if (DEBUG) {
                    Log.e(TAG, "[WriteRunnable.run] ", e);
                }
            }
        }
    }

    private static class RequestRunnable implements Runnable {

        @Override
        public void run() {
            try {
                for (int i = 0; i < TASK_NUMBER; ++i) {
                    final PipedOutputStream out = new PipedOutputStream();
                    PipedInputStream in = new PipedInputStream(out);
                    downloadExecutor.execute(new WriteRunnable(new WriteRunnable.ICallback() {
                        @Override
                        public void onData(int id, byte[] buffer, int len) {
                            try {
                                out.write(buffer, 0, len);
                            } catch (IOException e) {
                                if (DEBUG) {
                                    Log.e(TAG, "[RequestRunnable.onData] ", e);
                                }
                            }
                        }

                        @Override
                        public void onFinish(int id) {
                            try {
                                out.close();
                                if (DEBUG) {
                                    Log.d(TAG, "[RequestRunnable.onFinish] id: " + id);
                                }
                            } catch (IOException e) {
                                if (DEBUG) {
                                    Log.e(TAG, "[RequestRunnable.onFinish] ", e);
                                }
                            }
                        }
                    }, i));
                    tasks.add(in);
                }
                if (DEBUG) {
                    Log.d(TAG, "[RequestRunnable.run] request over, start render-------------------");
                }
                new Thread(new RenderRunnable(), "render thread").start();
            } catch (Exception e) {
                if (DEBUG) {
                    Log.e(TAG, "[RequestRunnable.run] ", e);
                }
            }
        }
    }

    private static class RenderRunnable implements Runnable {

        @Override
        public void run() {
            int len = tasks.size();
            if (DEBUG) {
                Log.d(TAG, "[RenderRunnable.run] start render, tasks: " + len + "*********************");
            }
            for (int i = 0; i < len; ++i) {
                InputStream in = tasks.get(i);
                try {
                    byte[] buffer = new byte[128];
                    int read;

                    if (DEBUG) {
                        Log.d(TAG, "[RenderRunnable.run] read start:" + i + ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                    }
                    StringBuilder msg = new StringBuilder();
                    while ((read = in.read(buffer, 0, buffer.length)) >= 0) {
                        msg.append(new String(buffer, 0, read));
                    }
                    if (DEBUG) {
                        Log.d(TAG, "[RenderRunnable.run] read msg: " + msg);
                    }
                    if (DEBUG) {
                        Log.d(TAG, "[RenderRunnable.run] read done:" + i + "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
                    }
                } catch (Exception e) {
                    if (DEBUG) {
                        Log.e(TAG, "[RenderRunnable.run] ", e);
                    }
                }
            }
            if (DEBUG) {
                Log.d(TAG, "[RenderRunnable.run] render done***************************************");
            }
        }
    }


    static final ThreadPoolExecutor downloadExecutor = new ThreadPoolExecutor(2, 4, 30, TimeUnit.SECONDS,
            new ArrayBlockingQueue<Runnable>(128), new ThreadFactory() {

        AtomicInteger count = new AtomicInteger(0);

        @Override
        public Thread newThread(@NonNull Runnable r) {
            String name = "thread " + count.getAndIncrement();
            if (DEBUG) {
                Log.d(TAG, "[MiPipeActivity.newThread] " + name);
            }
            return new Thread(r, name);
        }
    }, new ThreadPoolExecutor.DiscardPolicy());

    static final ArrayList<InputStream> tasks = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mi_piple);

        new Thread(new RequestRunnable(), "request thread").start();
    }
}
