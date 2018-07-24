package com.bro2.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;

import com.bro2.b2lib.R;
import com.bro2.exception.B2Exception;

/**
 * Created by Brotoo on 2018/7/23
 */
public class MultipleIndexLayout extends ViewGroup {

    private final SparseIntArray indexHeightMap = new SparseIntArray();

    public MultipleIndexLayout(Context context) {
        this(context, null);
    }

    public MultipleIndexLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MultipleIndexLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MultipleIndexLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        init(context, attrs, defStyleAttr, defStyleRes);
    }


    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        indexHeightMap.clear();

        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int height = heightSize;
        int maxWidth = 0;
        int maxHeight = 0;
        boolean matchWidth = false;
        int childCount = getChildCount();

        for (int i = 0; i < childCount; ++i) {
            View child = getChildAt(i);
            if (child.getVisibility() == GONE) {
                continue;
            }

            LayoutParams params = (LayoutParams) child.getLayoutParams();
            if (!matchWidth && params.width == ViewGroup.LayoutParams.MATCH_PARENT) {
                matchWidth = true;
            }

            int index = params.zIndex;
            int old = indexHeightMap.get(index);
            if (old == 0) {
                height = heightSize;
            } else {
                height -= old;
            }

            measureChild(child, widthMeasureSpec, MeasureSpec.makeMeasureSpec(height, heightMode));
            int indexTotalHeight = old + child.getMeasuredHeight();
            indexHeightMap.put(index, indexTotalHeight);
            maxWidth = Math.max(maxWidth, child.getMeasuredWidth());
            maxHeight = Math.max(maxHeight, indexTotalHeight);
        }

        setMeasuredDimension(maxWidth, maxHeight);

        if (matchWidth) {
            for (int i = 0; i < childCount; ++i) {
                View child = getChildAt(i);
                if (child.getVisibility() == GONE) {
                    continue;
                }

                LayoutParams params = (LayoutParams) child.getLayoutParams();
                if (params.width == ViewGroup.LayoutParams.MATCH_PARENT) {
                    measureChild(child, MeasureSpec.makeMeasureSpec(getMeasuredWidth(), MeasureSpec.EXACTLY), heightMeasureSpec);
                }
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int nowIndex = Integer.MAX_VALUE;
        int top = 0;
        for (int i = 0, n = getChildCount(); i < n; ++i) {
            View child = getChildAt(i);
            if (child.getVisibility() == GONE) {
                continue;
            }

            int index = ((LayoutParams) child.getLayoutParams()).zIndex;
            if (nowIndex != index) {
                nowIndex = index;
                top = 0;
            }

            int height = child.getMeasuredHeight();
            child.layout(l, top, l + child.getMeasuredWidth(), top + height);
            top += height;
        }
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (!checkLayoutParams(params)) {
            params = generateLayoutParams(params);
        } else if (!(params instanceof LayoutParams)) {
            throw new B2Exception("ill params");
        }

        int newIndex = -1;
        int zIndex = ((LayoutParams) params).zIndex;
        for (int i = 0, n = getChildCount(); i < n; ++i) {
            LayoutParams childParams = (LayoutParams) getChildAt(i).getLayoutParams();
            if (childParams.zIndex > zIndex) {
                newIndex = i;
                break;
            }
        }

        super.addView(child, newIndex, params);
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
        return new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public static class LayoutParams extends ViewGroup.LayoutParams {
        public int zIndex;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);

            TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.MultipleIndexLayout_Layout);
            if (a != null) {
                try {
                    zIndex = a.getInt(R.styleable.MultipleIndexLayout_Layout_zIndex, 0);
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
                zIndex = params.zIndex;
            }
        }
    }
}
