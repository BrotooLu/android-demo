package com.bro2.util;

import android.content.Context;
import android.util.TypedValue;

/**
 * Created by Brotoo on 2018/6/25
 */
public final class DimensionUtil {

    public static int dp2px(Context ctx, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, ctx.getResources().getDisplayMetrics());
    }

    public static int sp2px(Context ctx, int sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, ctx.getResources().getDisplayMetrics());
    }

}
