package com.bro2.demo.entry;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.bro2.b2lib.webviewpool.WebViewPool;
import com.bro2.demo.DemoApp;
import com.bro2.demo.R;

import static com.bro2.demo.DemoEnv.DEBUG;
import static com.bro2.demo.DemoEnv.TAG;

public class WebViewReuseActivity extends Activity {
    private static WebViewPool mWebViewPool = new WebViewPool(DemoApp.getApplication(), 4,
            new WebViewPool.WebViewFactory() {
                @Override
                public WebView newWebView(Context ctx) {
                    WebView view = new WebView(ctx);

                    view.loadUrl("http://www.bro2.tk/");

                    view.setWebViewClient(new WebViewClient() {
                        long now;

                        @Override
                        public void onPageStarted(WebView view, String url, Bitmap favicon) {
                            super.onPageStarted(view, url, favicon);

                            if (DEBUG) {
                                now = System.currentTimeMillis();
                                Log.d(TAG, "onPageStarted: " + now);
                            }
                        }

                        @Override
                        public void onPageFinished(WebView view, String url) {
                            if (DEBUG) {
                                long duration = System.currentTimeMillis() - now;
                                Log.d(TAG, "onPageFinished duration: " + duration + " " + url);
                            }

                            super.onPageFinished(view, url);
                        }
                    });
                    return view;
                }
            });

    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view_reuse);

        ViewGroup mContent = (ViewGroup) findViewById(R.id.content);

        long now = System.currentTimeMillis();
        long duration;

        if (DEBUG) {
            Log.d(TAG, "[WebViewReuseActivity.onCreate] before: " + mWebView);
        }

        mWebView = mWebViewPool.obtain(this);
        duration = System.currentTimeMillis() - now;

        if (DEBUG) {
            Log.d(TAG, "[WebViewReuseActivity.onCreate] obtain duration: " + duration);
        }

        now = System.currentTimeMillis();

        mContent.addView(mWebView);
        duration = System.currentTimeMillis() - now;

        if (DEBUG) {
            Log.d(TAG, "[WebViewReuseActivity.onCreate] view duration: " + duration);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (DEBUG) {
            Log.d(TAG, "[WebViewReuseActivity.onStop] ...");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (DEBUG) {
            Log.e(TAG, "[WebViewReuseActivity.onDestroy]");
        }

        mWebViewPool.recycle(mWebView);
    }

    public void openNew(View view) {
        Intent intent = new Intent(this, getClass());
        startActivity(intent);
    }
}
