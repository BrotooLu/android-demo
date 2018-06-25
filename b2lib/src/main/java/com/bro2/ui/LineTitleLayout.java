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
import com.bro2.util.DimensionUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Brotoo on 2018/6/21
 */
public class LineTitleLayout extends ViewGroup {

    public interface OnElementClickListener {
        void onClick(View view, String action);
    }

    private Drawable borderDrawable;
    private int borderHeight = getDefaultBorderHeight();
    private boolean borderVisible = true;
    private Drawable progressDrawable;
    private int progressHeight = getDefaultProgressHeight();
    private boolean progressVisible = true;
    private int progress;
    private boolean layoutVisible = true;

    private HashMap<String, Object> actions = new HashMap<>();
    private Map<String, OnElementClickListener> listenerMap = new HashMap<>();

    private OnClickListener listenerDispatcher;

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
            return;
        }

        try {
            for (int i = 0, n = a.getIndexCount(); i < n; ++i) {
                final int attr = a.getIndex(i);
                if (attr == R.styleable.LineTitleLayout_layoutVisible) {
                    layoutVisible = a.getBoolean(attr, true);
                } else if (attr == R.styleable.LineTitleLayout_borderDrawable) {
                    borderDrawable = a.getDrawable(attr);
                } else if (attr == R.styleable.LineTitleLayout_borderHeight) {
                    borderHeight = a.getDimensionPixelSize(attr, getDefaultBorderHeight());
                } else if (attr == R.styleable.LineTitleLayout_borderVisible) {
                    borderVisible = a.getBoolean(attr, true);
                } else if (attr == R.styleable.LineTitleLayout_progressDrawable) {
                    progressDrawable = a.getDrawable(attr);
                } else if (attr == R.styleable.LineTitleLayout_progressHeight) {
                    progressHeight = a.getDimensionPixelSize(attr, getDefaultProgressHeight());
                } else if (attr == R.styleable.LineTitleLayout_progressVisible) {
                    progressVisible = a.getBoolean(attr, true);
                } else if (attr == R.styleable.LineTitleLayout_progress) {
                    progress = a.getInt(attr, 0);
                    if (progress < 0) {
                        progress = 0;
                    } else if (progress > 100) {
                        progress = 100;
                    }
                }
            }
        } finally {
            a.recycle();
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        if (!layoutVisible) {
            int height = 0;
            if (borderVisible) {
                height = Math.max(height, borderHeight);
            }

            if (progressVisible) {
                height = Math.max(height, progressHeight);
            }

            if (height > heightSize && heightMode == MeasureSpec.AT_MOST) {
                height = heightSize;
            }

            setMeasuredDimension(widthSize, height);
            return;
        }

        final int childCount = getChildCount();
        for (int i = 0; i < childCount; ++i) {
            final View child = getChildAt(i);
            if (child.getVisibility() == GONE) {
                continue;
            }

            LayoutParams params = (LayoutParams) child.getLayoutParams();
            final float widthPercent = params.widthPercent;
            final int childHeightMeasureSpec = getChildMeasureSpec(heightMeasureSpec, 0, params.height);
            final int childWidthMeasureSpec;
            if (widthPercent > 0) {
                childWidthMeasureSpec = MeasureSpec.makeMeasureSpec((int) (widthSize * widthPercent), MeasureSpec.EXACTLY);
            } else {
                childWidthMeasureSpec = getChildMeasureSpec(widthMeasureSpec, 0, params.width);
            }

            child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
        }

        int width;
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
            width = 0;
            for (int i = 0; i < childCount; ++i) {
                final View child = getChildAt(i);

                if (child.getVisibility() == GONE) {
                    continue;
                }

                width += child.getMeasuredWidth();
            }

            if (width < widthSize || widthMode == MeasureSpec.AT_MOST) {
                width = widthSize;
            }
        }

        int height;
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            height = 0;
            for (int i = 0; i < childCount; ++i) {
                final View child = getChildAt(i);

                if (child.getVisibility() == GONE) {
                    continue;
                }

                height = Math.max(child.getMeasuredHeight(), height);
            }

            if (height > heightSize) {
                height = heightSize;
            }
        }

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (!layoutVisible) {
            return;
        }

        int offMark = 0;
        boolean accOrder = true;

        final int width = getMeasuredWidth();
        final int height = getMeasuredHeight();

        for (int i = 0, layoutChildIndex = -1, n = getChildCount(); i < n; ++i) {
            if (accOrder) {
                layoutChildIndex++;
            } else {
                layoutChildIndex--;
            }

            final View child = getChildAt(layoutChildIndex);
            if (child.getVisibility() == GONE) {
                continue;
            }

            final int childHeight = child.getMeasuredHeight();
            final int top = (height - childHeight) / 2;
            final int childWidth = child.getMeasuredWidth();
            final int bottom = (height + childHeight) / 2;

            final LayoutParams params = (LayoutParams) child.getLayoutParams();
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

    private int getDefaultBorderHeight() {
        return DimensionUtil.dp2px(getContext(), 1);
    }

    private int getDefaultProgressHeight() {
        return DimensionUtil.dp2px(getContext(), 3);
    }

    public void setProgress(int progress) {
        if (progress != this.progress && progress >= 0 && progress <= 100) {
            this.progress = progress;
            invalidate(getProgressRect());
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

    public boolean getLayoutVisible() {
        return layoutVisible;
    }

    public void setLayoutVisible(boolean visible) {
        if (layoutVisible != visible) {
            layoutVisible = visible;
            for (int i = 0, n = getChildCount(); i < n; ++i) {
                getChildAt(i).setVisibility(visible ? VISIBLE : GONE);
            }
            requestLayout();
        }
    }

    public boolean setOnElementClickListener(String action, OnElementClickListener listener) {
        if (!actions.containsKey(action)) {
            return false;
        }

        if (listener == listenerMap.get(action)) {
            return false;
        }

        listenerMap.put(action, listener);

        if (listenerDispatcher == null) {
            listenerDispatcher = new OnClickListener() {
                @Override
                public void onClick(View v) {
                    LayoutParams params = (LayoutParams) v.getLayoutParams();
                    String action = params.action;
                    OnElementClickListener listener = listenerMap.get(action);
                    if (listener != null) {
                        listener.onClick(v, action);
                    }
                }
            };

            for (int i = 0, n = getChildCount(); i < n; ++i) {
                getChildAt(i).setOnClickListener(listenerDispatcher);
            }
        }

        return true;
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        super.addView(child, index, params);

        if (listenerDispatcher != null) {
            child.setOnClickListener(listenerDispatcher);
        }

        if (params instanceof LayoutParams) {
            actions.put(((LayoutParams) params).action, null);
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
        public String element;
        public String action;
        public boolean primary;
        public float widthPercent;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);

            TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.LineTitleLayout_Layout);
            if (a != null) {
                try {
                    element = a.getString(R.styleable.LineTitleLayout_Layout_element);
                    action = a.getString(R.styleable.LineTitleLayout_Layout_action);
                    primary = a.getBoolean(R.styleable.LineTitleLayout_Layout_primary, false);
                    widthPercent = a.getFloat(R.styleable.LineTitleLayout_Layout_widthPercent, 0);

                    if (widthPercent < 0) {
                        widthPercent = 0;
                    } else if (widthPercent > 1) {
                        widthPercent = 1;
                    }
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
                widthPercent = params.widthPercent;
            }
        }
    }
}
