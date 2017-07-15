package com.bro2.demo;

import android.app.Application;
import android.content.Context;
import android.os.IBinder;
import android.util.Log;

import com.bro2.b2lib.util.ReflectUtil;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashMap;

import static com.bro2.demo.DemoEnv.DEBUG;
import static com.bro2.demo.DemoEnv.TAG;

/**
 * Created by Bro2 on 2017/6/4
 *
 */

public class DemoApp extends Application {
    private static final String HARD_CODE_SM = "android.os.ServiceManager";
    private static final String HARD_CODE_IAM = "android.app.IActivityManager";
    private static final String HARD_CODE_AMN = "android.app.ActivityManagerNative";

    private static final String HARD_CODE_SCACHE = "sCache";
    private static final String HARD_CODE_G_DEFAULT = "gDefault";
    private static final String HARD_CODE_M_INSTANCE = "mInstance";

    private static class MyHashMap<K, V> extends HashMap<K, V> {
        HashMap<K, V> map;

        MyHashMap(HashMap<K, V> map) {
            this.map = map;
        }

        @Override
        public V get(Object key) {
            V v = map.get(key);
            if (DEBUG) {
                Log.d(TAG, "[MyHashMap.get] key: " + key + " val: " + v);
            }
            return v;
        }

        @Override
        public V put(K key, V value) {
            V v = map.put(key, value);
            if (DEBUG) {
                Log.d(TAG, "[MyHashMap.put] key : " + key + " val: " + value);
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
                Log.d(TAG, "[MyAMP.invoke] method: " + method.getName() + " args: "
                        + (args == null ? "null" : Arrays.asList(args))
                        /*+ " proxy: " + proxy*/ + " ori: " + original);
            }

            return method.invoke(original, args);
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        HashMap<String, IBinder> cache = (HashMap<String, IBinder>) ReflectUtil
                .getStaticField(HARD_CODE_SM, HARD_CODE_SCACHE);
        for (String k : cache.keySet()) {
            if (DEBUG) {
                Log.d(TAG, "[DemoApp.attachBaseContext] now cache, k: " + k + " val: " + cache.get(k));
            }
        }
        MyHashMap<String, IBinder> my = new MyHashMap<>(cache);
        ReflectUtil.replaceStaticField(HARD_CODE_SM, HARD_CODE_SCACHE, my);

        Object gDefault = ReflectUtil.getStaticField(HARD_CODE_AMN, HARD_CODE_G_DEFAULT);
        Object amp = ReflectUtil.getField(gDefault, HARD_CODE_M_INSTANCE);
        Object proxy = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class[]{ReflectUtil.getClassOrNull(HARD_CODE_IAM)},
                new MyAMP(amp)
        );
        ReflectUtil.replaceField(gDefault, HARD_CODE_M_INSTANCE, proxy);

        super.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
