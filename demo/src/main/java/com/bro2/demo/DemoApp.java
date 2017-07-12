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
 * Created on 2017/6/4.
 *
 * @author Bro2
 * @version 1.0
 */

public class DemoApp extends Application {
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

        public MyAMP(Object original) {
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
        if (DEBUG) {
            Log.d(TAG, "[DemoApp.attachBaseContext] <----------------------------------------------");
        }
        HashMap<String, IBinder> cache = (HashMap<String, IBinder>) ReflectUtil
                .getStaticField("android.os.ServiceManager", "sCache");
        for (String k : cache.keySet()) {
            if (DEBUG) {
                Log.d(TAG, "[DemoApp.attachBaseContext] now cache, k: " + k + " val: " + cache.get(k));
            }
        }
        MyHashMap<String, IBinder> my = new MyHashMap<>(cache);
        ReflectUtil.replaceStaticField("android.os.ServiceManager", "sCache", my);

        Object gDefault = ReflectUtil.getStaticField("android.app.ActivityManagerNative", "gDefault");
        Object amp = ReflectUtil.getField(gDefault, "mInstance");
        Object proxy = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class[]{ReflectUtil.getClassOrNull("android.app.IActivityManager")},
                new MyAMP(amp)
        );
        ReflectUtil.replaceField(gDefault, "mInstance", proxy);

        super.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
