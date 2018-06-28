package com.bro2.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.bro2.b2lib.R;
import com.bro2.exception.B2Exception;
import com.bro2.util.DimensionUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Brotoo on 2018/6/21
 */
public class LineTitleLayout extends ViewGroup {
    public static final int DEFAULT_HEIGHT_DP_LAYOUT = 48;
    public static final int DEFAULT_HEIGHT_DP_BORDER = 1;
    public static final int DEFAULT_HEIGHT_DP_PROGRESS = 3;

    public static final int PRIMARY_GRAVITY_START = 0;
    public static final int PRIMARY_GRAVITY_CENTER = 1;
    public static final int PRIMARY_GRAVITY_END = 2;

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
    private int primaryGravity = PRIMARY_GRAVITY_CENTER;

    private Map<String, Integer> names = new HashMap<>();
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
        super(context, attrs, defStyleAttr);

        init(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public LineTitleLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        init(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        saveAttributes(context, attrs, defStyleAttr, defStyleRes);

        Resources resources = context.getResources();
        if (borderDrawable == null) {
            borderDrawable = resources.getDrawable(R.drawable.border_shadow);
        }

        if (progressDrawable == null) {
            progressDrawable = resources.getDrawable(R.drawable.horizontal_progress);
        }
        progressDrawable.mutate();
    }

    private void saveAttributes(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        setWillNotDraw(false);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LineTitleLayout, defStyleAttr, defStyleRes);
        if (a == null) {
            return;
        }

        try {
            for (int i = 0, n = a.getIndexCount(); i < n; ++i) {
                final int attr = a.getIndex(i);
                if (attr == R.styleable.LineTitleLayout_layoutVisible) {
                    layoutVisible = a.getBoolean(attr, true);
                } else if (attr == R.styleable.LineTitleLayout_borderRes) {
                    borderDrawable = a.getDrawable(attr);
                } else if (attr == R.styleable.LineTitleLayout_borderHeight) {
                    borderHeight = a.getDimensionPixelSize(attr, getDefaultBorderHeight());
                } else if (attr == R.styleable.LineTitleLayout_borderVisible) {
                    borderVisible = a.getBoolean(attr, true);
                } else if (attr == R.styleable.LineTitleLayout_progressRes) {
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
                } else if (attr == R.styleable.LineTitleLayout_primaryGravity) {
                    primaryGravity = a.getInt(attr, PRIMARY_GRAVITY_CENTER);
                }
            }
        } finally {
            a.recycle();
        }

    }

    private void measureChild(View child, int widthMeasureSpec, int heightMeasureSpec, int widthSize, LayoutParams params) {
        final double widthPercent = params.widthPercent;
        final int childHeightMeasureSpec = getChildMeasureSpec(heightMeasureSpec, 0, params.height);
        final int childWidthMeasureSpec;
        if (widthPercent > 0) {
            childWidthMeasureSpec = MeasureSpec.makeMeasureSpec((int) (widthSize * widthPercent), MeasureSpec.EXACTLY);
        } else {
            childWidthMeasureSpec = getChildMeasureSpec(widthMeasureSpec, 0, params.width);
        }

        child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
    }

    private void measureChildAtMost(View child, int maxWidth, int heightMeasureSpec) {
        child.measure(MeasureSpec.makeMeasureSpec(maxWidth, MeasureSpec.AT_MOST), heightMeasureSpec);
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

        View primary = null;

        int totalWidth = 0;
        int totalHeight = 0;
        final int childCount = getChildCount();
        int leftWidth = 0;
        int rightWidth = -1;
        for (int i = 0, rightMark = 0; i < childCount; ++i) {
            boolean fromRight = rightMark != 0;
            final View child = getChildAt(fromRight ? childCount - rightMark : i);
            if (child.getVisibility() == GONE) {
                continue;
            }

            LayoutParams params = (LayoutParams) child.getLayoutParams();
            if (params.primary) {
                if (primary != null) {
                    throw new B2Exception("only one primary supported");
                }
                primary = child;
                rightWidth = 0;
                rightMark = 1;
                continue;
            }

            measureChild(child, widthMeasureSpec, heightMeasureSpec, widthSize, params);

            int childWidth = child.getMeasuredWidth();
            int max = widthSize > 0 ? widthSize - totalWidth : Integer.MAX_VALUE;
            if (max < childWidth) {
                measureChildAtMost(child, max, heightMeasureSpec);
                childWidth = child.getMeasuredWidth();
            }

            if (fromRight) {
                rightWidth += childWidth;
                rightMark++;
            } else {
                leftWidth += childWidth;
            }

            totalWidth += childWidth;
            totalHeight = Math.max(child.getMeasuredHeight(), totalHeight);

            if (totalWidth >= widthSize && widthSize > 0) {
                break;
            }
        }

        int primaryMax = widthSize - (primaryGravity == PRIMARY_GRAVITY_CENTER ? (Math.max(leftWidth, rightWidth) * 2) : totalWidth);
        if (primary != null) {
            if (primaryMax > 0) {
                LayoutParams params = (LayoutParams) primary.getLayoutParams();
                measureChild(primary, widthMeasureSpec, heightMeasureSpec, widthSize, params);
                int primaryWidth = primary.getMeasuredWidth();
                if (primaryWidth > primaryMax) {
                    measureChildAtMost(primary, primaryMax, heightMeasureSpec);
                    primaryWidth = primary.getMeasuredWidth();
                }
                totalWidth += primaryWidth;
                totalHeight = Math.max(primary.getMeasuredHeight(), totalHeight);
            } else {
                measureChildAtMost(primary, 0, heightMeasureSpec);
            }
        }

        if (widthMode == MeasureSpec.EXACTLY) {
            totalWidth = widthSize;
        } else {
            if (totalWidth < widthSize || widthMode == MeasureSpec.AT_MOST) {
                totalWidth = widthSize;
            }
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            totalHeight = heightSize;
        } else {
            if (totalHeight > heightSize) {
                totalHeight = heightSize;
            }
        }

        setMeasuredDimension(totalWidth, totalHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (!layoutVisible) {
            return;
        }


        final int width = getMeasuredWidth();
        final int height = getMeasuredHeight();

        View primary = null;
        int offset = 0;
        int leftOffset = 0;
        int primaryTop = 0;
        int primaryBottom = 0;
        int primaryWidth = 0;

        for (int i = 0, rightMark = 0, n = getChildCount(); i < n; ++i) {
            boolean fromRight = rightMark != 0;

            final View child = getChildAt(fromRight ? n - rightMark : i);
            if (child.getVisibility() == GONE) {
                continue;
            }

            final int childHeight = child.getMeasuredHeight();
            final int top = (height - childHeight) / 2;
            final int childWidth = child.getMeasuredWidth();
            final int bottom = (height + childHeight) / 2;

            final LayoutParams params = (LayoutParams) child.getLayoutParams();
            if (params.primary) {
                rightMark = 1;
                offset = width;
                primary = child;
                primaryWidth = childWidth;
                primaryTop = top;
                primaryBottom = bottom;
            } else if (!fromRight) {
                int newMark = offset + childWidth;
                child.layout(offset, top, newMark, bottom);
                leftOffset = offset = newMark;
            } else {
                int newMark = offset - childWidth;
                child.layout(newMark, top, offset, bottom);
                offset = newMark;
                rightMark++;
            }
        }

        if (primary != null) {
            switch (primaryGravity) {
                case PRIMARY_GRAVITY_START:
                    primary.layout(leftOffset, primaryTop, leftOffset + primaryWidth, primaryBottom);
                    break;
                case PRIMARY_GRAVITY_CENTER:
                    primary.layout((width - primaryWidth) / 2, primaryTop, (width + primaryWidth) / 2, primaryBottom);
                    break;
                case PRIMARY_GRAVITY_END:
                    primary.layout(offset - primaryWidth, primaryTop, offset, primaryBottom);
                    break;
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
        return DimensionUtil.dp2px(getContext(), DEFAULT_HEIGHT_DP_BORDER);
    }

    private int getDefaultProgressHeight() {
        return DimensionUtil.dp2px(getContext(), DEFAULT_HEIGHT_DP_PROGRESS);
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
            progressDrawable.mutate();
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

    public void setBorderHeight(int height) {
        if (borderHeight != height) {
            borderHeight = height;
            invalidate();
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

    public int getChildIndex(String name) {
        Integer index = names.get(name);
        return index == null ? -1 : index;
    }

    public boolean removeView(String name) {
        int index = getChildIndex(name);
        if (index == -1) {

            return false;
        }

        removeViewAt(index);
        return true;
    }

    public void setPrimaryGravity(int gravity) {
        if (primaryGravity == gravity) {
            return;
        }

        switch (gravity) {
            case PRIMARY_GRAVITY_START:
            case PRIMARY_GRAVITY_CENTER:
            case PRIMARY_GRAVITY_END:
                primaryGravity = gravity;
                requestLayout();
                break;
        }
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (params instanceof LayoutParams) {
            String element = ((LayoutParams) params).element;
            String action = ((LayoutParams) params).action;
            if (TextUtils.isEmpty(element)) {
                throw new RuntimeException("no element name");
            }

            int old = getChildIndex(element);
            if (old != -1) {
                index = old;
                removeViewAt(index);
            }

            names.put(element, index > 0 ? index : getChildCount());

            if (!TextUtils.isEmpty(action)) {
                actions.put(action, null);
            }
        } else {
            throw new RuntimeException("not suitable layout params");
        }

        if (listenerDispatcher != null) {
            child.setOnClickListener(listenerDispatcher);
        }

        super.addView(child, index, params);
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
        public double widthPercent;

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
