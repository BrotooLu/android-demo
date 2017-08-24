package com.bro2.b2lib.webviewpool;

import android.content.Context;
import android.content.ContextWrapper;
import android.util.Log;

import com.bro2.b2lib.B2Exception;
import com.bro2.b2lib.B2LibEnv;
import com.bro2.b2lib.util.ReflectUtil;

import java.lang.reflect.Field;

/**
 * Created by Bro2 on 2017/8/22
 */

public class MutableContext extends ContextWrapper {
    private static final boolean DEBUG = B2LibEnv.DEBUG;
    private static final String TAG = B2LibEnv.TAG_PREFIX + "mutable_context";

    private final Field mBaseField;


    public MutableContext(Context base) {
        super(base);

        mBaseField = ReflectUtil.getClassField(ContextWrapper.class, "mBase");

        if (mBaseField == null) {
            throw new B2Exception("no context base field found");
        }
    }

    public void setBaseContext(Context ctx) {
        try {
            if (!mBaseField.isAccessible()) {
                mBaseField.setAccessible(true);
            }

            mBaseField.set(this, ctx);
        } catch (IllegalAccessException e) {
            if (DEBUG) {
                Log.e(TAG, "[MutableContext.setBaseContext]", e);
            }
        }
    }
}
