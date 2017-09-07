package com.bro2.demo.entry;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.bro2.demo.R;

import java.io.IOException;
import java.io.InputStream;

import static com.bro2.demo.DemoEnv.DEBUG;
import static com.bro2.demo.DemoEnv.TAG_PREFIX;

public class JsBridgeActivity extends Activity {
    private WebView mWebView;
    private TextView mWebResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_js_bridge);

        mWebResult = (TextView) findViewById(R.id.tv_web_result);
        mWebView = (WebView) findViewById(R.id.wv_content);

        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(new JsBridge(), "JsBridge");

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
                mWebResult.setText("url: " + url + " message: " + message + " default: " + defaultValue);
                result.confirm("done js prompt");
                return true;
            }
        });

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (DEBUG) {
                    Log.d(TAG_PREFIX, "shouldOverrideUrlLoading: " + url);
                }
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                if (DEBUG) {
                    Log.d(TAG_PREFIX, "shouldOverrideUrlLoading1: " + request.getUrl());
                }
                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                if (DEBUG) {
                    Log.d(TAG_PREFIX, "shouldInterceptRequest: old " + url);
                }

                if (url.contains("avatars3.githubusercontent.com")) {
                    if (DEBUG) {
                        Log.d(TAG_PREFIX, "shouldInterceptRequest: redirect");
                    }
                    InputStream input = null;
                    try {
                        input = getAssets().open("ic_launcher.png");
                    } catch (IOException e) {
                        if (DEBUG) {
                            Log.e(TAG_PREFIX, "shouldInterceptRequest", e);
                        }
                    }

                    WebResourceResponse response = new WebResourceResponse("image/png", "UTF-8", input);
                    return response;
                }
                return super.shouldInterceptRequest(view, url);
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                if (DEBUG) {
                    Log.d(TAG_PREFIX, "shouldInterceptRequest: " + request.getUrl());
                }
                return super.shouldInterceptRequest(view, request);
            }

        });

        mWebView.loadUrl("file:///android_asset/web.html");
    }

    public void callJs(View view) {
//        mWebView.loadUrl("javascript:setWebText(" + "'called from native'" + ")");

        mWebView.evaluateJavascript("setWebText(" + "'called from native')", new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {
                mWebResult.setText(value);
            }
        });
    }

    private class JsBridge {

        @JavascriptInterface
        String setText(final String txt) {
            mWebResult.post(new Runnable() {
                @Override
                public void run() {
                    mWebResult.setText(txt);
                }
            });
            return "done setText";
        }

    }
}
