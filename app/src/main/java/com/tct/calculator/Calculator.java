/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ==========================================================================
 *HISTORY
 *
 *Tag            Date         Author        Description
 *============== ============ =============== ==============================
 *BUGFIX-979517  2015/11/25   kaifeng.lu       [Android 5.1][Calculator_v5.2.1.7.0310.0]Change Entrance to "Calculator"
 *BUGFIX-967237  2015/11/27   kaifeng.lu       [Android 5.1][Calculator_v5.2.1.7.0310.0]'No history' can not disappear automatically after rotate the screen.
 *BUGFIX-983830  2015/11/30   kaifeng.lu       [调研][性能]Memory Leak清理
 *BUGFIX-904574  2015/12/03   jin.dong         [Android 5.1][Calculator_v5.2.1.3.0308.0][Ergo] No animation shows when click INV button.
 *BUGFIX_982004  2015/12/07   kaifeng.lu       [Android 5.1][Calculator_v5.2.1.7.0310.0]It has no work after press '='key when input number.
 *BUGFIX_966467  2015/12/07   kaifeng.lu       [Android 5.1][Calculator_v5.2.1.7.0309.3][Ergo]the background color of the input area flash from down to top quickly in red.
 *BUGFIX_1192349  2015/12/07   kaifeng.lu      [Lock screen][Func]Screen is not locked by press power key after open Calculator from Func
 *BUGFIX_1202285   2015/12/23   kaifeng.lu     [Android 6.0][Calculator_v5.2.1.7.0314.0]It has no 'select' the number when enter again.
 *BUGFIX_1239024   2015/12/28   kaifeng.lu      [Calculator]the Paste  is error when the copy is a negative number
 *BUGFIX_1271732   2015/12/30   kaifeng.lu     [Calculator][FUNC]Can not lock the screen when open calculator func from lock screen
 *BUGFIX_1202273   2015/12/31   kaifeng.lu     [Lock screen][Func]Can not open calculator when double tap calculator first time
 *BUGFIX_1293809   2016/01/05   kaifeng.lu     [GAPP][Android 6.0][Calculator]It shakes when press power key after opening calculator.
 *BUGFIX_1392730   2016/01/11   kaifeng.lu     [GAPP][Android 6.0][Calculator]The calculation disappears when launch in mainscreen  .
 *BUGFIX_1868303  2016/03/29    kaifeng.lu     [Monkey][Calculator]CRASH:com.tct.calculator
 *BUGFIX_1999448  2016/04/27    kaifeng.lu     [Monkey][Calculator][ANR]com.tct.calculator
 ===========================================================================
 */

package com.tct.calculator;

import android.Manifest;
import android.animation.Animator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection; // MODIFIED by qiong.liu1, 2017-03-23,BUG-3621966
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration; // MODIFIED by kaifeng.lu, 2016-11-04,BUG-3005276
import android.graphics.Color; // MODIFIED by qiong.liu1, 2017-04-07,BUG-4452809
import android.icu.util.BuddhistCalendar;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager; // MODIFIED by qiong.liu1, 2017-03-30,BUG-3621966
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent; // MODIFIED by qiong.liu1, 2017-04-11,BUG-4452809
import android.view.Surface;
import android.view.View;
import android.view.Gravity;
import android.content.res.Resources;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.tct.calculator.data.DBOperation; // MODIFIED by qiong.liu1, 2017-03-23,BUG-3621966
import com.tct.calculator.fragment.CalculatorFragment;
import com.tct.calculator.fragment.ConverterFragment;
import com.tct.calculator.fragment.CurrencyFragment;
import com.tct.calculator.fragment.CurrencyList_Fragment;
import com.tct.calculator.fragment.WelcomeFragmentCalculator;
import com.tct.calculator.fragment.WelcomeFragmentConverter;
import com.tct.calculator.fragment.WelcomeFragmentCurrency;
/* MODIFIED-BEGIN by qiong.liu1, 2017-03-20,BUG-3621966*/
import com.tct.calculator.http.HttpConstants;
import com.tct.calculator.http.HttpRequestService;
/* MODIFIED-END by qiong.liu1,BUG-3621966*/
import com.tct.calculator.utils.Constant;
import com.tct.calculator.utils.SharedPreferencesHelper; // MODIFIED by qiong.liu1, 2017-05-04,BUG-4656997
import com.tct.calculator.utils.Utils;
import com.tct.calculator.view.DisplayOverlay; // MODIFIED by qiong.liu1, 2017-04-07,BUG-4452809
import com.tct.calculator.view.ExpandableLayout;
import com.tct.calculator.view.History;
import com.tct.calculator.view.interfaces.CalculatorCallBacks; // MODIFIED by qiong.liu1, 2017-04-11,BUG-4452809
import com.tct.calculator.view.interfaces.ICalculatorInterfaces;

/* MODIFIED-BEGIN by qiong.liu1, 2017-03-23,BUG-3621966*/
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List; // MODIFIED by qiong.liu1, 2017-03-30,BUG-3621966


/* MODIFIED-BEGIN by kaifeng.lu, 2016-08-08,BUG-2695213*/
/* MODIFIED-END by kaifeng.lu,BUG-2695213*/

//Added it by hong.zhan for PR817289 about fix to support exttra feature for test 20141028 begin
//import android.os.SystemProperties;
//Added it by hong.zhan for PR817289 about fix to support exttra feature for test 20141028 end

public class Calculator extends AppCompatActivity implements View.OnClickListener, CurrencyFragment.GetCurrencyPopCallBack {


    /**
     * use to concrol the fragment
     */
    public FragmentTransaction mFragmentTransaction;
    public CalculatorFragment mCalculatorFragment;
    /* MODIFIED-BEGIN by qiong.liu1, 2017-03-30,BUG-3621966*/
    public CalculatorFragment mCalculatorFragment_land;
    private CurrencyFragment mCurrencyFragment;
    private ConverterFragment mConverterFragment;
    private final FragmentManager mManager = getSupportFragmentManager();
    /* MODIFIED-END by qiong.liu1,BUG-3621966*/
    WelcomeFragmentCalculator wfc;
    WelcomeFragmentConverter wfcon;
    WelcomeFragmentCurrency wfcu;
    /*******************************************/
    private Animator mCurrentAnimator;

    private Constant.CalculatorType mCurrnetCalculatorType = Constant.CalculatorType.CALCULATOR;
    private ImageView mLogo_Calculator, mLogo_Currency, mLogo_Converter;
    private static final float DISPLAY_VIEW_MOVE_DISTANCE = 160.0f; // MODIFIED by qiong.liu1, 2017-03-30,BUG-3621966
    private static final float DISPLAY_VIEW_MOVEDISTANCE_SPLIT = 120.0f;

    private static final String TAG = "com.tct.calculator";
    private final String SHOW_BY_LOCKED = "showByLocked";
    private final String IS_SERCURE = "IsSecure";
    private String JUMP_TO_CALCULATOR = "jumpToCalculator";
    private boolean isRegisted = false;
    public static final int INVALID_RES_ID = -1;
    private final String KEY = "firstin";
    private final String PREF = "first_pref";
    private String JUMP_TO_SPLASH = "jumpToSplash";
    private SharedPreferences pref;

    private RelativeLayout mGuideLayout;
    /* MODIFIED-BEGIN by kaifeng.lu, 2016-11-04,BUG-3005276*/
    public int mCurrentFragment;

    /* set the switch for hawkeye sdk */
    private boolean mCalcualtorHawkeyeEnable ;
    private CalculatorCallBacks mCurrencyCallBack; // MODIFIED by qiong.liu1, 2017-04-11,BUG-4452809
    private CalculatorCallBacks mConvertCallBack; // MODIFIED by qiong.liu1, 2017-04-11,BUG-4452809


    /***********
     * contans currencyType
     ***********/
    public final int CALCULATORSTATUS = 0x0001;
    public final int CURRENCYSTATUS = 0x0002;
    public final int CONVERTERSTATUS = 0x0003;

    private int mCurrentIndex = 0;
    private static final String KEY_INDEX = "index";

    /**
     *  set the GuidButton in Splt Screen
     */

    private ExpandableLayout mExpandableLayout;
    private ImageView mLogo_Calculator_Splt , mLogo_Currency_Splt, mLogo_Converter_Splt;
    private final static String CALCULATOR_FUNCTION_FLAG = "calculator_function_flag";


    /**
     *  the follow variates used to set the screen when window is in Slipe mode
     * @param newConfig
     */
    private int mMaxScreemHeight = 0;
    private Display mDispaly;
    private DBOperation mDBOperation; // MODIFIED by qiong.liu1, 2017-03-23,BUG-3621966
    private boolean isDoubleClick = false;
    /* MODIFIED-BEGIN by qiong.liu1, 2017-05-04,BUG-4656997*/
    private boolean isInMultiWindow = false;
    private SharedPreferencesHelper mSharedPreferencesHelper;
    /* MODIFIED-END by qiong.liu1,BUG-4656997*/



    public enum ScreenState{
        PORTFULL , PORTQUTERTHREE , PORTHALF, PORTQUTER, LANDFULL, LANDHALF
    }

    public final static int SCREENPORTFULL = 0x0001;
    public final static int SCREENPORTQUTERTHREE  = 0x0002;
    public final static int SCREENPORTHALF = 0x0003;
    public final static int SCREENPORTQUTER = 0x0004;
    public final static int SCREENLANDFULL = 0x0005;
    public final static int SCREENLANDHALF = 0x0006;

    public static int mScreenState;
    private enum CalculatorFragmentState{
        CALCULATOR , CURRENCY , CONVERTER;
    }

    private CalculatorFragmentState mCalculatorFragmentState = CalculatorFragmentState.CALCULATOR;
    private final static String CALCULATOR_FUNTION_FLAG ="calculator_funtion_flag";
    private final static String CALCULATOR_BUNDLE ="calculator_bundle"; // MODIFIED by qiong.liu1, 2017-04-13,BUG-4452809


    /**
     * Show or hide button
     *
     * @param type VISIBLE or GONE
     */
    public void toggleFragmentButton(int type) {
        mLogo_Calculator.setVisibility(type);
        mLogo_Currency.setVisibility(type);
        mLogo_Converter.setVisibility(type);
    }
    /* MODIFIED-END by kaifeng.lu,BUG-3005276*/

    public enum CalculatorState {
        INPUT, EVALUATE, RESULT, ERROR
    }

    private BroadcastReceiver powerReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            final String action = intent.getAction();
            if (action.equals(Intent.ACTION_SCREEN_OFF)) {
                //TS:kaifeng.lu 2016-01-11 Calculator BUGFIX_1392730  MOD_S
                moveTaskToBack(true);
//                    Calculator.this.finish();
                if (isRegisted && powerReceiver != null) {
                    try {
                        unregisterReceiver(powerReceiver);
                        powerReceiver = null;
                        isRegisted = false;
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
                    } catch (Exception e) {
                        Log.d("Calculator", " Receiver not registered");
                    }
                }
                //TS:kaifeng.lu 2016-01-11 Calculator BUGFIX_1392730  MOD_E
            }
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onCreate start" + System.currentTimeMillis()); // MODIFIED by kaifeng.lu, 2016-05-25,BUG-2162730
        mCalcualtorHawkeyeEnable = Utils.getBoolean(this,"def_jrdcalculator_hawkeye_enable");
        //TS:kaifeng.lu 2015-12-19 Calculator BUGFIX_1192349  MOD_S
        //FR552228 START This CR is to implement the behavior between Func and Calculator.update by xiaolu.li 1/9/2015
        if (getIntent() != null && getIntent().getBooleanExtra(SHOW_BY_LOCKED, false) && getIntent().getBooleanExtra(IS_SERCURE, false)) {
            //TS:kaifeng.lu 2015-12-30 Calculator BUGFIX_1271732 ADD_S
            IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
            //TS:kaifeng.lu 2016-01-05 Calculator BUGFIX_1293809  ADD_S
            filter.setPriority(10000);
            //TS:kaifeng.lu 2016-01-05 Calculator BUGFIX_1293809  ADD_E
            registerReceiver(powerReceiver, filter);
            isRegisted = true;
            //TS:kaifeng.lu 2015-12-30 Calculator BUGFIX_1271732 ADD_E
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
            //TS:kaifeng.lu 2015-12-31 Calculator BUGFIX_ 1202273  ADD_S
        }
        if (getIntent() != null && getIntent().getBooleanExtra(JUMP_TO_CALCULATOR, false)) {
            IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
            //TS:kaifeng.lu 2016-01-05 Calculator BUGFIX_1293809  ADD_S
            filter.setPriority(10000);
            //TS:kaifeng.lu 2016-01-05 Calculator BUGFIX_1293809  ADD_E
            registerReceiver(powerReceiver, filter);
            isRegisted = true;
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        }
        setStatusBarColor();
        super.onCreate(savedInstanceState);
        if (isFirstIn()) {
            boolean showLocked = getIntent().getBooleanExtra(SHOW_BY_LOCKED, false) && getIntent().getBooleanExtra(IS_SERCURE, false);
            jumpTo(showLocked, Splash.class);
            return;
        }

        /**
         *   handle the model of Screen size in Android 7.0
         */

        mDispaly = getWindowManager().getDefaultDisplay();
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getRealMetrics(dm);
        mMaxScreemHeight = dm.heightPixels;
        /* MODIFIED-BEGIN by qiong.liu1, 2017-03-30,BUG-3621966*/
        mSharedPreferencesHelper = SharedPreferencesHelper.getInstance(this);
        isInMultiWindow = mSharedPreferencesHelper.getIsInMultiWindow(SharedPreferencesHelper.IS_IN_MULTIWINDOW);
        Log.i(TAG, "onCreate::isInMultiWindow " + isInMultiWindow);
        mScreenState = judgeTheScreenState();

        setContentView(R.layout.activity_calculator_port_new);
        intiView();
        ininFragment();

        /* MODIFIED-BEGIN by qiong.liu1, 2017-04-13,BUG-4452809*/
        Bundle b = null;
        /* MODIFIED-BEGIN by qiong.liu1, 2017-05-06,BUG-4576971*/
        if (null != savedInstanceState && mScreenState != SCREENLANDFULL) {
        /* MODIFIED-END by qiong.liu1,BUG-3621966*/
            mCurrentFragment = savedInstanceState.getInt(CALCULATOR_FUNTION_FLAG);
            b = savedInstanceState.getBundle(CALCULATOR_BUNDLE);
            setCurrentFragment(mCurrentFragment);
            updateSplitFlagGuide(mCurrentFragment);
        } else {
        /* MODIFIED-END by qiong.liu1,BUG-4576971*/
            setCurrentFragment(CALCULATORSTATUS);
            updateSplitFlagGuide(CALCULATORSTATUS);
        }
        b = b == null ? new Bundle() : b;
        mCalculatorFragment.setArguments(b);
        mCalculatorFragment_land.setArguments(b);
        /* MODIFIED-END by qiong.liu1,BUG-4452809*/
        obtainScreenSize();
        initAllCurrency(); // MODIFIED by qiong.liu1, 2017-03-23,BUG-3621966
    }

    /* MODIFIED-BEGIN by qiong.liu1, 2017-05-04,BUG-4656997*/
    @Override
    public void onMultiWindowModeChanged(boolean isInMultiWindowMode) {
        super.onMultiWindowModeChanged(isInMultiWindowMode);
        Log.d(TAG, " Calculator::onMultiWindowModeChanged::  " + isInMultiWindowMode);
        mSharedPreferencesHelper.saveIsInMultiWindow(SharedPreferencesHelper.IS_IN_MULTIWINDOW, isInMultiWindowMode);
    }
    /* MODIFIED-END by qiong.liu1,BUG-4656997*/

    private int judgeTheScreenState() {
        int currentState = 1;
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        int currentScreenHeight = mDispaly.getHeight();
        int currentScreenWidth = mDispaly.getWidth();
        Log.d(TAG, " currentScreenHeight " + currentScreenHeight);
        Log.d(TAG, " currentScreenWidth " + currentScreenWidth);
        Log.d(TAG, " rotation " + rotation);
        Log.d(TAG, " mMaxScreemHeight " + mMaxScreemHeight);
        if(rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180 ){
            if (currentScreenHeight >= mMaxScreemHeight*2/3) {
                currentState = SCREENPORTFULL;
            /* MODIFIED-BEGIN by qiong.liu1, 2017-05-04,BUG-4656997*/
            } else if (currentScreenHeight > mMaxScreemHeight/2 && currentScreenHeight <= mMaxScreemHeight*2/3){
                currentState = SCREENPORTQUTERTHREE;
            } else if (currentScreenHeight >= mMaxScreemHeight*1/4 && currentScreenHeight <= mMaxScreemHeight/2){
                currentState = SCREENPORTHALF;
            }

            /* MODIFIED-BEGIN by qiong.liu1, 2017-05-05,BUG-4639606*/
            //fix the bug when come to split mode in land
            if (currentScreenHeight > currentScreenWidth) {
                int variance = Math.abs(currentScreenHeight - currentScreenWidth);
                int minSide = currentScreenHeight < currentScreenWidth
                        ? currentScreenHeight : currentScreenWidth;
                if (variance < (minSide / 3)) {
                    currentState = SCREENPORTHALF;
                } else {
                    currentState = SCREENPORTFULL;
                }
            }
            /* MODIFIED-END by qiong.liu1,BUG-4639606*/

        } else {
            if(rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270){
                if(currentScreenWidth < mMaxScreemHeight ){
                    currentState = SCREENLANDHALF;
                } else {
                /* MODIFIED-END by qiong.liu1,BUG-4656997*/
                    currentState = SCREENLANDFULL;
                }
/* MODIFIED-BEGIN by qiong.liu1, 2017-05-05,BUG-4639606*/
//                //The H & W unchanged when back
//                /* MODIFIED-BEGIN by qiong.liu1, 2017-04-19,BUG-4452809*/
//                if (currentScreenHeight > currentScreenWidth){
//                    currentState = SCREENLANDFULL;
//                    /* MODIFIED-END by qiong.liu1,BUG-4452809*/
//                }

                //fix the bug when come to split mode in land
                if (currentScreenHeight > currentScreenWidth) {
                    int variance = Math.abs(currentScreenHeight - currentScreenWidth);
                    int minSide = currentScreenHeight < currentScreenWidth
                            ? currentScreenHeight : currentScreenWidth;
                    if (variance < (minSide / 3)) {
                        currentState = SCREENLANDHALF;
                    } else {
                        currentState = SCREENLANDFULL;
                    }
                    /* MODIFIED-END by qiong.liu1,BUG-4639606*/
                }
            }
        }
        /* MODIFIED-BEGIN by qiong.liu1, 2017-05-04,BUG-4656997*/
        if (isInMultiWindow) {
            currentState = SCREENPORTHALF;
        }
        Log.d(TAG, " currentState " + currentState);
        /* MODIFIED-END by qiong.liu1,BUG-4656997*/
        return currentState;
    }

    private void initAllCurrency() {
        mDBOperation = DBOperation.getInstance(this);
        /* MODIFIED-BEGIN by qiong.liu1, 2017-04-21,BUG-4452809*/
        String updateTime = mDBOperation.queryUpdateTime();
        if(mDBOperation.isCurrencyRecordInDbEmpty() || updateTimeCompare(updateTime)){
        /* MODIFIED-END by qiong.liu1,BUG-4452809*/
            Intent intent = new Intent(this, HttpRequestService.class);
            intent.putExtra(HttpConstants.KEY_REQUEST_ID, HttpConstants.CONVERT_FROM_JSON_REQUEST);
            intent.putExtra(HttpConstants.KEY_TO, "*");
            intent.putExtra(HttpConstants.KEY_FROM, "USD");
            intent.putExtra(HttpConstants.KEY_AMOUNT, "1");
            startService(intent);
        }
    }

    /* MODIFIED-BEGIN by qiong.liu1, 2017-04-21,BUG-4452809*/
    private boolean updateTimeCompare(String updateTime) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String currentTime = format.format(new Date());
        Calendar cUpdate = Calendar.getInstance();
        Calendar cCurrent = Calendar.getInstance();
        try {
            cCurrent.setTime(format.parse(currentTime));
            cUpdate.setTime(format.parse(updateTime.substring(0, 10)));
            return cUpdate.compareTo(cCurrent) < 0 ? true : false;

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return true;
    }
    /* MODIFIED-END by qiong.liu1,BUG-4452809*/

    protected void intiView() {
        mGuideLayout = (RelativeLayout) findViewById(R.id.guide_layout);
        mLogo_Calculator = (ImageView) findViewById(R.id.logo_calculator);
        mLogo_Currency = (ImageView) findViewById(R.id.logo_currency);
        mLogo_Converter = (ImageView) findViewById(R.id.logo_conventer);
        mLogo_Calculator.setOnClickListener(this);
        mLogo_Currency.setOnClickListener(this);
        mLogo_Converter.setOnClickListener(this);

        mExpandableLayout = (ExpandableLayout) findViewById(R.id.calculator_expandablelayout);
        mLogo_Calculator_Splt = (ImageView) findViewById(R.id.logo_calculator_slip);
        mLogo_Currency_Splt = (ImageView) findViewById(R.id.logo_currency_slip);
        mLogo_Converter_Splt = (ImageView) findViewById(R.id.logo_conventer_slip);
        mLogo_Calculator_Splt.setOnClickListener(this);
        mLogo_Currency_Splt.setOnClickListener(this);
        mLogo_Converter_Splt.setOnClickListener(this);

        updateFlagForGuide(R.id.logo_calculator);
    }

    public void starItemClick(String data) {
        if (mCurrencyFragment != null) {
            mCurrencyFragment.onClickCallBack(data);
        }
    }

    public String getCurrencySelectData() {
        if (mCurrencyFragment != null) {
            return mCurrencyFragment.onGetCurrencySelectDataCallBack();
        }
        return null;
    }

    /**
     * Set color for status bar.
     */
    private void setStatusBarColor() {
        /* MODIFIED-BEGIN by qiong.liu1, 2017-04-07,BUG-4452809*/
        getWindow().setStatusBarColor(Color.parseColor("#20182023"));
/*
// MODIFIED-END by qiong.liu1, BUG-4452809
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        ViewGroup decorViewGroup = (ViewGroup) getWindow().getDecorView();
        View statusBarView = new View(getWindow().getContext());
        int statusBarHeight = getStatusBarHeight(getWindow().getContext());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, statusBarHeight);
        params.gravity = Gravity.TOP;
        statusBarView.setLayoutParams(params);
        statusBarView.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDarkGreen));
        decorViewGroup.addView(statusBarView);
*/ // MODIFIED by qiong.liu1, 2017-04-07,BUG-4452809
    }

    /**
     * Get height of status bar.
     * @param context context
     * @return status bar height
     */
    private int getStatusBarHeight(Context context) {
        int statusBarHeight = 0;
        Resources res = context.getResources();
        int resourceId = res.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = res.getDimensionPixelSize(resourceId);
        }
        return statusBarHeight;
    }

    protected void ininFragment() {

        mFragmentTransaction = getSupportFragmentManager().beginTransaction();
        mCalculatorFragment = new CalculatorFragment();
        mCalculatorFragment_land = new CalculatorFragment(); // MODIFIED by qiong.liu1, 2017-03-30,BUG-3621966
        mCurrencyFragment = new CurrencyFragment();
        mConverterFragment = new ConverterFragment();
//        mFragmentTransaction.add(R.id.content_fragment, mCurrencyFragment);
//        mFragmentTransaction.add(R.id.content_fragment, mConverterFragment);
//        mFragmentTransaction.add(R.id.content_fragment, mCalculatorFragment);
        // Get the orientation of display
        int orientation = this.getResources().getConfiguration().orientation;
        if(mScreenState == SCREENPORTFULL ){
            mGuideLayout.setVisibility(View.VISIBLE);
            mExpandableLayout.setVisibility(View.GONE);
//            setCurrentFragment(mCurrentFragment);
        }else if(mScreenState == SCREENLANDFULL){
            mGuideLayout.setVisibility(View.GONE);
            mExpandableLayout.setVisibility(View.GONE);
        }else{
            mGuideLayout.setVisibility(View.GONE);
            mExpandableLayout.setVisibility(View.VISIBLE);
        }

//        if (orientation == Configuration.ORIENTATION_LANDSCAPE && mScreenState == SCREENLANDFULL) {
//            setCurrentFragment(CALCULATORLANDSCAPE);
////            toggleFragmentButton(View.GONE);
//            Log.e("kftest","here is 1");
//        } else if (orientation == Configuration.ORIENTATION_PORTRAIT)
//            setCurrentFragment(CALCULATORSTATUS);

    }

    private boolean isFirstIn() {
        pref = getSharedPreferences(PREF, MODE_PRIVATE);
        return pref.getBoolean(KEY, true);
    }


    private void jumpTo(boolean b, Class<?> cls) {
        Intent mIntent = new Intent(this, cls);
        mIntent.putExtra(JUMP_TO_SPLASH, b);
        startActivity(mIntent);
        finish();
    }

    @Override
    public void onClick(View v) {
        /* MODIFIED-BEGIN by qiong.liu1, 2017-04-18,BUG-4452809*/
        if (v.getId() == R.id.logo_calculator_slip) {
            isDoubleClick = false;
        }
        if (mScreenState != SCREENLANDFULL && mScreenState != SCREENPORTFULL && isDoubleClick) {
            Log.d(TAG, " Click too fast");
            return;
        }
        mFragmentTransaction = getSupportFragmentManager().beginTransaction();
        switch (v.getId()) {
            case R.id.logo_calculator:
                setCurrentFragment(CALCULATORSTATUS);
                updateFlagForGuide(v.getId());
                break;
            case R.id.logo_currency:
                setCurrentFragment(CURRENCYSTATUS); // MODIFIED by kaifeng.lu, 2016-11-04,BUG-3005276
                updateFlagForGuide(v.getId());
                break;
            case R.id.logo_conventer:
                setCurrentFragment(CONVERTERSTATUS);
                updateFlagForGuide(v.getId());
                /* MODIFIED-END by qiong.liu1,BUG-4452809*/
                break;
            /**
             * handle the guider button in splt screen
             */
            case R.id.logo_calculator_slip:
                if(mExpandableLayout.isExpanded()) {
                    mLogo_Calculator_Splt.setImageResource(R.drawable.logo_calculator_select);
                    mLogo_Currency_Splt.setImageResource(R.drawable.logo_currency_unselect);
                    mLogo_Converter_Splt.setImageResource(R.drawable.logo_converter_unselect);
                    mCalculatorFragmentState = CalculatorFragmentState.CALCULATOR;
                    setCurrentFragment(CALCULATORSTATUS);
                }else{
                    switch (mCurrentFragment){
                        case CALCULATORSTATUS:
                            mLogo_Calculator_Splt.setImageResource(R.drawable.logo_calculator_select);
                            break;
                        case CURRENCYSTATUS:
                            mLogo_Calculator_Splt.setImageResource(R.drawable.logo_calculator_unselect);
                            break;
                        case CONVERTERSTATUS:
                            mLogo_Calculator_Splt.setImageResource(R.drawable.logo_calculator_unselect);
                            break;
                        default:
                            break;
                    }
                }
                mExpandableLayout.toggle();
                break;
            case R.id.logo_conventer_slip:
                if(mExpandableLayout.isExpanded())
                setCurrentFragment(CONVERTERSTATUS);
                setMultiLogo(v.getId());
                /* MODIFIED-BEGIN by qiong.liu1, 2017-04-18,BUG-4452809*/
                isDoubleClick = true;
                mExpandableLayout.toggle();
                break;
            case R.id.logo_currency_slip:
                if(mExpandableLayout.isExpanded());
                setCurrentFragment(CURRENCYSTATUS);
                setMultiLogo(v.getId());
                isDoubleClick = true;
                mExpandableLayout.toggle();
                break;
            default:
                break;
        }
//        updateFlagForGuide(v.getId());
/* MODIFIED-END by qiong.liu1,BUG-4452809*/
    }

    /**
     * Update picture for guide.
     **/
    private void updateFlagForGuide(int id) {
        switch (id) {
            case R.id.logo_calculator:
            case R.id.logo_calculator_slip:
                mLogo_Calculator.setImageResource(R.drawable.mainlogo_calculator_select);
                mLogo_Currency.setImageResource(R.drawable.mainlogo_currency_unselect);
                mLogo_Converter.setImageResource(R.drawable.mainlogo_converter_unselect);
                break;
            case R.id.logo_currency:
            case R.id.logo_currency_slip:
                mLogo_Calculator.setImageResource(R.drawable.mainlogo_calculator_unselect);
                mLogo_Currency.setImageResource(R.drawable.mainlogo_currency_select);
                mLogo_Converter.setImageResource(R.drawable.mainlogo_converter_unselect);
                break;
            case R.id.logo_conventer:
            case R.id.logo_conventer_slip:
                mLogo_Calculator.setImageResource(R.drawable.mainlogo_calculator_unselect);
                mLogo_Currency.setImageResource(R.drawable.mainlogo_currency_unselect);
                mLogo_Converter.setImageResource(R.drawable.mainlogo_convert_select);
                break;
            default:
                break;
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        Log.e("must to be","onDestroy");
        //TS:kaifeng.lu 2015-12-31 Calculator BUGFIX_ 1202273  MOD_S
        if (powerReceiver != null && isRegisted) {
            try {
                unregisterReceiver(powerReceiver);
                powerReceiver = null;
                isRegisted = false;
            } catch (Exception e) {
                Log.d("Calculator", " Receiver not registered");
            }
        }
        //TS:kaifeng.lu 2015-12-31 Calculator BUGFIX_ 1202273  MOD_E
    }

    @Override
    public void onBackPressed() {
        switch (mCurrnetCalculatorType) {
            case CALCULATOR:
                break;
            case CURRENCY:
                if (mCurrencyFragment != null) {
                    if (mCurrencyFragment.onBackPressed()) {
                        return;
                    }
                }
                break;
            case CONVERTER:
                if (mConverterFragment != null) {
                    if (mConverterFragment.onBackPressed()) {
                        return;
                    }
                }
                break;
            case CURRENCY_LIST:
                hidGuideButton(-1.0f);
                break;
        }
        super.onBackPressed();
    }

    public void onButtonClick(View view) {
        switch (mCurrnetCalculatorType) {
            case CALCULATOR:
                break;
            case CURRENCY:
                break;
            case CONVERTER:
                if (mConverterFragment != null) {
                    mConverterFragment.onClick(view);
                }
                break;
        }
    }

    public void onArea2Click(View view) {
        switch (mCurrnetCalculatorType) {
            case CALCULATOR:
                break;
            case CURRENCY:
                break;
            case CONVERTER:
                if (mConverterFragment != null) {
                    mConverterFragment.onArea2Click(view);
                }
                break;
        }
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();

        // If there's an animation in progress, cancel it so the user interaction can be handled
        // immediately.
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }
        switch (mCurrnetCalculatorType) {
            case CALCULATOR:
                break;
            case CURRENCY:
                break;
            case CONVERTER:
                if (mConverterFragment != null) {
                    mConverterFragment.onUserInteraction();
                }
                break;
        }
    }

    //TS:kaifeng.lu 2016-01-11 Calculator BUGFIX_1392730  ADD_S
    @Override
    protected void onNewIntent(Intent intent) {
        if (intent != null && intent.getBooleanExtra(SHOW_BY_LOCKED, false) && intent.getBooleanExtra(IS_SERCURE, false)) {
            if (!isRegisted) {
                IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
                filter.setPriority(10000);
                registerReceiver(powerReceiver, filter);
                isRegisted = true;
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
            }
//            mFormulaEditText.setText("");
//            cancelCopyPaste();
        } else {
            if (isRegisted) {
                try {
                    unregisterReceiver(powerReceiver);
                    powerReceiver = null;
                    isRegisted = false;
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
                } catch (Exception e) {
                }
            }
        }
        if (mConverterFragment != null) {
            mConverterFragment.onNewIntent(intent);
        }
        super.onNewIntent(intent);
    }

    @Override
    public void showListFragment() {
        mFragmentTransaction = this.getSupportFragmentManager().beginTransaction();
        CurrencyList_Fragment clf = new CurrencyList_Fragment();
        if (!clf.isAdded()) {
            mFragmentTransaction.add(R.id.content_fragment, clf);
        } else {
            mFragmentTransaction.replace(R.id.content_fragment, clf);
        }
        hidGuideButton(DISPLAY_VIEW_MOVE_DISTANCE);
        setShowStatusForGuideLayout(false);
        mCurrnetCalculatorType = Constant.CalculatorType.CURRENCY_LIST;
        mFragmentTransaction.addToBackStack(null);
//        mFragmentTransaction.hide(mConverterFragment).show(clf);
        mFragmentTransaction.commit();
    }

    /**
     * Set show status for guide layout, the reason is that guide layout can click though it's alpah is 0.0f.
     *
     * @param isShow
     */
    public void setShowStatusForGuideLayout(boolean isShow) {
        if (mGuideLayout != null) {
            if (isShow && mScreenState == SCREENPORTFULL) {
                mGuideLayout.setVisibility(View.VISIBLE);
            } else {
                mGuideLayout.setVisibility(View.GONE);
            }
        }
    }

    public void hidGuideButton(float spanY) {
        setGuideLayoutAlpah(spanY);
        setSplitGuideLayoutAlpah(spanY);
    }


    /**
     * used to set which fragment to show
     **/

    public void setCurrentFragment(int currentTpye) {
        /* MODIFIED-BEGIN by qiong.liu1, 2017-03-30,BUG-3621966*/
        Log.d(TAG, " setCurrentFragment currentTpye " + currentTpye);
        Log.d(TAG, " setCurrentFragment mCurrentFragment " + mCurrentFragment);
        if(currentTpye == mCurrentFragment){
            return;
        }else{
            mCurrentFragment = currentTpye; // MODIFIED by kaifeng.lu, 2016-11-04,BUG-3005276
        }
        FragmentTransaction transaction = mManager.beginTransaction();
        switch (currentTpye){
            case  CALCULATORSTATUS:
                if(mCurrencyFragment != null){
                    mCurrencyFragment.clearCurrencyFocus();
                    transaction.hide(mCurrencyFragment);
                }
                if (mConverterFragment != null) {
                    transaction.hide(mConverterFragment);
                }
                /* MODIFIED-BEGIN by kaifeng.lu, 2016-11-04,BUG-3005276*/
//                if (mCalculatorFragmentLandscape != null) {
//                    transaction.hide(mCalculatorFragmentLandscape);
//                }
                /* MODIFIED-END by kaifeng.lu,BUG-3005276*/
                /* MODIFIED-BEGIN by qiong.liu1, 2017-04-25,BUG-4635361*/
                if (mScreenState == SCREENLANDFULL) {
                    if (!mCalculatorFragment.isAdded()) {
                        transaction.add(R.id.content_fragment, mCalculatorFragment_land);
                        transaction.show(mCalculatorFragment_land);
                    } else {
                        transaction.show(mCalculatorFragment_land);
                    }
                    /* MODIFIED-BEGIN by qiong.liu1, 2017-05-04,BUG-4656997*/
                    mCalculatorFragment_land.requestCursor();
                } else {
                    if (!mCalculatorFragment.isAdded()) {
                        transaction.add(R.id.content_fragment, mCalculatorFragment);
                        transaction.show(mCalculatorFragment);
                    } else {
                        transaction.show(mCalculatorFragment);
                    }
                    mCalculatorFragment.requestCursor();
                    /* MODIFIED-END by qiong.liu1,BUG-4656997*/
                    /* MODIFIED-END by qiong.liu1,BUG-4635361*/
                }

                // If call commit, maybe force down
                transaction.commitAllowingStateLoss();
                mCurrnetCalculatorType = Constant.CalculatorType.CALCULATOR;
                break;
            case CURRENCYSTATUS:
                if (mCalculatorFragment != null) {
                    transaction.hide(mCalculatorFragment);
                }
                if (mConverterFragment != null) {
                    transaction.hide(mConverterFragment);
                }
                if (!mCurrencyFragment.isAdded()) {
                    transaction.add(R.id.content_fragment, mCurrencyFragment);
                    transaction.show(mCurrencyFragment);
                } else {
                    transaction.show(mCurrencyFragment);
                }

//                mCurrencyFragment.setCurrencyTime(); // MODIFIED by qiong.liu1, 2017-03-20,BUG-3621966
                mCurrencyFragment.setCurrencyCursorVisible();
                transaction.commitAllowingStateLoss();
                mCurrnetCalculatorType = Constant.CalculatorType.CURRENCY;
                break;
            case CONVERTERSTATUS:
                if (mCalculatorFragment != null) {
                    transaction.hide(mCalculatorFragment);
                }
                if(mCurrencyFragment != null){
                    mCurrencyFragment.clearCurrencyFocus();
                    transaction.hide(mCurrencyFragment);
                }
                if (!mConverterFragment.isAdded()) {
                    transaction.add(R.id.content_fragment, mConverterFragment);
                    transaction.show(mConverterFragment);
                } else {
                    transaction.show(mConverterFragment);
                }

                mConverterFragment.requestCursor();
                transaction.commitAllowingStateLoss();
                /* MODIFIED-END by qiong.liu1,BUG-3621966*/
                mCurrnetCalculatorType = Constant.CalculatorType.CONVERTER;
                break;
            /* MODIFIED-BEGIN by kaifeng.lu, 2016-11-04,BUG-3005276*/
            default:
                break;
        }

    }

    /**
     *  prohibit changing the size of font
     */
    @Override
    public Resources getResources() {

        Resources res = super.getResources();
        Configuration config = new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config,res.getDisplayMetrics());
        return res;
    }

    /**
     * Set alpah for guide layout.
     *
     * @param moveDistance move distance
     */
    private void setGuideLayoutAlpah(float moveDistance) {
        if (mGuideLayout != null) {
            float alphaValue = 1.0f - (moveDistance / DISPLAY_VIEW_MOVE_DISTANCE);
            mGuideLayout.setAlpha(alphaValue);

            if (moveDistance > DISPLAY_VIEW_MOVE_DISTANCE) {
                setShowStatusForGuideLayout(false);
            } else {
                setShowStatusForGuideLayout(true);
                if (mCurrnetCalculatorType == Constant.CalculatorType.CURRENCY_LIST) {
                    mCurrencyFragment.setCurrencyCursorVisible();
                }
            }
        }
    }

    /**
     *    Set the background picture fo Guid_button in splt sccreen
     */


    public int getmScreenState() {
        return mScreenState;
    }

    protected void setMultiLogo(int viewId){
        switch (viewId){
            case R.id.logo_calculator_slip:
                if(mExpandableLayout.isExpanded()) {
                    switch (mCalculatorFragmentState){
                        case CALCULATOR:
                            mLogo_Calculator_Splt.setImageResource(R.drawable.logo_calculator_select);
                            break;
                        case CURRENCY:
                            mLogo_Calculator_Splt.setImageResource(R.drawable.logo_calculator_unselect);
                            break;
                        case CONVERTER:
                            mLogo_Calculator_Splt.setImageResource(R.drawable.logo_calculator_unselect);
                            break;
                        default:
                            break;
                    }
                }
                break;
            case R.id.logo_currency_slip:
                mLogo_Calculator_Splt.setImageResource(R.drawable.logo_currency_select);
                mLogo_Currency_Splt.setImageResource(R.drawable.logo_currency_select);
                mLogo_Converter_Splt.setImageResource(R.drawable.logo_converter_unselect);
                mCalculatorFragmentState = CalculatorFragmentState.CURRENCY;

                break;
            case R.id.logo_conventer_slip:
                mLogo_Calculator_Splt.setImageResource(R.drawable.logo_convert_select);
                mLogo_Currency_Splt.setImageResource(R.drawable.logo_currency_unselect);
                mLogo_Converter_Splt.setImageResource(R.drawable.logo_convert_select);
                mCalculatorFragmentState = CalculatorFragmentState.CONVERTER;
                break;
            default:
                break;
        }
    }

//    @Override
//    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
//
//        switch (mCalculatorFragmentState){
//            case CONVERTER:
//                outState.putInt(CALCULATOR_FUNTION_FLAG,CONVERTERSTATUS);
//                break;
//            case CURRENCY:
//                outState.putInt(CALCULATOR_FUNTION_FLAG,CURRENCYSTATUS);
//                break;
//            default:
//                outState.putInt(CALCULATOR_FUNTION_FLAG,CALCULATORSTATUS);
//                break;
//        }
//
//        super.onSaveInstanceState(outState, outPersistentState);
//
//    }


    // obtain the Screen's size


    private void obtainScreenSize(){

        DisplayMetrics dm;
        dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int wid = dm.widthPixels;
        int heigh = dm.heightPixels;
        Log.e("xxxx","current screen size is ( widh = " + wid + " heigh = " + heigh + " )");
    }

    /**
     *   when the Click Event occur in the fragments in calculator coolase the guide button
     */
    public void coolapseButton() {
        if(mExpandableLayout.isExpanded() && mExpandableLayout.getVisibility() == View.VISIBLE){
            /* MODIFIED-BEGIN by qiong.liu1, 2017-04-18,BUG-4452809*/
            if (mCurrentFragment != CALCULATORSTATUS) {
                setMultiLogo(mCurrentFragment == CURRENCYSTATUS ? R.id.logo_currency_slip : R.id.logo_conventer_slip);
            }
            /* MODIFIED-END by qiong.liu1,BUG-4452809*/
            mExpandableLayout.toggle();
        }
    }

    /**
     *
     *
     */

    private void updateSplitFlagGuide(int mCurrentFragment){
        switch (mCurrentFragment){
            case CALCULATORSTATUS:
                mLogo_Calculator.setImageResource(R.drawable.mainlogo_calculator_select);
                mLogo_Currency.setImageResource(R.drawable.mainlogo_currency_unselect);
                mLogo_Converter.setImageResource(R.drawable.mainlogo_converter_unselect);
                if(!mExpandableLayout.isExpanded()) {
                    mLogo_Calculator_Splt.setImageResource(R.drawable.logo_calculator_select);
                    mLogo_Currency_Splt.setImageResource(R.drawable.logo_currency_unselect);
                    mLogo_Converter_Splt.setImageResource(R.drawable.logo_converter_unselect);
                }
                break;
            case CONVERTERSTATUS:
                mLogo_Calculator.setImageResource(R.drawable.mainlogo_calculator_unselect);
                mLogo_Currency.setImageResource(R.drawable.mainlogo_currency_unselect);
                mLogo_Converter.setImageResource(R.drawable.mainlogo_convert_select);

                if(!mExpandableLayout.isExpanded()){
                    mLogo_Calculator_Splt.setImageResource(R.drawable.logo_convert_select);
                    mLogo_Currency_Splt.setImageResource(R.drawable.logo_currency_unselect);
                    mLogo_Converter_Splt.setImageResource(R.drawable.logo_convert_select);
                }
                break;
            case CURRENCYSTATUS:
                mLogo_Calculator.setImageResource(R.drawable.mainlogo_calculator_unselect);
                mLogo_Currency.setImageResource(R.drawable.mainlogo_currency_select);
                mLogo_Converter.setImageResource(R.drawable.mainlogo_converter_unselect);

                if(!mExpandableLayout.isExpanded()){
                    mLogo_Calculator_Splt.setImageResource(R.drawable.logo_currency_select);
                    mLogo_Currency_Splt.setImageResource(R.drawable.logo_currency_select);
                    mLogo_Converter_Splt.setImageResource(R.drawable.logo_converter_unselect);
                }

                break;
            default:
                break;
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {

        Log.d(TAG, " Calculator::onSaveInstanceState::mCurrentFragment = " + mCurrentFragment); // MODIFIED by qiong.liu1, 2017-05-06,BUG-4576971
        switch (mCurrentFragment){
            case CONVERTERSTATUS:
                outState.putInt(CALCULATOR_FUNTION_FLAG,CONVERTERSTATUS);
                break;
            case CURRENCYSTATUS:
                outState.putInt(CALCULATOR_FUNTION_FLAG,CURRENCYSTATUS);
                break;
            default:
                outState.putInt(CALCULATOR_FUNTION_FLAG,CALCULATORSTATUS);
                outState.putBundle(CALCULATOR_BUNDLE, mCalculatorFragment.getArguments()); // MODIFIED by qiong.liu1, 2017-04-13,BUG-4452809
                break;
        }
        mSharedPreferencesHelper.saveIsInMultiWindow(SharedPreferencesHelper.IS_IN_MULTIWINDOW, false); // MODIFIED by qiong.liu1, 2017-05-04,BUG-4656997
        super.onSaveInstanceState(outState);
    }


    /**
     * The follow 2 methods controls the expandGudieLayout when history view is moving
     * @param xAlpah the Alpah value of the expandGuideLayout
     */

    protected void setSplitGuideLayoutAlpah(float xAlpah){
     if(mScreenState == SCREENPORTFULL || mScreenState == SCREENLANDFULL){
         return;
     }
        if (mExpandableLayout != null) {
            float alphaValue = 1.0f - (xAlpah / DISPLAY_VIEW_MOVEDISTANCE_SPLIT);
            mExpandableLayout.setAlpha(alphaValue);
            if (xAlpah > DISPLAY_VIEW_MOVEDISTANCE_SPLIT) {
                setShowExpandLayout(true);
            } else if (xAlpah != 0.0) { // MODIFIED by qiong.liu1, 2017-04-21,BUG-4452809
                setShowExpandLayout(false);
                if (mCurrnetCalculatorType == Constant.CalculatorType.CURRENCY_LIST) {
                    mCurrencyFragment.setCurrencyCursorVisible();
                }
            }
        }
    }

    /* MODIFIED-BEGIN by qiong.liu1, 2017-03-23,BUG-3621966*/
    @Override
    public boolean bindService(Intent service, ServiceConnection conn, int flags) {
        return super.bindService(service, conn, flags);
    }
    /* MODIFIED-END by qiong.liu1,BUG-3621966*/

    public void setShowExpandLayout(boolean isShow) {
        if (mExpandableLayout != null) {
            if (isShow){
                mExpandableLayout.setVisibility(View.GONE);
            } else {
                mExpandableLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    /* MODIFIED-BEGIN by qiong.liu1, 2017-04-11,BUG-4452809*/
    public void setCalculatorCallBacks(CalculatorCallBacks callBacks, int TYPES){
        if (TYPES == CURRENCYSTATUS) {
            mCurrencyCallBack = callBacks;
        }else {
            mConvertCallBack = callBacks;
        }
    }

    /* MODIFIED-BEGIN by qiong.liu1, 2017-03-30,BUG-3621966*/
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mScreenState = judgeTheScreenState();
        Log.d(TAG, "onConfigurationChanged  mScreenState "  + mScreenState);
        Log.d(TAG, "onConfigurationChanged  mCurrentFragment "  + mCurrentFragment);
        /* MODIFIED-BEGIN by qiong.liu1, 2017-04-13,BUG-4452809*/
        if (mCurrencyCallBack != null) {
            mCurrencyCallBack.onConfigurationChanged();
        }
        if (mConvertCallBack != null) {
            mConvertCallBack.onConfigurationChanged();
            /* MODIFIED-END by qiong.liu1,BUG-4452809*/
        }
        /* MODIFIED-END by qiong.liu1,BUG-4452809*/
        FragmentTransaction transaction = mManager.beginTransaction();
        switch (mScreenState){
            case SCREENPORTFULL:
                setCurrentFragment(CALCULATORSTATUS);
                transaction.replace(R.id.content_fragment,mCalculatorFragment);
                updateSplitFlagGuide(CALCULATORSTATUS);
                mGuideLayout.setVisibility(View.VISIBLE);
                mCalculatorFragment.requestCursor(); // MODIFIED by qiong.liu1, 2017-05-04,BUG-4656997
                transaction.commitAllowingStateLoss();
                break;
            case SCREENLANDFULL:
                setCurrentFragment(CALCULATORSTATUS);
                transaction.replace(R.id.content_fragment,mCalculatorFragment_land);
//                updateSplitFlagGuide(mCurrentFragment);
                mGuideLayout.setVisibility(View.GONE);
                mCalculatorFragment_land.requestCursor(); // MODIFIED by qiong.liu1, 2017-05-04,BUG-4656997
                transaction.commitAllowingStateLoss();
                break;
//            case SCREENPORTHALF:
//                transaction.replace(R.id.content_fragment,mCalculatorFragment_land);
//                break;
//            case SCREENLANDFULL:
//                transaction.replace(R.id.content_fragment,mCalculatorFragment_land);
//                break;
//            case SCREENLANDHALF:
//                transaction.replace(R.id.content_fragment,mCalculatorFragment_land);
//                break;

        }
    }
    /* MODIFIED-END by qiong.liu1,BUG-3621966*/
}
