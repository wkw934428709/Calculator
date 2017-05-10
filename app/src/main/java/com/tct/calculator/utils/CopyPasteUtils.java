/* Copyright (C) 2016 Tcl Corporation Limited */
package com.tct.calculator.utils;

import android.content.ClipboardManager;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;

import com.tct.calculator.CalculatorApplication;
import com.tct.calculator.data.PatternUtil;

/**
 * Copy and paste utilities
 * This class is thread safe single instance
 */
public class CopyPasteUtils {
    public  ClipboardManager mClipboard;
    private String TAG = "CopyPasteUtils";
    private static final CopyPasteUtils copyPasteInstance = new CopyPasteUtils();

    private CopyPasteUtils(){
        mClipboard = (ClipboardManager) CalculatorApplication.getInstance().getSystemService(Context.CLIPBOARD_SERVICE);
    }

    /**
     * Return copy paste instance
     * @return copyPasteInstance
     */
    public static CopyPasteUtils getInstance(){
        return copyPasteInstance;
    }

    /**
     * Get string from clip board
     * @return
     */
    public String getClipBoardText() {
        if (mClipboard.hasPrimaryClip()) {
            try {
                return mClipboard.getPrimaryClip().getItemAt(0).getText().toString();
            } catch (NullPointerException e) {
                Log.i(TAG, "getClipBoardText catch a NullPointerException");
            }
        }
        return null;
    }

    /**
     * Whether show paste button
     * @param currentState
     * @return
     */
    public boolean isCanPaste(Constant.CalculatorState currentState) {
        if (!TextUtils.isEmpty(getClipBoardText()) && PatternUtil.getInstance().isContainNumber(getClipBoardText()) && (currentState == Constant.CalculatorState.INPUT || currentState == Constant.CalculatorState.ERROR || currentState == Constant.CalculatorState.RESULT)) {
            return true;
        }
        return false;
    }

    /**
     * Whether show paste button
     * @param currentState
     * @return
     */
    public boolean isCanCopyFormula(Constant.CalculatorState currentState, EditText formulaEditText) {
        if ((Constant.CalculatorState.RESULT == currentState && (!TextUtils.isEmpty(formulaEditText.getText())))) {
            return true;
        }
        return false;
    }

    public boolean isCanCopyResult(Constant.CalculatorState currentState, EditText resultEditText) {
        if (((!TextUtils.isEmpty(resultEditText.getText())) && Constant.CalculatorState.ERROR != currentState)) {
            return true;
        }
        return false;
    }

}
