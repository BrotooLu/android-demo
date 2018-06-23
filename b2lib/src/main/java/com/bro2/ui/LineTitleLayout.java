package com.bro2.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
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
    private Drawable borderDrawable;
    private int borderHeight;
    private boolean borderVisible;
    private Drawable progressDrawable;
    private int progressHeight;
    private boolean progressVisible;
    private int progress;

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
        setWillNotDraw(false);
    }

    private void saveAttributes(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LineTitleLayout, defStyleAttr, defStyleRes);
        if (a == null) {
            height = getDefaultHeight();
            borderDrawable = null;
            borderHeight = getDefaultBorderHeight();
            borderVisible = true;
            progressDrawable = null;
            progressHeight = getDefaultProgressHeight();
            progressVisible = true;
            progress = 0;
            return;
        }

        try {
            height = a.getDimensionPixelSize(R.styleable.LineTitleLayout_height, getDefaultHeight());
            borderDrawable = a.getDrawable(R.styleable.LineTitleLayout_borderDrawable);
            borderHeight = a.getDimensionPixelSize(R.styleable.LineTitleLayout_borderHeight, getDefaultBorderHeight());
            borderVisible = a.getBoolean(R.styleable.LineTitleLayout_borderVisible, true);
            progressDrawable = a.getDrawable(R.styleable.LineTitleLayout_progressDrawable);
            progressHeight = a.getDimensionPixelSize(R.styleable.LineTitleLayout_progressHeight, getDefaultProgressHeight());
            progressVisible = a.getBoolean(R.styleable.LineTitleLayout_progressVisible, true);
            progress = a.getInt(R.styleable.LineTitleLayout_progress, 0);
            if (progress < 0) {
                progress = 0;
            } else if (progress > 100) {
                progress = 100;
            }
        } finally {
            a.recycle();
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int mode = MeasureSpec.getMode(heightMeasureSpec);
        measureChildren(widthMeasureSpec, MeasureSpec.makeMeasureSpec(height, mode));
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        int offMark = 0;
        boolean accOrder = true;

        for (int i = 0, layoutChildIndex = -1, n = getChildCount(); i < n; ++i) {
            if (accOrder) {
                layoutChildIndex++;
            } else {
                layoutChildIndex--;
            }

            View child = getChildAt(layoutChildIndex);
            if (child == null || child.getVisibility() == GONE) {
                continue;
            }

            int width = getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();
            int top = (height - childHeight) / 2;
            int childWidth = child.getMeasuredWidth();
            int bottom = (height + childHeight) / 2;

            LayoutParams params = (LayoutParams) child.getLayoutParams();
            if (params.primary) {
                child.layout((width - childWidth) / 2, top, (width + childWidth) / 2, bottom);
                accOrder = false;
                layoutChildIndex = n;
                offMark = width;
            } else {
                if (accOrder) {
                    int newMark = offMark + childWidth;
                    child.layout(offMark, top, newMark, bottom);
                    offMark = newMark;
                } else {
                    int newMark = offMark - childWidth;
                    child.layout(newMark, top, offMark, bottom);
                    offMark = newMark;
                }
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (borderDrawable != null && borderVisible) {
            canvas.save();
            borderDrawable.setBounds(getBorderRect());
            borderDrawable.draw(canvas);
            canvas.restore();
        }

        if (progressDrawable != null && progressVisible) {
            canvas.save();
            progressDrawable.setLevel(progress * 100);
            progressDrawable.setBounds(getProgressRect());
            progressDrawable.draw(canvas);
            canvas.restore();
        }
    }

    private int getDefaultHeight() {
        return 300;
    }

    private int getDefaultBorderHeight() {
        return 20;
    }

    private int getDefaultProgressHeight() {
        return 10;
    }

    public void setProgress(int progress) {
        if (progress != this.progress && progress >= 0 && progress <= 100) {
            this.progress = progress;
            invalidate();
        }
    }

    public int getProgress() {
        return progress;
    }

    private Rect getProgressRect() {
        int height = getMeasuredHeight();
        int width = getMeasuredWidth();
        return new Rect(0, height - progressHeight, width, height);
    }

    public boolean getProgressVisible() {
        return progressVisible;
    }

    public void setProgressVisible(boolean visible) {
        if (progressVisible != visible) {
            progressVisible = visible;
            invalidate(getProgressRect());
        }
    }

    public void setProgressDrawable(Drawable drawable) {
        if (progressDrawable != drawable) {
            progressDrawable = drawable;
            invalidate(getProgressRect());
        }
    }

    public boolean getBorderVisible() {
        return borderVisible;
    }

    private Rect getBorderRect() {
        int height = getMeasuredHeight();
        int width = getMeasuredWidth();
        return new Rect(0, height - borderHeight, width, height);
    }

    public void setBorderVisible(boolean visible) {
        if (borderVisible != visible) {
            borderVisible = visible;
            invalidate(getBorderRect());
        }
    }

    public void setBorderDrawable(Drawable drawable) {
        if (borderDrawable != drawable) {
            borderDrawable = drawable;
            invalidate(getBorderRect());
        }
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

            if (source instanceof LayoutParams) {
                LayoutParams params = (LayoutParams) source;
                element = params.element;
                action = params.action;
                primary = params.primary;
            }
        }
    }
}
