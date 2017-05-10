package com.tct.calculator.view.interfaces;

import android.text.Editable;
import android.view.View;

import com.tct.calculator.fragment.CalculatorFragment;
import com.tct.calculator.view.History;

/**
 * When change calculate orientation, call this interface
 */
public interface ICalculatorInterfaces {
    /**
     * Set calculator edit text
     */
    void setCalculateText(Editable calculateText);

    /**
     * Set result edit text
     */
    void setResultText(Editable resultText);

    /**
     * Set Calculate longclick state to ture or false
     */
    void setCalculateLongclickState(boolean isLongclick);

    /**
     * Set result longclic state to true or false
     */
    void setResultLongclickState(boolean isLongclick);

    /**
     * Set Calculate State
     * @param mode
     */
    void setCalulateState(int mode);

    /**
     * Set copy or paste state
     * @param state
     */
    void setCopyPasteState(boolean state);

    /**
     * Set the history
     * @param history
     */
    void setHistory(History history);

    /**
     * Set rad button state
     */
    void setRadVisibility(int visiVisbility);
}
