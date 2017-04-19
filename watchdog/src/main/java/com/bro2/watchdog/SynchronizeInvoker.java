package com.bro2.watchdog;

import android.os.Handler;

/**
 * Created by bro2 on 17-4-17.
 * 工作流程：
 *        invokeThread                      workThread
 *             | ...
 *             | invoke/invokeWithLock
 *             |-------------------------------->|doWork
 *                                               |
 *                          workDone/timeout     |
 *             |<--------------------------------|
 *             | checkException
 *             | ...
 */

public final class SynchronizeInvoker {
    private static class LockableRunnable implements Runnable {
        final Runnable mRunnable;
        final Object mLock;
        final long mWaitMax;
        Exception exception;

        LockableRunnable(Runnable runnable, Object lock, long waitMax) {
            mRunnable = runnable;
            mLock = lock == null ? new Object() : lock;
            mWaitMax = waitMax < 0 ? 0 : waitMax;
        }

        private void lock() {
            synchronized (mLock) {
                try {
                    mLock.wait(mWaitMax);
                } catch (InterruptedException e) {
                    // TODO exception handle
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void run() {
            try {
                mRunnable.run();
            } catch (Exception e) {
                exception = e;
            } finally {
                synchronized (mLock) {
                    mLock.notify();
                }
            }
        }
    }

    /**
     * 直接新启动一个线程作为工作线程
     * @param runnable 工作线程work的Runnable
     * @param threadName 启动新线程的线程名字
     * @param waitMax 调用线程等待的最长时间,(-,0]代表一直等待
     * @param lock 自定义的锁，不提供则直接创建一把新锁
     * @return 工作线程调用产生的exception
     */
    public static Exception invokeWithLock(Runnable runnable, String threadName,
                                           long waitMax, final Object lock) {
        if (runnable == null) {
            throw new IllegalArgumentException("no runnable specify");
        }

        final LockableRunnable lockableRunnable = new LockableRunnable(runnable, lock, waitMax);
        new Thread(lockableRunnable, threadName).start();
        lockableRunnable.lock();

        return lockableRunnable.exception;
    }

    /**
     * 直接新启动一个线程作为工作线程
     * @param runnable 工作线程work的Runnable
     * @param threadName 启动新线程的线程名字
     * @param waitMax 调用线程等待的最长时间,(-,0]代表一直等待
     * @return 工作线程调用产生的exception
     */
    public static Exception invoke(Runnable runnable, String threadName, long waitMax) {
        return invokeWithLock(runnable, threadName, waitMax, null);
    }

    /**
     * 使用handler绑定的线程作为工作线程，当工作线程和调用线程是同一个线程时会抛异常
     * @param runnable 工作线程work的Runnable
     * @param waitMax  调用线程等待的最长时间,(-,0]代表一直等待
     * @param lock 自定义的锁，不提供则直接创建一把新锁
     * @return 工作线程调用产生的exception
     */
    public static Exception invokeWithLock(Runnable runnable, Handler handler,
                                           long waitMax, Object lock) {
        if (runnable == null || handler == null) {
            throw new IllegalArgumentException("no runnable specify");
        }

        if (Thread.currentThread() == handler.getLooper().getThread()) {
            throw new IllegalArgumentException("can't wait on worker thread");
        }

        final LockableRunnable lockableRunnable = new LockableRunnable(runnable, lock, waitMax);
        handler.postAtFrontOfQueue(lockableRunnable);
        lockableRunnable.lock();

        return lockableRunnable.exception;
    }

    /**
     * 使用handler绑定的线程作为工作线程，当工作线程和调用线程是同一个线程时会抛异常
     * @param runnable 工作线程work的Runnable
     * @param waitMax  调用线程等待的最长时间,(-,0]代表一直等待
     * @return 工作线程调用产生的exception
     */
    public static Exception invoke(Runnable runnable, Handler handler, long waitMax) {
        return invokeWithLock(runnable, handler, waitMax, null);
    }
}
