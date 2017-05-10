/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tct.calculator;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.tct.calculator.utils.Constant;

public class CalculatorPadViewPager extends ViewPager {
    //PR540014 START clicking number's panel close scientic's panel when scientic 's panel  totally show.27/8/2015 update by xiaolu.li
    private onTouchUpFirstPagerOnBackgroundListener listener;

    public static interface onTouchUpFirstPagerOnBackgroundListener {
        void onTouchUpFirstPager();
        void onDEGModeSelect(); // MODIFIED by qiong.liu1, 2017-04-14,BUG-4452809
    }



    public void setOnTouchUpFirstPagerOnBackgroundListener(onTouchUpFirstPagerOnBackgroundListener listener) {
        this.listener = listener;
    }

    //PR540014 END clicking number's panel close scientic's panel when scientic 's panel  totally show.27/8/2015 update by xiaolu.li
    private final PagerAdapter mStaticPagerAdapter = new PagerAdapter() {
        @Override
        public int getCount() {
            return getChildCount();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            return getChildAt(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            removeViewAt(position);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public float getPageWidth(int position) {
            try {
                if (mCalculatorType != null) {
                    switch (mCalculatorType) {
                        case CALCULATOR:
                            return 1.0f;
                        default:
                            return position == 1 ? 7.0f / 9.0f : 1.0f;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return position == 1 ? 7.0f / 9.0f : 1.0f;
        }
    };

    //TS:kaifeng.lu 2016-01-26 Calculator BUGFIX_1527305 ADD_S
    @Override
    protected int getChildDrawingOrder(int childCount, int i) {
        try {
            return super.getChildDrawingOrder(childCount, i);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return 0;
    }

    //TS:kaifeng.lu 2016-01-26 Calculator BUGFIX_1527305 ADD_E
    private final OnPageChangeListener mOnPageChangeListener = new SimpleOnPageChangeListener() {
        private void recursivelySetEnabled(View view, boolean enabled) {
            if (view instanceof ViewGroup) {
                final ViewGroup viewGroup = (ViewGroup) view;
                for (int childIndex = 0; childIndex < viewGroup.getChildCount(); ++childIndex) {
                    recursivelySetEnabled(viewGroup.getChildAt(childIndex), enabled);
                }
            } else {
                view.setEnabled(enabled);
            }
        }

        @Override
        public void onPageSelected(int position) {
            /* MODIFIED-BEGIN by qiong.liu1, 2017-04-14,BUG-4452809*/
            if (position == 0) {
                if (listener != null) {
                    listener.onDEGModeSelect();
                }
            }
            /* MODIFIED-END by qiong.liu1,BUG-4452809*/
            if (getAdapter() == mStaticPagerAdapter) {
                for (int childIndex = 0; childIndex < getChildCount(); ++childIndex) {
                    // Only enable subviews of the current page.
                    recursivelySetEnabled(getChildAt(childIndex), childIndex == position);
                }
            }
        }
    };

    private final PageTransformer mPageTransformer = new PageTransformer() {
        @Override
        public void transformPage(View view, float position) {
            if (position < 0.0f) {
                // Pin the left page to the left side.
                view.setTranslationX(getWidth() * -position);
                view.setAlpha(Math.max(1.0f + position, 0.5f));//PR1074687  need change history font style & number panal is so blank then show.update by xiaolu.li Aug 25 2015
            } else {
                // Use the default slide transition when moving to the next page.
                view.setTranslationX(0.0f);
                view.setAlpha(1.0f);
            }
        }
    };

    public CalculatorPadViewPager(Context context) {
        this(context, null);
    }

    public CalculatorPadViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);

        setAdapter(mStaticPagerAdapter);
        setBackgroundColor(getResources().getColor(android.R.color.black));
        setOnPageChangeListener(mOnPageChangeListener);
        setPageTransformer(false, mPageTransformer);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        // Invalidate the adapter's data set since children may have been added during inflation.
        if (getAdapter() == mStaticPagerAdapter) {
            mStaticPagerAdapter.notifyDataSetChanged();
        }
    }

    //PR540014 START clicking number's panel close scientic's panel when scientic 's panel  totally show.27/8/2015 update by xiaolu.li
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (MotionEventCompat.getActionMasked(ev)) {
            case MotionEvent.ACTION_UP:
                if (this.getCurrentItem() == 1 && ev.getX() < (this.getWidth() - this.getChildAt(1).getWidth()) && listener != null) {
                    listener.onTouchUpFirstPager();
                }
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    //PR540014 END clicking number's panel close scientic's panel when scientic 's panel  totally show.27/8/2015 update by xiaolu.li
    private Constant.CalculatorType mCalculatorType;

    public void setCalculatorType(Constant.CalculatorType type) {
        this.mCalculatorType = type;
    }
}
