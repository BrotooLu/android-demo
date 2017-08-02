package com.bro2.demo.ui;

import android.content.Context;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.StyleRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.FrameLayout;

import com.bro2.demo.R;

/**
 * Created by Bro2 on 2017/8/2
 */

public class TrembledWebView extends FrameLayout {
    private WebView mWebView;

    public TrembledWebView(@NonNull Context context) {
        this(context, null);
    }

    public TrembledWebView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public TrembledWebView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public TrembledWebView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        init(context);
    }

    private void init(Context ctx) {
        View view = LayoutInflater.from(ctx).inflate(R.layout.view_trembled_webview, this);
        mWebView = (WebView) view.findViewById(R.id.wv_trembled_content);
    }

    public void loadUrl(String url) {
        mWebView.loadUrl(url);
    }
}
