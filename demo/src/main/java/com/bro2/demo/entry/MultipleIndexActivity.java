package com.bro2.demo.entry;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;

import com.bro2.demo.R;
import com.bro2.ui.LineTitleLayout;

/**
 * Created by Brotoo on 2018/6/21
 */
public class MultipleIndexActivity extends Activity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_multiple_index);

        LineTitleLayout titleLayout = findViewById(R.id.ltl_title);

        titleLayout.setOnElementClickListener("back", new LineTitleLayout.OnElementClickListener() {
            @Override
            public void onClick(View view, String action) {
                finish();
            }
        });

        WebView view = findViewById(R.id.wv_content);
        view.loadUrl("http://www.brotoolu.com/");
    }


}
