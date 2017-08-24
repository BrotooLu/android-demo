package com.bro2.b2lib.webviewpool;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.WebView;

import com.bro2.b2lib.B2Exception;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import static com.bro2.b2lib.B2LibEnv.DEBUG;
import static com.bro2.b2lib.B2LibEnv.TAG_PREFIX;

/**
 * Created by Bro2 on 2017/8/22
 */

public class WebViewPool {
    private static final String TAG = TAG_PREFIX + "web_view_pool";

    private final ConcurrentHashMap<WebView, Object> mUsingViews = new ConcurrentHashMap<>();
    private final ConcurrentLinkedQueue<WebView> mIdleViews = new ConcurrentLinkedQueue<>();

    private final int mMax;

    private final Application mApp;

    private final WebViewFactory mViewFactory;

    public interface WebViewFactory {

        WebView newWebView(Context ctx);

    }

    public WebViewPool(Application application, int max, WebViewFactory factory) {
        mApp = application;
        mMax = max;
        if (factory != null) {
            mViewFactory = factory;
        } else {
            mViewFactory = new WebViewFactory() {
                @Override
                public WebView newWebView(Context ctx) {
                    return new WebView(ctx);
                }
            };
        }
    }

    public WebView obtain(Context ctx) {
        if (ctx == null) {
            throw new B2Exception("NPE of context");
        }

        WebView view;
        if (mIdleViews.size() > 0) {
            view = mIdleViews.poll();
            MutableContext context = (MutableContext) view.getContext();
            context.setBaseContext(ctx);
            if (DEBUG) {
                Log.d(TAG, "[WebViewPool.obtain] reuse WebView");
            }
        } else {
            view = mViewFactory.newWebView(new MutableContext(ctx));
            if (view == null) {
                throw new B2Exception("NPE of WebView");
            }

            if (DEBUG) {
                Log.d(TAG, "[WebViewPool.obtain] new WebView");
            }
        }

        mUsingViews.put(view, Boolean.TRUE);
        return view;
    }

    public boolean recycle(WebView webView) {
        if (!mUsingViews.containsKey(webView)) {
            return false;
        }

        mUsingViews.remove(webView);

        ViewParent parent = webView.getParent();
        if (parent != null) {
            if (parent instanceof ViewGroup) {
                ((ViewGroup) parent).removeView(webView);
            } else {
                if (DEBUG) {
                    Log.e(TAG, "[WebViewPool.recycle] parent not a group");
                }
                return false;
            }
        }

        Context context = webView.getContext();
        if (context != null && context instanceof MutableContext) {
            ((MutableContext) context).setBaseContext(mApp);
            if (mIdleViews.size() < mMax) {
                mIdleViews.offer(webView);
            }
        }

        return true;
    }

}
