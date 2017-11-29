package com.bro2.b2lib;

import com.bro2.util.RWLock;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bro2 on 2017/7/24
 */

public class RWLockTest {
    private List<String> keys = new ArrayList<>();
    private RWLock<String> rwLock = new RWLock<>();
    private List<ReadRunnable> readRunnable = new ArrayList<>();
    private List<WriteRunnable> writeRunnable = new ArrayList<>();

    private class ReadRunnable implements Runnable {
        String key;

        ReadRunnable(String key) {
            this.key = key;
        }

        @Override
        public void run() {
            if (rwLock.checkAndSetRead(key, true)) {
                read(key);
                rwLock.checkAndSetRead(key, false);
            } else {
                System.out.println(key + " is writing, can't read");
            }
        }
    }

    private class WriteRunnable implements Runnable {
        String key;

        WriteRunnable(String key) {
            this.key = key;
        }

        @Override
        public void run() {
            if (rwLock.checkAndSetWrite(key, false, true)) {
                write(key);
                rwLock.checkAndSetWrite(key, true, false);
            } else {
                System.out.println(key + " is busying, can't write");
            }
        }
    }

    @Before
    public void setUp() {
        for (int i = 0; i < 10; ++i) {
            String key = "key" + i;
            keys.add(key);
            readRunnable.add(new ReadRunnable(key));
            writeRunnable.add(new WriteRunnable(key));
        }
    }

    private void read(String key) {
        System.out.println("reading " + key + " in " + Thread.currentThread().getId());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void write(String key) {
        System.out.println("writing to " + key + " in " + Thread.currentThread().getId());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test() {
        System.out.println("begin-------------------------------");
        for (int i = 0; i < keys.size(); ++i) {
            // read & read
//            new Thread(readRunnable.get(i)).start();
//            new Thread(readRunnable.get(i)).start();
//            new Thread(readRunnable.get(i)).start();
//            new Thread(readRunnable.get(i)).start();

            // read shared write mutex
                new Thread(readRunnable.get(i)).start();
                new Thread(readRunnable.get(i)).start();
                new Thread(readRunnable.get(i)).start();
                new Thread(writeRunnable.get(i)).start();
                new Thread(readRunnable.get(i)).start();


            // write mutex
//                new Thread(writeRunnable.get(i)).start();
//                new Thread(writeRunnable.get(i)).start();
//                new Thread(writeRunnable.get(i)).start();

            // write & read mutex
//                new Thread(writeRunnable.get(i)).start();
//                new Thread(readRunnable.get(i)).start();
        }

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

//        System.out.println(" now begin write............");

//        for (int i = 0; i < keys.size(); ++i) {
//            new Thread(writeRunnable.get(i)).start();
//        }


//        System.out.println(" write again............");
//        for (int i = 0; i < keys.size(); ++i) {
//            new Thread(writeRunnable.get(i)).start();
//        }


        System.out.println(" end dump ............");
        rwLock.dump();
    }

}
