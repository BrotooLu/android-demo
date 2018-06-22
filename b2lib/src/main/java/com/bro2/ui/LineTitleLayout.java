package com.bro2.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.bro2.b2lib.R;

/**
 * Created by Brotoo on 2018/6/21
 */
public class LineTitleLayout extends ViewGroup {
    private int height = -1;
    private int zIndex;
    private Drawable border;
    private int borderHeight;
    private boolean borderVisible;
    private Drawable progressDrawable;
    private boolean progressVisible;

    public LineTitleLayout(Context context) {
        this(context, null);
    }

    public LineTitleLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LineTitleLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public LineTitleLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        saveAttributes(context, attrs, defStyleAttr, defStyleRes);
    }

    private void saveAttributes(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LineTitleLayout, defStyleAttr, defStyleRes);
        if (a == null) {
            return;
        }

        try {
            for (int i = 0, l = a.getIndexCount(); i < l; ++i) {
                int attr = a.getIndex(i);
                if (attr == R.styleable.LineTitleLayout_height) {
                    height = a.getDimensionPixelSize(attr, getDefaultHeight());

                } else if (attr == R.styleable.LineTitleLayout_zIndex) {
                    zIndex = a.getInt(attr, 0);

                } else if (attr == R.styleable.LineTitleLayout_border) {
                    border = a.getDrawable(attr);

                } else if (attr == R.styleable.LineTitleLayout_borderHeight) {
                    borderHeight = a.getDimensionPixelSize(attr, -1);

                } else if (attr == R.styleable.LineTitleLayout_borderVisible) {
                    borderVisible = a.getBoolean(attr, true);

                } else if (attr == R.styleable.LineTitleLayout_progress) {
                    progressDrawable = a.getDrawable(attr);

                } else if (attr == R.styleable.LineTitleLayout_progressVisible) {
                    progressVisible = a.getBoolean(attr, true);

                }
            }
        } finally {
            a.recycle();
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), height != -1 ? height : getDefaultHeight());
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        int leftOff = l;

        for (int i = 0, n = getChildCount(); i < n; ++i) {
            View child = getChildAt(i);
            if (child == null || child.getVisibility() == GONE) {
                continue;
            }

            int newLeftOff = leftOff + child.getMeasuredWidth();
            child.layout(leftOff, t, newLeftOff, t + child.getMeasuredHeight());
            leftOff = newLeftOff;
        }
    }

    private int getDefaultHeight() {
        return 300;
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public static class LayoutParams extends ViewGroup.LayoutParams {
        String element;
        String action;
        boolean primary;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);

            TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.LineTitleLayout_Layout);
            if (a != null) {
                try {
                    element = a.getString(R.styleable.LineTitleLayout_Layout_element);
                    action = a.getString(R.styleable.LineTitleLayout_Layout_action);
                    primary = a.getBoolean(R.styleable.LineTitleLayout_Layout_primary, false);
                } finally {
                    a.recycle();
                }
            }
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
    }
}
