package com.tct.calculator.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.tct.calculator.R;

/**
 * This class is not used now, We will use it to options our animator in next time.
 */
public class ExchangeItemLayout extends FrameLayout {

    private View mTopLayout, mBottomLayout;
    private TextView mText1, mText2;

    private float mTranslateMax;

    private ItemAnimation mItemAnimation1, mItemAnimation2;

    public ExchangeItemLayout(Context context) {
        super(context, null);
//        init();
    }

    public ExchangeItemLayout(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
//        init();
    }

    public ExchangeItemLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr, 0);
//        init();
    }

    public ExchangeItemLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
//        init();
    }


    private void init() {
        inflate(getContext(), R.layout.exchange_item_layout, this);
        mTopLayout = findViewById(R.id.org_layout);
        mBottomLayout = findViewById(R.id.target_layout);
//        mText1 = (TextView) findViewById(R.id.item1_tv_formula);
//        mText2 = (TextView) findViewById(R.id.item2_tv_formula);
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed) {
            mTranslateMax = mTopLayout.getHeight();
            mItemAnimation1 = new ItemAnimation(mTopLayout, mText1, true);
            mItemAnimation2 = new ItemAnimation(mBottomLayout, mText2, false);
        }
    }

    public void toggle() {
        if (null != mItemAnimation1 && null != mItemAnimation2) {
            mItemAnimation1.start();
            mItemAnimation2.start();
        }
    }


    private class ItemAnimation {
        private ObjectAnimator mAnimation;
        private View mItem;
        private TextView mTextView;
        private float mDistance;
        private float mRealMaxDistance;
        private float mStartTy;

        ItemAnimation(View view, TextView tv, boolean isInTop) {
            this.mItem = view;
            this.mTextView = tv;
            mRealMaxDistance = isInTop ? mTranslateMax : -mTranslateMax;
        }

        public void start() {
            if (null == mAnimation) {
                mAnimation = ObjectAnimator.ofFloat(this, "progress", 0, 1);
                mAnimation.setDuration(400);
            }
            if (mAnimation.isRunning()) {
                mAnimation.cancel();
            }
            mStartTy = mItem.getTranslationY();
            if (mStartTy != 0) {
                mDistance = -mStartTy;
            } else {
                mDistance = mRealMaxDistance - mStartTy;
            }
            mAnimation.start();
        }

        public void setProgress(float progress) {
            mItem.setTranslationY(mStartTy + progress * mDistance);
            mTextView.setTextSize(20 * progress);
        }

    }
}
