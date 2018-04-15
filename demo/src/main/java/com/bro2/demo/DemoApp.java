package com.bro2.demo;

import android.app.Application;
import android.content.Context;
import android.os.IBinder;
import android.util.Log;

import com.bro2.ehook.EHook;
import com.bro2.timing.AverageClerk;
import com.bro2.timing.Timing;
import com.bro2.util.ReflectUtil;
import com.bro2.demo.entry.DgActivity;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashMap;

import static com.bro2.demo.DemoEnv.DEBUG;
import static com.bro2.demo.DemoEnv.SWITCH_HOOK_AMS;
import static com.bro2.demo.DemoEnv.SWITCH_HOOK_CLASS_LOADER;
import static com.bro2.demo.DemoEnv.SWITCH_HOOK_SM_CACHE;
import static com.bro2.demo.DemoEnv.TAG_PREFIX;

/**
 * Created by Bro2 on 2017/6/4
 *
 */

public class DemoApp extends Application {
    private static final String HARD_CODE_SM = "android.os.ServiceManager";
    private static final String HARD_CODE_IAM = "android.app.IActivityManager";
    private static final String HARD_CODE_AMN = "android.app.ActivityManagerNative";

    private static final String HARD_CODE_S_CACHE = "sCache";
    private static final String HARD_CODE_G_DEFAULT = "gDefault";
    private static final String HARD_CODE_M_INSTANCE = "mInstance";

    private static final String HARD_CODE_M_PACKAGE_INFO = "mPackageInfo";
    private static final String HARD_CODE_M_CLASS_LOADER = "mClassLoader";

    private static Application mApp;

    private static class MyHashMap<K, V> extends HashMap<K, V> {
        HashMap<K, V> map;

        MyHashMap(HashMap<K, V> map) {
            this.map = map;
        }

        @Override
        public V get(Object key) {
            V v = map.get(key);
            if (DEBUG) {
                Log.d(TAG_PREFIX, "[MyHashMap.get] key: " + key + " val: " + v);
            }
            return v;
        }

        @Override
        public V put(K key, V value) {
            V v = map.put(key, value);
            if (DEBUG) {
                Log.d(TAG_PREFIX, "[MyHashMap.put] key : " + key + " val: " + value);
            }
            return v;
        }
    }

    private static class MyAMP implements InvocationHandler {
        Object original;

        MyAMP(Object original) {
            this.original = original;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (DEBUG) {
                // proxy会导致崩溃
                Log.d(TAG_PREFIX, "[MyAMP.invoke] method: " + method.getName() + " args: "
                        + (args == null ? "null" : Arrays.asList(args))
                        /*+ " proxy: " + proxy*/ + " ori: " + original);
            }

            return method.invoke(original, args);
        }
    }

    private static class MyClassLoader extends ClassLoader {
        ClassLoader original;

        MyClassLoader(ClassLoader original) {
            this.original = original;
        }

        @Override
        public Class<?> loadClass(String name) throws ClassNotFoundException {
            if (DEBUG) {
                Log.d(TAG_PREFIX, "[MyClassLoader.loadClass] " + name);
            }

            if ("com.bro2.demo.entry.JsBridgeActivity".equals(name)) {
                return DgActivity.class;
            }

            return original.loadClass(name);
        }

    }

    @Override
    protected void attachBaseContext(Context base) {
        if (SWITCH_HOOK_SM_CACHE) {
            HashMap<String, IBinder> cache = ReflectUtil.getStaticField(HARD_CODE_SM, HARD_CODE_S_CACHE);
            for (String k : cache.keySet()) {
                if (DEBUG) {
                    Log.d(TAG_PREFIX, "[DemoApp.attachBaseContext] now cache, k: " + k + " val: " + cache.get(k));
                }
            }
            MyHashMap<String, IBinder> my = new MyHashMap<>(cache);
            ReflectUtil.replaceStaticField(HARD_CODE_SM, HARD_CODE_S_CACHE, my);
        }

        if (SWITCH_HOOK_AMS) {
            Object gDefault = ReflectUtil.getStaticField(HARD_CODE_AMN, HARD_CODE_G_DEFAULT);
            Object amp = ReflectUtil.getField(gDefault, HARD_CODE_M_INSTANCE);
            Object proxy = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                    new Class[]{ReflectUtil.getClassOrNull(HARD_CODE_IAM)},
                    new MyAMP(amp)
            );
            ReflectUtil.replaceField(gDefault, HARD_CODE_M_INSTANCE, proxy);
        }

        if (SWITCH_HOOK_CLASS_LOADER) {
            Object loadedApk = ReflectUtil.getField(base, HARD_CODE_M_PACKAGE_INFO);
            ClassLoader ori = ReflectUtil.getField(loadedApk, HARD_CODE_M_CLASS_LOADER);
            ReflectUtil.replaceField(loadedApk, HARD_CODE_M_CLASS_LOADER, new MyClassLoader(ori));
        }

        super.attachBaseContext(base);

        mApp = this;
    }

    @Override
    public void onCreate() {
        if (DEBUG) {
            Log.d(TAG_PREFIX, "[DemoApp.attachBaseContext] class loader: "
                    + this.getClassLoader().getClass().getName());
        }
        super.onCreate();

        Timing.prepareClerk(new AverageClerk());

        EHook.registerHandler(null, null, null);
    }

    public static Application getApplication() {
        return mApp;
    }
}
