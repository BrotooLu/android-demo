package com.bro2.sp;

import android.content.SharedPreferences;
import android.util.Log;

import java.util.Map;
import java.util.Set;

import static com.bro2.b2lib.B2LibEnv.TAG_PREFIX;

/**
 * Created by Brotoo on 2018/11/21
 */
final class B2SharedPreference implements SharedPreferences {
    private static final String TAG = TAG_PREFIX + "b2sp";

    private SharedPreferences impl;

    B2SharedPreference(SharedPreferences impl) {
        this.impl = impl;
    }

    @Override
    public Map<String, ?> getAll() {
        logInvokeChain("getAll");
        return impl.getAll();
    }

    @Override
    public String getString(String key, String defValue) {
        logInvokeChain("getString: " + key);
        return impl.getString(key, defValue);
    }

    @Override
    public Set<String> getStringSet(String key, Set<String> defValues) {
        logInvokeChain("getStringSet: " + key);
        return impl.getStringSet(key, defValues);
    }

    @Override
    public int getInt(String key, int defValue) {
        logInvokeChain("getInt: " + key);
        return impl.getInt(key, defValue);
    }

    @Override
    public long getLong(String key, long defValue) {
        logInvokeChain("getLong: " + key);
        return impl.getLong(key, defValue);
    }

    @Override
    public float getFloat(String key, float defValue) {
        logInvokeChain("getFloat: " + key);
        return impl.getFloat(key, defValue);
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        logInvokeChain("getBoolean: " + key);
        return impl.getBoolean(key, defValue);
    }

    @Override
    public boolean contains(String key) {
        logInvokeChain("contains: " + key);
        return impl.contains(key);
    }

    @Override
    public Editor edit() {
        logInvokeChain("edit");
        return impl.edit();
    }

    @Override
    public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        logInvokeChain("registerOnSharedPreferenceChangeListener");
        impl.registerOnSharedPreferenceChangeListener(listener);
    }

    @Override
    public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        logInvokeChain("unregisterOnSharedPreferenceChangeListener");
        impl.unregisterOnSharedPreferenceChangeListener(listener);
    }

    private void logInvokeChain(String... args) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0, n = args != null ? args.length : 0; i < n; ++i) {
            sb.append(i == 0 ? "" : "\t");
            sb.append(args[i]);
        }

        Log.e(TAG, sb.toString(), new Exception());
    }
}
