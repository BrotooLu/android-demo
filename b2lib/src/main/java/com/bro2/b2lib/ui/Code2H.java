package com.bro2.b2lib.ui;

import android.view.MotionEvent;

/**
 * Created by Bro2 on 2017/6/4
 * code to human-readable string
 *
 */

public class Code2H {

    public static String hMotionEvent(int action) {
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                return "down";
            case MotionEvent.ACTION_MOVE:
                return "move";
            case MotionEvent.ACTION_CANCEL:
                return "cancel";
            case MotionEvent.ACTION_OUTSIDE:
                return "outside";
            case MotionEvent.ACTION_UP:
                return "up";
            default:
                return "action: " + action;
        }
    }

}
