package com.tct.calculator.view.scrollwheel;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log; // MODIFIED by qiong.liu1, 2017-04-13,BUG-4452809
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.tct.calculator.R;
import com.tct.calculator.convert.Unit;
import com.tct.calculator.data.Currency;
import com.tct.calculator.utils.Constant;
import com.tct.calculator.Calculator;

/**
 * 滚轮控件
 */
public class ScrollWheelPickerView extends FrameLayout {
    private WheelView mLeftWheel;
    private WheelView mRightWheel;
    private Context mContext;
    private View view;
    private OnWheelScrollListener scrollFinishListener;
    private OnScrollWheelClickedListener onScrollWheelClickedListener;
    // Default visible item is 6.
    private int mVisibleItems = 6;

    public ScrollWheelPickerView(Context context) {
        this(context, null);
        this.mContext = context;

    }

    public ScrollWheelPickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.calculator_wheel, this, true);
        mLeftWheel = (WheelView) view.findViewById(R.id.left_wheel);
        mLeftWheel.setTag(WHEEL.LEFT_WHEEL);
        mRightWheel = (WheelView) view.findViewById(R.id.right_wheel);
        mRightWheel.setTag(WHEEL.RIGHT_WHEEL);
    }

    public void setScrollFinishListener(OnWheelScrollListener scrollFinishListener) {
        this.scrollFinishListener = scrollFinishListener;
        mLeftWheel.addScrollingListener(scrollFinishListener);
        mRightWheel.addScrollingListener(scrollFinishListener);
    }


    public enum WHEEL {
        LEFT_WHEEL, RIGHT_WHEEL
    }

    public void initView() {
        setVisibleItemsForWheelView();
        mLeftWheel.setVisibleItems(mVisibleItems);
        mRightWheel.setVisibleItems(mVisibleItems);
        // left wheel
        // FIXME
        if (leftDatas != null) {
            Unit lefts[] = new Unit[leftDatas.size()];
            leftDatas.toArray(lefts);
            ArrayWheelAdapter leftAdapter = new ArrayWheelAdapter(this.mContext,
                    lefts);
            leftAdapter.setTextSize(38);
            leftAdapter.setTextColor(Color.BLUE);//TODO ts

            mLeftWheel.setCyclic(false);
            mLeftWheel.setViewAdapter(leftAdapter);
            mLeftWheel.setIsDrawItemBackground(false);
            mLeftWheel.setIsDrawBackgroundGrid(false);
        }

        // right wheel
        // FIXME
        if (rightDatas != null) {
            Unit rights[] = new Unit[rightDatas.size()];
            rightDatas.toArray(rights);
            ArrayWheelAdapter rightAdapter = new ArrayWheelAdapter(this.mContext,
                    rights);
            rightAdapter.setTextSize(38);
            rightAdapter.setTextColor(Color.BLUE);// >>>

            mRightWheel.setCyclic(false);//
            mRightWheel.setViewAdapter(rightAdapter);
            mRightWheel.setIsDrawItemBackground(false);
            mRightWheel.setIsDrawBackgroundGrid(false);
        }

        OnScrollWheelChangedListener wheelLeftListener = new OnScrollWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                mInitialLeftValue = newValue;
                if (mOnWheelChangedListener != null)
                    mOnWheelChangedListener.onChanged(wheel, oldValue, mInitialLeftValue);

            }
        };
        OnScrollWheelChangedListener wheelRightListener = new OnScrollWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                mInitialRightValue = newValue;
                if (mOnWheelChangedListener != null)
                    mOnWheelChangedListener.onChanged(wheel, oldValue, newValue);

            }
        };
        OnScrollWheelClickedListener leftWheelItemClickListener = new OnScrollWheelClickedListener() {
            @Override
            public void onItemClicked(WheelView wheel, int itemIndex) {
                if (onScrollWheelClickedListener != null){
                    mInitialLeftValue = itemIndex;
                    onScrollWheelClickedListener.onItemClicked(wheel,mInitialLeftValue);
                }
            }
        };
        OnScrollWheelClickedListener rightWheelItemClickListener = new OnScrollWheelClickedListener() {
            @Override
            public void onItemClicked(WheelView wheel, int itemIndex) {
                if (onScrollWheelClickedListener != null){
                    mInitialRightValue = itemIndex;
                    onScrollWheelClickedListener.onItemClicked(wheel,mInitialRightValue);
                }
            }
        };
        mLeftWheel.addClickingListener(leftWheelItemClickListener);
        mRightWheel.addClickingListener(rightWheelItemClickListener);
        mLeftWheel.addChangingListener(wheelLeftListener);
        mRightWheel.addChangingListener(wheelRightListener);
    }

    private void setVisibleItemsForWheelView() {
        /* MODIFIED-BEGIN by qiong.liu1, 2017-04-13,BUG-4452809*/
        if (Calculator.mScreenState == Constant.SCREEN_PORT_HALF
                || Calculator.mScreenState == Constant.SCREEN_PORT_QUTER
                || Calculator.mScreenState == Constant.SCREEN_LAND_HALF
                || Calculator.mScreenState == Constant.SCREEN_PORT_QUTER_THREE) {
            mVisibleItems = 3;
            /* MODIFIED-END by qiong.liu1,BUG-4452809*/
        }
    }

    /**
     * Init currency view.
     **/
    public void currencyInitView() {
        // left wheel
        setVisibleItemsForWheelView();
        mLeftWheel.setVisibleItems(mVisibleItems);
        mRightWheel.setVisibleItems(mVisibleItems);
        if (currencyLeftDatas != null) {
            Currency lefts[] = new Currency[currencyLeftDatas.size()];
            currencyLeftDatas.toArray(lefts);
            CurrencyArrayWheelAdapter leftAdapter = new CurrencyArrayWheelAdapter(this.mContext, lefts);
            leftAdapter.setTextSize(38);
            leftAdapter.setTextColor(Color.BLUE);

            mLeftWheel.setCyclic(false);
            mLeftWheel.setViewAdapter(leftAdapter);
            mLeftWheel.setIsDrawItemBackground(false);
            mLeftWheel.setIsDrawBackgroundGrid(false);
        }

        // right wheel
        if (currencyRightData != null) {
            Currency rights[] = new Currency[currencyRightData.size()];
            currencyRightData.toArray(rights);
            CurrencyArrayWheelAdapter rightAdapter = new CurrencyArrayWheelAdapter(this.mContext, rights);
            rightAdapter.setTextSize(38);
            rightAdapter.setTextColor(Color.BLUE);

            mRightWheel.setCyclic(false);
            mRightWheel.setViewAdapter(rightAdapter);
            mRightWheel.setIsDrawItemBackground(false);
            mRightWheel.setIsDrawBackgroundGrid(false);
        }

        OnScrollWheelChangedListener wheelLeftListener = new OnScrollWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                mInitialLeftValue = newValue;
                if (mOnWheelChangedListener != null)
                    mOnWheelChangedListener.onChanged(wheel, oldValue, mInitialLeftValue);

            }
        };
        OnScrollWheelChangedListener wheelRightListener = new OnScrollWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                mInitialRightValue = newValue;
                if (mOnWheelChangedListener != null)
                    mOnWheelChangedListener.onChanged(wheel, oldValue, newValue);

            }
        };
        OnScrollWheelClickedListener leftWheelItemClickListener = new OnScrollWheelClickedListener() {
            @Override
            public void onItemClicked(WheelView wheel, int itemIndex) {
                if (onScrollWheelClickedListener != null){
                    mInitialLeftValue = itemIndex;
                    onScrollWheelClickedListener.onItemClicked(wheel,mInitialLeftValue);
                }
            }
        };
        OnScrollWheelClickedListener rightWheelItemClickListener = new OnScrollWheelClickedListener() {
            @Override
            public void onItemClicked(WheelView wheel, int itemIndex) {
                if (onScrollWheelClickedListener != null){
                    mInitialRightValue = itemIndex;
                    onScrollWheelClickedListener.onItemClicked(wheel,mInitialRightValue);
                }
            }
        };
        mLeftWheel.addClickingListener(leftWheelItemClickListener);
        mRightWheel.addClickingListener(rightWheelItemClickListener);
        mLeftWheel.addChangingListener(wheelLeftListener);
        mRightWheel.addChangingListener(wheelRightListener);
    }

    private int mInitialLeftValue;
    private int mInitialRightValue;

    /**
     * 设置时
     */
    public void setCurrentLeftWheel(int initialLeftValue) {
        this.mInitialLeftValue = initialLeftValue;
        mLeftWheel.setCurrentItem(initialLeftValue);
    }

    /**
     * 设置分
     */
    public void setCurrentRightWheel(int initialRightValue) {
        this.mInitialRightValue = initialRightValue;
        mRightWheel.setCurrentItem(initialRightValue);
    }

    private OnScrollWheelChangedListener mOnWheelChangedListener;

    public void setOnTimeChangedListener(OnScrollWheelChangedListener onTimeChangedListener) {
        mOnWheelChangedListener = onTimeChangedListener;
    }
    public void setOnItemClickListener(OnScrollWheelClickedListener onItemClickListener){
        onScrollWheelClickedListener = onItemClickListener;
    }

    public int getLeftValue() {
        return mInitialLeftValue;
    }

    public int getRightValue() {
        return mInitialRightValue;
    }

    public void setWheelNumber(int wheelNum) {
        if (wheelNum == 1) {
            mLeftWheel.setVisibility(View.VISIBLE);
            mRightWheel.setVisibility(View.GONE);
        } else if (wheelNum == 2) {
            mLeftWheel.setVisibility(View.VISIBLE);
            mRightWheel.setVisibility(View.VISIBLE);
        }
    }

    private List<Unit> leftDatas;

    public void setLeftDatas(List<Unit> datas) {
        this.leftDatas = datas;
    }
    private List<Unit> rightDatas;

    public void setRightDatas(List<Unit> data) {
        this.rightDatas = data;
    }
    private List<Currency> currencyLeftDatas;

    public void setCurrencyLeftDatas(List<Currency> data) {
        this.currencyLeftDatas = data;
    }

    private List<Currency> currencyRightData;

    public void setCurrencyRightDatas(List<Currency> data) {
        this.currencyRightData = data;
    }
}
