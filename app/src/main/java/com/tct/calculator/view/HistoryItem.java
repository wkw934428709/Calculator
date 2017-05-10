package com.tct.calculator.view;

/**
 * Created by xlli on 7/29/15.
 */
public class HistoryItem {
    public String formula;
    public String result;
    public long timeStap;
    public HistoryItem(String formula,String result,long timeStap){
        this.formula = formula;
        this.result = result;
        this.timeStap = timeStap;
    }
    public HistoryItem(){
        this("","",System.currentTimeMillis());
    }
}
