package com.tct.calculator.data;


public class DefaultCurrency {

    private String mCurrencyName;

    private String mCurrencyFlag;

    private String mCurrencyTousd;

    private String mCurrencySignal;

    private String mCurrencyIsLocal;

    private String mCurrencyShorterForm;


    public String getCurrencyName() {
        return mCurrencyName;
    }

    public void setCurrencyName(String currencyName) {
        this.mCurrencyName = currencyName;
    }

    public String getCurrencyFlag() {
        return mCurrencyFlag;
    }

    public void setCurrencyFlag(String currencyFlag) {
        this.mCurrencyFlag = currencyFlag;
    }

    public String getCurrencyTousd() {
        return mCurrencyTousd;
    }

    public void setCurrencyTousd(String currencyTousd) {
        this.mCurrencyTousd = currencyTousd;
    }

    public String getCurrencySignal() {
        return mCurrencySignal;
    }

    public void setCurrencySignal(String currencySignal) {
        this.mCurrencySignal = currencySignal;
    }

    public String getCurrencyIsLocal() {
        return mCurrencyIsLocal;
    }

    public void setCurrencyIsLocal(String currencyIsLocal) {
        this.mCurrencyIsLocal = currencyIsLocal;
    }

    public String getCurrencyShorterForm() {
        return mCurrencyShorterForm;
    }

    public void setCurrencyShorterForm(String currencyShorterForm) {
        this.mCurrencyShorterForm = currencyShorterForm;
    }

    @Override
    public String toString() {
        return "DefaultCurrency{" +
            "mCurrencyName='" + mCurrencyName + '\'' +
            ", mCurrencyFlag='" + mCurrencyFlag + '\'' +
            ", mCurrencyTousd='" + mCurrencyTousd + '\'' +
            ", mCurrencySignal='" + mCurrencySignal + '\'' +
            ", mCurrencyIsLocal='" + mCurrencyIsLocal + '\'' +
            ", mCurrencyShorterForm='" + mCurrencyShorterForm + '\'' +
            '}';
    }
}
