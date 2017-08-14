package com.bro2.demo.entry;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.bro2.demo.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static com.bro2.demo.DemoEnv.DEBUG;
import static com.bro2.demo.DemoEnv.TAG;

public class MiPipeActivity extends Activity {

    private static final int TASK_NUMBER = 4;
    private static CountDownLatch latch;

    private static class ReadRunnable implements Runnable {
        final InputStream in;
        final int id;

        private ReadRunnable(InputStream in, int id) {
            this.in = in;
            this.id = id;
        }

        @Override
        public void run() {
            try {
                byte[] buffer = new byte[128];
                int read;

                StringBuilder msg = new StringBuilder();
                while ((read = in.read(buffer, 0, buffer.length)) >= 0) {
                    msg.append(new String(buffer, 0, read));
                }

                if (DEBUG) {
                    Log.e(TAG, "[ReadRunnable.run] read done id: " + id + " content: " + msg);
                }

                latch.countDown();
            } catch (Exception e) {
                if (DEBUG) {
                    Log.e(TAG, "[ReadRunnable.run] ", e);
                }
            }
        }
    }

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
                    Log.d(TAG, "[WriteRunnable.run] start write:" + id + ">>>>>>>>>>>>>>>>>>>>>>>");
                }
                int random = (int) (Math.random() * 1000);
                Thread.sleep(random);
                StringBuilder writeMsg = new StringBuilder();
                String data = "id: " + id + " sleep: " + random;
                byte[] buffer = data.getBytes();
                writeMsg.append(data);
                callback.onData(id, buffer, buffer.length);

                data = " content";
                writeMsg.append(data);
                buffer = data.getBytes();
                callback.onData(id, buffer, buffer.length);

                data = " " + Math.random();
                writeMsg.append(data);
                buffer = data.getBytes();
                callback.onData(id, buffer, buffer.length);

                if (DEBUG) {
                    Log.d(TAG, "[WriteRunnable.run] write done " + writeMsg);
                }
                callback.onFinish(id);
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
                ArrayList<ReadRunnable> tasks = new ArrayList<>();

                for (int i = 0; i < TASK_NUMBER; ++i) {
                    final PipedOutputStream out = new PipedOutputStream();
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
                    tasks.add(new ReadRunnable(new PipedInputStream(out), i));
                }

                if (DEBUG) {
                    Log.e(TAG, "[RequestRunnable.run] request over, start render#################");
                }

                for (int i = 0; i < TASK_NUMBER; ++i) {
                    readExecutor.execute(tasks.get(i));
                }

                latch.await();

                if (DEBUG) {
                    Log.e(TAG, "[RequestRunnable.run] render over################################");
                }
            } catch (Exception e) {
                if (DEBUG) {
                    Log.e(TAG, "[RequestRunnable.run] ", e);
                }
            }
        }
    }

    static final ThreadPoolExecutor downloadExecutor = new ThreadPoolExecutor(2, 4, 30, TimeUnit.SECONDS,
            new ArrayBlockingQueue<Runnable>(128), new ThreadFactory() {

        AtomicInteger count = new AtomicInteger(0);

        @Override
        public Thread newThread(@NonNull Runnable r) {
            String name = "download " + count.getAndIncrement();
            if (DEBUG) {
                Log.d(TAG, "[download.newThread] " + name);
            }
            return new Thread(r, name);
        }
    }, new ThreadPoolExecutor.DiscardPolicy());

    static final ThreadPoolExecutor readExecutor = new ThreadPoolExecutor(2, 4, 30, TimeUnit.SECONDS,
            new ArrayBlockingQueue<Runnable>(128), new ThreadFactory() {

        AtomicInteger count = new AtomicInteger(0);

        @Override
        public Thread newThread(@NonNull Runnable r) {
            String name = "read " + count.getAndIncrement();
            if (DEBUG) {
                Log.d(TAG, "[read.newThread] " + name);
            }
            return new Thread(r, name);
        }
    }, new ThreadPoolExecutor.DiscardPolicy());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mi_piple);

        latch = new CountDownLatch(TASK_NUMBER);
        new Thread(new RequestRunnable(), "request").start();
    }
}
