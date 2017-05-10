package com.tct.calculator.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
/* MODIFIED-BEGIN by qiong.liu1, 2017-03-23,BUG-3621966*/
import android.content.BroadcastReceiver;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color; // MODIFIED by qiong.liu1, 2017-04-21,BUG-4452809
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
/* MODIFIED-BEGIN by qiong.liu1, 2017-03-27,BUG-3621966*/
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
/* MODIFIED-END by qiong.liu1,BUG-3621966*/
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
/* MODIFIED-END by qiong.liu1,BUG-3621966*/
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.tct.calculator.Calculator;
import com.tct.calculator.CalculatorEditText;
import com.tct.calculator.CalculatorExpressionBuilder;
import com.tct.calculator.CalculatorExpressionEvaluator;
import com.tct.calculator.CalculatorExpressionTokenizer;
import com.tct.calculator.CalculatorPadViewPager;
import com.tct.calculator.OnCurrencyListClickCallBack;

import android.content.ActivityNotFoundException;

import com.tct.calculator.data.DBOperation;
import com.tct.calculator.data.DefaultCurrency;
import com.tct.calculator.CalculatorApplication;
/* MODIFIED-BEGIN by qiong.liu1, 2017-03-20,BUG-3621966*/
import com.tct.calculator.http.HttpConstants;
import com.tct.calculator.http.HttpRequestService;
/* MODIFIED-END by qiong.liu1,BUG-3621966*/
import com.tct.calculator.utils.Constant;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;

import com.tct.calculator.R;
import com.tct.calculator.data.HistoryContentUri;
import com.tct.calculator.utils.SharedPreferencesHelper;
import com.tct.calculator.adapter.CurrencyHistoryAdapter;
import com.tct.calculator.utils.Utils;
import com.tct.calculator.view.CurrencyChartView;
import com.tct.calculator.view.CurrencyDisplayOverlay;
import com.tct.calculator.view.DisplayOverlay;
import com.tct.calculator.view.History;
import com.tct.calculator.view.HistoryAdapter;
import com.tct.calculator.view.HistoryItem;

import android.view.animation.AccelerateInterpolator;
import android.os.Message;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.tct.calculator.view.interfaces.CalculatorCallBacks; // MODIFIED by qiong.liu1, 2017-04-11,BUG-4452809
import com.tct.calculator.view.interfaces.ChangeGraphViewListener; // MODIFIED by qiong.liu1, 2017-03-23,BUG-3621966
import com.tct.calculator.view.interfaces.ChangeViewInParentCallBack;

import org.javia.arity.SyntaxException;

import android.util.TypedValue;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List; // MODIFIED by qiong.liu1, 2017-03-23,BUG-3621966
import java.util.Map;
import java.util.Iterator;

import com.tct.calculator.data.Recycler_Data;

import java.util.Vector;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import com.tct.calculator.view.scrollwheel.CurrencyScrollWheelPicker;
import com.tct.calculator.view.scrollwheel.OnScrollWheelClickedListener;
import com.tct.calculator.view.scrollwheel.OnWheelScrollListener;
import com.tct.calculator.view.scrollwheel.ScrollWheelPickerView;
import com.tct.calculator.view.scrollwheel.WheelView;
import com.tct.calculator.data.Currency;

import java.util.ArrayList;

/**
 * Created by user on 16-9-28.
 */
/* MODIFIED-BEGIN by qiong.liu1, 2017-03-23,BUG-3621966*/
public class CurrencyFragment extends Fragment implements CalculatorEditText.OnTextSizeChangeListener,
        CalculatorExpressionEvaluator.EvaluateCallback, View.OnClickListener,
        ChangeViewInParentCallBack, OnCurrencyListClickCallBack, View.OnLongClickListener, CalculatorCallBacks { // MODIFIED by qiong.liu1, 2017-04-11,BUG-4452809
        /* MODIFIED-END by qiong.liu1,BUG-3621966*/


    private static final String KEY_SHOWING_POP = "key_showing_pop";
    private static final String LEFT_WHEEL_INDEX = "left_wheel_index";
    private static final String RIGHT_WHEEL_INDEX = "right_wheel_index";
    private static final String CURRENCY_TOP_COUNTRY_INDEX_KEY = "currency_top_index";
    private static final String CURRENCY_BOTTOM_COUNTRY_INDEX_KEY = "currency_bottom_index";
    private static final CharSequence EXCHANGE_DEFAULT_VALUE = "100"; // MODIFIED by qiong.liu1, 2017-04-25,BUG-4598868
    private PopupWindow mCurrencyPopWindow;
    private int POPUPWINDOW_LAYOUT_WIDTH;
    private Button mWheelPickerConfirm, mWheelPickerCancel, mButtonLeftMore, mButtonRightMore;
    private FragmentTransaction mFragmentTransaction;
    private GetCurrencyPopCallBack mGetCurrencyPopCallBack;
    private static final String NAME = "com.tct.calculator";
    // instance state keys
    private static final String KEY_CURRENT_STATE = NAME + "_currentState";
    private static final String KEY_CURRENT_EXPRESSION = NAME + "_currentExpression";
    //PR1012727 The display of app is not match with it in recent key by tingma at 2015-06-02
    private static final String KEY_CURRENT_VIEWPAGER = "current_viewpager";
    private static final String KEY_DISPLAY_MODE = NAME + "_displayMode";
    private static final String TAG = "Currency";// MODIFIED by kaifeng.lu, 2016-03-23,BUG-1722335 // MODIFIED by qiong.liu1, 2017-05-05,BUG-4639606
    private static final String KEY_CURRENT_TRANSLATE_STATE = NAME + "_currentTranslateState";
    private static final String KEY_CURRENT_EVALUATOR_MODE = NAME + "_currentEvaluatorMode";
    //TS:kaifeng.lu 2015-12-23 Calculator BUGFIX_1202285 ADD_S
    private static final String FORMULA_CLICKED_STATE = "formula_clicked_state";
    private static final String RESULT_CLICKED_STATE = "result_clicked_state";
    //TS:kaifeng.lu 2015-12-23 Calculator BUGFIX_1202285 ADD_E
    /**
     * Constant for an invalid resource id.
     */
    public static final int INVALID_RES_ID = -1;

    //Added it by hong.zhan for PR817289 20141027 begin
    private static final String MMI_AUTOTEST = "0×0−0+";
    private static final String MMI_IMEIVERSION = "1×1−1+";
    private static final String MMI_LOGTOOL = "2×2−2+";
    private static final String MMI_BUILDVERSION = "3×3−3+";
    private static final String MMI_AGPS = "4×4−4+";
    private final String SHOW_BY_LOCKED = "showByLocked";
    private final String IS_SERCURE = "IsSecure";
    private History mHistory;
    private CurrencyHistoryAdapter mHistoryAdapter;
    /* MODIFIED-BEGIN by kaifeng.lu, 2016-08-16,BUG-2712192*/
    //TS:kaifeng.lu 2015-12-30 Calculator BUGFIX_1271732 ADD_S
    private boolean mDataFromHistory = false;
    /* MODIFIED-END by kaifeng.lu,BUG-2712192*/
    private Display mDisplay;
    private int evaluateCount = 0; // MODIFIED by kaifeng.lu, 2016-06-02,BUG-2222411

    private RelativeLayout mCurrencyTargetLayout;
    private RelativeLayout mCurrencyOrangeLayout;
    private int ANIM_TIME = 300;
    private float mTopFrom, mTopTo, mBottomFrom, mBottomTo;
    private static final int EXCHANGE_CURRENCY_ANIM_END = 0;
    private static final int CURRENCY_SHOW_POPWINDOW = 1;
    private ImageView mOrgDownShow;
    private ImageView mTargetDownShow;
    private TextView mTargetFlag;
    private TextView mOrgFlag;
    private boolean mIsShowHistory;
    private int mLeftIndex, mRightIndex;
    private CurrencyScrollWheelPicker mCurrencyScrollWheelPicker;
    private View mWheelContainer;
    private ArrayList<Currency> mLeftData;
    private ArrayList<Currency> mRightData;
    private TextView mOrgCountry, mTargetCountry;
    private ImageView mXEImageView;
    private SharedPreferencesHelper mSharedPreferencesHelper;
    private ImageView mOrgCountryImageView;
    private ImageView mTargetCountryImage;
    private DBOperation mDBOperation;
    private boolean mIsClickEqualsBtn;
    private boolean mWheelPickerDone = true;
    private float mCurLeftCurrency;
    private float mCurRightCurrency;
    private String mUpdateTime;
    private String mOrgCountryOld = Constant.CURRENCY_COUNTRY_CNY;
    private String mTargetCountryOld = Constant.CURRENCY_COUNTRY_USD;
    private LinearLayout mCommonContainer; // MODIFIED by qiong.liu1, 2017-04-21,BUG-4452809
    private Drawable drawableBg;

    /**
     * the constant for saveInstance
     */

    private static final String TOP_AREA_SHORTFLAG = "top_area_shortflag";
    private static final String BUTTON_AREA_SHORTFLAG = "button_area_shortflag";
    private static final String TOP_FORMULATE = "top_formulate";
    private static final String TOP_RESULT = "top_result";
    private static final String BUTTON_RESULT = "button_result";
    /* MODIFIED-BEGIN by qiong.liu1, 2017-04-21,BUG-4452809*/
    private static final int TOP = 0;
    private static final int BOTTOM = 1;
    /* MODIFIED-END by qiong.liu1,BUG-4452809*/


    /**
     * chartview  show the currency change
     */

    private CurrencyChartView mCurrencyChartView ;
    private TextView mCharViewLow , mCharViewHight;
    private static final String CHARTIME_STATES = "current_currency_chat_time";
    private static final int RATES_NEED_UPDATE = 3; // MODIFIED by qiong.liu1, 2017-03-24,BUG-3621966
    private static final int DB_HAD_INIT = 4; // MODIFIED by qiong.liu1, 2017-03-24,BUG-3621966
    private String mCurrentCharTime ;




    /**
     *   The follow constant use to check the chartview in currency_advence pad
     */
    private final static float[] DATA7D = {2, 1, 5, 6, 8, 7, 4, 6, 9, 10, 11, 12};
    private final static float[] DATA1M = {1.415261f, 1.463432f, 1.454563f, 1.423454f, 1.4312585f,
            1.412546f, 1.401237f, 1.424538f, 1.471109f, 1.463421f, 1.443212f, 1.489113f};

    private final static float[] DATA6M = {2.147562f,2.312416f,2.176412f,2.212344f,2.124535f,2.223454f,2.245461f,2.267454f,2.1842313f,2.221542f,2.163465f,2.1912351f};
    private final static float[] DATA1Y = {100.154622f,100.188622f,101.4564512f,101.654521f,102.321545f,101.321545f,103.1023921f,101.124784f,102.545641f,101.123321f,102.231511f};

    private static float[] rates7dArray;
    private static float[] rates1mArray;
    private static float[] rates6mArray;
    private static float[] rates1yArray;
    private boolean isRatesChanged = false;
    private HttpRequestService mHttpRequestService;

    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mHttpRequestService = ((HttpRequestService.MyBinder)iBinder).getService();
            mHttpRequestService.setChangeGraphViewListener(mChangeGrapViewListener);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    /* MODIFIED-BEGIN by qiong.liu1, 2017-03-24,BUG-3621966*/
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case RATES_NEED_UPDATE:
                    updateCharView("oneweek");
                    break;
                /* MODIFIED-BEGIN by qiong.liu1, 2017-03-24,BUG-3621966*/
                case DB_HAD_INIT:
                    resetCountryData();
                    break;
                    /* MODIFIED-END by qiong.liu1,BUG-3621966*/
            }
        }
    };
    private boolean mShowAnimFromResult; // MODIFIED by qiong.liu1, 2017-04-25,BUG-4598868
    /* MODIFIED-END by qiong.liu1,BUG-3621966*/

    @Override
    public void onEvaluate(String expr, String result, int errorResourceId) {
        Log.i(TAG, "onEvaluate start" + System.currentTimeMillis()); // MODIFIED by kaifeng.lu, 2016-05-25,BUG-2162730
        Log.i(TAG, "---onEvaluate---");// MODIFIED by kaifeng.lu, 2016-03-23,BUG-1722335
        Log.i(TAG, "onEvaluate start" + System.currentTimeMillis() + "evaluateCount = " + evaluateCount);
        Log.i(TAG, "onEvaluate=expr=" + expr + ",result=" + result + ",errorResourceId=" + errorResourceId); // MODIFIED by qiong.liu1, 2017-04-01,BUG-3621966
        String commaResult;
        if (mIsStanderPoint) {
            if (TextUtils.isEmpty(result)) {
                commaResult = getCommaString(result);
            } else {
                commaResult = getCommaString(result.replaceAll(",", "."));
            }
        } else {
            if (TextUtils.isEmpty(result)) {
                commaResult = getCommaString(result);
            } else {
                commaResult = getCommaString(result.replace(".", ","));
            }
        }
//        commaResult = getCommaString(result);
        if (mCurrentState == CalculatorState.INPUT) {
            mResultEditText.setText(commaResult);
        } else if (errorResourceId != INVALID_RES_ID) {
            onError(errorResourceId);
        } else if (!TextUtils.isEmpty(result)) {
            updateCurrencyHistoryData(mOrgCountry.getText().toString(), mTargetCountry.getText().toString()); // MODIFIED by qiong.liu1, 2017-03-20,BUG-3621966
            onResult(result);
        } else if (mCurrentState == CalculatorState.EVALUATE) {
            double mResult = 0;
            try {
                mResult = mEvaluator.getmSymbols().eval(expr);
                String resultStr = "";
                for (int precision = 12; precision > 6; precision--) {
                    resultStr = mEvaluator.tryFormattingWithPrecision(mResult, precision);
                    if (resultStr.length() <= 12) {
                        break;
                    }
                }
                if (resultStr.equals("-0")) {
                    resultStr = "0";
                }
                resultStr = mTokenizer.evaluate2Local(resultStr);
                onResult(resultStr);
            } catch (SyntaxException | NullPointerException e) {
                e.printStackTrace();
            }
        }
        Log.i(TAG, "onEvaluate end " + System.currentTimeMillis()); // MODIFIED by kaifeng.lu, 2016-05-25,BUG-2162730

        /* MODIFIED-BEGIN by qiong.liu1, 2017-03-20,BUG-3621966*/
        performCurrency();
    }

    private void performCurrency() {
        /* MODIFIED-BEGIN by qiong.liu1, 2017-04-01,BUG-3621966*/
        try{
            String result = mResultEditText.getText().toString();
            if(TextUtils.isEmpty(result)){
                result = mFormulaEditText.getText().toString();
            }
            result = result.replace(" ", "").replace("−", "-");
            if (TextUtils.isEmpty(result)) {
                result = Constant.DEFAULT_VALUE;
            }
            float rate = mCurRightCurrency / mCurLeftCurrency;
            float purpCurrency = rate * Float.valueOf(result);
            mTargetResultEditText.setText(purpCurrency + "");
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, " performCurrency e "  +  e); // MODIFIED by qiong.liu1, 2017-04-07,BUG-4452809
            /* MODIFIED-END by qiong.liu1,BUG-3621966*/
        }
    }

    @Override
    public void onTextSizeChanged(TextView textView, float oldSize) {

    }

    /* MODIFIED-BEGIN by qiong.liu1, 2017-03-23,BUG-3621966*/
    ChangeGraphViewListener mChangeGrapViewListener = new ChangeGraphViewListener() {
        @Override
        public void onViewRatesChanged(List<List> allRates) {
            Log.d(TAG, " ====== onViewRatesChanged =========== ");
            updateViewRates(allRates);
            mHandler.sendEmptyMessage(RATES_NEED_UPDATE); // MODIFIED by qiong.liu1, 2017-03-24,BUG-3621966
            isRatesChanged = true;
        }

        /* MODIFIED-BEGIN by qiong.liu1, 2017-03-24,BUG-3621966*/
        @Override
        public void onDbHadInit() {
            mHandler.sendEmptyMessage(DB_HAD_INIT);
        }
        /* MODIFIED-END by qiong.liu1,BUG-3621966*/
    };

    private void updateViewRates(List<List> allRates) {
        List rates7days = allRates.get(0);
        List rates1month = allRates.get(1);
        List rates6month = allRates.get(2);
        List rates1year = allRates.get(3);
        rates7dArray = Utils.getFloatArray(rates7days);
        rates1mArray = Utils.getFloatArray(rates1month);
        rates6mArray = Utils.getFloatArray(rates6month);
        rates1yArray = Utils.getFloatArray(rates1year);
    }

    /* MODIFIED-BEGIN by qiong.liu1, 2017-04-11,BUG-4452809*/
    @Override
    public void onConfigurationChanged() {
        if (mCurrencyPopWindow.isShowing()) {
            mIsClickPopWindowCancel = true;
            resetCountryData();
            dismissCurrencyPopWindow();
        }
    }
    /* MODIFIED-END by qiong.liu1,BUG-4452809*/
    /* MODIFIED-END by qiong.liu1,BUG-3621966*/

    private enum CalculatorState {
        INPUT, EVALUATE, RESULT, ERROR
    }

    private final TextWatcher mFormulaTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int start, int count, int after) {
            if (charSequence.length() == 1 && charSequence.charAt(0) == '0') {
                mFormulaEditText.setTextColor(getResources().getColor(R.color.display_formula_hint_text_color));
                mResultEditText.setTextColor(getResources().getColor(R.color.display_formula_hint_text_color));
                mOrgFlag.setTextColor(getResources().getColor(R.color.display_formula_hint_text_color));
            } else {
                mFormulaEditText.setTextColor(getResources().getColor(R.color.display_formula_text_color));
                mResultEditText.setTextColor(getResources().getColor(R.color.display_result_text_color));
                mOrgFlag.setTextColor(getResources().getColor(R.color.display_result_text_color));
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
            Log.i(TAG, "afterTextChanged: " + editable.toString());
            if (editable != null && mFormulaEditText != null) {
                mFormulaEditText.removeTextChangedListener(this);
                mFormulaEditText.setText(getCommaString(editable.toString()));
                mFormulaEditText.addTextChangedListener(this);
            }
            if ((mCurrentState == CalculatorState.EVALUATE && editable.toString().contains("e")) || (mCurrentState == CalculatorState.RESULT)) {
                setState(CalculatorState.INPUT);
                if (mResultEditText.getText().length() != 0 && mIsClickEqualsBtn) {
                    mResultEditText.setText("");
                    mIsClickEqualsBtn = false;
                }
            } else {
                setState(CalculatorState.INPUT);
            }
            mEvaluator.evaluate(editable.toString().replaceAll(" ", "").replaceAll(",", "."), CurrencyFragment.this); // MODIFIED by qiong.liu1, 2017-04-07,BUG-4452809
            if (TextUtils.isEmpty(editable.toString())) {
                mResultEditText.setText(Constant.CURRENCY_ORG_DEFAULT);
            }
        }
    };

    private final View.OnKeyListener mFormulaOnKeyListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_NUMPAD_ENTER:
                case KeyEvent.KEYCODE_ENTER:
                    if (keyEvent.getAction() == KeyEvent.ACTION_UP) {
                        mCurrentButton = mEqualButton;
                        onEquals();
                    }
                    // ignore all other actions
                    return true;
            }
            return false;
        }
    };
    private final Editable.Factory mFormulaEditableFactory = new Editable.Factory() {
        @Override
        public Editable newEditable(CharSequence source) {
            final boolean isEdited = mCurrentState == CalculatorState.INPUT
                    || mCurrentState == CalculatorState.ERROR;
            return new CalculatorExpressionBuilder(source, mTokenizer, isEdited);
        }
    };


    private CalculatorState mCurrentState;
    private CalculatorExpressionTokenizer mTokenizer;
    private CalculatorExpressionEvaluator mEvaluator;
    private CurrencyDisplayOverlay mDisplayView;
    private CalculatorEditText mFormulaEditText;
    private CalculatorEditText mResultEditText;
    private ViewPager mPadViewPager;
    private View mDeleteButton;
    private View mEqualButton;
    private View mClearButton;
    private SharedPreferences pref;
    private Button formulaCopyBtn, formulaPasteBtn, resultCopyBtn;
    private boolean copyPasteState;
    private ClipboardManager clipboard;
    private Button tipsBtn;


    private TextView radTv, degTv;
    private Button mBtn0, mBtn1, mBtn2, mBtn3, mBtn4, mBtn5, mBtn6, mBtn7, mBtn8, mBtn9;
    private View mBtndiv, mBtndel, mBtnmul, mBtnclear, mBtnsub, mBtnexpend, mBtnpoint, mBtnadd, mBtneq, mExchangeLayout;
    private View mBtnexdiv, mBtnexsub, mBtnexmul, mBtnexpoint, mBtnexadd, mBtnexeq;
    private View mImgConvert;
    private CalculatorEditText mTargetResultEditText;
    private static String[] buttonTexts = {"ln(", "log(", "sin(", "cos(", "tan(", "exp(", "sin⁻¹(", "cos⁻¹(", "tan⁻¹("};
    private static boolean inv = false;
    private static boolean needExpand = false;

    ContentResolver cr;
    private View mCurrentButton;
    private Animator mCurrentAnimator;
    //PR1012727 The display of app is not match with it in recent key by tingma at 2015-06-02
    private int mCurrentViewPager = 0, mCurrentTranslate = 0, mCurrentEvaMode = 0;
    private FrameLayout.LayoutParams mLayoutParams =
            new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, 0);
    private Context mContext;
    //TS:kaifeng.lu 2015-12-23 Calculator BUGFIX_1202285 ADD_S
    private boolean isFormulaLongClicked = false;
    private boolean isResultLongClicked = false;
    //TS:kaifeng.lu 2015-12-23 Calculator BUGFIX_1202285 ADD_E
    private boolean isRegisted = false;
    private float mMaxScreemHeight = 0;
    private int mCreatCount = 0;
    private Button mPoint;
    private boolean mIsStanderPoint = true;
    private View mShowWheelButton;
    private View mTargetShowWheelButton;
    private String mOrgResultData;
    private String mTargetResultData;
    private TextView mCurrencyTime;
    private float mResultEditSize;
    private float mTargetEditSize;
    private TextView mOrgUnit;
    private TextView mTargetUnit;
    private static final int TRANSLATION_DELAY_TIME = 5000;
    private boolean mHasSameData = false;
    private boolean mIsClickLeftMoreButton;
    private boolean mIsClickPopWindowCancel;
    private ImageView mOrgLocation;
    private ImageView mTargetLocation;
    private DefaultCurrency mDefaultCurrency;
    // Animation layout for org and target.
    private RelativeLayout mCurrencyOrgLayoutAnimation;
    private RelativeLayout mCurrencyTargetLayoutAnimation;
    // Country image.
    private ImageView mCurrencyOrgImgAnimation;
    private ImageView mCurrencyTargetImgAnimation;
    // Country shorter.
    private TextView mCurrencyOrgCountryShorterAnimation;
    private TextView mCurrencyTargetCountryShorterAnimation;
    // wheel view show image.
    private ImageView mCurrencyOrgDownShowImgAnimation;
    // Location image.
    private ImageView mCurrencyOrgLocationImgAnimation;
    // Currency symbol.
    private TextView mCurrencyOrgFlagAnimation;
    private TextView mCurrencyTargetFlagAnimation;
    // Currency Org result.
    private CalculatorEditText mCurrencyOrgResultEditAnimation;
    private CalculatorEditText mCurrencyOrgFormulaEditAnimation;
    // Currency Target result.
    private CalculatorEditText mCurrencyTargetResultEditAnimation;
    // Object of save animation data.
    private SaveAnimationData mSaveAnimationData;
    // Divider of animation.
    private View mCurrencyDividerAnimation;
    // Divider of view.
    private View mCurrencyDivider;

    private int mScreenState;
    private static Calculator mActivity;
    private CalculatorPadViewPager mCalculatorPadViewPager;
    private TextView mTvOneWeek , mTvOneMonth , mTvSixMouth, mTvOneYear;
    private static final String ONEWEEK_STATE = "oneweek";
    private static final String ONEMONTH_STATE = "onemonth";
    private static final String SIXMONTH_STATE = "sixmonth";
    private static final String ONEYEAT_STATE = "oneyear";


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        setState(CalculatorState.INPUT);
        mActivity = (Calculator) activity;
        mActivity.setCalculatorCallBacks(this, mActivity.CURRENCYSTATUS); // MODIFIED by qiong.liu1, 2017-04-11,BUG-4452809
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = null;
        if (Calculator.mScreenState == Constant.SCREEN_PORT_FULL) {
            view = inflater.inflate(R.layout.fragment_currency, container, false);
        } else if (Calculator.mScreenState == Constant.SCREEN_PORT_QUTER_THREE) {
            view = inflater.inflate(R.layout.fragment_currency_half, container, false);
        } else if (Calculator.mScreenState == Constant.SCREEN_PORT_HALF || mScreenState == Constant.SCREEN_PORT_QUTER) {
            view = inflater.inflate(R.layout.fragment_currency_half, container, false);
        } else if (Calculator.mScreenState == Constant.SCREEN_LAND_HALF) {
            view = inflater.inflate(R.layout.fragment_currency_half, container, false);
        } else if(Calculator.mScreenState == Constant.SCREEN_LAND_FULL){
            view = inflater.inflate(R.layout.fragment_currency_half, container, false);
        }
        Log.d(TAG, " Currency onCreateView " + Calculator.mScreenState); // MODIFIED by qiong.liu1, 2017-03-30,BUG-3621966
        initView(view);
        initAnimationView(view);
        initWheelView();
        resetCountryData();
        resetCurrencyData(savedInstanceState);
        initData(savedInstanceState);
        /* MODIFIED-BEGIN by qiong.liu1, 2017-03-23,BUG-3621966*/
        initService();
        bindServiceInitDates();
        return view;
    }

    private void initService() {
        Intent queryPeriod = new Intent(mContext, HttpRequestService.class);
        mContext.bindService(queryPeriod, connection, Context.BIND_AUTO_CREATE);
    }
    /* MODIFIED-END by qiong.liu1,BUG-3621966*/

    private void initData(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mFormulaEditText.setText(savedInstanceState.getString(TOP_FORMULATE, ""));
            mResultEditText.setText(savedInstanceState.getString(TOP_RESULT, ""));

            if (savedInstanceState.getBoolean(KEY_SHOWING_POP)) {
                Message msg = mUIHandler.obtainMessage();
                msg.what = CURRENCY_SHOW_POPWINDOW;
                mUIHandler.sendMessageDelayed(msg, 500);
            }
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        computeAnimHeight();
        setCurrencyTime();
        setCurrencyCursorVisible();
        startTranslationTask();
    }

    private void resetCurrencyData(Bundle bundle) {
        if (bundle != null) {
            String countryTxt = bundle.getString(LEFT_WHEEL_INDEX, "");
            if (countryTxt.equals("")) {
                resetCountryData();
            } else {
                Currency currency = new Currency();
                currency.setCountryUnit(countryTxt);
                mDBOperation.queryCurrencyPicture(countryTxt, currency);
                /* MODIFIED-BEGIN by qiong.liu1, 2017-04-21,BUG-4452809*/
                int drawableID = mContext.getResources().getIdentifier(
                        currency.getCountryPicture(), "drawable", mContext.getPackageName());
                        /* MODIFIED-END by qiong.liu1,BUG-4452809*/
                mOrgCountry.setText(countryTxt);
                mOrgFlag.setText(currency.getCountrySignal());
                mOrgCountryImageView.setImageResource((drawableID != -1) ? drawableID : R.drawable.china);
                setShowStateForLocation();
            }
            mCurrentCharTime = bundle.getString(CHARTIME_STATES,ONEWEEK_STATE);
            updateCharView(mCurrentCharTime);
        }else{
            mCurrentCharTime = ONEWEEK_STATE;
            updateCharView(ONEWEEK_STATE);
        }
    }

    private void setDefaultCurrency() {
        mDefaultCurrency = CalculatorApplication.getInstance().mDefaultCurrency;
        if (mDefaultCurrency != null) {
            /* MODIFIED-BEGIN by qiong.liu1, 2017-04-21,BUG-4452809*/
            int drawableID = mContext.getResources().getIdentifier(mDefaultCurrency
                    .getCurrencyFlag(), "drawable", mContext.getPackageName());
                    /* MODIFIED-END by qiong.liu1,BUG-4452809*/
            mOrgCountry.setText(mDefaultCurrency.getCurrencyShorterForm());
            mOrgFlag.setText(mDefaultCurrency.getCurrencySignal());
            mOrgCountryImageView.setImageResource((drawableID != -1) ? drawableID : R.drawable.china);
            setShowStateForLocation();
        } else {
            setShowStateForLocation();
        }
    }

    private void setShowStateForLocation() {
        if (mOrgLocation != null) {
            if (mDefaultCurrency != null && mOrgCountry.getText().toString()
                    .equals(mDefaultCurrency.getCurrencyShorterForm())) {
                mOrgLocation.setVisibility(View.VISIBLE);
            } else {
                mOrgLocation.setVisibility(View.GONE);
            }
            mTargetLocation.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Start translation currency time view to left.
     */
    private void startTranslation() {
        ObjectAnimator translationAnim = ObjectAnimator.ofFloat(mCurrencyTime, "translationX",
                0, -mCurrencyTime.getWidth() - mCurrencyTime.getPaddingLeft());
        translationAnim.setDuration(600);
        AnimatorSet animSetXY = new AnimatorSet();
        animSetXY.play(translationAnim);
        animSetXY.setInterpolator(new AccelerateInterpolator());
        animSetXY.start();
    }

    /**
     * Restore currency time view to initial position.
     */
    private void restoreTranslation() {
        ObjectAnimator restoreTranslation = ObjectAnimator.ofFloat(mCurrencyTime, "translationX",
                -mCurrencyTime.getWidth() - mCurrencyTime.getPaddingLeft(), 0);
        restoreTranslation.setDuration(600);
        AnimatorSet animSetXY = new AnimatorSet();
        animSetXY.play(restoreTranslation);
        animSetXY.setInterpolator(new AccelerateInterpolator());
        animSetXY.start();
    }

    /**
     * Start  translation task.
     */
    private void startTranslationTask() {
        Handler translationHandler = new Handler();
        translationHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startTranslation();
            }
        }, TRANSLATION_DELAY_TIME);
    }

    /**
     * Set display status for national flag.
     */
    private void setNationalFlagDisplayStatus() {
        if (mOrgCountryImageView != null && mTargetCountryImage != null
                && mCurrencyTargetImgAnimation != null && mCurrencyOrgImgAnimation != null) {
            switch (Calculator.mScreenState) {
                case Constant.SCREEN_PORT_HALF:
                case Constant.SCREEN_PORT_QUTER:
                case Constant.SCREEN_PORT_QUTER_THREE:
                case Constant.SCREEN_LAND_HALF:
                    mOrgCountryImageView.setVisibility(View.GONE);
                    mTargetCountryImage.setVisibility(View.GONE);
                    mCurrencyTargetImgAnimation.setVisibility(View.GONE);
                    mCurrencyOrgImgAnimation.setVisibility(View.GONE);
                    break;
                default:
                    mOrgCountryImageView.setVisibility(View.VISIBLE);
                    mTargetCountryImage.setVisibility(View.VISIBLE);
                    mCurrencyTargetImgAnimation.setVisibility(View.VISIBLE);
                    mCurrencyOrgImgAnimation.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }

    protected void initView(View view) {

        mContext = this.getActivity();
        mDBOperation = DBOperation.getInstance(getActivity());
        mSharedPreferencesHelper = SharedPreferencesHelper.getInstance(getActivity());
        View contentView = LayoutInflater.from(this.getActivity())
                .inflate(R.layout.pad_popwindow, null);
        mWheelContainer = contentView.findViewById(R.id.currency_wheel_container);
        if (Calculator.mScreenState == Constant.SCREEN_PORT_FULL) {
            POPUPWINDOW_LAYOUT_WIDTH = getActivity().getResources().getDimensionPixelSize(R.dimen.currency_popupWindow_layout_height);
        }else {
            POPUPWINDOW_LAYOUT_WIDTH = getActivity().getResources().getDimensionPixelSize(R.dimen.currency_popupWindow_layout_split_height);
        }
        mCurrencyPopWindow = new PopupWindow(contentView,
                LinearLayout.LayoutParams.MATCH_PARENT, POPUPWINDOW_LAYOUT_WIDTH, true);
        mCurrencyPopWindow.setContentView(contentView);
        mCurrencyPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                /* MODIFIED-BEGIN by qiong.liu1, 2017-04-25,BUG-4635361*/
                if (Calculator.mScreenState != Constant.SCREEN_PORT_FULL && Calculator.mScreenState != Calculator.SCREENLANDFULL) {
                    if (mCommonContainer != null) {
                        mCommonContainer.setPadding(150, 0, 0, 0);
                    }
                    mActivity.setShowExpandLayout(false);
                    mXEImageView.setVisibility(View.VISIBLE);
                    /* MODIFIED-END by qiong.liu1,BUG-4635361*/
                }
                setCurrencyCursorVisible();
                saveCountryInitialData();
                if (mShowState == CURRENT_DOWN_SHOW.CURRENCY_ORG_DOWN) {
                    mOrgDownShow.setVisibility(View.VISIBLE);
                } else {
                    mTargetDownShow.setVisibility(View.VISIBLE);
                }
                mCurrencyOrangeLayout.setBackgroundColor(Color.WHITE);
                mCurrencyTargetLayout.setBackgroundColor(Color.WHITE);
            }
        });

        drawableBg = getResources().getDrawable(R.drawable.calculator_edittext_bg); // MODIFIED by qiong.liu1, 2017-05-04,BUG-4656997
        mWheelPickerCancel = (Button) contentView.findViewById(R.id.pop_cancel);
        mWheelPickerConfirm = (Button) contentView.findViewById(R.id.pop_ok);
        mButtonLeftMore = (Button) contentView.findViewById(R.id.bt_left_more);
        mButtonRightMore = (Button) contentView.findViewById(R.id.bt_right_more);


        mShowWheelButton = view.findViewById(R.id.show_wheel_currency);
        mFormulaEditText = (CalculatorEditText) view.findViewById(R.id.formula);
        mDisplayView = (CurrencyDisplayOverlay) view.findViewById(R.id.display_currency);
        mTargetShowWheelButton = view.findViewById(R.id.target_show_wheel_currency);
        mCurrencyTargetLayout = (RelativeLayout) view.findViewById(R.id.target_layout);
        mCurrencyOrangeLayout = (RelativeLayout) view.findViewById(R.id.org_layout);
        mOrgDownShow = (ImageView) view.findViewById(R.id.org_down_show);
        mTargetDownShow = (ImageView) view.findViewById(R.id.target_down_show);
        mTargetFlag = (TextView) view.findViewById(R.id.target_flag);
        mOrgFlag = (TextView) view.findViewById(R.id.org_flag);
        mResultEditText = (CalculatorEditText) view.findViewById(R.id.result);
        mPadViewPager = (ViewPager) view.findViewById(R.id.pad_pager);
        formulaCopyBtn = (Button) view.findViewById(R.id.formula_copy_btn);
        formulaPasteBtn = (Button) view.findViewById(R.id.formula_paste_btn);
        resultCopyBtn = (Button) view.findViewById(R.id.result_copy_btn);
        clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        tipsBtn = (Button) view.findViewById(R.id.tips_btn);
        mPoint = (Button) view.findViewById(R.id.dec_point);
        mCurrencyTime = (TextView) view.findViewById(R.id.currency_time);
        mTargetResultEditText = (CalculatorEditText) view.findViewById(R.id.target_result);
        mImgConvert = view.findViewById(R.id.currency_convert);
        mOrgUnit = (TextView) view.findViewById(R.id.org_flag);
        mTargetUnit = (TextView) view.findViewById(R.id.target_flag);
        mOrgCountry = (TextView) view.findViewById(R.id.currency_org_country);
        mTargetCountry = (TextView) view.findViewById(R.id.currency_target_country);
        mCurrencyDivider = view.findViewById(R.id.currency_divider);
        mCalculatorPadViewPager = (CalculatorPadViewPager) view.findViewById(R.id.pad_pager);
        mCommonContainer = (LinearLayout) view.findViewById(R.id.exchanged_layout_in); // MODIFIED by qiong.liu1, 2017-04-21,BUG-4452809
        if(mCalculatorPadViewPager != null) {
            mCalculatorPadViewPager.setCalculatorType(Constant.CalculatorType.CURRENCY);
        }
        mCurrencyChartView = (CurrencyChartView) view.findViewById(R.id.currency_charview);
        mCharViewLow = (TextView) view.findViewById(R.id.charviewlow);
        mCharViewHight = (TextView) view.findViewById(R.id.charviewhigh);


        mBtn0 = (Button) view.findViewById(R.id.digit_0);
        mBtn1 = (Button) view.findViewById(R.id.digit_1);
        mBtn2 = (Button) view.findViewById(R.id.digit_2);
        mBtn3 = (Button) view.findViewById(R.id.digit_3);
        mBtn4 = (Button) view.findViewById(R.id.digit_4);
        mBtn5 = (Button) view.findViewById(R.id.digit_5);
        mBtn6 = (Button) view.findViewById(R.id.digit_6);
        mBtn7 = (Button) view.findViewById(R.id.digit_7);
        mBtn8 = (Button) view.findViewById(R.id.digit_8);
        mBtn9 = (Button) view.findViewById(R.id.digit_9);

        mBtndiv = view.findViewById(R.id.currency_op_div);
        mBtnsub = view.findViewById(R.id.currency_op_sub);
        mBtnmul = view.findViewById(R.id.currency_op_mul);
        mBtnpoint = view.findViewById(R.id.dec_point);
        mBtnadd = view.findViewById(R.id.currency_op_add);
        mBtneq = view.findViewById(R.id.currency_eq);
        mBtndel = view.findViewById(R.id.currency_clear);
        mBtnexpend = view.findViewById(R.id.currency_expend);
        mXEImageView = (ImageView) view.findViewById(R.id.currency_ic_xe);
        mOrgCountryImageView = (ImageView) view.findViewById(R.id.org_image);
        mTargetCountryImage = (ImageView) view.findViewById(R.id.target_image);
        mOrgLocation = (ImageView) view.findViewById(R.id.org_location);
        mTargetLocation = (ImageView) view.findViewById(R.id.target_location);

        mTvOneWeek = (TextView) view.findViewById(R.id.tv_oneweek);
        mTvOneMonth = (TextView) view.findViewById(R.id.tv_onemonth);
        mTvSixMouth = (TextView) view.findViewById(R.id.tv_sixmonth);
        mTvOneYear = (TextView) view.findViewById(R.id.tv_oneyear);

        mBtn0.setOnClickListener(this);
        mBtn1.setOnClickListener(this);
        mBtn2.setOnClickListener(this);
        mBtn3.setOnClickListener(this);
        mBtn4.setOnClickListener(this);
        mBtn5.setOnClickListener(this);
        mBtn6.setOnClickListener(this);
        mBtn7.setOnClickListener(this);
        mBtn8.setOnClickListener(this);
        mBtn9.setOnClickListener(this);

        mBtndiv.setOnClickListener(this);
        mBtnsub.setOnClickListener(this);
        mBtnmul.setOnClickListener(this);
        mBtnpoint.setOnClickListener(this);
        mBtnadd.setOnClickListener(this);
        mBtneq.setOnClickListener(this);
        mBtndel.setOnClickListener(this);
        mBtndel.setOnLongClickListener(this);
        mBtnexpend.setOnClickListener(this);

        mXEImageView.setOnClickListener(this);
        mImgConvert.setOnClickListener(this);

        mWheelPickerCancel.setOnClickListener(this);
        mWheelPickerConfirm.setOnClickListener(this);
        mButtonLeftMore.setOnClickListener(this);
        mButtonRightMore.setOnClickListener(this);
        mShowWheelButton.setOnClickListener(this);
        mTargetShowWheelButton.setOnClickListener(this);
        mDisplayView.setChangeViewCallBack(this);
        mCurrencyTargetLayout.setOnClickListener(this);
        view.findViewById(R.id.org_formula_layout).setOnClickListener(this);
        mTvOneWeek.setOnClickListener(this);
        mTvOneMonth.setOnClickListener(this);
        mTvSixMouth.setOnClickListener(this);
        mTvOneYear.setOnClickListener(this);

        mTokenizer = new CalculatorExpressionTokenizer(this.getActivity());
        mEvaluator = new CalculatorExpressionEvaluator(mTokenizer);
        /* MODIFIED-BEGIN by qiong.liu1, 2017-05-02,BUG-4598039*/
        //show default $100.00 when first in
        if (!TextUtils.isEmpty(mFormulaEditText.getText().toString()
                .replaceAll(" ", "").replaceAll(",", "."))) {
            mEvaluator.evaluate(mFormulaEditText.getText().toString()
                    .replaceAll(" ", "").replaceAll(",", "."), this);
        }
        /* MODIFIED-END by qiong.liu1,BUG-4598039*/
        mFormulaEditText.setEditableFactory(mFormulaEditableFactory);
        mFormulaEditText.addTextChangedListener(mFormulaTextWatcher);
        mDisplayView.bringToFront();
        CurrencyDisplayOverlay.DisplayMode displayMode = CurrencyDisplayOverlay.DisplayMode.FORMULA;
//        final int modeOrdinal = savedInstanceState.getInt(KEY_DISPLAY_MODE, -1);
//        if (modeOrdinal != -1) {
//            displayMode = CurrencyDisplayOverlay.DisplayMode.values()[modeOrdinal];
//        }
        mDisplayView.setMode(displayMode);
        mDisplayView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        if (mDisplayView.getHeight() > 0) {
                            mDisplayView.initializeHistoryAndGraphView(true);
                            if (mDisplayView.getMode() == CurrencyDisplayOverlay.DisplayMode.GRAPH) {
                                //  mGraphController.startGraph(mFormulaEditText.getText());
                            }
                        }
                    }
                });
        mDisplayView.setTranslateStateListener(new CurrencyDisplayOverlay.TranslateStateListener() {
            @Override
            public void onTranslateStateChanged(CurrencyDisplayOverlay.TranslateState newState) {
                cancelCopyPaste();
            }
        });

        if (mCalculatorPadViewPager != null) {
             /* MODIFIED-BEGIN by qiong.liu1, 2017-04-21,BUG-4452809*/
             mCalculatorPadViewPager.setOnTouchUpFirstPagerOnBackgroundListener(
                     new CalculatorPadViewPager.onTouchUpFirstPagerOnBackgroundListener() {
                     /* MODIFIED-END by qiong.liu1,BUG-4452809*/

                @Override
                public void onTouchUpFirstPager() {
                    if (mCalculatorPadViewPager.getCurrentItem() == 1) {
                        // Otherwise, select the previous pad.
                        mCalculatorPadViewPager.setCurrentItem(mCalculatorPadViewPager.getCurrentItem() - 1);
                    }
                }

                 /* MODIFIED-BEGIN by qiong.liu1, 2017-04-14,BUG-4452809*/
                 @Override
                 public void onDEGModeSelect() {

                 }
             });
             /* MODIFIED-END by qiong.liu1,BUG-4452809*/
        }

        // Set default value.
//        mResultEditText.setText(Constant.CURRENCY_ORG_DEFAULT); // MODIFIED by qiong.liu1, 2017-03-20,BUG-3621966
    }

    /**
     * Init animation view.
     *
     * @param view
     */
    private void initAnimationView(View view) {
        if (view != null) {
            mSaveAnimationData = new SaveAnimationData();
            mCurrencyOrgLayoutAnimation = (RelativeLayout) view.findViewById(R.id.org_layout_animation);
            mCurrencyTargetLayoutAnimation = (RelativeLayout) view.findViewById(R.id.target_layout_animation);

            mCurrencyOrgImgAnimation = (ImageView) view.findViewById(R.id.org_image_animation);
            mCurrencyTargetImgAnimation = (ImageView) view.findViewById(R.id.target_image_animation);

            mCurrencyOrgCountryShorterAnimation = (TextView) view.findViewById(R.id.currency_org_country_animation);
            mCurrencyTargetCountryShorterAnimation = (TextView) view.findViewById(R.id.currency_target_country_animation);

            mCurrencyOrgDownShowImgAnimation = (ImageView) view.findViewById(R.id.org_down_show_animation);

            mCurrencyOrgLocationImgAnimation = (ImageView) view.findViewById(R.id.org_location_animation);

            mCurrencyOrgFlagAnimation = (TextView) view.findViewById(R.id.org_flag_animation);
            mCurrencyTargetFlagAnimation = (TextView) view.findViewById(R.id.target_flag_animation);

            mCurrencyOrgResultEditAnimation = (CalculatorEditText) view.findViewById(R.id.result_animation);
            mCurrencyOrgFormulaEditAnimation = (CalculatorEditText) view.findViewById(R.id.formula_animation);

            mCurrencyTargetResultEditAnimation = (CalculatorEditText) view.findViewById(R.id.target_result_animation);

            mCurrencyDividerAnimation = view.findViewById(R.id.currency_divider_animation);

            setNationalFlagDisplayStatus();
        }
    }

    /**
     * Copy data before  start animation.
     */
    private void copyDataBeforeStartAnimation() {
        // save country resource.
        mSaveAnimationData.setOrgImgAnimData(mOrgCountryImageView.getDrawable());
        mSaveAnimationData.setTargetImgAnimData(mTargetCountryImage.getDrawable());

        // save country shorter.
        mSaveAnimationData.setOrgCountryShorterAnimData(mOrgCountry.getText().toString());
        mSaveAnimationData.setTargetCountryShorterAnimData(mTargetCountry.getText().toString());

        // save currency symbol.
        mSaveAnimationData.setOrgFlagAnimData(mOrgFlag.getText().toString());
        mSaveAnimationData.setTargetFlagAnimData(mTargetFlag.getText().toString());

        // save calculator result.
        mSaveAnimationData.setOrgResultEditAnimData(mResultEditText.getText().toString());
        mSaveAnimationData.setTargetResultEditAnimData(mTargetResultEditText.getText().toString());
        mSaveAnimationData.setOrgFormulaEditAnimData(mFormulaEditText.getText().toString());

        /* MODIFIED-BEGIN by qiong.liu1, 2017-03-27,BUG-3621966*/
        //save currency rates
        mSaveAnimationData.setOrgCurrencyRateData(mCurLeftCurrency);
        mSaveAnimationData.setTargetCurrencyRateData(mCurRightCurrency);
        /* MODIFIED-END by qiong.liu1,BUG-3621966*/
    }

    /**
     * We need to set the data to animation view so that letting it has a move efect.
     */
    private void setAnimDataBeforeStart(boolean isBeforeStart) {
        if (isBeforeStart) {
            mCurrencyOrgImgAnimation.setImageDrawable(mSaveAnimationData.getOrgImgAnimData());
            mCurrencyTargetImgAnimation.setImageDrawable(mSaveAnimationData.getTargetImgAnimData());

            mCurrencyOrgCountryShorterAnimation.setText(mSaveAnimationData.getOrgCountryShorterAnimData());
            mCurrencyTargetCountryShorterAnimation.setText(mSaveAnimationData.getTargetCountryShorterAnimData());

            mCurrencyOrgFlagAnimation.setText(mSaveAnimationData.getOrgFlagAnimData());
            mCurrencyTargetFlagAnimation.setText(mSaveAnimationData.getTargetFlagAnimData());

            mCurrencyOrgResultEditAnimation.setText(mSaveAnimationData.getOrgResultEditAnimData());
            mCurrencyTargetResultEditAnimation.setText(mSaveAnimationData.getTargetResultEditAnimData());
            mCurrencyOrgFormulaEditAnimation.setText(mSaveAnimationData.getOrgFormulaEditAnimData());

        } else {
            mOrgCountryImageView.setImageDrawable(mSaveAnimationData.getTargetImgAnimData());
            mTargetCountryImage.setImageDrawable(mSaveAnimationData.getOrgImgAnimData());

            mOrgCountry.setText(mSaveAnimationData.getTargetCountryShorterAnimData());
            mTargetCountry.setText(mSaveAnimationData.getOrgCountryShorterAnimData());

            mTargetFlag.setText(mSaveAnimationData.getOrgFlagAnimData());
            mOrgFlag.setText(mSaveAnimationData.getTargetFlagAnimData());

            mResultEditText.setText(mSaveAnimationData.getOrgResultEditAnimData());
            mTargetResultEditText.setText(mSaveAnimationData.getTargetResultEditAnimData());
            /* MODIFIED-BEGIN by qiong.liu1, 2017-03-27,BUG-3621966*/
            mCurLeftCurrency = mSaveAnimationData.getTargetCurrencyRateData();
            mCurRightCurrency = mSaveAnimationData.getOrgCurrencyRateData();
            /* MODIFIED-END by qiong.liu1,BUG-3621966*/

            String formulaEditResult = mSaveAnimationData.getOrgFormulaEditAnimData();
/* MODIFIED-BEGIN by qiong.liu1, 2017-04-25,BUG-4598868*/
//            mFormulaEditText.setText(formulaEditResult);

            if (mShowAnimFromResult) {
                if (mFormulaEditText != null) {
                    mFormulaEditText.setText(EXCHANGE_DEFAULT_VALUE);
                    if (!TextUtils.isEmpty(EXCHANGE_DEFAULT_VALUE)) {
                        setState(CalculatorState.RESULT);
                    }
                }
            }else {
                if (mFormulaEditText != null) {
                    mFormulaEditText.setText(formulaEditResult);
                }
                setState(CalculatorState.RESULT);
            }
            /* MODIFIED-END by qiong.liu1,BUG-4598868*/

            // Change EditText's status when we have a animation.
            if (!TextUtils.isEmpty(formulaEditResult) && mIsClickEqualsBtn) {
                setState(CalculatorState.RESULT);
            }
        }
    }

    class SaveAnimationData {
        private Drawable orgImgAnimData;
        private Drawable targetImgAnimData;

        private String orgCountryShorterAnimData;
        private String targetCountryShorterAnimData;

        private String orgResultEditAnimData;
        private String orgFormulaEditAnimData;
        private String targetResultEditAnimData;

        private String orgFlagAnimData;
        private String targetFlagAnimData;

        /* MODIFIED-BEGIN by qiong.liu1, 2017-03-27,BUG-3621966*/
        private float orgCurrencyRateData;
        private float targetCurrencyRateData;

        public float getTargetCurrencyRateData() {
            return targetCurrencyRateData;
        }

        public void setTargetCurrencyRateData(float targetCurrencyRateData) {
            this.targetCurrencyRateData = targetCurrencyRateData;
        }

        public float getOrgCurrencyRateData() {
            return orgCurrencyRateData;
        }

        public void setOrgCurrencyRateData(float orgCurrencyRateData) {
            this.orgCurrencyRateData = orgCurrencyRateData;
        }
        /* MODIFIED-END by qiong.liu1,BUG-3621966*/

        public Drawable getOrgImgAnimData() {
            return orgImgAnimData;
        }

        public void setOrgImgAnimData(Drawable orgImgAnimData) {
            this.orgImgAnimData = orgImgAnimData;
        }

        public String getOrgCountryShorterAnimData() {
            return orgCountryShorterAnimData;
        }

        public void setOrgCountryShorterAnimData(String orgCountryShorterAnimData) {
            this.orgCountryShorterAnimData = orgCountryShorterAnimData;
        }

        public Drawable getTargetImgAnimData() {
            return targetImgAnimData;
        }

        public void setTargetImgAnimData(Drawable targetImgAnimData) {
            this.targetImgAnimData = targetImgAnimData;
        }

        public String getTargetCountryShorterAnimData() {
            return targetCountryShorterAnimData;
        }

        public void setTargetCountryShorterAnimData(String targetCountryShorterAnimData) {
            this.targetCountryShorterAnimData = targetCountryShorterAnimData;
        }

        public String getOrgResultEditAnimData() {
            return orgResultEditAnimData;
        }

        public void setOrgResultEditAnimData(String orgResultEditAnimData) {
            this.orgResultEditAnimData = orgResultEditAnimData;
        }

        public String getOrgFormulaEditAnimData() {
            return orgFormulaEditAnimData;
        }

        public void setOrgFormulaEditAnimData(String orgFormulaEditAnimData) {
            this.orgFormulaEditAnimData = orgFormulaEditAnimData;
        }

        public String getTargetResultEditAnimData() {
            return targetResultEditAnimData;
        }

        public void setTargetResultEditAnimData(String targetResultEditAnimData) {
            this.targetResultEditAnimData = targetResultEditAnimData;
        }

        public void setOrgFlagAnimData(String orgFlagAnimData) {
            this.orgFlagAnimData = orgFlagAnimData;
        }

        public String getOrgFlagAnimData() {
            return orgFlagAnimData;
        }

        public void setTargetFlagAnimData(String targetFlagAnimData) {
            this.targetFlagAnimData = targetFlagAnimData;
        }

        public String getTargetFlagAnimData() {
            return targetFlagAnimData;
        }
    }

    private void initWheelView() {
        mLeftData = new ArrayList<>();
        mRightData = new ArrayList<>();
        mLeftData = mDBOperation.queryCurrencyWheelData();
        /* MODIFIED-BEGIN by qiong.liu1, 2017-04-01,BUG-3621966*/
        if (mLeftData == null) {
            Log.d(TAG, " initWheelView failed as the Data is null ");
            return;
        }
        mRightData = (ArrayList<Currency>) mLeftData.clone();
        /* MODIFIED-END by qiong.liu1,BUG-3621966*/
        mUpdateTime = mDBOperation.updateTime;
        getLeftWheelDefaultIndex();
        getRightWheelDefaultIndex();
        mCurrencyScrollWheelPicker = new CurrencyScrollWheelPicker(mContext, mWheelContainer, mCallbackChanged,
                mCallbackFinished,clickedListener, 0, 0, mLeftData, mRightData);
        /* MODIFIED-BEGIN by qiong.liu1, 2017-03-23,BUG-3621966*/
        mOrgCountryOld = mOrgCountry.getText().toString();
        mTargetCountryOld = mTargetCountry.getText().toString();
    }

    private void bindServiceInitDates() {
        if (rates7dArray == null || !mOrgCountryOld.equals(mOrgCountry.getText()) // MODIFIED by qiong.liu1, 2017-03-24,BUG-3621966
                || !mTargetCountryOld.equals(mTargetCountry.getText())) {
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);
            String endDate = year + "-" + month + "-" + day;
            String startDate = (year - 1) + "-" + month + "-" + day;
            Log.d(TAG, " currentDate " + startDate);
            Log.d(TAG, " endDate " + endDate);
            Intent queryPeriod = new Intent(mContext, HttpRequestService.class);
            queryPeriod.putExtra(HttpConstants.KEY_REQUEST_ID, HttpConstants.HISTORIC_RATE_PERIOD_REQUEST);
            queryPeriod.putExtra(HttpConstants.KEY_FROM, mTargetCountry.getText().toString());
            queryPeriod.putExtra(HttpConstants.KEY_TO, mOrgCountry.getText().toString());
            queryPeriod.putExtra(HttpConstants.KEY_AMOUNT, "1");
            queryPeriod.putExtra(HttpConstants.KEY_START_TIMES_TAMP, startDate);
            queryPeriod.putExtra(HttpConstants.KEY_END_TIMES_TAMP, endDate);
            queryPeriod.putExtra(HttpConstants.KEY_PER_PAGE_MAX, "370");
            mContext.startService(queryPeriod);
            /* MODIFIED-END by qiong.liu1,BUG-3621966*/
        }
    }

    private Currency getLeftCurrencyValue(int wheelIndex) {
        for (int i = 0; i < mLeftData.size(); i++) {
            if (i == wheelIndex) {
                return mLeftData.get(i);
            }
        }
        return null;
    }

    private Currency getRightCurrencyValue(int wheelIndex) {
        for (int i = 0; i < mRightData.size(); i++) {
            if (i == wheelIndex) {
                return mRightData.get(i);
            }
        }
        return null;
    }

    private void getLeftWheelDefaultIndex() {
        for (int lIndex = 0; lIndex < mLeftData.size(); lIndex++) {
            String leftDefaultCountry = mLeftData.get(lIndex).getCountryUnit();
            if (!TextUtils.isEmpty(leftDefaultCountry) && leftDefaultCountry.equals(mOrgCountry.getText().toString())) {
                mLeftIndex = lIndex;
                return;
            }
        }
    }

    private void getRightWheelDefaultIndex() {
        for (int rIndex = 0; rIndex < mRightData.size(); rIndex++) {
            String rightDefaultCountry = mRightData.get(rIndex).getCountryUnit();
            if (!TextUtils.isEmpty(rightDefaultCountry) && rightDefaultCountry.equals(mTargetCountry.getText().toString())) {
                mRightIndex = rIndex;
                return;
            }
        }
    }

    private void updateWheelData() {
        if (mCurrencyScrollWheelPicker != null) {
            if (mShowState == CURRENT_DOWN_SHOW.CURRENCY_TARGET_DOWN) {
                mCurrencyScrollWheelPicker.updateWheel(mRightIndex, mLeftIndex, mRightData, mLeftData);
            } else if (mShowState == CURRENT_DOWN_SHOW.CURRENCY_ORG_DOWN) {
                mCurrencyScrollWheelPicker.updateWheel(mLeftIndex, mRightIndex, mLeftData, mRightData);
            }
        }
    }

    CurrencyScrollWheelPicker.OnWheelChangedListener mCallbackChanged = new CurrencyScrollWheelPicker.OnWheelChangedListener() {
        @Override
        public void onWheelChanged(int leftIndex, int rightIndex) {
            mWheelPickerDone = false;
            if (mShowState == CURRENT_DOWN_SHOW.CURRENCY_ORG_DOWN) {
                mLeftIndex = leftIndex;
                mRightIndex = rightIndex;
            } else if (mShowState == CURRENT_DOWN_SHOW.CURRENCY_TARGET_DOWN) {
                mLeftIndex = rightIndex;
                mRightIndex = leftIndex;
            }
        }
    };

    OnWheelScrollListener mCallbackFinished = new OnWheelScrollListener() {
        @Override
        public void onScrollingStarted(WheelView wheel) {
        }

        @Override
        public void onScrollingFinished(WheelView wheel) {
            try {
                mWheelPickerDone = true;
                ScrollWheelPickerView.WHEEL wheelType = (ScrollWheelPickerView.WHEEL) wheel.getTag();
                switch (wheelType) {
                    case LEFT_WHEEL:
                        updateCountryUnit(ScrollWheelPickerView.WHEEL.LEFT_WHEEL);
                        break;
                    case RIGHT_WHEEL:
                        updateCountryUnit(ScrollWheelPickerView.WHEEL.RIGHT_WHEEL);
                        break;
                }
                Utils.setSoundForWheelView(getActivity(), mButtonRightMore);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    OnScrollWheelClickedListener clickedListener = new OnScrollWheelClickedListener() {
        @Override
        public void onItemClicked(WheelView wheel, int itemIndex) {
            try {
                ScrollWheelPickerView.WHEEL wheelType = (ScrollWheelPickerView.WHEEL) wheel.getTag();
                switch (wheelType) {
                    case LEFT_WHEEL:
                        mLeftIndex = itemIndex;
                        updateCountryUnit(ScrollWheelPickerView.WHEEL.LEFT_WHEEL);
                        break;
                    case RIGHT_WHEEL:
                        mRightIndex = itemIndex;
                        updateCountryUnit(ScrollWheelPickerView.WHEEL.RIGHT_WHEEL);
                        break;
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    };

    /**
     * Dismiss currency popwindow.
     **/
    private void dismissCurrencyPopWindow() {
        /* MODIFIED-BEGIN by qiong.liu1, 2017-04-21,BUG-4452809*/
        if (Calculator.mScreenState != Constant.SCREEN_PORT_FULL
                && Calculator.mScreenState != Constant.SCREEN_LAND_FULL) {
            if (mCommonContainer != null) {
                mCommonContainer.setPadding(150, 0, 0, 0);
            }
            mActivity.setShowExpandLayout(false);
            mXEImageView.setVisibility(View.VISIBLE); // MODIFIED by qiong.liu1, 2017-04-25,BUG-4635361
        }
        if (mCurrencyPopWindow != null && mCurrencyPopWindow.isShowing()) {
            if (!mIsClickPopWindowCancel) {
                saveCountryInitialData();
            }
            mCurrencyOrangeLayout.setBackgroundColor(Color.WHITE);
            mCurrencyTargetLayout.setBackgroundColor(Color.WHITE);
            /* MODIFIED-END by qiong.liu1,BUG-4452809*/
            mCurrencyPopWindow.dismiss();
        }
    }

    @Override
    public void onClick(View view) {
        /* MODIFIED-BEGIN by qiong.liu1, 2017-04-25,BUG-4598661*/
        if (view.getId() != R.id.bt_right_more && view.getId() != R.id.bt_left_more){
            mDisplayView.collapseHistory();
        }
        /* MODIFIED-END by qiong.liu1,BUG-4598661*/
        mActivity.coolapseButton();
        switch (view.getId()) {
            case R.id.currency_clear:
                onDelete();
                break;
            case R.id.del2:
                onClear();
                break;
            case R.id.pop_cancel:
                mIsClickPopWindowCancel = true;
                resetCountryData();
                dismissCurrencyPopWindow();
                break;
            case R.id.pop_ok:
                mIsClickPopWindowCancel = false;
                dismissCurrencyPopWindow();
                bindServiceInitDates(); // MODIFIED by qiong.liu1, 2017-03-23,BUG-3621966
                break;
            case R.id.bt_left_more:
                mIsClickLeftMoreButton = true;
                dismissCurrencyPopWindow();
                ((Calculator) this.getActivity()).showListFragment();
                break;
            case R.id.bt_right_more:
                mIsClickLeftMoreButton = false;
                dismissCurrencyPopWindow();
                ((Calculator) this.getActivity()).showListFragment();
                break;
            case R.id.currency_op_add:
            case R.id.currency_op_mul:
            case R.id.currency_op_div:
                String mFormulaEditTextStr = mFormulaEditText.getText().toString()
                        .trim();
                String op_sub = getResources().getString(R.string.op_sub);
                if (mFormulaEditTextStr.length() == 1
                        && op_sub.equals(mFormulaEditTextStr)) {
                    break;
                }
                /* MODIFIED-BEGIN by kaifeng.lu, 2016-06-23,BUG-2396079*/
                setState(CalculatorState.INPUT);
                mFormulaEditText.append(((Button) view).getText());
                break;
            case R.id.currency_eq:
                mIsClickEqualsBtn = true;
                onEquals();
                break;
            case R.id.currency_convert:
                mShowAnimFromResult = false; // MODIFIED by qiong.liu1, 2017-04-25,BUG-4598868
                startCurrencyAnim();
                rotateAnim();
                break;
            case R.id.show_wheel_currency:
                /* MODIFIED-BEGIN by qiong.liu1, 2017-04-21,BUG-4452809*/
                if (mCalculatorPadViewPager != null
                        && mCalculatorPadViewPager.getCurrentItem() > 0
                        && mDisplayView.getTranslateState()
                        == CurrencyDisplayOverlay.TranslateState.COLLAPSED) {
                    mCalculatorPadViewPager.setCurrentItem(mCalculatorPadViewPager.getCurrentItem() - 1);
                    break;
                }
                if (mOrgDownShow.getVisibility() == View.VISIBLE && !mIsShowHistory) {
                    showPopupWindow(mShowWheelButton, TOP);
                }
                break;
            case R.id.target_show_wheel_currency:
                if (mTargetDownShow.getVisibility() == View.VISIBLE && !mIsShowHistory) {
                    showPopupWindow(mTargetShowWheelButton, BOTTOM);
                    /* MODIFIED-END by qiong.liu1,BUG-4452809*/
                }
                break;
            case R.id.dec_point:
                /* MODIFIED-BEGIN by kaifeng.lu, 2016-06-29,BUG-2398506*/
                if (mCurrentState == CalculatorState.RESULT) {
                    onDelete();
                    mFormulaEditText.setText("0" + getString(R.string.dec_point_rel4));
                    break;
                }
                /* MODIFIED-END by kaifeng.lu,BUG-2398506*/
                String point_str = mFormulaEditText.getText().toString();
                char c = 'a';
                int point_length = point_str.length();
                if (point_length >= 1) {
                    c = point_str.charAt(point_length - 1);
                    Log.d(TAG, "c=" + c);
                }
                if (c == '.' || c == ',') {
                    break;
                }
                if (point_str.equals("")) {
                    mFormulaEditText.append("0" + getString(R.string.dec_point_rel4));
                } else if (!(c >= '0' && c <= '9')) {
                    mFormulaEditText.append("0" + getString(R.string.dec_point_rel4));
                } else {
                    mFormulaEditText.append(((Button) view).getText());
                }
                break;
            case R.id.target_layout:
                /* MODIFIED-BEGIN by qiong.liu1, 2017-04-21,BUG-4452809*/
                if (mCalculatorPadViewPager != null
                        && mCalculatorPadViewPager.getCurrentItem() > 0
                        && mDisplayView.getTranslateState()
                        == CurrencyDisplayOverlay.TranslateState.COLLAPSED) {
                    mCalculatorPadViewPager.setCurrentItem(mCalculatorPadViewPager.getCurrentItem() - 1);
                    break;
                }
                mShowAnimFromResult = true; // MODIFIED by qiong.liu1, 2017-04-25,BUG-4598868
                startCurrencyAnim();
                break;
            case R.id.org_formula_layout:
                if (mCalculatorPadViewPager != null
                        && mCalculatorPadViewPager.getCurrentItem() > 0
                        && mDisplayView.getTranslateState()
                        == CurrencyDisplayOverlay.TranslateState.COLLAPSED) {
                        /* MODIFIED-END by qiong.liu1,BUG-4452809*/
                    mCalculatorPadViewPager.setCurrentItem(mCalculatorPadViewPager.getCurrentItem() - 1);
                }
                break;
            case R.id.currency_expend:
                mCalculatorPadViewPager.setCurrentItem(1);
                break;
            case R.id.currency_ic_xe:
                startXEWebSite();
                break;
            case R.id.tv_oneweek:
                updateCharView("oneweek");
                break;
            case R.id.tv_onemonth:
                updateCharView("onemonth");
                break;
            case R.id.tv_sixmonth:
                updateCharView("sixmonth");
                break;
            case R.id.tv_oneyear:
                updateCharView("oneyear");
                break;
            case R.id.digit_0:
                String forum = mFormulaEditText.getEditableText().toString();
                if (forum.length() == 1 && forum.equals(getResources().getString(R.string.digit_0))) {
                    break;
                }
                if (forum.length() == 2 && forum.equals(getResources().getString(R.string.op_sub) + getResources().getString(R.string.digit_0))) {
                    break;
                }
            default:
                if (mCurrentState != CalculatorState.RESULT) {
                    mFormulaEditText.append(((Button) view).getText());
                } else {
                    setState(CalculatorState.INPUT);
                    mFormulaEditText.append(((Button) view).getText());
                }
                break;
        }
    }

    private void rotateAnim() {
        ObjectAnimator convertRotateAnim = ObjectAnimator.ofFloat(mImgConvert, "rotation", 0.0f, 360.0f);
        convertRotateAnim.setDuration(ANIM_TIME);
        AnimatorSet animSetXY = new AnimatorSet();
        animSetXY.play(convertRotateAnim);
        animSetXY.start();
    }

    /**
     * Set currency time when first into this screen.
     **/
    public void setCurrencyTime() {
        if (mCurrencyTime != null) {
            /* MODIFIED-BEGIN by qiong.liu1, 2017-03-20,BUG-3621966*/
            if(!TextUtils.isEmpty(mUpdateTime)){
                mCurrencyTime.setText(getUpdateTime(mUpdateTime));
            /* MODIFIED-BEGIN by qiong.liu1, 2017-03-27,BUG-3621966*/
            }else {
                ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo netInfo = cm.getActiveNetworkInfo();
                if (netInfo != null && netInfo.isConnected()) { // MODIFIED by qiong.liu1, 2017-03-30,BUG-3621966
                    mCurrencyTime.setText(R.string.updating);
                }else {
                    mCurrencyTime.setText(R.string.no_internet_access);
                }
                /* MODIFIED-END by qiong.liu1,BUG-3621966*/
            }
            /* MODIFIED-END by qiong.liu1,BUG-3621966*/
        }
    }

    /**
     * Set cursor visible of currency screen.
     */
    public void setCurrencyCursorVisible() {
        if (mFormulaEditText != null) {
            mFormulaEditText.setFocusable(true);
            mFormulaEditText.setFocusableInTouchMode(true);
            mFormulaEditText.requestFocus();
            mFormulaEditText.setCursorVisible(true);
        }
    }

    public void clearCurrencyFocus() {
        if (mFormulaEditText != null) {
            mFormulaEditText.clearFocus();
        }
    }

    /**
     * Get system time.
     **/
    /* MODIFIED-BEGIN by qiong.liu1, 2017-03-20,BUG-3621966*/
    private String getUpdateTime(String time) {
//        Calendar calendar = Calendar.getInstance();
//        String month = String.valueOf(calendar.get(Calendar.MONTH) + 1);
//        String day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
//        String hour = String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
//        String minute = String.valueOf(calendar.get(Calendar.MINUTE));
        String month = null;
        String day = null;
        String hour = null;
        String minute = null;
        if (time.length() > 16){
            month = time.substring(5,7);
            day = time.substring(8,10);
            hour = time.substring(11,13);
            minute = time.substring(14,16);
        }
//        if (Integer.valueOf(month) < 10) {
//            month = Constant.CURRENCY_ADD_ZERZO_TIME + month;
//        }
//        if (Integer.valueOf(day) < 10) {
//            day = Constant.CURRENCY_ADD_ZERZO_TIME + day;
//        }
//        if (Integer.valueOf(hour) < 10) {
//            hour = Constant.CURRENCY_ADD_ZERZO_TIME + hour;
//        }
//        if (Integer.valueOf(minute) < 10) {
//            minute = Constant.CURRENCY_ADD_ZERZO_TIME + minute;
//        }
/* MODIFIED-END by qiong.liu1,BUG-3621966*/
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getResources().getString(R.string.currency_time));
        stringBuilder.append(String.valueOf(month)).append("/");
        stringBuilder.append(String.valueOf(day)).append(" ");
        stringBuilder.append(String.valueOf(hour)).append(":");
        stringBuilder.append(String.valueOf(minute));
        return stringBuilder.toString();
    }

    private void computeAnimHeight() {
        float topHeight ;
        if(Calculator.mScreenState == Calculator.SCREENPORTFULL) {
         topHeight   =getResources().getDimension(R.dimen.currency_layout_top_anim_height);
        }else{
            topHeight = getResources().getDimension(R.dimen.currency_layout_top_anim_height_split);
        }
        mTopFrom = 0.0f;
        mBottomFrom = 0.0f;

        mTopTo = topHeight;
        mBottomTo = -mTopTo;
    }

    private void showAnimView(boolean isBeforeAnim) {
        try {
            if (isBeforeAnim) {
                mCurrencyOrgLayoutAnimation.setVisibility(View.VISIBLE);
                mCurrencyTargetLayoutAnimation.setVisibility(View.VISIBLE);
                mCurrencyDividerAnimation.setVisibility(View.VISIBLE);
                mCurrencyOrgDownShowImgAnimation.setVisibility(View.VISIBLE);
                mCurrencyOrgCountryShorterAnimation.setVisibility(View.VISIBLE);
                mCurrencyTargetCountryShorterAnimation.setVisibility(View.VISIBLE);

                if (mDefaultCurrency != null && mOrgCountry.getText().toString().equals(mDefaultCurrency.getCurrencyShorterForm())) {
                    mCurrencyOrgLocationImgAnimation.setVisibility(View.VISIBLE);
                } else {
                    mCurrencyOrgLocationImgAnimation.setVisibility(View.GONE);
                }

                mCurrencyOrangeLayout.setVisibility(View.GONE);
                mCurrencyTargetLayout.setVisibility(View.GONE);
                mCurrencyDivider.setVisibility(View.GONE);
                mOrgLocation.setVisibility(View.GONE);
                mOrgDownShow.setVisibility(View.GONE);
                mOrgCountry.setVisibility(View.GONE);
                mTargetCountry.setVisibility(View.GONE);
            } else {
                mCurrencyOrgLayoutAnimation.setVisibility(View.GONE);
                mCurrencyTargetLayoutAnimation.setVisibility(View.GONE);
                mCurrencyDividerAnimation.setVisibility(View.GONE);
                mCurrencyOrgLocationImgAnimation.setVisibility(View.GONE);
                mCurrencyOrgDownShowImgAnimation.setVisibility(View.GONE);
                mCurrencyOrgCountryShorterAnimation.setVisibility(View.GONE);
                mCurrencyTargetCountryShorterAnimation.setVisibility(View.GONE);

                mCurrencyOrangeLayout.setVisibility(View.VISIBLE);
                mCurrencyTargetLayout.setVisibility(View.VISIBLE);
                mCurrencyDivider.setVisibility(View.VISIBLE);
                mOrgDownShow.setVisibility(View.VISIBLE);
                mOrgCountry.setVisibility(View.VISIBLE);
                mTargetCountry.setVisibility(View.VISIBLE);

                /* MODIFIED-BEGIN by qiong.liu1, 2017-04-21,BUG-4452809*/
                if (mDefaultCurrency != null
                        && mTargetCountry.getText().toString().equals(mDefaultCurrency.getCurrencyShorterForm())) {
                        /* MODIFIED-END by qiong.liu1,BUG-4452809*/
                    mOrgLocation.setVisibility(View.VISIBLE);
                } else {
                    mOrgLocation.setVisibility(View.GONE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    Handler mUIHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case EXCHANGE_CURRENCY_ANIM_END:
                    computeAnimHeight();
                    showAnimView(false);
                    saveCountryInitialData();
                    setAnimDataBeforeStart(false);
                    setCurrencyCursorVisible();
                    break;
                case CURRENCY_SHOW_POPWINDOW:
                    showPopupWindow(mShowWheelButton, TOP); // MODIFIED by qiong.liu1, 2017-04-21,BUG-4452809
            }
        }
    };

    private void startCurrencyAnim() {
        copyDataBeforeStartAnimation();
        setAnimDataBeforeStart(true);

        ObjectAnimator topOutAnim = ObjectAnimator.ofFloat(mCurrencyOrgLayoutAnimation, "translationY", mTopFrom, mTopTo);
        ObjectAnimator bottomOutAnim = ObjectAnimator.ofFloat(mCurrencyTargetLayoutAnimation, "translationY", mBottomFrom, mBottomTo);

        bottomOutAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                showAnimView(true);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                mUIHandler.sendEmptyMessage(EXCHANGE_CURRENCY_ANIM_END);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        topOutAnim.setDuration(ANIM_TIME);
        bottomOutAnim.setDuration(ANIM_TIME);

        AnimatorSet animSetXY = new AnimatorSet();
        animSetXY.play(topOutAnim).with(bottomOutAnim);
        animSetXY.setInterpolator(new AccelerateInterpolator());
        animSetXY.start();
    }

    private enum CURRENT_DOWN_SHOW {
        CURRENCY_ORG_DOWN, CURRENCY_TARGET_DOWN
    }

    private CURRENT_DOWN_SHOW mShowState = CURRENT_DOWN_SHOW.CURRENCY_ORG_DOWN;

    /* MODIFIED-BEGIN by qiong.liu1, 2017-04-21,BUG-4452809*/
    private void showPopupWindow(View view, int whichButton) {
        if (mOrgCountry != null) {
            mOrgCountryOld = mOrgCountry.getText().toString();
            mTargetCountryOld = mTargetCountry.getText().toString();
        }
        if (Calculator.mScreenState != Constant.SCREEN_PORT_FULL) {
            mActivity.setShowExpandLayout(true);
            /* MODIFIED-BEGIN by qiong.liu1, 2017-04-25,BUG-4635361*/
            mXEImageView.setVisibility(View.GONE);
            if (mCommonContainer != null) {
                mCommonContainer.setPadding(15, 0, 0, 0);
            }
            /* MODIFIED-END by qiong.liu1,BUG-4635361*/
        }
        /* MODIFIED-BEGIN by qiong.liu1, 2017-05-04,BUG-4656997*/
        if (mCurrencyPopWindow != null) {
            mCurrencyPopWindow.showAsDropDown(view, 0, -11);
        }
        if (drawableBg != null && whichButton == TOP) {
            mCurrencyOrangeLayout.setBackground(drawableBg);
        }else {
            mCurrencyTargetLayout.setBackground(drawableBg);
            /* MODIFIED-END by qiong.liu1,BUG-4656997*/
        }
        /* MODIFIED-END by qiong.liu1,BUG-4452809*/
        getRightWheelDefaultIndex();
        getLeftWheelDefaultIndex();
        saveCountryInitialData();
        updateWheelData();
        clearCurrencyFocus();
        if (mOrgDownShow.getVisibility() == View.VISIBLE) {
            mShowState = CURRENT_DOWN_SHOW.CURRENCY_ORG_DOWN;
            mOrgDownShow.setVisibility(View.INVISIBLE);
        } else if (mTargetDownShow.getVisibility() == View.VISIBLE) {
            mShowState = CURRENT_DOWN_SHOW.CURRENCY_TARGET_DOWN;
            mTargetDownShow.setVisibility(View.INVISIBLE);
        }
    }

    private void saveCountryInitialData() {
        if (mSharedPreferencesHelper != null) {
            if (mShowState == CURRENT_DOWN_SHOW.CURRENCY_ORG_DOWN) {
                mSharedPreferencesHelper.saveCurrencyTopCountry(mOrgCountry.getText().toString());
                mSharedPreferencesHelper.saveCurrencyBottomCountry(mTargetCountry.getText().toString());
            } else if (mShowState == CURRENT_DOWN_SHOW.CURRENCY_TARGET_DOWN) {
                mSharedPreferencesHelper.saveCurrencyTopCountry(mTargetCountry.getText().toString());
                mSharedPreferencesHelper.saveCurrencyBottomCountry(mOrgCountry.getText().toString());
            }
        }
    }

    private void resetCountryData() {
        if (mSharedPreferencesHelper != null) {
            String originalTopCountry = mSharedPreferencesHelper.getCurrencyTopCountry();
            String originalBottomCountry = mSharedPreferencesHelper.getCurrencyBottomCountry();
            mOrgCountry.setText(!TextUtils.isEmpty(originalTopCountry) ? originalTopCountry : Constant.CURRENCY_COUNTRY_CNY);
            mTargetCountry.setText(!TextUtils.isEmpty(originalBottomCountry) ? originalBottomCountry : Constant.CURRENCY_COUNTRY_USD);
            // update picture.
            updateCurrencyPicture(true, true);
        } else {
            setDefaultCurrency();
        }
    }

    /**
     * Update currency selected picture.
     *
     * @param isTop    top currency
     * @param isBottom bottom currency
     */
    private void updateCurrencyPicture(boolean isTop, boolean isBottom) {
        if (isTop) {
            Currency topCurrency = new Currency();
            mDBOperation.queryCurrencyPicture(mOrgCountry.getText().toString(), topCurrency);
            int topDrawableId = -1;
            if (!TextUtils.isEmpty(topCurrency.getCountryPicture())) {
                topDrawableId = mContext.getResources().getIdentifier(topCurrency.getCountryPicture(), "drawable", mContext.getPackageName());
            }
            mOrgCountryImageView.setImageResource((topDrawableId != -1) ? topDrawableId : R.drawable.china);
            mOrgFlag.setText(topCurrency.getCountrySignal());
            mCurLeftCurrency = Float.valueOf(topCurrency.getCountryCurrency()); // MODIFIED by qiong.liu1, 2017-03-20,BUG-3621966

        }
        if (isBottom) {
            Currency bottomCurrency = new Currency();
            mDBOperation.queryCurrencyPicture(mTargetCountry.getText().toString(), bottomCurrency);
            int bottomDrawableId = -1;
            if (!TextUtils.isEmpty(bottomCurrency.getCountryPicture())) {
                bottomDrawableId = mContext.getResources().getIdentifier(bottomCurrency.getCountryPicture(), "drawable", mContext.getPackageName());
            }
            mTargetCountryImage.setImageResource((bottomDrawableId != -1) ? bottomDrawableId : R.drawable.united_states);
            mTargetFlag.setText(bottomCurrency.getCountrySignal());
            mCurRightCurrency = Float.valueOf(bottomCurrency.getCountryCurrency()); // MODIFIED by qiong.liu1, 2017-03-20,BUG-3621966
        }
    }

    public interface GetCurrencyPopCallBack {

        void showListFragment();

    }

    public String getCommaString(String originalStr) {


        if (originalStr == null) {
            return originalStr;
        }
        if (originalStr.length() < 4) {
            return originalStr;
        }

        Log.i(TAG, "getCommaString: originalStr: " + originalStr);
        String noCommaStr = originalStr.replace(" ", "");
        StringBuilder builder = new StringBuilder();
        boolean hasSpecialChar = false;
        int markIndex = 0;

        for (int i = 0; i < noCommaStr.length(); i++) {
            try {
                if (!Character.isDigit(noCommaStr.charAt(i))) {
                    hasSpecialChar = true;
                    if ((i - markIndex) > 0) {
                        String tmpStr = noCommaStr.substring(markIndex, i);
                        if ((markIndex > 1) && noCommaStr.charAt(markIndex - 1) == '.' || (markIndex > 1) && noCommaStr.charAt(markIndex - 1) == ',') {
                            /*tmpStr = new StringBuilder(tmpStr).reverse().toString();
                            tmpStr = formatNumber(tmpStr, 3);
                            tmpStr = new StringBuilder(tmpStr).reverse().toString();*/
                        } else {
                            tmpStr = formatNumber(tmpStr);
                        }
                        builder.append(tmpStr);
                    }
                    builder.append(noCommaStr.charAt(i));
                    markIndex = i + 1;
                }
            } catch (IndexOutOfBoundsException e) {
                Log.e(TAG, "getCommaString: IndexOutOfBoundsException");
            }
        }

        if (hasSpecialChar && markIndex < noCommaStr.length()) {
            try {
                String tmpStr = noCommaStr.substring(markIndex, noCommaStr.length());
                Log.i(TAG, "getCommaString: hasSpecialChar, tmpStr: " + tmpStr);
                if ((markIndex > 1) && noCommaStr.charAt(markIndex - 1) == '.' || (markIndex > 1) && noCommaStr.charAt(markIndex - 1) == ',') {
                    /*tmpStr = new StringBuilder(tmpStr).reverse().toString();
                    tmpStr = formatNumber(tmpStr, 3);
                    tmpStr = new StringBuilder(tmpStr).reverse().toString();*/
                } else {
                    tmpStr = formatNumber(tmpStr);
                }
                builder.append(tmpStr);
            } catch (IndexOutOfBoundsException e) {
                Log.e(TAG, "getCommaString: IndexOutOfBoundsException");
            }
        }
        if (!hasSpecialChar) {
            Log.i(TAG, "has no SpecialChar");
            String section = formatNumber(noCommaStr);
            builder.append(section);
        }

        String commaStr = builder.toString();

        if (TextUtils.isEmpty(commaStr)) {
            return originalStr;
        } else {
            return commaStr;
        }
    }


    public String formatNumber(String iniNum) {
        try {
            StringBuffer tmp = new StringBuffer().append(iniNum.replaceAll(" ", "")).reverse();
            String retNum = Pattern.compile("(\\d{" + 3 + "})(?=\\d)")
                    .matcher(tmp.toString()).replaceAll("$1 ");
            return new StringBuffer().append(retNum).reverse().toString();
        } catch (PatternSyntaxException e) {
            Log.e(TAG, "formatNumber: PatternSyntaxException");
            return null;
        } catch (NullPointerException e) {
            Log.e(TAG, "formatNumber: NullPointerException");
            return null;
        }
    }

    private void onEquals() {
        if (mFormulaEditText.getText().toString().contains("e") || mCurrentState == CalculatorState.INPUT) {
            setState(CalculatorState.EVALUATE);
            mEvaluator.evaluate(mFormulaEditText.getText().toString().replace(" ", "").replaceAll(",", "."), CurrencyFragment.this);
        }
    }

    private void setState(CalculatorState state) {
        if (mCurrentState != state) {
            mCurrentState = state;

            if (state == CalculatorState.RESULT || state == CalculatorState.ERROR) {
//                mDeleteButton.setVisibility(View.GONE);
//                mClearButton.setVisibility(View.VISIBLE);
            } else {
//                mDeleteButton.setVisibility(View.VISIBLE);
//                mClearButton.setVisibility(View.GONE);
            }

            if (state == CalculatorState.ERROR) {
                final int errorColor = getResources().getColor(R.color.calculator_error_color);
                mFormulaEditText.setTextColor(errorColor);
                mResultEditText.setTextColor(errorColor);
                mOrgFlag.setTextColor(errorColor);

//                getWindow().setStatusBarColor(errorColor);
            } else {
//                mFormulaEditText.setTextColor(
//                        getResources().getColor(R.color.display_formula_text_color));
//                mResultEditText.setTextColor(
//                        getResources().getColor(R.color.display_result_text_color));
//                getWindow().setStatusBarColor(
//                        getResources().getColor(R.color.calculator_accent_color));
            }
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, " Currency onResume  "); // MODIFIED by qiong.liu1, 2017-03-30,BUG-3621966
        mHistory = getHistory();

        mHistoryAdapter = new CurrencyHistoryAdapter(this.getActivity(), mHistory
                , new CurrencyHistoryAdapter.HistoryHeadCallBack() {
            //PR 540691 don't collapse the history panel when long click history item to copy the result.->DELETE codes about longclick history item to collopse the history panel  28/8/2015 update by xiaolu.li
            @Override
            public void onHistoryHeadSelected() {

                mDisplayView.collapseHistory();
                (new Handler()).postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        cr.delete(HistoryContentUri.CONTENT_URI, "", null);
                        ((CurrencyHistoryAdapter) mDisplayView.getHistoryView().getAdapter()).clear();
                        mHistory.clear();
                        mDBOperation.clearCurrencyHistoryData();
                        ((CurrencyHistoryAdapter) mDisplayView.getHistoryView().getAdapter()).notifyDataSetChanged();
                        mDisplayView.setMaxTranlation(-1);
                        mDisplayView.initializeHistoryAndGraphView(false);
                    }

                }, 300);

            }
        });
        /* MODIFIED-BEGIN by kaifeng.lu, 2016-08-16,BUG-2712192*/
        mHistoryAdapter.setOnItemClickListener(new CurrencyHistoryAdapter.OnCurrencyHistoryItemClickListener() {
            @Override
            public void onItemClick(View view, String data) {
                if (!TextUtils.isEmpty(data) && data.split("-").length >= 2) {
                    if (mOrgDownShow.getVisibility() == View.VISIBLE) {
                        mOrgCountry.setText(data.split("-")[0]);
                        mTargetCountry.setText(data.split("-")[1]);
                        setShowStateForLocation();
                    } else if (mTargetCountry.getVisibility() == View.VISIBLE) {
                        mTargetCountry.setText(data.split("-")[0]);
                        mOrgCountry.setText(data.split("-")[1]);
                        setShowStateForLocation();
                    }
                    updateCurrencyPicture(true, true);
                    updateCurrencyHistoryData(mOrgCountry.getText().toString(), mTargetCountry.getText().toString());
                    mDisplayView.collapseHistory();
                    mDisplayView.setMaxTranlation(-1);
                    mDisplayView.initializeHistoryAndGraphView(false);
                }
            }
        });
        mHistory.setObserver(mHistoryAdapter);
        mDisplayView.getHistoryView().setAdapter(mHistoryAdapter);

        mDisplayView.scrollToMostRecent();

        if (needExpand) {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    mDisplayView.expandHistory();
                }
            });
            needExpand = false;
        }
        if (mDisplayView.getTranslateState() == CurrencyDisplayOverlay.TranslateState.EXPANDED) {
            mDisplayView.expandHistory();
        }
        //TS:kaifeng.lu 2015-11-27 Calculator BUGFIX_967237 ADD_S
        try {
            if (mDisplayView.getHistoryView().getAdapter().getItemCount() == 1) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mDisplayView.getTranslateState() != CurrencyDisplayOverlay.TranslateState.COLLAPSED)
                            mDisplayView.collapseHistory();
                    }
                }, 1000);
            }
        } catch (NullPointerException e) {
            Log.d("Calcultor", "NullPointerException to close history");
        }



        /* MODIFIED-BEGIN by kaifeng.lu, 2016-09-22,BUG-2960280*/
        if (!TextUtils.isEmpty(mFormulaEditText.getText())) {
            String currentStr;
            if (mIsStanderPoint) {
                currentStr = mFormulaEditText.getText().toString().replaceAll(",", ".");
            } else {
                currentStr = mFormulaEditText.getText().toString().replaceAll("\\.", ",");
            }
            mFormulaEditText.setText(currentStr);
        }
        /* MODIFIED-END by kaifeng.lu,BUG-2960280*/

        if (!TextUtils.isEmpty(mFormulaEditText.getText())) {
            mEvaluator.evaluate(mFormulaEditText.getText().toString().replaceAll(" ", "").replaceAll(",", "."), this);
        }

        //TS:kaifeng.lu 2015-12-23 Calculator BUGFIX_1202285 ADD_E
        Log.i(TAG, "onResume end " + System.currentTimeMillis()); // MODIFIED by kaifeng.lu, 2016-05-25,BUG-2162730
    }

    private void onResult(final String result) {
        Log.i(TAG, "---onResult---");// MODIFIED by kaifeng.lu, 2016-03-23,BUG-1722335
        Log.i(TAG, "---result---" +result);// MODIFIED by kaifeng.lu, 2016-03-23,BUG-1722335 // MODIFIED by qiong.liu1, 2017-03-20,BUG-3621966
// Calculate the values needed to perform the scale and translation animations,
// accounting for how the scale will affect the final position of the text.
        final String commaResult = getCommaString(result);
        final float resultScale =
                mFormulaEditText.getVariableTextSize(commaResult) / mResultEditText.getTextSize();

        if (mResultEditText != null && TextUtils.isEmpty(mResultEditText.getText().toString())) {
            mResultEditText.setText(commaResult);
        }
        final int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        final int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        mResultEditText.measure(w, h);

        float resultTranslationX = (1.0f - resultScale) * (mResultEditText.getMeasuredWidth() / 2 - mResultEditText.getPaddingEnd());
//        final float resultTranslationY = (1.0f - resultScale)  * (mCurrencyTargetLayout.getHeight() - mResultEditText.getBottom() + mFormulaEditText.getBottom());
        final float resultTranslationY = (1.0f - resultScale) *
                (mResultEditText.getHeight() / 2.0f - mResultEditText.getPaddingBottom()) +
                (mFormulaEditText.getBottom() - mResultEditText.getBottom()) +
                (mResultEditText.getPaddingBottom() - mFormulaEditText.getPaddingBottom());
        final float formulaTranslationY = -mFormulaEditText.getBottom();

// Use a value animator to fade to the final text color over the course of the animation.
        final int resultTextColor = mResultEditText.getCurrentTextColor();
        final int formulaTextColor = mFormulaEditText.getCurrentTextColor();
        final ValueAnimator textColorAnimator =
                ValueAnimator.ofObject(new ArgbEvaluator(), resultTextColor, formulaTextColor);
        textColorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                //mResultEditText.setTextColor((int) valueAnimator.getAnimatedValue());
                mResultEditText.setTextColor(resultTextColor);

            }
        });

        final AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(
                textColorAnimator,
                ObjectAnimator.ofFloat(mResultEditText, View.SCALE_X, resultScale),
                ObjectAnimator.ofFloat(mResultEditText, View.SCALE_Y, resultScale),
                ObjectAnimator.ofFloat(mResultEditText, View.TRANSLATION_X, resultTranslationX),
                ObjectAnimator.ofFloat(mResultEditText, View.TRANSLATION_Y, resultTranslationY),
                ObjectAnimator.ofFloat(mFormulaEditText, View.TRANSLATION_Y, formulaTranslationY));
        animatorSet.setDuration(getResources().getInteger(android.R.integer.config_longAnimTime));
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (mIsStanderPoint) {
                    mResultEditText.setText(commaResult.replaceAll(",", "."));
                } else {
                    if (TextUtils.isEmpty(commaResult)) {
                        mResultEditText.setText(commaResult);
                    } else {
                        mResultEditText.setText(commaResult.replaceAll("\\.", ","));
                    }
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {

                mResultEditText.setTextColor(resultTextColor);
                mResultEditText.setScaleX(1.0f);
                mResultEditText.setScaleY(1.0f);
                mResultEditText.setTranslationX(0.0f);
                mResultEditText.setTranslationY(0.0f);
                mFormulaEditText.setTranslationY(0.0f);

                // Finally update the formula to use the current result.
                if (mIsStanderPoint) {
                    mFormulaEditText.setText(commaResult.replaceAll(",", "."));
                } else {
                    if (TextUtils.isEmpty(commaResult)) {
                        mFormulaEditText.setText(commaResult);
                    } else {
                        mFormulaEditText.setText(commaResult.replaceAll("\\.", ","));
                    }

                }

                if (mSaveAnimationData != null) {
                    mSaveAnimationData.setOrgFormulaEditAnimData(mFormulaEditText.getText().toString());
                }
                setState(CalculatorState.RESULT);

                mCurrentAnimator = null;
            }
        });

        mCurrentAnimator = animatorSet;
        animatorSet.start();
    }

    private void onError(final int errorResourceId) {
        if (mCurrentState != CalculatorState.EVALUATE) {
            // Only animate error on evaluate.
            mResultEditText.setText(errorResourceId);
            return;
        }

        reveal(mCurrentButton, R.color.calculator_error_color, new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                setState(CalculatorState.ERROR);
                mResultEditText.setText(errorResourceId);
            }
        });
    }

    private void reveal(View sourceView, int colorRes, Animator.AnimatorListener listener) {
        final View revealView = new View(this.getActivity());

        mLayoutParams.height = mDisplayView.getDisplayHeight();
        mLayoutParams.gravity = Gravity.BOTTOM;
        revealView.setLayoutParams(mLayoutParams);
        revealView.setBackgroundColor(getResources().getColor(colorRes));
        // groupOverlay.add(revealView);
        mDisplayView.addView(revealView);
        //TS:kaifeng.lu 2015-12-07 Calculator BUGFIX_966467  MOD_S
//    mDisplayView.addView(revealView);
//        final int[] clearLocation = new int[2];
//        sourceView.getLocationInWindow(clearLocation);
//        clearLocation[0] += sourceView.getWidth();
//        clearLocation[1] += sourceView.getHeight();
//
        final int revealCenterX = mDisplayView.getWidth();
        final int revealCenterY = mDisplayView.getDisplayHeight();

        final double x_2 = Math.pow(mDisplayView.getWidth(), 2);
//        final double x2_2 = Math.pow(mDisplayView.getDisplayHeight() - revealCenterX, 2);
        final double y_2 = Math.pow(mDisplayView.getDisplayHeight(), 2);
//        final float revealRadius = (float) Math.max(Math.sqrt(x1_2 + y_2), Math.sqrt(x2_2 + y_2));
        final float revealRadius = (float) Math.sqrt(x_2 + y_2);
        //TS:kaifeng.lu 2015-12-07 Calculator BUGFIX_966467  MOD_E

        //TS:kaifeng.lu 2016-05-17 Calculator BUGFIX-2157429  MOD_S
        try {
            final Animator revealAnimator =
                    ViewAnimationUtils.createCircularReveal(revealView,
                            revealCenterX, revealCenterY, 0.0f, revealRadius);

            revealAnimator.setDuration(
                    getResources().getInteger(android.R.integer.config_longAnimTime));
            revealAnimator.addListener(listener);

            final Animator alphaAnimator = ObjectAnimator.ofFloat(revealView, View.ALPHA, 0.0f);
            alphaAnimator.setDuration(
                    getResources().getInteger(android.R.integer.config_mediumAnimTime));

            final AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.play(revealAnimator).before(alphaAnimator);
            animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
            animatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    mDisplayView.removeView(revealView);
                    mCurrentAnimator = null;
                }
            });
            mCurrentAnimator = animatorSet;
            animatorSet.start();

        } catch (IllegalStateException e) {
            Log.i(TAG, "revealView has happend IllegalStateException");
        }
        //TS:kaifeng.lu 2016-05-17 Calculator BUGFIX-2157429  MOD_E
    }

    public void cancelCopyPaste() {
        //TS:kaifeng.lu 2015-12-23 Calculator BUGFIX_1202285 MOD_S
        isFormulaLongClicked = false;
        isResultLongClicked = false;
        mFormulaEditText.setPressed(false);
        mResultEditText.setPressed(false);
        //TS:kaifeng.lu 2015-12-23 Calculator BUGFIX_1202285 MOD_E
        Selection.removeSelection(mFormulaEditText.getText());
        Selection.removeSelection(mResultEditText.getText());
        copyPasteState = false;
    }

    private void onDelete() {
// Delete works like backspace; remove the last character from the expression.
        final Editable formulaText = mFormulaEditText.getEditableText();
        // added for PR928452 Can't delete the whole function sign when tapping delete by ting.ma at 2015.02.09
        final int formulaLength = formulaText.length();
        if (formulaLength <= 0) {
            return;
        }
        String formulaStr = formulaText.toString();
        for (String buttonText : buttonTexts) {
            if (formulaStr.endsWith(buttonText)) {
                formulaText.delete(formulaLength - buttonText.length(), formulaLength);
                return;
            }
        }
        formulaText.delete(formulaLength - 1, formulaLength);

//        final int formulaLength = formulaText.length();
        if (formulaLength > 0) {

            // added for PR928452 Can't delete the whole function sign when tapping delete by ting.ma at 2015.02.09 begin
            if (formulaLength >= 3) {
                if (formulaLength >= 4) {
                    String last4Chars = formulaStr.substring(formulaLength - 4);
                    if ("sin(cos(tan(log(".indexOf(last4Chars) != -1) {
                        formulaText.delete(formulaLength - 4, formulaLength);
                        return;
                    }
                }
                String last3Chars = formulaStr.substring(formulaLength - 3);
                if ("ln(".indexOf(last3Chars) != -1) {
                    formulaText.delete(formulaLength - 3, formulaLength);
                    return;
                }
            }
            // added for PR928452 Can't delete the whole function sign when tapping delete by ting.ma at 2015.02.09 end

//            formulaText.delete(formulaLength - 1, formulaLength);
        }
    }

    private void onClear() {
        if (TextUtils.isEmpty(mFormulaEditText.getText())) {
            return;
        }

        reveal(mCurrentButton, R.color.calculator_accent_color, new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mFormulaEditText.getEditableText().clear();
            }
        });
    }


    private History getHistory() {
        Log.i(TAG, "getHistory start " + System.currentTimeMillis()); // MODIFIED by kaifeng.lu, 2016-05-25,BUG-2162730
        cr = getActivity().getContentResolver();
        History history = new History(cr);
        Cursor cursor = null;
        cursor = cr.query(HistoryContentUri.CONTENT_CURRENCY_HISTORY_URI, null, null, null, null);
        if (cursor != null) {

            int historyFrom = cursor.getColumnIndex(HistoryContentUri.CURRENCY_HISTORY_FROM);
            int historyTo = cursor.getColumnIndex(HistoryContentUri.CURRENCY_HISTORY_TO);
            int timestap = cursor.getColumnIndex(HistoryContentUri.CURRENCY_HISTORY_TIME_STAP);
            Vector<HistoryItem> mEntries = new Vector<HistoryItem>();
            if (historyFrom != -1 && historyTo != -1) {
                while (cursor.moveToNext()) {
                    HistoryItem item = new HistoryItem();
                    item.formula = cursor.getString(historyFrom).replaceAll(",", ".");
                    item.result = cursor.getString(historyTo).replaceAll(",", ".");
                    item.timeStap = cursor.getLong(timestap);
                    mEntries.add(item);
                }
                history.clear();
                history.setData(mEntries);
            }
            //TS:kaifeng.lu 2015-11-30 EMAIL BUGFIX_983830 ADD_S
            cursor.close();
            //TS:kaifeng.lu 2015-11-30 EMAIL BUGFIX_983830 ADD_E
        }
        Log.i(TAG, "getHistory end " + System.currentTimeMillis()); // MODIFIED by kaifeng.lu, 2016-05-25,BUG-2162730
        return history;
    }

    @Override
    public void changCalculatorView(float translaionY) {
        ((Calculator) mContext).hidGuideButton(translaionY);
        if (translaionY > 0) {
            mIsShowHistory = true;
        } else {
            mIsShowHistory = false;
        }
    }

    /**
     * Update country unit when changes of wheel view.
     *
     * @param wheel flag of wheel view
     */
    private void updateCountryUnit(ScrollWheelPickerView.WHEEL wheel) {
        if (wheel == ScrollWheelPickerView.WHEEL.LEFT_WHEEL) {
            Currency leftCurrency;
            String orgCountry;
            if (mShowState == CURRENT_DOWN_SHOW.CURRENCY_ORG_DOWN) {
                leftCurrency = getLeftCurrencyValue(mLeftIndex);
                orgCountry = leftCurrency.getCountryUnit();
                mOrgCountry.setText(orgCountry);
                updateCurrencyPicture(true, false);
/* MODIFIED-BEGIN by qiong.liu1, 2017-03-20,BUG-3621966*/
//                updateCurrencyHistoryData(mOrgCountry.getText().toString(), mTargetCountry.getText().toString());
                setShowStateForLocation();
            } else if (mShowState == CURRENT_DOWN_SHOW.CURRENCY_TARGET_DOWN) {
                leftCurrency = getRightCurrencyValue(mRightIndex);
                orgCountry = leftCurrency.getCountryUnit();
                mTargetCountry.setText(orgCountry);
                updateCurrencyPicture(false, true);
//                updateCurrencyHistoryData(mTargetCountry.getText().toString(), mOrgCountry.getText().toString());
/* MODIFIED-END by qiong.liu1,BUG-3621966*/
                setShowStateForLocation();
            }
        } else {
            Currency rightCurrency;
            String targetCountry;
            if (mShowState == CURRENT_DOWN_SHOW.CURRENCY_TARGET_DOWN) {
                rightCurrency = getLeftCurrencyValue(mLeftIndex);
                targetCountry = rightCurrency.getCountryUnit();
                mOrgCountry.setText(targetCountry);
                updateCurrencyPicture(true, false);
/* MODIFIED-BEGIN by qiong.liu1, 2017-03-20,BUG-3621966*/
//                updateCurrencyHistoryData(mTargetCountry.getText().toString(), mOrgCountry.getText().toString());
                setShowStateForLocation();
            } else if (mShowState == CURRENT_DOWN_SHOW.CURRENCY_ORG_DOWN) {
                rightCurrency = getRightCurrencyValue(mRightIndex);
                targetCountry = rightCurrency.getCountryUnit();
                mTargetCountry.setText(targetCountry);
                updateCurrencyPicture(false, true);
//                updateCurrencyHistoryData(mOrgCountry.getText().toString(), mTargetCountry.getText().toString());
                setShowStateForLocation();
            }
        }
        performCurrency(); // MODIFIED by qiong.liu1, 2017-03-27,BUG-3621966
    }

    private void updateCurrencyHistoryData(String from, String to) {
        Log.d(TAG, " from " + from + " to " + to);
        mHistory.CurrencyEnter(from, to);
        mDisplayView.scrollToMostRecent();
        mDisplayView.initializeHistoryAndGraphView(false);
    }

    private void checkHistoryData(String[] backData, ScrollWheelPickerView.WHEEL wheel) {
        if (backData != null && backData.length >= 3 && !TextUtils.isEmpty(backData[1].trim())) {
        /* MODIFIED-END by qiong.liu1,BUG-3621966*/
            if (mShowState == CURRENT_DOWN_SHOW.CURRENCY_ORG_DOWN) {
                changedOrgCountryUnit(backData, wheel);
            } else if (mShowState == CURRENT_DOWN_SHOW.CURRENCY_TARGET_DOWN) {
                changeTargetCountryUnit(backData, wheel);
            }
        }
    }

    private void changeTargetCountryUnit(String[] backData, ScrollWheelPickerView.WHEEL wheel) {
        ArrayList<Currency> changedData;
        if (wheel == ScrollWheelPickerView.WHEEL.LEFT_WHEEL) {
            changedData = mRightData;
        } else {
            changedData = mLeftData;
        }
        for (int index = 0; index < changedData.size(); index++) {
            if (changedData.get(index).getCountryUnit().equals(backData[1].trim())) {
                if (wheel == ScrollWheelPickerView.WHEEL.LEFT_WHEEL) {
                    mRightIndex = index;
                } else {
                    mLeftIndex = index;
                }
                mHasSameData = true;
                return;
            }
        }
        if (!mHasSameData) {
            Currency currency = new Currency(backData[0].trim(), backData[1].trim(), backData[2].trim()); // MODIFIED by qiong.liu1, 2017-03-20,BUG-3621966
            mLeftData.add(0, currency);
            mRightData.add(0, currency);
            if (wheel == ScrollWheelPickerView.WHEEL.LEFT_WHEEL) {
                mRightIndex = 0;
                mLeftIndex = mLeftIndex + 1;
            } else {
                mLeftIndex = 0;
                mRightIndex = mRightIndex + 1;
            }
        }
    }

    private void changedOrgCountryUnit(String[] backData, ScrollWheelPickerView.WHEEL wheel) {
        ArrayList<Currency> changedData;
        if (wheel == ScrollWheelPickerView.WHEEL.LEFT_WHEEL) {
            changedData = mLeftData;
        } else {
            changedData = mRightData;
        }
        for (int index = 0; index < changedData.size(); index++) {
            if (changedData.get(index).getCountryUnit().trim().equals(backData[1].trim())) {
                if (wheel == ScrollWheelPickerView.WHEEL.LEFT_WHEEL) {
                    mLeftIndex = index;
                } else {
                    mRightIndex = index;
                }
                mHasSameData = true;
                return;
            } else {
                mHasSameData = false;
            }
        }
        if (!mHasSameData) {
            Currency currency = new Currency(backData[0].trim(), backData[1].trim(), backData[2].trim()); // MODIFIED by qiong.liu1, 2017-03-20,BUG-3621966
            mLeftData.add(0, currency);
            mRightData.add(0, currency);
            if (wheel == ScrollWheelPickerView.WHEEL.LEFT_WHEEL) {
                mLeftIndex = 0;
                mRightIndex = mRightIndex + 1;
            } else {
                mRightIndex = 0;
                mLeftIndex = mLeftIndex + 1;
            }
        }
    }

    @Override
    public void onClickCallBack(String data) {
        // Showing popwindow according to current view.
        if (mShowState == CURRENT_DOWN_SHOW.CURRENCY_ORG_DOWN && !mIsShowHistory) {
            /* MODIFIED-BEGIN by qiong.liu1, 2017-04-21,BUG-4452809*/
            showPopupWindow(mShowWheelButton, TOP);
        }
        if (mShowState == CURRENT_DOWN_SHOW.CURRENCY_TARGET_DOWN && !mIsShowHistory) {
            showPopupWindow(mTargetShowWheelButton, BOTTOM);
            /* MODIFIED-END by qiong.liu1,BUG-4452809*/
        }

        // Update wheel view data.
        String[] backData = (data != null) ? data.split("-") : null;
        // Changed of left wheel view.
        if (mIsClickLeftMoreButton) {
            checkHistoryData(backData, ScrollWheelPickerView.WHEEL.LEFT_WHEEL);
        } else {
            // Changed of right wheel view.
            checkHistoryData(backData, ScrollWheelPickerView.WHEEL.RIGHT_WHEEL);
        }

        updateWheelData();

        if (mIsClickLeftMoreButton) {
            updateCountryUnit(ScrollWheelPickerView.WHEEL.LEFT_WHEEL);
        } else {
            updateCountryUnit(ScrollWheelPickerView.WHEEL.RIGHT_WHEEL);
        }
    }

    @Override
    public String onGetCurrencySelectDataCallBack() {
        String selectCurrencyData = "";
        if (mShowState == CURRENT_DOWN_SHOW.CURRENCY_ORG_DOWN) {
            if (mTargetCountry != null && mOrgCountry != null) {
                if (!mIsClickLeftMoreButton) {
                    selectCurrencyData = mTargetCountry.getText().toString();
                } else {
                    selectCurrencyData = mOrgCountry.getText().toString();
                }
            } else if (mShowState == CURRENT_DOWN_SHOW.CURRENCY_TARGET_DOWN) {
                if (!mIsClickLeftMoreButton) {
                    selectCurrencyData = mOrgCountry.getText().toString();
                } else {
                    selectCurrencyData = mTargetCountry.getText().toString();
                }
            }
        }
        return selectCurrencyData;
    }

    /**
     * Start XE web site.
     */
    private void startXEWebSite() {
        try {
            Uri uri = Uri.parse(Constant.XE_WEB_SITE);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Log.e(TAG, "Activity not found exception. please check your web.");
            e.printStackTrace();
        }
    }

    @Override
    public boolean onLongClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.currency_clear:
                onClear();
                return true;
            default:
                break;
        }
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (!TextUtils.isEmpty(mFormulaEditText.getText().toString())) {
            outState.putString(TOP_FORMULATE, mFormulaEditText.getText().toString());
        }
        if (!TextUtils.isEmpty(mResultEditText.getText().toString())) {
            outState.putString(TOP_RESULT, mResultEditText.getText().toString());
        }
        outState.putString(LEFT_WHEEL_INDEX, mOrgCountry.getText().toString());
        outState.putString(RIGHT_WHEEL_INDEX, mTargetCountry.getText().toString());

        boolean isShowing;
        if (mCurrencyPopWindow.isShowing()) {
            isShowing = true;
        } else {
            isShowing = false;
        }
//        dismissCurrencyPopWindow(); // MODIFIED by qiong.liu1, 2017-05-02,BUG-4598039
        outState.putBoolean(KEY_SHOWING_POP, isShowing);
        outState.putString(CHARTIME_STATES,mCurrentCharTime);
        super.onSaveInstanceState(outState);
    }

    /**
     * when use press the back button to set the splash view to common
     * @return   wheather finish the app
     */

    public boolean onBackPressed(){
        /* MODIFIED-BEGIN by qiong.liu1, 2017-04-11,BUG-4452809*/
        //history is show,so just collapse
        Log.i(TAG, "CurrencyActivity=onBackPressed=state=" + mDisplayView.getTranslateState());
        if (mDisplayView.getTranslateState() == CurrencyDisplayOverlay.TranslateState.EXPANDED) {
            mDisplayView.collapseHistory();
            mDisplayView.scrollToMostRecent();
            return true;
        }
        /* MODIFIED-END by qiong.liu1,BUG-4452809*/
        if (mCalculatorPadViewPager == null || mCalculatorPadViewPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first pad (or the pad is not paged),
            // allow the system to handle the Back button.
            return false;
        } else {
            // Otherwise, select the previous pad.
            mCalculatorPadViewPager.setCurrentItem(0, true);
            return true;
        }
    }


    /**
     *  when click the button fo line chart change the textview's style and change the base date for line chart
     * @param mCricle
     */

    private void updateCharView(String mCricle){
        mTvOneWeek.setTypeface(Typeface.DEFAULT);
        mTvOneMonth.setTypeface(Typeface.DEFAULT);
        mTvSixMouth.setTypeface(Typeface.DEFAULT);
        mTvOneYear.setTypeface(Typeface.DEFAULT);
        switch (mCricle){
            case ONEWEEK_STATE:
                mTvOneWeek.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                /* MODIFIED-BEGIN by qiong.liu1, 2017-03-23,BUG-3621966*/
                Log.d(TAG, " isRatesChanged " + isRatesChanged );
                if (isRatesChanged && rates7dArray.length != 0){
                    mCurrencyChartView.setData(rates7dArray);
                    mCharViewHight.setText(String.valueOf(Utils.getArrayMax(rates7dArray)));
                    mCharViewLow.setText(String.valueOf(Utils.getArrayMin(rates7dArray)));
                }else {
                    mCharViewHight.setText(String.valueOf(Utils.getArrayMax(DATA7D)));
                    mCharViewLow.setText(String.valueOf(Utils.getArrayMin(DATA7D)));
                    mCurrencyChartView.setData(DATA7D);
                }
                break;
            case ONEMONTH_STATE:
                mTvOneMonth.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                if (isRatesChanged && rates1mArray.length != 0){
                    mCurrencyChartView.setData(rates1mArray);
                    mCharViewLow.setText(String.valueOf(Utils.getArrayMin(rates1mArray)));
                    mCharViewHight.setText(String.valueOf(Utils.getArrayMax(rates1mArray)));
                }else {
                    mCharViewLow.setText(String.valueOf(Utils.getArrayMin(DATA1M)));
                    mCharViewHight.setText(String.valueOf(Utils.getArrayMax(DATA1M)));
                    mCurrencyChartView.setData(DATA1M);
                }
                break;
            case SIXMONTH_STATE:
                mTvSixMouth.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                if (isRatesChanged && rates6mArray.length != 0){
                    mCharViewHight.setText(String.valueOf(Utils.getArrayMax(rates6mArray)));
                    mCharViewLow.setText(String.valueOf(Utils.getArrayMin(rates6mArray)));
                    mCurrencyChartView.setData(rates6mArray);
                }else {
                    mCharViewLow.setText(String.valueOf(Utils.getArrayMin(DATA6M)));
                    mCharViewHight.setText(String.valueOf(Utils.getArrayMax(DATA6M)));
                    mCurrencyChartView.setData(DATA6M);
                }
                break;
            case ONEYEAT_STATE:
                mTvOneYear.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                if (isRatesChanged && rates1yArray.length != 0){
                    mCharViewLow.setText(String.valueOf(Utils.getArrayMin(rates1yArray)));
                    mCharViewHight.setText(String.valueOf(Utils.getArrayMax(rates1yArray)));
                    mCurrencyChartView.setData(rates1yArray);
                }else {
                    mCharViewHight.setText(String.valueOf(Utils.getArrayMax(DATA1Y)));
                    mCharViewLow.setText(String.valueOf(Utils.getArrayMin(DATA1Y)));
                    mCurrencyChartView.setData(DATA1Y);
                }
                /* MODIFIED-END by qiong.liu1,BUG-3621966*/
                break;
            default:
                break;
        }
        mCurrentCharTime = mCricle;
        mCurrencyChartView.invalidate();
    }

    /* MODIFIED-BEGIN by qiong.liu1, 2017-03-23,BUG-3621966*/
    @Override
    public void onDestroy() {
        Log.d(TAG, " Currency onDestroy "); // MODIFIED by qiong.liu1, 2017-03-30,BUG-3621966
        mIsClickPopWindowCancel = true;
        resetCountryData();
        dismissCurrencyPopWindow();
        mContext.unbindService(connection);
        super.onDestroy();
    }
    /* MODIFIED-END by qiong.liu1,BUG-3621966*/
}
