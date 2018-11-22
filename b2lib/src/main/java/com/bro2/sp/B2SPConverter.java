package com.bro2.sp;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Brotoo on 2018/11/21
 */
public final class B2SPConverter {

    public static SharedPreferences getSharedPreference(Context ctx, String name, int flag) {
        return new B2SharedPreference(ctx.getSharedPreferences(name, flag));
    }

}
