package com.tct.calculator.view;

import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;

/**
 * Copyright (C) 2016 Tcl Corporation Limited
 * <p/>
 * Created on 16-12-1.
 * $desc
 */
public class ExpandableLayout extends LinearLayout {

    private final static int DEFAULT_ANIMATION_TIMEMILLS = 200; // MODIFIED by qiong.liu1, 2017-04-18,BUG-4452809
    private final static int STATE_EXPANDED = 0;
    private final static int STATE_UNEXPANDED = 1;
    private final static int CHILDVIEW_SAPN = 40;

    private int mAnimationTimeMills = DEFAULT_ANIMATION_TIMEMILLS;

    private int mCurrentState = STATE_UNEXPANDED;

    private float mOffsetProgress;
    private ObjectAnimator mOffsetAnimator;

    public ExpandableLayout(Context context) {
        super(context);
        init();
    }

    public ExpandableLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @TargetApi(21)
    public ExpandableLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(21)
    public ExpandableLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        setOrientation(HORIZONTAL);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        View child;
        int offsetX = getPaddingLeft();
        int topY = getPaddingTop();
        for (int i = getChildCount() - 1; i >= 0; i--) {// child 0 is at left
            child = getChildAt(i);
            child.layout(offsetX, topY, offsetX + child.getMeasuredWidth(), topY + child.getMeasuredHeight());
            offsetX += child.getMeasuredWidth() + CHILDVIEW_SAPN;
        }
        setCurrentState(mCurrentState, false, true);
    }

    private void startAnimation() {
        if (null != mOffsetAnimator && mOffsetAnimator.isRunning())
            mOffsetAnimator.cancel();
        mOffsetAnimator = new ObjectAnimator().ofFloat(this, "offsetProgress", mOffsetProgress, mCurrentState == STATE_EXPANDED ? 0f : 1.0f);
        mOffsetAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        mOffsetAnimator.setDuration(mAnimationTimeMills);
        mOffsetAnimator.start();
    }

    /*progress is in[0,1.0f], 0 means expand, 1.0f means unexpand*/
    private void setOffsetProgress(float progress) {
        mOffsetProgress = progress;
        View child;
        for (int i = getChildCount() - 2; i >= 0; i--) {// child 0 is at left
            child = getChildAt(i);
            child.setTranslationX(-child.getLeft() * mOffsetProgress);
            child.setAlpha(1 - progress);
        }
    }

    public void toggle() {
        setCurrentState(mCurrentState == STATE_EXPANDED ? STATE_UNEXPANDED : STATE_EXPANDED, true);
    }


    public void expand() {
        setCurrentState(STATE_EXPANDED, true);
    }


    public void unexpand() {
        setCurrentState(STATE_UNEXPANDED, true);
    }

    /**
     * set current state
     *
     * @param state
     * @param isAnimation
     */
    public void setCurrentState(int state, boolean isAnimation) {
        setCurrentState(state, isAnimation, false);
    }

    private void setCurrentState(int state, boolean isAnimation, boolean isForceSet) {
        if (state != mCurrentState || isForceSet) {
            mCurrentState = state;
            if (isAnimation) {
                startAnimation();
            } else {
                if (mCurrentState == STATE_EXPANDED) {
                    setOffsetProgress(0);
                } else if (mCurrentState == STATE_UNEXPANDED) {
                    setOffsetProgress(1.0f);
                }
            }
        }
    }

    public boolean isExpanded(){

        if (mCurrentState == STATE_EXPANDED) {
            return true;
        } else if (mCurrentState == STATE_UNEXPANDED) {
            return false;
        }else{
            return false;
        }
    }

}

