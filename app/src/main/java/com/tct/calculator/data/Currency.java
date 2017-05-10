package com.tct.calculator.data;

/**
 * Created by ts on 10/31/16.
 */
public class Currency {

    private String mCountryCode;

    private String mCountryUnit;

    private String mCountryPicture;

    private String mCountrySignal;

    /* MODIFIED-BEGIN by qiong.liu1, 2017-03-20,BUG-3621966*/
    private String mCountryCurrency;

    public Currency() {
    }

    public Currency(String countryCode, String countryUnit, String currencyValue) {
        this.mCountryCode = countryCode;
        this.mCountryUnit = countryUnit;
        this.mCountryCurrency = currencyValue;
        /* MODIFIED-END by qiong.liu1,BUG-3621966*/
    }

    public String getCountryCode() {
        return mCountryCode;
    }

    public void setCountryCode(String countryCode) {
        this.mCountryCode = countryCode;
    }

    public String getCountryUnit() {
        return mCountryUnit;
    }

    public void setCountryUnit(String countryUnit) {
        this.mCountryUnit = countryUnit;
    }

    public void setCountryPicture(String countryPicture) {
        this.mCountryPicture = countryPicture;
    }

    public String getCountryPicture() {
        return mCountryPicture;
    }

    public void setCountrySignal(String signal) {
        this.mCountrySignal = signal;
    }

    public String getCountrySignal() {
        return mCountrySignal;
    }

    /* MODIFIED-BEGIN by qiong.liu1, 2017-03-20,BUG-3621966*/
    public String getCountryCurrency() {
        return mCountryCurrency;
    }

    public void setCountryCurrency(String mCountryCurrency) {
        this.mCountryCurrency = mCountryCurrency;
    }

    @Override
    public String toString() {
        return "Unit:mCountryCode=" + mCountryCode + ",mCountryUnit=" + mCountryUnit
                + ",mCountryPicture=" + mCountryPicture + ",mCountrySignal=" + mCountrySignal
                + ",mCountryCurrency=" + mCountryCurrency;
                /* MODIFIED-END by qiong.liu1,BUG-3621966*/
    }
}
