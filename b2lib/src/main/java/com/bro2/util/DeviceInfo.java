package com.bro2.util;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.os.BatteryManager;
import android.os.Build;
import android.view.WindowManager;

/**
 * Created by Bro2 on 2017/9/14
 */

public final class DeviceInfo {
    private DeviceInfo() {
    }

    public static Point getScreenRealSize(Context context, Point out) {
        if (out == null) {
            out = new Point(0, 0);
        }

        if (context == null) {
            return out;
        }

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getRealSize(out);
        return out;
    }

    public static int getRemainingBattery(Context context) {
        if (context == null) {
            return -1;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            BatteryManager battery = (BatteryManager) context.getSystemService(Context.BATTERY_SERVICE);
            return battery.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        }

        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent levelIntent = context.registerReceiver(null, filter);
        return levelIntent != null ? levelIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) : -1;
    }
}
