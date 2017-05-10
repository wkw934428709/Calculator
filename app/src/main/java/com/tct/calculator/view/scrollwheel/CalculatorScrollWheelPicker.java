package com.tct.calculator.view.scrollwheel;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.tct.calculator.R;
import com.tct.calculator.convert.Unit;


/**
 */
public class CalculatorScrollWheelPicker implements OnClickListener, OnScrollWheelChangedListener, OnScrollWheelClickedListener {
    private View view;
    private View mTimePickerDivider;
    private OnScrollWheelClickedListener onScrollWheelClickedListener;
    private static final String TAG = "CalculatorScrollWheelPicker";

    public CalculatorScrollWheelPicker(Context context, View view, OnWheelChangedListener callBack, OnWheelScrollListener callbackFinished, OnScrollWheelClickedListener onScrollWheelClickedListener,
                                       int leftDefaultIndex, int rigthDefaultIndex, List<Unit> leftDatas,
                                       List<Unit> rightDatas) {
        mCallback = callBack;
        this.mCallbackFinished = callbackFinished;
        this.onScrollWheelClickedListener = onScrollWheelClickedListener;
        mLeftIndex = leftDefaultIndex;
        mRightIndex = rigthDefaultIndex;
        this.view = view;
        mTimePicker = (ScrollWheelPickerView) view.findViewById(R.id.wheel_picker_view);
        mTimePickerDivider = view.findViewById(R.id.wheel_picker_divider);
        mTimePicker.setLeftDatas(leftDatas);
        mTimePicker.setRightDatas(rightDatas);
        // initialize state
        mTimePicker.initView();
        setWheelNumber(2);
        mTimePicker.setCurrentLeftWheel(mLeftIndex);
        mTimePicker.setCurrentRightWheel(mRightIndex);
        mTimePicker.setOnTimeChangedListener(this);
        mTimePicker.setOnItemClickListener(this);
        mTimePicker.setScrollFinishListener(callbackFinished);
    }

    public CalculatorScrollWheelPicker(Context context, View view, OnWheelChangedListener callBack, int i, int i1, List<Unit> leftDatas, List<Unit> rightDatas) {
        mCallback = callBack;

        this.view = view;
        mTimePicker = (ScrollWheelPickerView) view.findViewById(R.id.wheel_picker_view);
        mTimePickerDivider =  view.findViewById(R.id.wheel_picker_divider);
    }

    public void updateWheel(int leftDefaultIndex, int rigthDefaultIndex,
                            List<Unit> leftDatas, List<Unit> rightDatas) {
        mLeftIndex = leftDefaultIndex;
        mRightIndex = rigthDefaultIndex;
        mTimePicker.setCurrentLeftWheel(mLeftIndex);
        mTimePicker.setCurrentRightWheel(mRightIndex);
        mTimePicker.setCurrentLeftWheel(mLeftIndex);
        mTimePicker.setCurrentRightWheel(mRightIndex);
        mTimePicker.setLeftDatas(leftDatas);
        mTimePicker.setRightDatas(rightDatas);
        mTimePicker.initView();
        setWheelNumber(2);
        mTimePicker.setOnTimeChangedListener(this);
    }

    private ScrollWheelPickerView mTimePicker;
    private OnWheelChangedListener mCallback;
    private OnWheelScrollListener mCallbackFinished;
    private int mLeftIndex;
    private int mRightIndex;

    public void setWheelNumber(int wheelNumber) {
        if (wheelNumber == 1) {
            mTimePickerDivider.setVisibility(View.GONE);
        } else if (wheelNumber == 2) {
            mTimePickerDivider.setVisibility(View.INVISIBLE);
        }
        mTimePicker.setWheelNumber(wheelNumber);
    }

    @Override
    public void onItemClicked(WheelView wheel, int itemIndex) {
        if (onScrollWheelClickedListener != null){
            onScrollWheelClickedListener.onItemClicked(wheel, itemIndex);
        }
    }

    /**
     */
    public interface OnWheelChangedListener {

        /**
         */
        void onWheelChanged(int leftIndex, int rightIndex);
    }

    @Override
    public void onClick(View v) {
        // if (mCallback != null) {
        // mTimePicker.clearFocus();
        // mCallback.onTimeSet(mTimePicker, mTimePicker.getCurrentHour(),
        // mTimePicker.getCurrentMinute());
        // }
    }

    @Override
    public void onChanged(WheelView wheel, int oldValue, int newValue) {
        Log.i(TAG, "===XXX=====oldValue=" + oldValue + "=newValue=" + newValue);
        if (mCallback != null)
            mCallback.onWheelChanged(mTimePicker.getLeftValue(), mTimePicker.getRightValue());
    }
}
