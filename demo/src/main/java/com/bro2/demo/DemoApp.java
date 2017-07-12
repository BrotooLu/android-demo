package com.bro2.demo;

import android.app.Application;
import android.content.Context;
import android.os.IBinder;
import android.util.Log;

import com.bro2.b2lib.util.ReflectUtil;

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
    public static class MyHashMap<K, V> extends HashMap<K, V> {
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

    @Override
    protected void attachBaseContext(Context base) {
        if (DEBUG) {
            Log.d(TAG, "[DemoApp.attachBaseContext] <----------------------------------------------");
        }
        HashMap<String, IBinder> cache = (HashMap<String, IBinder>) ReflectUtil
                .getStaticField("android.os.ServiceManager", "sCache");
        if (DEBUG) {
            Log.d(TAG, "[DemoApp.attachBaseContext] get sCache: " + cache + " size: " + cache.size());
        }
        for (String k : cache.keySet()) {
            if (DEBUG) {
                Log.d(TAG, "[DemoApp.attachBaseContext] now cache, k: " + k + " val: " + cache.get(k));
            }
        }
        MyHashMap<String, IBinder> my = new MyHashMap<>(cache);
        boolean res = ReflectUtil.replaceStaticField("android.os.ServiceManager", "sCache", my);
        if (DEBUG) {
            Log.d(TAG, "[DemoApp.attachBaseContext] ---------------------------------------------->" + res);
        }
        super.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
