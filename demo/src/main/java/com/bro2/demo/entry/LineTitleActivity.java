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
import com.bro2.util.BitOperator;

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
    }


    private static final int MASK_PROGRESS = 1;
    private static final int MASK_BORDER = 1 << 1;

    private int styleFlag = 0;

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
                if (!BitOperator.getBit(styleFlag, MASK_PROGRESS)) {
                    styleFlag = BitOperator.setBit(styleFlag, MASK_PROGRESS, true);
                    progressDrawable = new ClipDrawable(new ColorDrawable(Color.BLUE), Gravity.START, ClipDrawable.HORIZONTAL);
                } else {
                    styleFlag = BitOperator.setBit(styleFlag, MASK_PROGRESS, false);
                    progressDrawable = getResources().getDrawable(R.drawable.progress_horizontal_drawable);
                }
                titleLayout.setProgressDrawable(progressDrawable);
                break;
            case R.id.bt_border_visible:
                titleLayout.setBorderVisible(!titleLayout.getBorderVisible());
                break;
            case R.id.bt_border_style:
                ColorDrawable borderDrawable = new ColorDrawable();
                if (!BitOperator.getBit(styleFlag, MASK_BORDER)) {
                    styleFlag = BitOperator.setBit(styleFlag, MASK_BORDER, true);
                    borderDrawable.setColor(getResources().getColor(R.color.colorPrimary));
                } else {
                    styleFlag = BitOperator.setBit(styleFlag, MASK_BORDER, false);
                    borderDrawable.setColor(getResources().getColor(R.color.colorAccent));
                }
                titleLayout.setBorderDrawable(borderDrawable);
                break;
        }
    }
}
