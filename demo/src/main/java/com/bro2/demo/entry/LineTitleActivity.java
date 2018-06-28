package com.bro2.demo.entry;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;

import com.bro2.demo.R;
import com.bro2.ui.LineTitleLayout;

/**
 * Created by Brotoo on 2018/6/21
 */
public class LineTitleActivity extends Activity {

    private LineTitleLayout titleLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_line_title);

        titleLayout = findViewById(R.id.ltl_title);

        titleLayout.setOnElementClickListener("back", new LineTitleLayout.OnElementClickListener() {
            @Override
            public void onClick(View view, String action) {
                finish();
            }
        });
    }

    private boolean progressStyle;
    private boolean borderStyle;


    public void debug(View view) {
        switch (view.getId()) {
            case R.id.bt_progress_visible:
                titleLayout.setProgressVisible(!titleLayout.getProgressVisible());
                break;
            case R.id.bt_inc_progress:
                titleLayout.setProgress(titleLayout.getProgress() + 10);
                break;
            case R.id.bt_dec_progress:
                titleLayout.setProgress(titleLayout.getProgress() - 10);
                break;
            case R.id.bt_progress_style:
                Drawable progressDrawable;
                if (!progressStyle) {
                    progressStyle = true;
                    progressDrawable = new ClipDrawable(new ColorDrawable(Color.BLUE), Gravity.START, ClipDrawable.HORIZONTAL);
                } else {
                    progressStyle = false;
                    progressDrawable = getResources().getDrawable(R.drawable.horizontal_progress);
                }
                titleLayout.setProgressDrawable(progressDrawable);
                break;
            case R.id.bt_border_visible:
                titleLayout.setBorderVisible(!titleLayout.getBorderVisible());
                break;
            case R.id.bt_border_style:
                Drawable borderDrawable;
                if (!borderStyle) {
                    borderStyle = true;
                    borderDrawable = new ColorDrawable(getResources().getColor(R.color.colorPrimary));
                } else {
                    borderStyle = false;
                    borderDrawable = getResources().getDrawable(R.drawable.border_shadow);
                }
                titleLayout.setBorderDrawable(borderDrawable);
                break;
            case R.id.bt_title_layout_visible:
                titleLayout.setLayoutVisible(!titleLayout.getLayoutVisible());
                break;
            case R.id.bt_primary_start:
                titleLayout.setPrimaryGravity(LineTitleLayout.PRIMARY_GRAVITY_START);
                break;
            case R.id.bt_primary_center:
                titleLayout.setPrimaryGravity(LineTitleLayout.PRIMARY_GRAVITY_CENTER);
                break;
            case R.id.bt_primary_end:
                titleLayout.setPrimaryGravity(LineTitleLayout.PRIMARY_GRAVITY_END);
                break;
        }
    }
}
