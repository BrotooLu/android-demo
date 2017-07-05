package com.bro2.demo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.TextView;

public class JsBridgeActivity extends Activity {
    private WebView mWebView;
    private TextView mWebResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_js_bridge);

        mWebResult = (TextView) findViewById(R.id.tv_web_result);
        mWebView = (WebView) findViewById(R.id.wv_content);
        mWebView.loadUrl("file:///android_asset/web.html");

        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(new JsBridge(), "JsBridge");

        mWebView.setWebChromeClient(new WebChromeClient(){
            @Override
            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
                mWebResult.setText("url: " + url + " message: " + message + " default: " + defaultValue);
                result.confirm("done js prompt");
                return true;
            }
        });
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
