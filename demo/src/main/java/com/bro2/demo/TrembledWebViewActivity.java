package com.bro2.demo;

import android.app.Activity;
import android.os.Bundle;

import com.bro2.demo.ui.TrembledWebView;

public class TrembledWebViewActivity extends Activity {
    private static final String url = "file:///android_asset/web-simple-input.html";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trembled_web_view);

        TrembledWebView webView = (TrembledWebView) findViewById(R.id.twv_content);
        webView.loadUrl(url);
    }
}
