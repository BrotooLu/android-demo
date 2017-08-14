package com.bro2.demo;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import static com.bro2.demo.DemoEnv.DEBUG;
import static com.bro2.demo.DemoEnv.DEMO_ENTRY_PKG_NAME;
import static com.bro2.demo.DemoEnv.TAG;

/**
 * Created by Bro2 on 2017/6/4
 *
 */

public class MainActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String dest = null;
        try {
            ApplicationInfo app = getPackageManager()
                    .getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            dest = app.metaData.getString("test-module");
        } catch (PackageManager.NameNotFoundException e) {
            if (DEBUG) {
                Log.e(TAG, "", e);
            }
        }

        if (TextUtils.isEmpty(dest)) {
            setContentView(R.layout.activity_main);
            return;
        }

        if (DEBUG) {
            Log.d(TAG, "test-module:" + dest);
        }

        try {
            Intent intent = new Intent(this, Class.forName(DEMO_ENTRY_PKG_NAME + "." + dest));
            startActivity(intent);
            finish();
        } catch (ClassNotFoundException e) {
            if (DEBUG) {
                Log.e(TAG, "", e);
            }
            setContentView(R.layout.activity_main);
            ((TextView)findViewById(R.id.tv_module)).setText("No test module: " + dest);
        }
    }
}
