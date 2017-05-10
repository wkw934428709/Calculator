package com.tct.calculator;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;

import com.tcl.statisticsdk.agent.StatisticsAgent;
import com.tcl.statisticsdk.util.Unit;
import com.tcl.update.UpdateSdkManager;
import com.tct.calculator.data.DBOperation;
import com.tct.calculator.data.DefaultCurrency;
import com.tct.calculator.utils.Utils;

public class CalculatorApplication extends Application {

    private static final String TAG = "CalculatorApplication";

    private static CalculatorApplication mInstance = null;

    private DBOperation mDBOperation;

    private QueryDefaultCurrencyThread mQueryDefaultCurrencyThread;

    public DefaultCurrency mDefaultCurrency;

    private boolean mCalcualtorHawkeyeEnable;
    private boolean mCalculatorUpdateEnable;

    public static CalculatorApplication getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            mCalcualtorHawkeyeEnable = Utils.getBoolean(this, "def_jrdcalculator_hawkeye_enable");
        }catch (Exception e){
            Log.e(TAG,"def_jrdcalculator_hawkeye_enable not found");
        }
        if (mCalcualtorHawkeyeEnable) {
            try {
                boolean hasFeedBack = Utils.isPackageExist(getApplicationContext(), "com.tct.endusertest");
                StatisticsAgent.init(this.getApplicationContext());
                StatisticsAgent.setDebugMode(true);
                StatisticsAgent.setAutoTraceActivity(false);
                StatisticsAgent.setWifiOnly(this.getApplicationContext(), true);
                StatisticsAgent.experienceImprove(this.getApplicationContext(), true);
                int day = 7;
                if (hasFeedBack){
                    day = 1;
                }
//                StatisticsAgent.setPeriod(day, Unit.DAY); // MODIFIED by qiong.liu1, 2017-03-31,BUG-3621966
            }catch (Exception e){
                Log.e(TAG,"com.tct.endusertest package not found ");
            }
        }
        try{
            mCalculatorUpdateEnable = Utils.getBoolean(this,"def_calculator_upgrade_enable");
            if(mCalculatorUpdateEnable) {
                UpdateSdkManager.init(getApplicationContext());
            }
        }catch (Exception e){
            Log.e(TAG,"mCalculatorUpdateEnable not found");
        }
        initApplication();
    }

    private void initApplication() {
        mInstance = this;
        mDBOperation = DBOperation.getInstance(this);
        mQueryDefaultCurrencyThread = new QueryDefaultCurrencyThread();
        mQueryDefaultCurrencyThread.start();
    }

    private void getDefaultCurrencyData() {
        String currentLanguageCode = getCurrentSystemLanguage();
        mDefaultCurrency = mDBOperation.queryDefaultCurrency(currentLanguageCode);
    }

    private String getCurrentSystemLanguage() {
        String language = mInstance.getResources().getConfiguration().locale.getLanguage();
        String country = mInstance.getResources().getConfiguration().locale.getCountry();
        String languageCode;
        if (!TextUtils.isEmpty(language) && TextUtils.isEmpty(country)) {
            languageCode = language;
        } else if (!TextUtils.isEmpty(language) && !TextUtils.isEmpty(country)) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(language).append("-").append(country);
            languageCode = stringBuilder.toString();
        } else {
            languageCode = "";
        }
        Log.d(TAG, "languageCode = " + languageCode);
        return languageCode;
    }

    class QueryDefaultCurrencyThread extends Thread {
        @Override
        public void run() {
            super.run();
            getDefaultCurrencyData();
        }
    }

    public void setDefaultCurrency(DefaultCurrency defaultCurrency) {
        this.mDefaultCurrency = defaultCurrency;
    }

    public DefaultCurrency getDefaultCurrency() {
        return mDefaultCurrency;
    }


}
