package com.bro2.demo.entry;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.ProgressBar;

import com.bro2.demo.R;

public class ProgressBarActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_bar);

        ProgressBar progress = (ProgressBar) findViewById(R.id.pb_progress);

        ColorDrawable color = new ColorDrawable(Color.BLUE);
        ClipDrawable clip = new ClipDrawable(color, Gravity.START, ClipDrawable.HORIZONTAL);

        Drawable drawable = progress.getProgressDrawable();
        if (drawable != null && drawable instanceof LayerDrawable) {
            ((LayerDrawable) drawable).setDrawableByLayerId(android.R.id.progress, clip);
        } else {
            progress.setProgressDrawable(clip);
        }

        progress.setProgress(49);
    }
}
