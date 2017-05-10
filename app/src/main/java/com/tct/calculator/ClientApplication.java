/* Copyright (C) 2017 Tcl Corporation Limited */
package com.tct.calculator;

import android.app.Application;
import android.provider.Settings;
import android.util.Log;

import com.tcl.statisticsdk.agent.StatisticsAgent;
import com.tcl.statisticsdk.agent.StatisticsConfig;
import com.tcl.statisticsdk.util.Unit;
import com.tct.calculator.data.Utils;

/**
 * Created by user on 16-11-17.
 */
public class ClientApplication extends Application {

    private static final String TAG = "Calculator";
    private static boolean mCalculatorUpgradeEnable;
    public static boolean mCalcualtorHawkeyeEnable;
    private static final long  CALCULATORSESSIONTIMEOUT = 30000;

    @Override
    public void onCreate() {
        super.onCreate();
//        try {
//            mCalculatorUpgradeEnable = Utils.getBoolean(this, "def_jrdcalculator_upgrade_enable");
//            Log.e(TAG,"mCalculatorUpgradeEnable = " + mCalculatorUpgradeEnable);
//            if (mCalculatorUpgradeEnable) {
//                UpdateSdkManager.init(getApplicationContext());
//                UpdateSdkManager.setSDKSwitch(getApplicationContext(), true);
//            }
//        } catch (Exception e) {
//            Log.e(TAG, "Upgrade initialization error");
//        }

        try {
            mCalcualtorHawkeyeEnable = Utils.getBoolean(this, "def_jrdcalculator_hawkeye_enable");
            Log.e(TAG,"mCalcualtorHawkeyeEnable = " + mCalcualtorHawkeyeEnable);
            if (mCalcualtorHawkeyeEnable) {
                    new StatisticsConfig()
                            .setAutoTraceActivity(false)
                            .setIntervalTime(1, Unit.DAY)
                            .setDebugMode(true)
                            .setWifiOnly(true)
                            .init(getApplicationContext());

//                boolean hasFeedBack = Utils.isPackageExist(getApplicationContext(), "com.tct.endusertest");
//                StatisticsAgent.init(this.getApplicationContext());
//                StatisticsAgent.setDebugMode(true);
//                StatisticsAgent.setAutoTraceActivity(false);
//                StatisticsAgent.setWifiOnly(this.getApplicationContext(), true);
//                boolean isDiagnostic = Settings.Global.getInt(getApplicationContext().getContentResolver(), "def.diagnostic.on", -1) != -1 ? true : false;
//                Log.d(TAG, "isDiagnostic : " + isDiagnostic);
//                if (isDiagnostic) {
//                    StatisticsAgent.experienceImprove(getApplicationContext(), true);
//                } else {
//                    StatisticsAgent.enableReport(getApplicationContext(), false);
//                }
//                int day = 1;
//                if (hasFeedBack) {
//                    day = 1;
//                }
//                StatisticsAgent.setPeriod(day, Unit.DAY);
//                StatisticsAgent.setSessionTimeOut(this.getApplicationContext(),CALCULATORSESSIONTIMEOUT);
            }
        } catch (Exception e) {
            Log.e(TAG, "Hawkeys initialization error");
        }
    }
}
