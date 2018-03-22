package com.bro2.demo;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.bro2.demo.entry.DgActivity;
import com.bro2.demo.entry.JsBridgeActivity;
import com.bro2.demo.entry.LocalSocketActivity;
import com.bro2.demo.entry.MiPipeActivity;
import com.bro2.demo.entry.ProgressBarActivity;
import com.bro2.demo.entry.WebViewReuseActivity;

import java.util.ArrayList;

/**
 * Created by Bro2 on 2017/6/4
 */

public class MainActivity extends ListActivity {
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
        addCase(new TestCase("DgActivity") {
            @Override
            void onClick() {
                startActivity(new Intent(MainActivity.this, DgActivity.class));
            }
        });

        addCase(new TestCase("JsBridgeActivity") {
            @Override
            void onClick() {
                startActivity(new Intent(MainActivity.this, JsBridgeActivity.class));
            }
        });

        addCase(new TestCase("LocalSocketActivity") {
            @Override
            void onClick() {
                startActivity(new Intent(MainActivity.this, LocalSocketActivity.class));
            }
        });

        addCase(new TestCase("MiPipeActivity") {
            @Override
            void onClick() {
                startActivity(new Intent(MainActivity.this, MiPipeActivity.class));
            }
        });

        addCase(new TestCase("ProgressBarActivity") {
            @Override
            void onClick() {
                startActivity(new Intent(MainActivity.this, ProgressBarActivity.class));
            }
        });

        addCase(new TestCase("WebViewReuseActivity") {
            @Override
            void onClick() {
                startActivity(new Intent(MainActivity.this, WebViewReuseActivity.class));
            }
        });
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
