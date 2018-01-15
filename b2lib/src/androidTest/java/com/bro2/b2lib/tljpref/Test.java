package com.bro2.b2lib.tljpref;

import android.support.test.runner.AndroidJUnit4;

import org.junit.runner.RunWith;

/**
 * Created by Brotoo on 15/01/2018.
 */

@RunWith(AndroidJUnit4.class)
public class Test {

    static class ConfA {

        String name;

        int type;
    }

    static class ConfB {
        String name;

        int type;
    }

    static class Total {

        ConfA a;

        ConfB b;
    }

}
