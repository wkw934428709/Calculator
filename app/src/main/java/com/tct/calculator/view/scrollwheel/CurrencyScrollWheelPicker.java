package com.tct.calculator.view.scrollwheel;

import android.content.Context;
import android.util.Log; // MODIFIED by qiong.liu1, 2017-04-21,BUG-4452809
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.tct.calculator.R;
import com.tct.calculator.data.Currency;

import java.util.List;


/**
 */
public class CurrencyScrollWheelPicker implements OnClickListener, OnScrollWheelChangedListener,OnScrollWheelClickedListener {
    private static final String TAG = "CurrencyScrollWheelPicker";
    private View view;
    private TextView mCurrencyPickerDivider;
    private ScrollWheelPickerView mCurrencyPicker;
    private OnScrollWheelClickedListener itemClickListener;
    private OnWheelChangedListener mCallback;
    private OnWheelScrollListener mCallbackFinished;
    private int mLeftIndex;
    private int mRightIndex;

    public CurrencyScrollWheelPicker(Context context, View view, OnWheelChangedListener callBack, OnWheelScrollListener callbackFinished, OnScrollWheelClickedListener callbackClick,
                                     int leftDefaultIndex, int rigthDefaultIndex, List<Currency> leftDatas,
                                     List<Currency> rightDatas) {
        mCallback = callBack;
        this.mCallbackFinished = callbackFinished;
        this.itemClickListener = callbackClick;
        mLeftIndex = leftDefaultIndex;
        mRightIndex = rigthDefaultIndex;
        this.view = view;
        mCurrencyPicker = (ScrollWheelPickerView) view.findViewById(R.id.currency_wheel_picker_view);
        mCurrencyPickerDivider = (TextView) view.findViewById(R.id.currency_wheel_picker_divider);
        mCurrencyPicker.setCurrencyLeftDatas(leftDatas);
        mCurrencyPicker.setCurrencyRightDatas(rightDatas);
        mCurrencyPicker.currencyInitView();
        setWheelNumber(2);
        mCurrencyPicker.setCurrentLeftWheel(mLeftIndex);
        mCurrencyPicker.setCurrentRightWheel(mRightIndex);
        mCurrencyPicker.setOnTimeChangedListener(this);
        mCurrencyPicker.setOnItemClickListener(this);
        mCurrencyPicker.setScrollFinishListener(callbackFinished);
        view.findViewById(R.id.bt_left_more).setVisibility(View.VISIBLE);
        view.findViewById(R.id.bt_right_more).setVisibility(View.VISIBLE);
    }

    public CurrencyScrollWheelPicker(Context context, View view, OnWheelChangedListener callBack, int i, int i1, List<Currency> leftDatas, List<Currency> rightDatas) {
        mCallback = callBack;

        this.view = view;
        mCurrencyPicker = (ScrollWheelPickerView) view.findViewById(R.id.currency_wheel_picker_view);
        mCurrencyPickerDivider = (TextView) view.findViewById(R.id.currency_wheel_picker_divider);
    }

    public void updateWheel(int leftDefaultIndex, int rigthDefaultIndex,
                            List<Currency> leftDatas, List<Currency> rightDatas) {
        mLeftIndex = leftDefaultIndex;
        mRightIndex = rigthDefaultIndex;
        mCurrencyPicker.setCurrentLeftWheel(mLeftIndex);
        mCurrencyPicker.setCurrentRightWheel(mRightIndex);
        mCurrencyPicker.setCurrencyLeftDatas(leftDatas);
        mCurrencyPicker.setCurrencyRightDatas(rightDatas);
        mCurrencyPicker.currencyInitView();
        setWheelNumber(2);
        mCurrencyPicker.setOnTimeChangedListener(this);
    }

    public void setWheelNumber(int wheelNumber) {
        if (wheelNumber == 1) {
            mCurrencyPickerDivider.setVisibility(View.GONE);
        } else if (wheelNumber == 2) {
            mCurrencyPickerDivider.setVisibility(View.INVISIBLE);
        }
        mCurrencyPicker.setWheelNumber(wheelNumber);
    }

    @Override
    public void onItemClicked(WheelView wheel, int itemIndex) {
        if (itemClickListener != null){
            itemClickListener.onItemClicked(wheel, itemIndex);
        }
    }

    public interface OnWheelChangedListener {
        void onWheelChanged(int leftIndex, int rightIndex);
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    public void onChanged(WheelView wheel, int oldValue, int newValue) {
        if (mCallback != null) {
            mCallback.onWheelChanged(mCurrencyPicker.getLeftValue(), mCurrencyPicker.getRightValue());
        }
    }
}
