/* Copyright (C) 2016 Tcl Corporation Limited */
package com.tct.calculator.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesHelper {
    public final static String SHARED_PREFERENCES_CONVERT_RECORDER = "calculator_convert_recorder";
    public final static String KEY_CONVERT_UNIT = "key_convert_unit";
    private static SharedPreferencesHelper sharedPreferencesHelper;
    private Context mContext;
    private SharedPreferences.Editor editor;
    private SharedPreferences preferences;
    public static final String CURRENCY_TOP_COUNTRY_KEY = "currency_top";
    public static final String CURRENCY_BOTTOM_COUNTRY_KEY = "currency_bottom";
    public static final String IS_FIRST_RUN = "isFistRun";
    public static final String WHEN_UPDATE = "when_update"; // MODIFIED by qiong.liu1, 2017-03-20,BUG-3621966
    public static final String IS_IN_MULTIWINDOW = "is_in_multiwidow";

    private SharedPreferencesHelper(Context context) {
        mContext = context;
        preferences = mContext.getSharedPreferences(SHARED_PREFERENCES_CONVERT_RECORDER, Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    public static SharedPreferencesHelper getInstance(Context context) {
        if (null == sharedPreferencesHelper) {
            sharedPreferencesHelper = new SharedPreferencesHelper(context);
        }
        return sharedPreferencesHelper;
    }


    /* MODIFIED-BEGIN by qiong.liu1, 2017-05-04,BUG-4656997*/
    public void saveIsInMultiWindow(String key, boolean is) {
        editor.putBoolean(key, is).commit();
    }

    public boolean getIsInMultiWindow(String key) {
        return preferences.getBoolean(key, false);
    }
    /* MODIFIED-END by qiong.liu1,BUG-4656997*/

    /**
     * @param key
     * @param datas
     */
    public void saveConvertOperations(String key, String datas) {
        editor.putString(key, datas).commit();
    }

    /**
     * @param key
     * @return
     */
    public String getConvertOperations(String key) {
        return preferences.getString(key, "");
    }

    /**
     * Save currency top country value.
     *
     * @param topValue top country
     */
    public void saveCurrencyTopCountry(String topValue) {
        editor.putString(CURRENCY_TOP_COUNTRY_KEY, topValue).commit();
    }

    /**
     * Get currency top country value.
     *
     * @return top country
     */
    public String getCurrencyTopCountry() {
        return preferences.getString(CURRENCY_TOP_COUNTRY_KEY, "");
    }

    /**
     * Save currency bottom country value.
     *
     * @param bottomValue bottom country
     */
    public void saveCurrencyBottomCountry(String bottomValue) {
        editor.putString(CURRENCY_BOTTOM_COUNTRY_KEY, bottomValue).commit();
    }

    /**
     * Get currency bottom country value.
     *
     * @return bottom country
     */
    public String getCurrencyBottomCountry() {
        return preferences.getString(CURRENCY_BOTTOM_COUNTRY_KEY, "");
    }

    /**
     * Save this application is not first run.
     *
     * @param firstRun first run of application
     */
    public void saveApplicationFirstRun(boolean firstRun) {
        editor.putBoolean(IS_FIRST_RUN, firstRun).commit();
    }

    /**
     * Get this application is first run.
     *
     * @return bottom country
     */
    public boolean getApplicationFirstRun() {
        return preferences.getBoolean(IS_FIRST_RUN, true);
    }

    public void saveInt(String key, int index){
        editor.putInt(key, index).commit();
    }

    public int getInt(String key){
        return preferences.getInt(key, -1);
    }

    public void saveString(String key, String value){
        editor.putString(key, value).commit();
    }

    public String getString(String key){
        return preferences.getString(key, "");
    }

}
