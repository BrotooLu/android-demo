package com.bro2.b2lib.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Button;

import static com.bro2.b2lib.B2LibEnv.DEBUG;
import static com.bro2.b2lib.B2LibEnv.TAG;

/**
 * Created by Bro2 on 2017/6/4
 *
 */

public class DraggableBt extends Button {
    public DraggableBt(Context context) {
        super(context);
    }

    public DraggableBt(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DraggableBt(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public DraggableBt(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (DEBUG) {
            Log.d(TAG, "bt onTouchEvent: " + Code2H.hMotionEvent(event.getAction()));
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (DEBUG) {
            Log.d(TAG, "bt dispatchTouchEvent: " + Code2H.hMotionEvent(ev.getAction()));
        }
        return super.dispatchTouchEvent(ev);
    }
}
