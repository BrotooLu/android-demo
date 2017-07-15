package com.bro2.b2lib.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import static com.bro2.b2lib.B2LibEnv.DEBUG;
import static com.bro2.b2lib.B2LibEnv.TAG;

/**
 * Created by Bro2 on 2017/6/4
 *
 */

public class DraggableVG extends RelativeLayout {
    public DraggableVG(Context context) {
        super(context);
    }

    public DraggableVG(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DraggableVG(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DraggableVG(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (DEBUG) {
            Log.d(TAG, "vg onTouchEvent: " + Code2H.hMotionEvent(ev.getAction()));
        }
        return super.onTouchEvent(ev);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (DEBUG) {
            Log.d(TAG, "vg dispatchTouchEvent: " + Code2H.hMotionEvent(ev.getAction()));
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (DEBUG) {
            Log.d(TAG, "vg onInterceptTouchEvent: " + Code2H.hMotionEvent(ev.getAction()));
        }
        return super.onInterceptTouchEvent(ev);
    }
}
