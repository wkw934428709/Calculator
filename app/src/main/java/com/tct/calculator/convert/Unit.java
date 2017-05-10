package com.tct.calculator.convert;

import android.content.Context;

/**
 * Created by user on 9/28/16.
 */
public class Unit {

    private String fullName;
    private String abbreviation;
    private int defaultUnit = -1;

    private float unitValue;
    private float convertRate;
    private Context mContext;

    public Unit(Context context, String abbreviation, String strKey, int defaultUnit) {
        this.abbreviation = abbreviation;
        this.mContext = context;
        this.fullName = mContext.getString(mContext.getResources().getIdentifier(strKey,"string", mContext.getPackageName()));
        this.defaultUnit = defaultUnit;

    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public int getDefaultUnitType() {
        return defaultUnit;
    }


    public float getUnitValue() {
        return unitValue;
    }

    public void setUnitValue(float unitValue) {
        this.unitValue = unitValue;
    }

    public float getConvertRate() {
        return convertRate;
    }

    public void setConvertRate(float convertRate) {
        this.convertRate = convertRate;
    }

    @Override
    public String toString() {
        return "Unit:fullName=" + fullName + ",abbreviation=" + abbreviation + ",unitValue=" + unitValue + ",convertRate=" + convertRate + ",defaultUnit=" + defaultUnit;
    }

}
