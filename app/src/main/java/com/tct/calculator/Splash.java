/*
 ==========================================================================
 *HISTORY
 *
 *Tag            Date         Author        Description
 *============== ============ =============== ==============================
 *BUGFIX-979517  2015/11/25   kaifeng.lu       [Android 5.1][Calculator_v5.2.1.7.0310.0]Change Entrance to "Calculator"
 *BUGFIX-1052566  2015/12/08   kaifeng.lu      [Android 6.0][Calculator_v5.2.1.7.0311.0]It can change when click icon in Tutorial mode.
 *BUGFIX-1077632  2015/12/09   kaifeng.lu      [Android 6.0][Calculator_v5.2.1.7.0312.0][GD]It is not consistent with GD in Tutorial interface.
 *BUGFIX_1202273   2015/12/31   kaifeng.lu     [Lock screen][Func]Can not open calculator when double tap calculator first time
 *BUGFIX_1293809   2016/01/05   kaifeng.lu     [GAPP][Android 6.0][Calculator]It shakes when press power key after opening calculator.
 *BUGFIX_1392730   2016/01/11   kaifeng.lu     [GAPP][Android 6.0][Calculator]The calculation disappears when launch in mainscreen  .
 *BUGFIX_1392684   2016/01/11   kaifeng.lu     [GAPP][Android 6.0][Calculator]It exits automatically when press power key .
 *BUGFIX_1273635   2016/01/22   kaifeng.lu     [Android 6.0][Calculator][Ergo]input area drops to the bottom of screen,it size keep.
 ===========================================================================
 */
package com.tct.calculator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
/* MODIFIED-BEGIN by qiong.liu1, 2017-03-31,BUG-3621966*/
/* MODIFIED-BEGIN by Mingjun-wu, 2016-11-22,BUG-3534304*/
import android.content.res.Configuration;
import android.content.res.Resources;
/* MODIFIED-END by Mingjun-wu,BUG-3534304*/
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build; // MODIFIED by qiong.liu1, 2017-03-10,BUG-4289154
/* MODIFIED-END by qiong.liu1,BUG-3621966*/
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display; // MODIFIED by qiong.liu1, 2017-03-31,BUG-3621966
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.tct.calculator.fragment.WelcomeFragmentCalculator;
import com.tct.calculator.fragment.WelcomeFragmentConverter;
import com.tct.calculator.fragment.WelcomeFragmentCurrency;

import java.util.ArrayList;

import com.tcl.statisticsdk.agent.StatisticsAgent; // MODIFIED by qiong.liu1, 2017-03-31,BUG-3621966
import com.tct.calculator.utils.SharedPreferencesHelper;

import static com.tct.calculator.utils.SharedPreferencesHelper.SHARED_PREFERENCES_CONVERT_RECORDER;

/**
 * Created by boyang on 11/11/15.
 */
public class Splash extends AppCompatActivity implements View.OnClickListener{

    private final String KEY ="firstin";
    private final String PREF = "first_pref";
    private SharedPreferences pref;

    private static final  int VIEWPAGE_ONE = 0;
    private static final  int VIEWPAGE_TWO = 1;
    private static final  int VIEWPAGE_THREE = 2;
    private int CURRENT_PAGE = 0;

    private ViewPager mViewPager ;
    private TextView mSkip,mNext ;
    private ArrayList<Fragment> mFragments;
    private WelcomeFragmentCalculator mWelcomeFragmentCalculator;
    private WelcomeFragmentCurrency mWelcomeFragmentCurrency;
    private WelcomeFragmentConverter mWelcomeFragmentThree ;
    private ImageView mPoint1, mPoint2 , mPoint3;
    private boolean showLocked;
    private String JUMP_TO_SPLASH="jumpToSSSSActivity";
    //TS:kaifeng.lu 2016-04-27 Calculator BUGFIX_ 1999448  ADD_S
    private String TAG = "Splash";
    //TS:kaifeng.lu 2016-04-27 Calculator BUGFIX_ 1999448  ADD_E
    private String JUMP_TO_CALCULATOR="jumpToCalculator";
    private static String CURRENT_PAGE_KEY = "CURRENT_PAGE";
    private boolean isRegisted=false;
    private SharedPreferencesHelper mSharedPreferencesHelper; // MODIFIED by qiong.liu1, 2017-05-04,BUG-4656997

    /* MODIFIED-BEGIN by qiong.liu1, 2017-03-31,BUG-3621966*/
    /* MODIFIED-BEGIN by Mingjun-wu, 2016-11-22,BUG-3534304*/
//    private int mMaxScreemHeight = 0;
//    private int mCreatCount = 0 ;
//    private Display mDisply ;
    /* MODIFIED-END by Mingjun-wu,BUG-3534304*/

    private int mMaxScreemHeight = 0;
    private int mCreatCount = 0 ;
    private Display mDisply ;
    /* MODIFIED-END by qiong.liu1,BUG-3621966*/


    private final BroadcastReceiver powerReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            final String action = intent.getAction();
            if(action.equals(Intent.ACTION_SCREEN_OFF)) {
                //TS:kaifeng.lu 2016-01-11 Calculator BUGFIX_1392730  MOD_S
                moveTaskToBack(true);
//                SSSSActivity.this.finish();
                if(isRegisted) {
                    try {
                        unregisterReceiver(powerReceiver);
                        isRegisted = false;
                        getWindow().clearFlags( WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
                    }catch (Exception e){
                        Log.d(TAG, " Receiver not registered"); // MODIFIED by qiong.liu1, 2017-01-23,BUG-4048871
                    }
                }
                //TS:kaifeng.lu 2016-01-11 Calculator BUGFIX_1392730  MOD_E
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.state_bar_color));
        if(getIntent()!=null && getIntent().getBooleanExtra(JUMP_TO_SPLASH,false)){
            IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
            //TS:kaifeng.lu 2016-01-05 Calculator BUGFIX_1293809  ADD_S
            filter.setPriority(10000);
            //TS:kaifeng.lu 2016-01-05 Calculator BUGFIX_1293809  ADD_E
            registerReceiver(powerReceiver, filter);
            isRegisted=true;
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        }

        /**
         *   Android N 分屏幕适预留
         */

//        Display display = getWindowManager().getDefaultDisplay();
//        DisplayMetrics dm = getResources().getDisplayMetrics();
//        int screenHeight = dm.heightPixels;
//        int currentHeight = display.getHeight();
//
//        if (currentHeight > screenHeight*2/3){
//            setContentView(R.layout.activity_splash);
//        }else {
//            setContentView(R.layout.activity_splash_multiwindow);
//        }
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash_new);
        initView();
        initFragments();
        if (savedInstanceState != null) {
            CURRENT_PAGE = savedInstanceState.getInt(CURRENT_PAGE_KEY, VIEWPAGE_ONE);
        }
        setPoint(CURRENT_PAGE);
        FragmentManager fm = this.getSupportFragmentManager() ;
        MyViewPageAdpter viewPagerAdpter = new MyViewPageAdpter(fm,mFragments);
        mViewPager.setAdapter(viewPagerAdpter);
        mViewPager.addOnPageChangeListener(new MyViewPageListener());

    }
    @Override
    protected void onNewIntent(Intent intent) {
        if(intent != null && intent.getBooleanExtra(JUMP_TO_SPLASH,false)){
            if(!isRegisted) {
                IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
                filter.setPriority(10000);
                registerReceiver(powerReceiver, filter);
                isRegisted = true;
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
            }
        }else {
            if(isRegisted){
                try {
                    unregisterReceiver(powerReceiver);
                    isRegisted = false;
                    getWindow().clearFlags( WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
                }catch (Exception e){
                }
            }
        }
        super.onNewIntent(intent);
    }

    private void setFirstIn(){
        //TS:kaifeng.lu 2015-11-25 Calculator BUGFIX_979517 ADD_S
        pref=getSharedPreferences(PREF,MODE_PRIVATE);
        //TS:kaifeng.lu 2015-11-25 Calculator BUGFIX_979517 ADD_E
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(KEY, false);
        editor.commit();
    }

    protected  void initView(){

        mViewPager = (ViewPager) findViewById(R.id.splash_viewpager);
        mSkip = (TextView) findViewById(R.id.tv_skip);
        mNext = (TextView) findViewById(R.id.tv_next);
        mPoint1 = (ImageView) findViewById(R.id.point_one);
        mPoint2 = (ImageView) findViewById(R.id.point_two);
        mPoint3 = (ImageView) findViewById(R.id.point_three);
        mSkip.setOnClickListener(this);
        mNext.setOnClickListener(this);
        mSharedPreferencesHelper = SharedPreferencesHelper.getInstance(this); // MODIFIED by qiong.liu1, 2017-05-04,BUG-4656997
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.tv_skip) {
            setFirstIn();
            jumpTo(Calculator.class);
            Splash.this.finish();
            return;
        }
        if (view.getId() == R.id.tv_next) {
            if (mViewPager.getCurrentItem() == 2) {
                setFirstIn();
                jumpTo(Calculator.class);
                Splash.this.finish();
                return;
            }else{
                mViewPager.setCurrentItem(mViewPager.getCurrentItem()+1);
                return;
            }
        }
    }
    /* MODIFIED-BEGIN by qiong.liu1, 2017-05-04,BUG-4656997*/
    @Override
    public void onMultiWindowModeChanged(boolean isInMultiWindowMode) {
        super.onMultiWindowModeChanged(isInMultiWindowMode);
        Log.d(TAG, " Splash::onMultiWindowModeChanged::  " + isInMultiWindowMode);
        mSharedPreferencesHelper.saveIsInMultiWindow(SharedPreferencesHelper.IS_IN_MULTIWINDOW, isInMultiWindowMode);
    }
    /* MODIFIED-END by qiong.liu1,BUG-4656997*/
    protected void initFragments() { // MODIFIED by qiong.liu1, 2017-03-31,BUG-3621966
        mFragments = new ArrayList<Fragment>();
        mWelcomeFragmentCalculator = new WelcomeFragmentCalculator();
        mWelcomeFragmentCurrency = new WelcomeFragmentCurrency();
        mWelcomeFragmentThree = new WelcomeFragmentConverter();
        mFragments.add(mWelcomeFragmentCalculator);
        mFragments.add(mWelcomeFragmentCurrency);
        mFragments.add(mWelcomeFragmentThree);
    }
    /* MODIFIED-BEGIN by qiong.liu1, 2017-03-31,BUG-3621966*/
    //TS:kaifeng.lu 2016-04-27 Calculator BUGFIX_ 1999448  ADD_S
    @Override
    protected void onPause() {
        Log.i(TAG,"-----onPause-----");
        super.onPause();
        if(ClientApplication.mCalcualtorHawkeyeEnable){
            StatisticsAgent.onPause(this);
        }
    }
    /* MODIFIED-END by qiong.liu1,BUG-3621966*/

    private void jumpTo(Class<?> cls){
        Intent mIntent=new Intent(this, cls);
        mIntent.putExtra(JUMP_TO_CALCULATOR,true);  //锁屏问题 后面在解决
        startActivity(mIntent);
        Splash.this.finish();
    }

    private class MyViewPageAdpter extends FragmentPagerAdapter {

        ArrayList<Fragment> mFragments ;

        public MyViewPageAdpter(FragmentManager fm, ArrayList<Fragment>  mFragments) {
            super(fm);
            this.mFragments = mFragments;
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }
    }


    protected void setPoint(int num_point){
        if(num_point == VIEWPAGE_ONE){
            CURRENT_PAGE = VIEWPAGE_ONE;
            mPoint1.setBackgroundResource(R.drawable.dot_on);
            mPoint2.setBackgroundResource(R.drawable.dot_off);
            mPoint3.setBackgroundResource(R.drawable.dot_off);
        }else if(num_point == VIEWPAGE_TWO){
            CURRENT_PAGE = VIEWPAGE_TWO;
            mPoint1.setBackgroundResource(R.drawable.dot_off);
            mPoint2.setBackgroundResource(R.drawable.dot_on);
            mPoint3.setBackgroundResource(R.drawable.dot_off);
        }else if(num_point == VIEWPAGE_THREE){
            CURRENT_PAGE = VIEWPAGE_THREE;
            mPoint1.setBackgroundResource(R.drawable.dot_off);
            mPoint2.setBackgroundResource(R.drawable.dot_off);
            mPoint3.setBackgroundResource(R.drawable.dot_on);
        }else{}
    }

    private class MyViewPageListener implements ViewPager.OnPageChangeListener{

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            if (position == VIEWPAGE_THREE){
                mNext.setText(getResources().getString(R.string.splash_tv_open));
            }else {
                mNext.setText(getResources().getString(R.string.splash_tv_next));
            }
            setPoint(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    @Override
    public void onBackPressed() {
        if(mViewPager.getCurrentItem()!= 0){
            mViewPager.setCurrentItem(mViewPager.getCurrentItem()-1);
        }else{
            Splash.this.finish();
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        if(powerReceiver != null && isRegisted) {
            try {
                unregisterReceiver(powerReceiver);
                isRegisted=false;
            }catch (Exception e){
                Log.d(TAG," Receiver not registered"); // MODIFIED by qiong.liu1, 2017-01-23,BUG-4048871
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putInt(CURRENT_PAGE_KEY, CURRENT_PAGE);
    }

    /* MODIFIED-BEGIN by qiong.liu1, 2017-03-31,BUG-3621966*/
    @Override
    protected void onResume() {
        super.onResume();
        if(ClientApplication.mCalcualtorHawkeyeEnable){
            StatisticsAgent.onResume(this);
        }
    }
    /* MODIFIED-END by qiong.liu1,BUG-3621966*/
}
