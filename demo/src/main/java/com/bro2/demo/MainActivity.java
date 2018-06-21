package com.bro2.demo;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Bro2 on 2017/6/4
 */

public class MainActivity extends ListActivity {
    private static final String TAG = DemoEnv.TAG_PREFIX + "main";

    private abstract static class TestCase implements Comparable<TestCase> {
        int priority = Integer.MIN_VALUE;
        String title;

        TestCase(String title) {
            this.title = title;
        }

        @Override
        public int compareTo(@NonNull TestCase o) {
            return priority - o.priority;
        }

        abstract void onClick();
    }

    private final ArrayList<TestCase> mCases = new ArrayList<>();

    {
        Context ctx = DemoApp.getApplication();
        PackageManager pm = ctx.getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);
            ActivityInfo[] activities = packageInfo == null ? null : packageInfo.activities;
            for (int i = 0, l = activities == null ? 0 : activities.length; i < l; ++i) {
                ActivityInfo activity = activities[i];
                String name = activity.name;
                if (name.startsWith("com.bro2.demo.entry")) {
                    String tag = name.substring(name.lastIndexOf('.') + 1);
                    final Class target = Class.forName(name);

                    addCase(new TestCase(tag) {
                        @Override
                        void onClick() {
                            startActivity(new Intent(MainActivity.this, target));
                        }
                    });
                }

            }
        } catch (Throwable e) {
            Log.e(TAG, null, e);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setListAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return mCases.size();
            }

            @Override
            public Object getItem(int i) {
                return mCases.get(i);
            }

            @Override
            public long getItemId(int i) {
                return i;
            }

            @Override
            public View getView(int i, View view, ViewGroup viewGroup) {
                TextView textView;
                if (view == null) {
                    view = textView = new TextView(viewGroup.getContext());
                    textView.setPadding(60, 20, 60, 20);
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                } else {
                    textView = (TextView) view;
                }

                TestCase testCase = (TestCase) getItem(i);
                textView.setText(testCase.title);
                return view;
            }
        });
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        TestCase testCase = (TestCase) getListAdapter().getItem(position);
        testCase.onClick();
    }

    private void addCase(TestCase testCase) {
        if (testCase.priority == Integer.MIN_VALUE) {
            testCase.priority = mCases.size();
        }
        mCases.add(testCase);
    }
}
