package com.tct.calculator.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color; // MODIFIED by qiong.liu1, 2017-04-21,BUG-4452809
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tct.calculator.Calculator;
import com.tct.calculator.CalculatorEditText;
import com.tct.calculator.CalculatorExpressionBuilder;
import com.tct.calculator.CalculatorExpressionEvaluator;
import com.tct.calculator.CalculatorExpressionTokenizer;
import com.tct.calculator.CalculatorPadViewPager;
import com.tct.calculator.R;
import com.tct.calculator.convert.Convert;
import com.tct.calculator.convert.ConvertSlideAdapter;
import com.tct.calculator.convert.ConvertUtil;
import com.tct.calculator.convert.Unit;
import com.tct.calculator.data.PatternUtil;
import com.tct.calculator.utils.Constant;
import com.tct.calculator.utils.SharedPreferencesHelper;
import com.tct.calculator.utils.Utils;
import com.tct.calculator.view.DisplayOverlay;
import com.tct.calculator.view.history.CommonDisplayOverlay;
import com.tct.calculator.view.history.ConvertHistory;
import com.tct.calculator.view.history.CommonHistoryAdapter;
import com.tct.calculator.view.history.ConvertHistoryItem;
import com.tct.calculator.view.interfaces.CalculatorCallBacks; // MODIFIED by qiong.liu1, 2017-04-11,BUG-4452809
import com.tct.calculator.view.interfaces.ChangeViewInParentCallBack;
import com.tct.calculator.view.scrollwheel.CalculatorScrollWheelPicker;
import com.tct.calculator.view.scrollwheel.OnScrollWheelClickedListener;
import com.tct.calculator.view.scrollwheel.OnWheelScrollListener;
import com.tct.calculator.view.scrollwheel.ScrollWheelPickerView;
import com.tct.calculator.view.scrollwheel.WheelView;

import org.javia.arity.SyntaxException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Created by user on 16-9-28.
 */
/* MODIFIED-BEGIN by qiong.liu1, 2017-04-11,BUG-4452809*/
public class ConverterFragment extends Fragment implements
        CommonHistoryAdapter.HistoryItemSelectedInterface, View.OnClickListener, CalculatorCallBacks,
        CalculatorExpressionEvaluator.EvaluateCallback, CalculatorEditText.OnTextSizeChangeListener,
        View.OnLongClickListener, ChangeViewInParentCallBack, CommonDisplayOverlay.HideWheelListener {
        /* MODIFIED-END by qiong.liu1,BUG-4452809*/

    private static final String TOP_RESULT_TEXT_KEY = "top_result_key";
    private static final String BOTTOM_RESULT_TEXT_KEY = "bottom_result_key";
    private static final String POPWINDOW_SHOW_STATUS = "popwindow_show_status";
    private CommonDisplayOverlay mDisplayView;
    private CalculatorEditText mCommonTopFormulaEditText;
    private CalculatorEditText mCommonTopResultEditText;
    private CalculatorEditText mCommonBottomResultEditText;
    private Button mCommonTopFormulaCopyBtn;
    private Button mCommonTopFormulaPasteBtn;
    private Button mCommonTopResultCopyBtn;
    private Button mCommonBottomResultCopyBtn;
    private ContentResolver mContentResolver;
    private Activity mContext;
    private CommonHistoryAdapter mHistoryAdapter;
    private static final String TAG = "ConverterFragment";
    private static boolean needExpand = false;
    private boolean mIsStanderPoint = true;
    private ConvertHistory mConvertHistory;
    private View mConvertBottomItemLayout;
    private RelativeLayout mConvertTopItemLayout;
    private View mWheelContainer;
    private Convert mCurrentConvert;
    private CalculatorScrollWheelPicker mCalculatorWheelPicker;
    private TextView mConvertTopFullname, mConvertTopAbbreviation;
    private TextView mConvertBottomFullname, mConvertBottomAbbreviation;
    private CalculatorPadViewPager mCalculatorPadViewPager;
    private Button mWheelPickerConfirm, mWheelPickerCancel;
    private View mCommonContainer;
    private CalculatorExpressionTokenizer mTokenizer;
    private CalculatorExpressionEvaluator mEvaluator;
    private static final String NAME = ConverterFragment.class.getName();
    private static final String KEY_CURRENT_EXPRESSION = NAME + "_currentExpression";
    private View mDeleteButton, mClearButton, mEqualButton;
    private static final String KEY_DISPLAY_MODE = NAME + "_displayMode";
    private int mCurrentViewPager = 0, mCurrentTranslate = 0, mCurrentEvaMode = 0;
    private Button tipsBtn;
    private final String SHOW_BY_LOCKED = "showByLocked";
    private String JUMP_TO_CALCULATOR = "jumpToCalculator";
    private final String IS_SERCURE = "IsSecure";
    private Display mDisplay;
    private int mCreatCount = 0;
    private float mMaxScreemHeight = 0;
    private Button mPoint;
    private SetHistoryCopyStatue mSetHistoryCopyStatue;
    private Constant.ConvertType mCurrnetConvertType;
    private ConvertUtil mConvertUtil;
    private SharedPreferencesHelper mSharedPreferencesHelper;
    private GridView mConvertGrid;
    private List<Convert> mConverts;
    private ConvertSlideAdapter mConvertSlideAdapter;
    private int mRecordConvertIndex = 0;
    private HandlerThread mConvertThread;
    private boolean isReversed = false;
    private float mTopContainerY;
    private float mBottomContainerY;
    private int mTopContainerHeight;
    private int mBottomContainerHeight;
    private RelativeLayout mCommonTopContainer;
    private RelativeLayout mCommonBottomContainer;
    private ImageView mExchangeBtn;
    private RelativeLayout mSwitchLayoutAnim;
    private RelativeLayout mSwitchLayoutTopAnim;
    private CalculatorEditText mCommonTopFormulaAnim;
    private TextView mConvertTopFullnameAnim;
    private TextView mConvertTopAbbreviationAnim;
    private RelativeLayout mSwitchLayoutBottomAnim;
    private CalculatorEditText mCommonBottomResultAnim;
    private TextView mConvertBottomFullnameAnim;
    private TextView mConvertBottomAbbreviationAnim;
    private RelativeLayout mSwitchLayoutDividerAnim;
    private LinearLayout mConvertBottomItemParentAnim;
    private LinearLayout mConvertTopItemParentAnim;
    private float mTopFrom, mTopTo, mBottomFrom, mBottomTo;
    private int ANIM_TIME = 300;
    private boolean mShowAnimFromResult = false;
    private String EXCHANGE_DEFAULT_VALUE = "100";
    private View mCommonTopBottomDivider;
    private boolean mIsConverterShowHistory;
    private static Calculator mActivity; // MODIFIED by qiong.liu1, 2017-04-11,BUG-4452809

    /**
     * handle the logic of PopWindow for convertFragment
     */
    private ImageView mArrowView;
    private View mConvertTopLayout;
    private PopupWindow mConvertPopWindow;
    private int CONVERT_POPWINDOW_WIDTH;
    private int mCurrentTopIndex;
    private int mCurrentButtonIndex;
    private WheelView mWheelView;

    private int mScreenState;
    private final static int SCREENPORTFULL = 0x0001;
    private final static int SCREENPORTQUTERTHREE = 0x0002;
    private final static int SCREENPORTHALF = 0x0003;
    private final static int SCREENPORTQUTER = 0x0004;
    private final static int SCREENLANDFULL = 0x0005;
    private final static int SCREENLANDHALF = 0x0006;


    private Button mBtn0, mBtn1, mBtn2, mBtn3, mBtn4, mBtn5, mBtn6, mBtn7, mBtn8, mBtn9;
    private View mBtndiv, mBtndel, mBtnmul, mBtnclear, mBtnsub, mBtnexpend, mBtnpoint, mBtnadd, mBtneq, mBtnClear2;


    @Override
    /* MODIFIED-BEGIN by qiong.liu1, 2017-04-11,BUG-4452809*/
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (Calculator) activity;
        mActivity.setCalculatorCallBacks(this, mActivity.CONVERTERSTATUS); // MODIFIED by qiong.liu1, 2017-04-13,BUG-4452809
    }

    @Override
    /* MODIFIED-END by qiong.liu1,BUG-4452809*/
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this.getActivity();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        mContext = this.getActivity();
//        initView();
//        initStatus();
    }

    private void initView(View view) {
        mCommonTopContainer = (RelativeLayout) view.findViewById(R.id.common_top_ll_formula_result_container);
        mCommonBottomContainer = (RelativeLayout) view.findViewById(R.id.common_bottom_ll_formula_result_container);
        mDisplayView = (CommonDisplayOverlay) view.findViewById(R.id.common_display);
        mDisplayView.setChangeViewCallBack(this);
        mCommonTopFormulaEditText = (CalculatorEditText) view.findViewById(R.id.common_top_formula);
        mCommonTopFormulaEditText.setTextColor(getResources().getColor(R.color.display_formula_hint_text_color));
        mCommonTopResultEditText = (CalculatorEditText) view.findViewById(R.id.common_top_result);
        mCommonBottomResultEditText = (CalculatorEditText) view.findViewById(R.id.common_bottom_result);
        mCommonTopFormulaCopyBtn = (Button) view.findViewById(R.id.common_top_formula_copy_btn);
        mCommonTopFormulaPasteBtn = (Button) view.findViewById(R.id.common_top_formula_paste_btn);
        mCommonTopResultCopyBtn = (Button) view.findViewById(R.id.common_top_result_copy_btn);
        mCommonBottomResultCopyBtn = (Button) view.findViewById(R.id.common_bottom_result_copy_btn);
        mCommonContainer = view.findViewById(R.id.common_container);
        mExchangeBtn = (ImageView) view.findViewById(R.id.exchange);
        mConvertBottomItemLayout = view.findViewById(R.id.convert_bottom_item_layout);
        mConvertTopItemLayout = (RelativeLayout) view.findViewById(R.id.convert_top_item_layout);
        mArrowView = (ImageView) view.findViewById(R.id.arrow_covert);
        mConvertTopLayout = view.findViewById(R.id.convert_top_item_parent);

        View contentView = LayoutInflater.from(this.getActivity()).inflate(R.layout.convery_wheel_container, null);
        mWheelContainer = contentView.findViewById(R.id.wheel_container);
        /* MODIFIED-BEGIN by qiong.liu1, 2017-04-13,BUG-4452809*/
        if (Calculator.mScreenState == Constant.SCREEN_PORT_FULL) {
            CONVERT_POPWINDOW_WIDTH = getActivity().getResources().getDimensionPixelSize(R.dimen.convert_popupWindow_layout_height);
        }else {
            CONVERT_POPWINDOW_WIDTH = getActivity().getResources().getDimensionPixelSize(R.dimen.convert_popupWindow_layout_split_height);
        }
        /* MODIFIED-END by qiong.liu1,BUG-4452809*/
        mConvertPopWindow = new PopupWindow(contentView, LinearLayout.LayoutParams.MATCH_PARENT,  CONVERT_POPWINDOW_WIDTH, true);
        mConvertPopWindow.setContentView(contentView);
        mConvertPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                mArrowView.setVisibility(View.VISIBLE);
                requestCursor();
                /* MODIFIED-BEGIN by qiong.liu1, 2017-04-21,BUG-4452809*/
                if (Calculator.mScreenState != Constant.SCREEN_PORT_FULL && Calculator.mScreenState != Calculator.SCREENLANDFULL) {
                    mCommonContainer.setPadding(150, 0, 0, 0);
                    mActivity.setShowExpandLayout(false);
                }
                mCommonTopContainer.setBackgroundColor(Color.WHITE);
                /* MODIFIED-END by qiong.liu1,BUG-4452809*/
            }
        });
        mWheelPickerConfirm = (Button) contentView.findViewById(R.id.wheel_picker_confirm);
        mWheelPickerCancel = (Button) contentView.findViewById(R.id.wheel_picker_cancel);

//        mConvertTopItemLayout.setFocusable(true);//TODO ts
//        mConvertTopItemLayout.setFocusableInTouchMode(true);
//        mConvertTopItemLayout.setClickable(true);
//        mConvertTopLayout.setOnClickListener(this);
//        view.findViewById(R.id.arrow_covert).setOnClickListener(this);
        mConvertTopFullname = (TextView) view.findViewById(R.id.convert_top_fullname);
//        mConvertTopFullname.setOnClickListener(this);
        mConvertTopAbbreviation = (TextView) view.findViewById(R.id.convert_top_abbreviation);
//        mConvertTopAbbreviation.setOnClickListener(this);
        mConvertBottomFullname = (TextView) view.findViewById(R.id.convert_bottom_fullname);
//        mConvertBottomFullname.setOnClickListener(this);
        mConvertBottomAbbreviation = (TextView) view.findViewById(R.id.convert_bottom_abbreviation);
//        mConvertBottomAbbreviation.setOnClickListener(this);
        mConvertTopItemLayout.setOnClickListener(this);
        mCommonBottomResultEditText.setOnClickListener(this);
        mWheelPickerConfirm.setOnClickListener(this);
        mWheelPickerCancel.setOnClickListener(this);
        mClearButton = view.findViewById(R.id.clr_convert);
        mDeleteButton = view.findViewById(R.id.del_convert);
        mEqualButton = view.findViewById(R.id.eq_convert);
        if (mEqualButton == null || mEqualButton.getVisibility() != View.VISIBLE) {
            mEqualButton = view.findViewById(R.id.eq_convert);
        }

        mCommonTopBottomDivider = (View) view.findViewById(R.id.common_top_bottom_divider);
        mSwitchLayoutDividerAnim = (RelativeLayout) view.findViewById(R.id.switch_layout_divider_anim);
        mSwitchLayoutAnim = (RelativeLayout) view.findViewById(R.id.switch_layout_anim);
        mSwitchLayoutTopAnim = (RelativeLayout) view.findViewById(R.id.switch_layout_top_anim);
        mCommonTopFormulaAnim = (CalculatorEditText) view.findViewById(R.id.common_top_formula_anim);
        mConvertTopFullnameAnim = (TextView) view.findViewById(R.id.convert_top_fullname_anim);
        mConvertTopAbbreviationAnim = (TextView) view.findViewById(R.id.convert_top_abbreviation_anim);
        mSwitchLayoutBottomAnim = (RelativeLayout) view.findViewById(R.id.switch_layout_bottom_anim);
        mCommonBottomResultAnim = (CalculatorEditText) view.findViewById(R.id.common_bottom_result_anim);
        mConvertBottomFullnameAnim = (TextView) view.findViewById(R.id.convert_bottom_fullname_anim);
        mConvertBottomAbbreviationAnim = (TextView) view.findViewById(R.id.convert_bottom_abbreviation_anim);

        mConvertBottomItemParentAnim = (LinearLayout) view.findViewById(R.id.convert_bottom_item_parent_anim);
        mConvertTopItemParentAnim = (LinearLayout) view.findViewById(R.id.convert_top_item_parent_anim);

        mBtn0 = (Button) view.findViewById(R.id.digit_0_convert);
        mBtn1 = (Button) view.findViewById(R.id.digit_1_convert);
        mBtn2 = (Button) view.findViewById(R.id.digit_2_convert);
        mBtn3 = (Button) view.findViewById(R.id.digit_3_convert);
        mBtn4 = (Button) view.findViewById(R.id.digit_4_convert);
        mBtn5 = (Button) view.findViewById(R.id.digit_5_convert);
        mBtn6 = (Button) view.findViewById(R.id.digit_6_convert);
        mBtn7 = (Button) view.findViewById(R.id.digit_7_convert);
        mBtn8 = (Button) view.findViewById(R.id.digit_8_convert);
        mBtn9 = (Button) view.findViewById(R.id.digit_9_convert);

        mBtndiv = view.findViewById(R.id.op_div_convert);
        mBtnsub = view.findViewById(R.id.op_sub_convert);
        mBtnmul = view.findViewById(R.id.op_mul_convert);
        mBtnpoint = view.findViewById(R.id.dec_point_convert);
        mBtnadd = view.findViewById(R.id.op_add_convert);
        mBtneq = view.findViewById(R.id.eq_convert);
//        mBtndel = view.findViewById(R.id.del_convert);

        mDeleteButton.setOnClickListener(this);
        mDeleteButton.setOnLongClickListener(this);
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
//        mBtndel.setOnClickListener(this);
//        mBtndel.setOnLongClickListener(this);
        view.findViewById(R.id.exchange).setOnClickListener(this);
        view.findViewById(R.id.slide_menu).setOnClickListener(this);
    }

    /* MODIFIED-BEGIN by qiong.liu1, 2017-04-21,BUG-4452809*/
    @Override
    public void onMultiWindowModeChanged(boolean isInMultiWindowMode) {
        super.onMultiWindowModeChanged(isInMultiWindowMode);
    }
    /* MODIFIED-END by qiong.liu1,BUG-4452809*/

    private void initStatus(View view) {
        initDatas(view);
        initWheel(view);
        initSlidePanel(view);
        //================
        long start = System.currentTimeMillis();
        mContentResolver = mContext.getContentResolver();
        mSharedPreferencesHelper = SharedPreferencesHelper.getInstance(mContext);
        mConvertThread = new HandlerThread("Convert");
        mConvertThread.start();
        mConvertHistory = new ConvertHistory(mContentResolver);
        mConvertHistory.initConvertHistoryThread(mConvertThread);
        clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        tipsBtn = (Button) view.findViewById(R.id.tips_btn);
        mPoint = (Button) view.findViewById(R.id.dec_point_convert);
        if (mPoint.getText().equals(",")) {
            mIsStanderPoint = false;
        }
        mDisplayView.setHideWheelListener(this);
        //TODO handle savedInstanceState
//        if (mPadViewPager != null && savedInstanceState != null) {
//            mCurrentViewPager = savedInstanceState.getInt(KEY_CURRENT_VIEWPAGER, 0);
//            mPadViewPager.setCurrentItem(mCurrentViewPager);
//        } else if (savedInstanceState != null) {
//            mCurrentViewPager = savedInstanceState.getInt(KEY_CURRENT_VIEWPAGER, 0);
//        }
        mTokenizer = new CalculatorExpressionTokenizer(mContext);
        mEvaluator = new CalculatorExpressionEvaluator(mTokenizer);
        //TODO handle savedInstanceState
//        if (savedInstanceState != null) {
//            mCommonTopFormulaEditText.setText(mTokenizer.build2Local(
//                    savedInstanceState.getString(KEY_CURRENT_EXPRESSION, "")));
//        }
        mEvaluator.evaluate(mCommonTopFormulaEditText.getText().toString().replaceAll(" ", "").replaceAll(",", "."), this);
        mCommonTopFormulaEditText.setEditableFactory(mFormulaEditableFactory);
        mCommonTopFormulaEditText.addTextChangedListener(mFormulaTextWatcher);
        mCommonTopFormulaEditText.setOnKeyListener(mFormulaOnKeyListener);
        mCommonTopFormulaEditText.setOnTextSizeChangeListener(this);
        mCommonTopFormulaEditText.setOnLongClickListener(this);
        mCommonTopResultEditText.setOnLongClickListener(this);
        mCommonTopFormulaEditText.setOnClickListener(this);
        mCommonTopFormulaEditText.setCursorVisible(true);
        mDeleteButton.setOnLongClickListener(this);
        //=======================
//        mDisplayView.setWheelView(mWheelContainer);
        mDisplayView.bringToFront();
//        mConvertTopItemLayout.bringToFront();//TODO ts
//        mConvertBottomItemLayout.bringToFront();

        CommonDisplayOverlay.DisplayMode displayMode = CommonDisplayOverlay.DisplayMode.FORMULA;
        //TODO handle savedInstanceState
//        if (savedInstanceState != null) {
//            final int modeOrdinal = savedInstanceState.getInt(KEY_DISPLAY_MODE, -1);
//            if (modeOrdinal != -1) {
//                displayMode = CommonDisplayOverlay.DisplayMode.values()[modeOrdinal];
//            }
//        }

        mDisplayView.setMode(displayMode);
        mDisplayView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        if (mDisplayView.getHeight() > 0) {
                            mDisplayView.initializeHistoryAndGraphView(true);
                            if (mDisplayView.getMode() == CommonDisplayOverlay.DisplayMode.GRAPH) {
                                //  mGraphController.startGraph(mCommonTopFormulaEditText.getText());
                            }
                        }
                    }
                });
        mDisplayView.setTranslateStateListener(new CommonDisplayOverlay.TranslateStateListener() {
            @Override
            public void onTranslateStateChanged(CommonDisplayOverlay.TranslateState newState) {

                cancelCopyPaste();
            }
        });
        if (mCalculatorPadViewPager != null) {
            ((CalculatorPadViewPager) mCalculatorPadViewPager).setOnTouchUpFirstPagerOnBackgroundListener(new CalculatorPadViewPager.onTouchUpFirstPagerOnBackgroundListener() {

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
                /* MODIFIED-END by qiong.liu1,BUG-4452809*/
            });
        }

//        if (savedInstanceState != null) {//TODO ts

//            mCurrentTranslate = savedInstanceState.getInt(KEY_CURRENT_TRANSLATE_STATE, 1);
//            mCurrentEvaMode = savedInstanceState.getInt(KEY_CURRENT_EVALUATOR_MODE, 0);
//            if (invBtn != null) {
//                invBtn.setSelected(inv);
//            }
//            if (mCurrentTranslate == 0) {
//                needExpand = true;
//            }
//            if (mCurrentEvaMode == 1) {
//                mEvaluator.setEvaluatorMode(CalculatorExpressionEvaluator.EvaluatorMode.RADIUS);
//            }
//            inverseFunctions(inv);
//            isFormulaLongClicked = savedInstanceState.getBoolean(FORMULA_CLICKED_STATE);
//            isResultLongClicked = savedInstanceState.getBoolean(RESULT_CLICKED_STATE);
//        }
        Log.i(TAG, "onCreate end" + System.currentTimeMillis());
        mCommonTopFormulaEditText.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View view, DragEvent dragEvent) {
                if (dragEvent.getAction() == DragEvent.ACTION_DROP) {

                    if (!TextUtils.isEmpty(dragEvent.getClipData().getItemAt(0).getText())) {

                        mCommonTopFormulaEditText.setText(getCommaString(dragEvent.getClipData().getItemAt(0).getText().toString()));
                    }
                }
                return true;
            }
        });
        initConvertRecord();
        mConvertGrid.postDelayed(new Runnable() {
            @Override
            public void run() {
                mConvertGrid.setFocusable(true);
                mConvertGrid.setClickable(true);
                mConvertGrid.setSelected(true);
                mConvertGrid.requestFocus();
                mConvertGrid.requestFocusFromTouch();
                mConvertGrid.setItemChecked(mRecordConvertIndex, true);
                mConvertGrid.setSelection(mRecordConvertIndex);
            }
        }, 200);
        long end = System.currentTimeMillis();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mTopContainerY = mCommonTopContainer.getY();
                mTopContainerHeight = mCommonTopContainer.getHeight();
                mBottomContainerHeight = mCommonBottomContainer.getHeight();
                mBottomContainerY = mCommonBottomContainer.getY();
                resetPadViewPagerLayout();
            }
        }, 500);
        computeAnimHeight();
    }

    private void initWheel(View view) {
        mConvertUtil = ConvertUtil.getsConvertUtil();
        mCurrentConvert = mConverts.get(0);
        List<Unit> leftDatas = mCurrentConvert.getUnit();
        List<Unit> rightDatas = mCurrentConvert.getUnit();
//        mWheelContainer = view.findViewById(R.id.wheel_container);
//        View wheel = LayoutInflater.from(this).inflate(R.layout.calculator_wheel_layout, null);
//        mWheelContainer.addView(wheel);
//        topPanel = (Panel) findViewById(R.id.topPanel);
        mCalculatorWheelPicker = new CalculatorScrollWheelPicker(mContext, mWheelContainer, callbackChanged, callbackFinished, onScrollWheelClickedListener, 0, 0, leftDatas, rightDatas);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = null;
        if (Calculator.mScreenState == SCREENPORTFULL) {
            view = inflater.inflate(R.layout.fragment_convert, container, false);
        } else if (Calculator.mScreenState == SCREENPORTQUTERTHREE) {
            view = inflater.inflate(R.layout.fragment_convert_half, container, false);
        } else if (Calculator.mScreenState == SCREENPORTHALF || mScreenState == SCREENPORTQUTER) {
            view = inflater.inflate(R.layout.fragment_convert_half, container, false);
        } else if (Calculator.mScreenState == SCREENLANDFULL) {
            view = inflater.inflate(R.layout.fragment_convert, container, false);
        } else if (Calculator.mScreenState == SCREENLANDHALF) {
            view = inflater.inflate(R.layout.fragment_convert_half, container, false);
        }
        Log.d(TAG, " Converter onCreateView " + Calculator.mScreenState); // MODIFIED by qiong.liu1, 2017-03-30,BUG-3621966
        initView(view);
        initStatus(view);
        resetText(savedInstanceState);
        return view;
    }

    private void resetText(Bundle savedInstanceState) {
        Log.d(TAG, " Converter savedInstanceState " + savedInstanceState); // MODIFIED by qiong.liu1, 2017-05-02,BUG-4598039
        if (savedInstanceState != null) {
            mCommonTopFormulaEditText.setText(savedInstanceState.getString(KEY_CURRENT_EXPRESSION));
            mCommonTopResultEditText.setText(savedInstanceState.getString(TOP_RESULT_TEXT_KEY));
            mCommonBottomResultEditText.setText(savedInstanceState.getString(BOTTOM_RESULT_TEXT_KEY));
            /**
             *   record whether the mConvertPopWindow is showing when screen size change
             */
            boolean popWindowShow = savedInstanceState.getBoolean(POPWINDOW_SHOW_STATUS);
             if(popWindowShow){
                 Message msg = mUIHandler.obtainMessage();
                 msg.what = CONVERTER_SHOW_POPWINDOW;
                 mUIHandler.sendMessageDelayed(msg, 500);
             }
        }
        if (mSharedPreferencesHelper != null) {
            /* MODIFIED-BEGIN by qiong.liu1, 2017-04-01,BUG-3621966*/
            int left = 1;
            int right = 1;
            String convertRecord = mSharedPreferencesHelper.getConvertOperations(SharedPreferencesHelper.KEY_CONVERT_UNIT);
            if (!convertRecord.equals("")) {
                String[] arr = convertRecord.split("_");
                if (arr.length == 3) {
                    left = Integer.parseInt(arr[1]);
                    right = Integer.parseInt(arr[2]);
                }
                /* MODIFIED-END by qiong.liu1,BUG-3621966*/
                Unit topUnit = mCurrentConvert.getUnit().get(left);
                Unit bottomUnit = mCurrentConvert.getUnit().get(right);
                mConvertTopFullname.setText(topUnit.getFullName());
                mConvertTopAbbreviation.setText(topUnit.getAbbreviation());
                mConvertBottomFullname.setText(bottomUnit.getFullName());
                mConvertBottomAbbreviation.setText(bottomUnit.getAbbreviation());
                mCalculatorWheelPicker.updateWheel(left, right, mCurrentConvert.getUnit(), mCurrentConvert.getUnit());
            }
        }

    }


    private void initSlidePanel(View view) {
        mConvertGrid = (GridView) view.findViewById(R.id.convert_slide_unit_layout);
        mConvertSlideAdapter = new ConvertSlideAdapter(this.getActivity(), mConverts);
        mConvertGrid.setAdapter(mConvertSlideAdapter);
        mConvertGrid.setSelector(R.drawable.convert_gridview_selector);
        mConvertGrid.setOnItemClickListener(mConvertItemClickedListener);

        mConvertGrid.setOnItemSelectedListener(mConvertItemSelectedListener);
        mCalculatorPadViewPager = (CalculatorPadViewPager) view.findViewById(R.id.pad_pager_convert);
        mCalculatorPadViewPager.setCalculatorType(Constant.CalculatorType.CONVERTER);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, " Converter onDestoy "); // MODIFIED by qiong.liu1, 2017-03-30,BUG-3621966
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void onUserInteraction() {

        // If there's an animation in progress, cancel it so the user interaction can be handled
        // immediately.
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }
    }

    public boolean onBackPressed() {
        //history is show,so just collapse
        Log.i(TAG, "ConvertActivity=onBackPressed=state=" + mDisplayView.getTranslateState());
        if (mDisplayView.getTranslateState() == CommonDisplayOverlay.TranslateState.EXPANDED) {
            mDisplayView.collapseHistory();
            mDisplayView.scrollToMostRecent();
            return true;
        }
        //longclick result ,so just hide
        if (copyPasteState) {
            cancelCopyPaste();
            return true;
        }
        if (mWheelContainer != null && mWheelContainer.isShown()) {
//            mWheelContainer.setVisibility(View.GONE);
            return true;
        }
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

    @Override
    public void onResume() {
        super.onResume();
        long start = System.currentTimeMillis();
        queryHistory();
        mHistoryAdapter = new CommonHistoryAdapter(mContext, mConvertHistory
                , new CommonHistoryAdapter.HistoryHeadCallBack() {
            @Override
            public void onHistoryHeadSelected() {

                mDisplayView.collapseHistory();
                (new Handler()).postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        mConvertHistory.deleteConvertHistory();
                        ((CommonHistoryAdapter) mDisplayView.getHistoryView().getAdapter()).clear();
                        mConvertHistory.clear();
                        ((CommonHistoryAdapter) mDisplayView.getHistoryView().getAdapter()).notifyDataSetChanged();
                        mDisplayView.setMaxTranlation(-1);
                        mDisplayView.initializeHistoryAndGraphView(false);
                    }

                }, 300);

            }
        });

        mSetHistoryCopyStatue = new SetHistoryCopyStatue();
        mHistoryAdapter.setmHistoruCallback(mSetHistoryCopyStatue);
        mConvertHistory.setObserver(mHistoryAdapter);
        mDisplayView.getHistoryView().setAdapter(mHistoryAdapter);
        mHistoryAdapter.setCommonDisplayOverlay(mDisplayView);
        mHistoryAdapter.setHistoryItemSelected(this);
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
        try {
            if (mDisplayView.getHistoryView().getAdapter().getItemCount() == 1) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mDisplayView.getTranslateState() != CommonDisplayOverlay.TranslateState.COLLAPSED)
                            mDisplayView.collapseHistory();
                    }
                }, 1000);
            }
        } catch (NullPointerException e) {
            Log.d("Calcultor", "NullPointerException to close history");
        }


        if (isFormulaLongClicked) {
            copyPasteState = true;
            mCommonTopFormulaEditText.setPressed(true);
            Selection.selectAll(mCommonTopFormulaEditText.getText());
        }
        if (isResultLongClicked) {
            copyPasteState = true;
            mCommonTopResultEditText.setPressed(true);
            Selection.selectAll(mCommonTopResultEditText.getText());
        }

        if (!TextUtils.isEmpty(mCommonTopFormulaEditText.getText())) {
            String currentStr;
            if (mIsStanderPoint) {
                currentStr = mCommonTopFormulaEditText.getText().toString().replaceAll(",", ".");
            } else {
                currentStr = mCommonTopFormulaEditText.getText().toString().replaceAll("\\.", ",");
            }
            mCommonTopFormulaEditText.setText(currentStr);
        }
        if (!TextUtils.isEmpty(mCommonTopFormulaEditText.getText())) {
            mEvaluator.evaluate(mCommonTopFormulaEditText.getText().toString().replaceAll(" ", "").replaceAll(",", "."), ConverterFragment.this);
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mConvertSlideAdapter.setTotalHeight(mCalculatorPadViewPager.getHeight());
                mConvertSlideAdapter.notifyDataSetChanged();
            }
        }, 200);
        Log.i(TAG, "onResume end " + System.currentTimeMillis());
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

    public static final int INVALID_RES_ID = -1;

    @Override
    public void onEvaluate(String expr, String result, int errorResourceId) {
        Log.i(TAG, "onEvaluate start" + System.currentTimeMillis());
        Log.i(TAG, "---onEvaluate---");
        Log.i(TAG, "onEvaluate start" + System.currentTimeMillis() + "evaluateCount = " + result);
        Log.i(TAG, "onEvaluate=expr=" + expr + ",result=" + result + ",errorResourceId=" + errorResourceId);
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
        String standString = getCommaString(expr.replaceAll(",", "."));
        if (mCurrentState == Calculator.CalculatorState.INPUT) {
            mCommonTopResultEditText.setText(commaResult);
            if(!isNumeric(standString)&&TextUtils.isEmpty(commaResult)){
                mCommonBottomResultEditText.setText(commaResult);
            }
        } else if (errorResourceId != INVALID_RES_ID) {
            onError(errorResourceId);
        } else if (!TextUtils.isEmpty(result)) {
            if (mConvertHistory != null) {
                mConvertHistory.insertConvertHistory(mCurrentConvert);
            }
            mDisplayView.scrollToMostRecent();
            onResult(result);
            mDisplayView.initializeHistoryAndGraphView(false);
        } else if (mCurrentState == Calculator.CalculatorState.EVALUATE) {
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

        performConvert();

    }

    private void onResult(final String result) {
        final String commaResult = getCommaString(result);
        final float resultScale =
                mCommonTopFormulaEditText.getVariableTextSize(commaResult) / mCommonTopResultEditText.getTextSize();
        final float resultTranslationX = (1.0f - resultScale) *
                (mCommonTopResultEditText.getWidth() / 2.0f - mCommonTopResultEditText.getPaddingEnd());
        final float resultTranslationY = (1.0f - resultScale) *
                (mCommonTopResultEditText.getHeight() / 2.0f - mCommonTopResultEditText.getPaddingBottom()) +
                (mCommonTopFormulaEditText.getBottom() - mCommonTopResultEditText.getBottom()) +
                (mCommonTopResultEditText.getPaddingBottom() - mCommonTopFormulaEditText.getPaddingBottom());
        final float formulaTranslationY = -mCommonTopFormulaEditText.getBottom();

        // Use a value animator to fade to the final text color over the course of the animation.
        final int resultTextColor = mCommonTopResultEditText.getCurrentTextColor();
        final int formulaTextColor = mCommonTopFormulaEditText.getCurrentTextColor();
        final ValueAnimator textColorAnimator =
                ValueAnimator.ofObject(new ArgbEvaluator(), resultTextColor, formulaTextColor);
        textColorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                //mCommonTopResultEditText.setTextColor((int) valueAnimator.getAnimatedValue());
                mCommonTopResultEditText.setTextColor(resultTextColor);

            }
        });

        final AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(
                textColorAnimator,
                ObjectAnimator.ofFloat(mCommonTopResultEditText, View.SCALE_X, resultScale),
                ObjectAnimator.ofFloat(mCommonTopResultEditText, View.SCALE_Y, resultScale),
                ObjectAnimator.ofFloat(mCommonTopResultEditText, View.TRANSLATION_X, resultTranslationX),
                ObjectAnimator.ofFloat(mCommonTopResultEditText, View.TRANSLATION_Y, resultTranslationY),
                ObjectAnimator.ofFloat(mCommonTopFormulaEditText, View.TRANSLATION_Y, formulaTranslationY));
        animatorSet.setDuration(getResources().getInteger(android.R.integer.config_longAnimTime));
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (mIsStanderPoint) {
                    mCommonTopResultEditText.setText(commaResult.replaceAll(",", "."));
                } else {
                    if (TextUtils.isEmpty(commaResult)) {
                        mCommonTopResultEditText.setText(commaResult);
                    } else {
                        mCommonTopResultEditText.setText(commaResult.replaceAll("\\.", ","));
                    }
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                // Reset all of the values modified during the animation.
                mCommonTopResultEditText.setTextColor(resultTextColor);
                mCommonTopResultEditText.setScaleX(1.0f);
                mCommonTopResultEditText.setScaleY(1.0f);
                mCommonTopResultEditText.setTranslationX(0.0f);
                mCommonTopResultEditText.setTranslationY(0.0f);
                mCommonTopFormulaEditText.setTranslationY(0.0f);
                // Finally update the formula to use the current result.
                if (mIsStanderPoint) {
                    mCommonTopFormulaEditText.setText(commaResult.replaceAll(",", "."));
                } else {
                    if (TextUtils.isEmpty(commaResult)) {
                        mCommonTopFormulaEditText.setText(commaResult);
                    } else {
                        mCommonTopFormulaEditText.setText(commaResult.replaceAll("\\.", ","));
                    }
                }
                setState(Calculator.CalculatorState.RESULT);
                mCurrentAnimator = null;
            }
        });
        mCurrentAnimator = animatorSet;
        animatorSet.start();
    }

    private void onError(final int errorResourceId) {
        if (mCurrentState != Calculator.CalculatorState.EVALUATE) {
            // Only animate error on evaluate.
            mCommonTopResultEditText.setText(errorResourceId);
            return;
        }

        reveal(mCurrentButton, R.color.calculator_error_color, new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                setState(Calculator.CalculatorState.ERROR);
                mCommonTopResultEditText.setText(errorResourceId);
            }
        });
    }

    private Animator mCurrentAnimator;
    private Calculator.CalculatorState mCurrentState;
    private final Editable.Factory mFormulaEditableFactory = new Editable.Factory() {
        @Override
        public Editable newEditable(CharSequence source) {
            final boolean isEdited = mCurrentState == Calculator.CalculatorState.INPUT
                    || mCurrentState == Calculator.CalculatorState.ERROR;
            return new CalculatorExpressionBuilder(source, mTokenizer, isEdited);
        }
    };
    private final TextWatcher mFormulaTextWatcher = new TextWatcher() {
        private String origin;

        @Override
        public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            origin = charSequence.toString();
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            if (charSequence.length() == 1 && charSequence.charAt(0) == '0') {
                mCommonTopFormulaEditText.setTextColor(getResources().getColor(R.color.display_formula_hint_text_color));
            } else {
                mCommonTopFormulaEditText.setTextColor(getResources().getColor(R.color.display_formula_text_color));
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (editable != null && mCommonTopFormulaEditText != null) {
                mCommonTopFormulaEditText.removeTextChangedListener(this);
                mCommonTopFormulaEditText.setText(getCommaString(checkNumber(editable.toString())));
                mCommonTopFormulaEditText.addTextChangedListener(this);
            }

            if ((mCurrentState == Calculator.CalculatorState.EVALUATE && editable.toString().contains("e")) || (mCurrentState == Calculator.CalculatorState.RESULT)) {
                setState(Calculator.CalculatorState.INPUT);
                if (mCommonTopResultEditText.getText().length() != 0) {
                    mCommonTopResultEditText.setText("");
                }
            } else {
                setState(Calculator.CalculatorState.INPUT);
            }
            mEvaluator.evaluate(editable.toString().replaceAll(" ", "").replaceAll(",", "."), ConverterFragment.this); // MODIFIED by qiong.liu1, 2017-04-25,BUG-4598868
            requestCursor();
        }
    };

    public String checkNumber(String str) {
        str = str.replaceAll(" ", "").replaceAll(",", ".");
        String regex1 = "-?(0{1,})\\d*(\\.?)\\d*";
        StringBuilder sb = new StringBuilder();
        if (str.matches(regex1)) {
            for (int i = 0; i < str.length(); i++) {
                char s = str.charAt(i);
                if ('0' != s) {
                    if ('-' != s) {
                        if ('.' == s) {
                            sb.append("0");
                        }
                        sb.append(str.substring(i, str.length()));
                        break;
                    } else {
                        sb.append(s);
                        continue;
                    }
                }
            }
            if ("".equals(sb.toString()) || "-".equals(sb.toString())) {
                sb.append("0");
            }
        } else {
            sb.append(str);
        }
        Log.i(TAG, "checkNumber sb.toString()=" + sb.toString());
        return sb.toString();
    }

    private void performConvert() {
        try {
            mCurrnetConvertType = mCurrentConvert.getCurrentType();
            String result = mCommonTopResultEditText.getText().toString();
            if (TextUtils.isEmpty(result)) {
                result = mCommonTopFormulaEditText.getText().toString();
            }
            result = result.replace(" ", "").replace("−", "-");
            if (TextUtils.isEmpty(result)) {
                result = Constant.DEFAULT_VALUE;
            }
            Unit defUnit = mCurrentConvert.getDefaultLeftUnit();
            if (defUnit != null) {
                defUnit.setUnitValue(Float.valueOf(result));
            }
            Unit toUnit = mConvertUtil.getConvertRate(mCurrnetConvertType, mCurrentConvert.getDefaultLeftUnit(), mCurrentConvert.getDefaultRightUnit());
            Log.i(TAG, "performConvert=result=" + result + ",toUnit=" + toUnit);
            mCommonBottomResultEditText.setText(toUnit.getUnitValue() + "");
            checkFormulaResult0();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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

    private void queryHistory() {
        mConvertHistory = mConvertHistory.queryConvertHistory(mIsStanderPoint);
    }

    private View mCurrentButton;

    public void cancelCopyPaste() {
        isFormulaLongClicked = false;
        isResultLongClicked = false;
        mCommonTopFormulaEditText.setPressed(false);
        mCommonTopResultEditText.setPressed(false);
//        Selection.removeSelection(mCommonTopFormulaEditText.getText());
//        Selection.removeSelection(mCommonTopResultEditText.getText());
        copyPasteState = false;
    }

    private void onEquals() {
        if (mCommonTopFormulaEditText.getText().toString().contains("e") || mCurrentState == Calculator.CalculatorState.INPUT) {
            setState(Calculator.CalculatorState.EVALUATE);
            mEvaluator.evaluate(mCommonTopFormulaEditText.getText().toString().replace(" ", "").replaceAll(",", "."), this);
        }
    }

    private void setState(Calculator.CalculatorState state) {
        if (mCurrentState != state) {
            mCurrentState = state;

            if (state == Calculator.CalculatorState.RESULT || state == Calculator.CalculatorState.ERROR) {
                mDeleteButton.setVisibility(View.GONE);
                mClearButton.setVisibility(View.VISIBLE);
            } else {
                mDeleteButton.setVisibility(View.VISIBLE);
                mClearButton.setVisibility(View.GONE);
            }

            if (state == Calculator.CalculatorState.ERROR) {
                final int errorColor = getResources().getColor(R.color.calculator_error_color);
                mCommonTopFormulaEditText.setTextColor(errorColor);
                mCommonTopResultEditText.setTextColor(errorColor);
                mContext.getWindow().setStatusBarColor(errorColor);
            } else {
                if (mCommonTopFormulaEditText.getText().toString().equals("0")) {
                    mCommonTopFormulaEditText.setTextColor(getResources().getColor(R.color.display_formula_hint_text_color));
                } else {
                    mCommonTopFormulaEditText.setTextColor(
                            getResources().getColor(R.color.display_formula_text_color));
                }
                mCommonTopResultEditText.setTextColor(
                        getResources().getColor(R.color.display_result_text_color));
                mContext.getWindow().setStatusBarColor(
                        getResources().getColor(R.color.calculator_accent_color));
            }
        }
    }


    private void onDelete() {
        try {
            final Editable formulaText = mCommonTopFormulaEditText.getEditableText();
            final int formulaLength = formulaText.length();
            if (formulaLength == 1 || formulaLength == 0) {
                mCommonTopFormulaEditText.setText(Constant.DEFAULT_VALUE);
                mCommonBottomResultEditText.setText(Constant.DEFAULT_VALUE);
                return;
            }
            formulaText.delete(formulaLength - 1, formulaLength);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkFormulaResult0() {
        try {
            String formula = mCommonTopFormulaEditText.getText().toString();
            String result = mCommonBottomResultEditText.getText().toString();
            String topResult = mCommonTopResultEditText.getText().toString();
            if (TextUtils.isEmpty(formula) || TextUtils.isEmpty(result)) {
                mCommonTopFormulaEditText.setText(Constant.DEFAULT_VALUE);
                mCommonBottomResultEditText.setText(Constant.DEFAULT_VALUE);
            }
            if (Constant.DEFAULT_VALUE.equals(formula) || Constant.DEFAULT_VALUE.equals(topResult)) {
                mCommonBottomResultEditText.setText(Constant.DEFAULT_VALUE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onClear() {
        if (Constant.DEFAULT_VALUE.equals(mCommonTopFormulaEditText.getText()) && Constant.DEFAULT_VALUE.equals(mCommonBottomResultEditText.getText())) {
            return;
        }
        reveal(mCurrentButton, R.color.calculator_accent_color, new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCommonTopFormulaEditText.getEditableText().clear();
                mCommonTopResultEditText.getEditableText().clear();
                mCommonBottomResultEditText.getEditableText().clear();
                mCommonTopFormulaEditText.setText(Constant.DEFAULT_VALUE);
                mCommonBottomResultEditText.setText(Constant.DEFAULT_VALUE);
            }
        });
    }

    private static final String KEY_CURRENT_VIEWPAGER = "current_viewpager";
    private static final String KEY_CURRENT_STATE = NAME + "_currentState";
    private static final String KEY_CURRENT_TRANSLATE_STATE = NAME + "_currentTranslateState";
    private static final String KEY_CURRENT_EVALUATOR_MODE = NAME + "_currentEvaluatorMode";
    private static final String FORMULA_CLICKED_STATE = "formula_clicked_state";
    private static final String RESULT_CLICKED_STATE = "result_clicked_state";


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        // If there's an animation in progress, cancel it first to ensure our state is up-to-date.
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }

        super.onSaveInstanceState(outState);

        if (mCalculatorPadViewPager != null) {
            outState.putInt(KEY_CURRENT_VIEWPAGER, mCalculatorPadViewPager.getCurrentItem());
        } else {
            outState.putInt(KEY_CURRENT_VIEWPAGER, mCurrentViewPager);
        }
        if (mCurrentState != null) {
            outState.putInt(KEY_CURRENT_STATE, mCurrentState.ordinal());
        }

/* MODIFIED-BEGIN by qiong.liu1, 2017-05-02,BUG-4598039*/
//        if(mConvertPopWindow.isShowing()){
//            dissMissPopWindow(); // MODIFIED by qiong.liu1, 2017-04-21,BUG-4452809
//            outState.putBoolean(POPWINDOW_SHOW_STATUS,true);
//        }else{
//            outState.putBoolean(POPWINDOW_SHOW_STATUS,false);
//        }
/* MODIFIED-END by qiong.liu1,BUG-4598039*/

        outState.putString(KEY_CURRENT_EXPRESSION,
                mTokenizer.local2Build(mCommonTopFormulaEditText.getText().toString()));
        outState.putInt(KEY_DISPLAY_MODE, mDisplayView.getMode().ordinal());
        outState.putString(TOP_RESULT_TEXT_KEY, mCommonTopResultEditText.getText().toString());
        outState.putString(BOTTOM_RESULT_TEXT_KEY, mCommonBottomResultEditText.getText().toString());
        outState.putInt(KEY_CURRENT_TRANSLATE_STATE, mDisplayView.getTranslateState().ordinal());
        outState.putInt(KEY_CURRENT_EVALUATOR_MODE, mEvaluator.getEvaluatorMode().ordinal());
        outState.putBoolean(FORMULA_CLICKED_STATE, isFormulaLongClicked);
        outState.putBoolean(RESULT_CLICKED_STATE, isResultLongClicked);
        String convertRecord = mCurrentConvert.getConvertName() + "_" + mCurrentConvert.getDefaultLeftUnitIndex() + "_" + mCurrentConvert.getDefaultRightUnitIndex();
        mSharedPreferencesHelper.saveConvertOperations(SharedPreferencesHelper.KEY_CONVERT_UNIT, convertRecord);
    }

    /* MODIFIED-BEGIN by qiong.liu1, 2017-04-21,BUG-4452809*/
    private void dissMissPopWindow() {
        /* MODIFIED-BEGIN by qiong.liu1, 2017-04-21,BUG-4452809*/
        if (Calculator.mScreenState != SCREENPORTFULL && Calculator.mScreenState != SCREENLANDFULL ) {
            if (mCommonContainer != null) {
                mCommonContainer.setPadding(150, 0, 0, 0);
            }
            /* MODIFIED-END by qiong.liu1,BUG-4452809*/
            mActivity.setShowExpandLayout(false);
        }
        mCommonTopContainer.setBackgroundColor(Color.WHITE);
        mConvertPopWindow.dismiss();
    }
    /* MODIFIED-END by qiong.liu1,BUG-4452809*/

    private FrameLayout.LayoutParams mLayoutParams =
            new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, 0);

    private void reveal(View sourceView, int colorRes, Animator.AnimatorListener listener) {
        final View revealView = new View(mContext);

        mLayoutParams.height = mDisplayView.getDisplayHeight();
        mLayoutParams.gravity = Gravity.BOTTOM;
        revealView.setLayoutParams(mLayoutParams);
        revealView.setBackgroundColor(getResources().getColor(colorRes));
        mDisplayView.addView(revealView);
        final int revealCenterX = mDisplayView.getWidth();
        final int revealCenterY = mDisplayView.getDisplayHeight();

        final double x_2 = Math.pow(mDisplayView.getWidth(), 2);
        final double y_2 = Math.pow(mDisplayView.getDisplayHeight(), 2);
        final float revealRadius = (float) Math.sqrt(x_2 + y_2);
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
    }

    private boolean mDataFromHistory = false;

    public void onArea2Click(View view) {
        switch (view.getId()) {
            case R.id.common_top_formula_copy_btn:
                if (mIsStanderPoint) {
                    clipboard.setPrimaryClip(ClipData.newPlainText(null, mCommonTopFormulaEditText.getText().toString().replaceAll(" ", "").replaceAll(",", ".")));
                } else {
                    clipboard.setPrimaryClip(ClipData.newPlainText(null, mCommonTopFormulaEditText.getText().toString().replaceAll(" ", "").replaceAll("\\.", ",")));
                }
                mDataFromHistory = false;
                mDisplayView.collapseHistory();
                break;
            case R.id.common_top_formula_paste_btn:
                if (clipboard.hasPrimaryClip()) {
                    if (mCurrentState == Calculator.CalculatorState.RESULT && !TextUtils.isEmpty(mCommonTopFormulaEditText.getText()) && !mDataFromHistory) {
                        reveal(mCurrentButton, R.color.calculator_accent_color, new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                mCommonTopFormulaEditText.getEditableText().clear();
                                mCommonTopFormulaEditText.append(clipboard.getPrimaryClip().getItemAt(0).getText().toString());
                            }
                        });
                    } else {
                        if (mDataFromHistory) {
                            setState(Calculator.CalculatorState.INPUT);
                        }
                        Editable editable = mCommonTopFormulaEditText.getText();
                        int length = editable.length();
                        if (length > 0 && Character.isDigit(editable.charAt(length - 1)))
                            mCommonTopFormulaEditText.append(getString(R.string.op_mul));
//                        mCommonTopFormulaEditText.append(getCommaString(PatternUtil.getInstance().getVaildPaster(clipboard.getPrimaryClip().getItemAt(0).getText().toString())));//PR1075592 don't show paster button when clipboard hasn't vaild paster content Update by xiaolu.li 26/8/2015
                        mCommonTopFormulaEditText.append(clipboard.getPrimaryClip().getItemAt(0).getText().toString());
                    }
                }
                mDisplayView.collapseHistory();
                break;
            case R.id.common_top_result_copy_btn:
                if (mIsStanderPoint) {
                    clipboard.setPrimaryClip(ClipData.newPlainText(null, mCommonTopResultEditText.getText().toString().replaceAll(",", ".").replaceAll(" ", "")));
                } else {
                    clipboard.setPrimaryClip(ClipData.newPlainText(null, mCommonTopResultEditText.getText().toString().replaceAll("\\.", ",").replaceAll(" ", "")));
                }
                mDataFromHistory = false;
                mDisplayView.collapseHistory();
                break;
            case R.id.common_top_result:
            case R.id.common_top_formula:
                if (mCalculatorPadViewPager != null && mCalculatorPadViewPager.getCurrentItem() > 0 && mDisplayView.getTranslateState() == CommonDisplayOverlay.TranslateState.COLLAPSED) {
                    mCalculatorPadViewPager.setCurrentItem(mCalculatorPadViewPager.getCurrentItem() - 1);
                }
                break;
            default:
                break;
        }
        cancelCopyPaste();
        requestCursor();

    }

    @Override
    public void onClick(View view) {
        /* MODIFIED-BEGIN by qiong.liu1, 2017-04-01,BUG-3621966*/
        if (((Calculator) this.getActivity()) != null) {
            ((Calculator) this.getActivity()).coolapseButton();
        }
        /* MODIFIED-END by qiong.liu1,BUG-3621966*/
        mCurrentButton = view;
        mDisplayView.collapseHistory();
        cancelCopyPaste();
        switch (view.getId()) {
            case R.id.convert_top_item_layout:
                // Only don't show history we can show the PopupWindow.
                if (!mIsConverterShowHistory) {
                    mCurrentTopIndex = mLeftIndex;
                    mCurrentButtonIndex = mRightIndex;
                    showPopupWindow(mConvertTopItemLayout);
                    mArrowView.setVisibility(View.INVISIBLE);
                    mCalculatorPadViewPager.setCurrentItem(0, true);
                }
                break;
            case R.id.wheel_picker_confirm:
                mArrowView.setVisibility(View.VISIBLE);
//                mWheelContainer.setVisibility(View.GONE);
                mCommonTopFormulaEditText.requestFocus();
                mCommonTopFormulaEditText.requestFocusFromTouch();
                String convertRecord = mCurrentConvert.getConvertName() + "_" + mCurrentConvert.getDefaultLeftUnitIndex() + "_" + mCurrentConvert.getDefaultRightUnitIndex();
                Log.i(TAG, "onClick wheel_picker_confirm convertRecord=" + convertRecord);
                dissMissPopWindow(); // MODIFIED by qiong.liu1, 2017-04-21,BUG-4452809
                mSharedPreferencesHelper.saveConvertOperations(SharedPreferencesHelper.KEY_CONVERT_UNIT, convertRecord);
                break;
            case R.id.wheel_picker_cancel:
                Log.i(TAG, "onClick wheel_picker_cancel");
//                mWheelContainer.setVisibility(View.GONE);
                mArrowView.setVisibility(View.VISIBLE);
                mCommonTopFormulaEditText.requestFocus();
                mCommonTopFormulaEditText.requestFocusFromTouch();
                dissMissPopWindow(); // MODIFIED by qiong.liu1, 2017-04-21,BUG-4452809
                resetCurrentUnit();
                break;
            case R.id.common_bottom_result:
                if (mDisplayView.getTranslateState() == CommonDisplayOverlay.TranslateState.EXPANDED) {
                    return;
                }
                /* MODIFIED-BEGIN by qiong.liu1, 2017-04-25,BUG-4598868*/
                mShowAnimFromResult = true;
                mCalculatorPadViewPager.setCurrentItem(0, true);
                /* MODIFIED-END by qiong.liu1,BUG-4598868*/
                exchangeConvertAnim();
                break;
            case R.id.common_top_formula:
                mCalculatorPadViewPager.setCurrentItem(0, true);
                mCommonTopFormulaEditText.requestFocus();
                mCommonTopFormulaEditText.requestFocusFromTouch();
                break;
            case R.id.eq_convert:
                onEquals();
                break;
            case R.id.del_convert:
                onDelete();
                break;
            case R.id.clr_convert:
                onClear();
                break;
            case R.id.op_add_convert:
            case R.id.op_mul_convert:
            case R.id.op_div_convert:
                String mCommonTopFormulaEditTextStr = mCommonTopFormulaEditText.getText().toString()
                        .trim();
                String op_sub = getResources().getString(R.string.op_sub);
                if (mCommonTopFormulaEditTextStr.length() == 1
                        && op_sub.equals(mCommonTopFormulaEditTextStr)) {
                    break;
                }
                setState(Calculator.CalculatorState.INPUT);
                mCommonTopFormulaEditText.append(((Button) view).getText());
                break;
            case R.id.dec_point_convert:
                if (mCurrentState == Calculator.CalculatorState.RESULT) {
                    onDelete();
                    mCommonTopFormulaEditText.setText("0" + getString(R.string.dec_point_rel4));
                    break;
                }
                String point_str = mCommonTopFormulaEditText.getText().toString();
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
                    mCommonTopFormulaEditText.append("0" + getString(R.string.dec_point_rel4));
                } else if (!(c >= '0' && c <= '9')) {
                    mCommonTopFormulaEditText.append("0" + getString(R.string.dec_point_rel4));
                } else {
                    mCommonTopFormulaEditText.append(((Button) view).getText());
                }
                break;
            case R.id.exchange:
                if (mDisplayView.getTranslateState() == CommonDisplayOverlay.TranslateState.EXPANDED) {
                    return;
                }
                mShowAnimFromResult = false;
                exchangeConvertAnim();
                rotateAnim();
                break;
            case R.id.slide_menu:
                mCalculatorPadViewPager.setCurrentItem(1, true);
                break;
            default:
                if (mCurrentState != Calculator.CalculatorState.RESULT) {
                    mCommonTopFormulaEditText.append(((Button) view).getText());
                } else {
                    setState(Calculator.CalculatorState.INPUT);
                    mCommonTopFormulaEditText.append(((Button) view).getText());
                }
                break;
        }
        requestCursor();
    }

    private void initConvertRecord() {
        try {
            String convertRecord = mSharedPreferencesHelper.getConvertOperations(SharedPreferencesHelper.KEY_CONVERT_UNIT);
            if (!TextUtils.isEmpty(convertRecord)) {
                String[] records = convertRecord.split("_");
                mCurrentConvert = getConvertByName(records[0]);
                mRecordConvertIndex = mCurrentConvert.getCurrentType().ordinal();
                mCurrentConvert.setDefaultLeftUnitIndex(Integer.parseInt(records[1]));
                mCurrentConvert.setDefaultRightUnitIndex(Integer.parseInt(records[2]));
            } else {
                mCurrentConvert = getConvertByName(Constant.ConvertType.LENGTH.name());
                mRecordConvertIndex = Constant.ConvertType.LENGTH.ordinal();
            }
            Log.i(TAG, "initConvertRecord convertRecord=" + convertRecord + ",mRecordConvertIndex=" + mRecordConvertIndex);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Convert getConvertByName(String convertName) {
        try {
            for (Convert convert : mConverts) {
                if (convert != null && convert.getConvertName().equalsIgnoreCase(convertName)) {
                    return convert;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private int mLeftIndex, mRightIndex;
    CalculatorScrollWheelPicker.OnWheelChangedListener callbackChanged = new CalculatorScrollWheelPicker.OnWheelChangedListener() {
        @Override
        public void onWheelChanged(int leftIndex, int rightIndex) {
            Log.i(TAG, "onWheelChanged,leftIndex=" + mLeftIndex + ",rightIndex=" + mRightIndex);
            mLeftIndex = leftIndex;
            mRightIndex = rightIndex;
        }
    };
    OnWheelScrollListener callbackFinished = new OnWheelScrollListener() {
        @Override
        public void onScrollingStarted(WheelView wheel) {
            mWheelView = wheel;
        }

        @Override
        public void onScrollingFinished(WheelView wheel) {
            setWheelItemData(wheel, mRightIndex, mLeftIndex);
            Utils.setSoundForWheelView(getActivity(), mWheelPickerConfirm);
        }
    };
    OnScrollWheelClickedListener onScrollWheelClickedListener = new OnScrollWheelClickedListener() {
        @Override
        public void onItemClicked(WheelView wheel, int itemIndex) {
            mWheelView = wheel;
            ScrollWheelPickerView.WHEEL wheelType = (ScrollWheelPickerView.WHEEL) wheel.getTag();
            if (wheelType == ScrollWheelPickerView.WHEEL.LEFT_WHEEL) {
                mLeftIndex = itemIndex;
                setWheelItemData(wheel, mRightIndex, mLeftIndex);
            } else if (wheelType == ScrollWheelPickerView.WHEEL.RIGHT_WHEEL) {
                mRightIndex = itemIndex;
                setWheelItemData(wheel, mRightIndex, mLeftIndex);
            }
        }
    };

    private void initDatas(View view) {
        mConverts = new ArrayList<Convert>();
        String[] convertNames = getResources().getStringArray(R.array.unit_list);
        String[] convertDisplayNames = getResources().getStringArray(R.array.unit_list_display_name);
        int[] convertImgRes;
        if (Calculator.mScreenState == SCREENPORTFULL) {
            /* MODIFIED-BEGIN by qiong.liu1, 2017-03-20,BUG-3621966*/
            convertImgRes = new int[]{R.drawable.convert_length_icon,
                    R.drawable.convert_area_icon,
                    R.drawable.convert_mass_icon,
                    R.drawable.convert_volume_icon,
                    R.drawable.convert_temperature_icon,
                    R.drawable.convert_fuel_icon,
/* MODIFIED-BEGIN by qiong.liu1, 2017-04-01,BUG-3621966*/
//                    R.drawable.convert_shoes_icon,
//                    R.drawable.convert_clothes_icon,
/* MODIFIED-END by qiong.liu1,BUG-3621966*/
                    R.drawable.convert_cooking_icon};
        } else {
            convertImgRes = new int[]{R.drawable.convert_length_icon_split,
                    R.drawable.convert_area_icon_split,
                    R.drawable.convert_mass_icon_split,
                    R.drawable.convert_volume_icon_split,
                    R.drawable.convert_temperature_icon_split,
                    R.drawable.convert_fuel_icon_split,
/* MODIFIED-BEGIN by qiong.liu1, 2017-04-01,BUG-3621966*/
//                    R.drawable.convert_shoes_icon_split,
//                    R.drawable.convert_clothes_icon_split,
/* MODIFIED-END by qiong.liu1,BUG-3621966*/
                    R.drawable.convert_cooking_icon_split};
        }

        int[] convertHistoryImgRes = {R.drawable.convert_length_icon_history,
                R.drawable.convert_area_icon_history,
                R.drawable.convert_mass_icon_history,
                R.drawable.convert_volume_icon_history,
                R.drawable.convert_temperature_icon_history,
                R.drawable.convert_fuel_icon_history,
/* MODIFIED-BEGIN by qiong.liu1, 2017-04-01,BUG-3621966*/
//                R.drawable.convert_shoes_icon_history,
//                R.drawable.convert_clothes_icon_history,
/* MODIFIED-END by qiong.liu1,BUG-3621966*/
                R.drawable.convert_cooking_icon_history};
        int[] convertUnits = {R.array.unit_name_length,
                R.array.unit_name_area,
                R.array.unit_name_mass,
                R.array.unit_name_volume,
                R.array.unit_name_temperature,
                R.array.unit_name_fuel,
/* MODIFIED-BEGIN by qiong.liu1, 2017-04-01,BUG-3621966*/
//                R.array.unit_name_shoes,
//                R.array.unit_name_clothes,
/* MODIFIED-END by qiong.liu1,BUG-3621966*/
                R.array.unit_name_cooking};
        Log.i(TAG, "=======MainFragment==program=onItemClick======" + Arrays.toString(convertNames));
        for (int i = 0; i < convertNames.length; i++) {
            mConverts.add(new Convert(mContext, convertNames[i], convertDisplayNames[i],
                    convertImgRes[i], convertHistoryImgRes[i], convertUnits[i]));
        }
    }

    private AdapterView.OnItemClickListener mConvertItemClickedListener =
            new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            updateConvertItem(view, position);
            if (mCalculatorPadViewPager.getCurrentItem() > 0) {
                mCalculatorPadViewPager.setCurrentItem(mCalculatorPadViewPager.getCurrentItem() - 1);
            }
            mCommonTopFormulaEditText.setText("0");
            mCommonBottomResultEditText.setText("0");
            Log.i(TAG, "=======ConvertActivity==mConvertItemClickedListener===position=" + position);
        }
    };
    private AdapterView.OnItemSelectedListener mConvertItemSelectedListener =
            new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
            updateConvertItem(view, position);
            Log.i(TAG, "=======ConvertActivity==mConvertItemSelectedListener===position=" + position);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };

    private void updateConvertItem(View view, int position) {
        try {
            Convert convert = ((Convert) view.findViewById(R.id.grid_item_program_layout).getTag());
            String name = convert.getConvertName();
            setConvertState(convert);
            mConvertSlideAdapter.notifyDataSetChanged();
            updateSubConvertDatas(position);
            performConvert();
            Log.i(TAG, "=======ConvertActivity==updateConvertItem===position=" + position + "=name=" + name);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void updateSubConvertDatas(int position) {
        try {
            mCurrentConvert = mConverts.get(position);
            List<Unit> leftDatas = mCurrentConvert.getUnit();
            List<Unit> rightDatas = mCurrentConvert.getUnit();
            int leftUnitIndex = mCurrentConvert.getDefaultLeftUnitIndex();
            int rightUnitIndex = mCurrentConvert.getDefaultRightUnitIndex();
            Log.i(TAG, "ConvertActivity=onHistorySelected updateSubConvertDatas===leftUnitIndex=" + leftUnitIndex + "=rightUnitIndex=" + rightUnitIndex);
            String topFullName = mCurrentConvert.getDefaultLeftUnit().getFullName();
            topFullName = topFullName.substring(0, 1).toUpperCase() + topFullName.substring(1);
            mConvertTopFullname.setText(topFullName);
            mConvertTopAbbreviation.setText(mCurrentConvert.getDefaultLeftUnit().getAbbreviation());
            String bottomFullName = mCurrentConvert.getDefaultRightUnit().getFullName();
            bottomFullName = bottomFullName.substring(0, 1).toUpperCase() + bottomFullName.substring(1);
            mConvertBottomFullname.setText(bottomFullName);
            mConvertBottomAbbreviation.setText(mCurrentConvert.getDefaultRightUnit().getAbbreviation());
            mCalculatorWheelPicker.updateWheel(leftUnitIndex, rightUnitIndex, leftDatas, rightDatas);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setConvertState(Convert convert) {
        for (Convert con : mConverts) {
            if (con.getConvertName().equalsIgnoreCase(convert.getConvertName())) {
                con.setSelected(true);
            } else {
                con.setSelected(false);
            }
        }
    }


    private boolean copyPasteState;
    private boolean isFormulaLongClicked = false;
    private boolean isResultLongClicked = false;

    @Override
    public boolean onLongClick(View view) {
        mCurrentButton = view;
        switch (view.getId()) {
            case R.id.del_convert:
                onClear();
                return true;
//            case R.id.common_top_formula:
//                if (((isCanCopyFormula() || isCanPaste()) && !copyPasteState)) {
//                    isFormulaLongClicked = true;
//                    copyPasteState = true;
//                    Selection.selectAll(mCommonTopFormulaEditText.getText());
//                    displayFormulaCopyPasteButton(true);
//                }
//                return true;
//            case R.id.common_top_result:
//                if (mCurrentState == Calculator.CalculatorState.RESULT && !isCanPaste()) {
//                    clipboard.setPrimaryClip(ClipData.newPlainText(null, mCommonTopFormulaEditText.getText()));
//                    Toast.makeText(mContext, mContext.getString(R.string.copy_result_toast), Toast.LENGTH_SHORT).show();
//                    return true;
//                } else if (isCanCopyResult() && !copyPasteState) {
//                    isResultLongClicked = true;
//                    copyPasteState = true;
//                    Selection.selectAll(mCommonTopResultEditText.getText());
//                    displayResultCopyButton(true);
//                    return true;
//                }
        }
        return true;
    }

    @Override
    public void onTextSizeChanged(TextView textView, float oldSize) {
        if (mCurrentState != Calculator.CalculatorState.INPUT) {
            // Only animate text changes that occur from user input.
            return;
        }
// Calculate the values needed to perform the scale and translation animations,
// maintaining the same apparent baseline for the displayed text.
        final float textScale = oldSize / textView.getTextSize();
        final float translationX = (1.0f - textScale) *
                (textView.getWidth() / 2.0f - textView.getPaddingEnd());
        final float translationY = (1.0f - textScale) *
                (textView.getHeight() / 2.0f - textView.getPaddingBottom());

        final AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(
                ObjectAnimator.ofFloat(textView, View.SCALE_X, textScale, 1.0f),
                ObjectAnimator.ofFloat(textView, View.SCALE_Y, textScale, 1.0f),
                ObjectAnimator.ofFloat(textView, View.TRANSLATION_X, translationX, 0.0f),
                ObjectAnimator.ofFloat(textView, View.TRANSLATION_Y, translationY, 0.0f));
        animatorSet.setDuration(getResources().getInteger(android.R.integer.config_mediumAnimTime));
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.start();
        Log.i(TAG, "---onTextSizeChanged---end");
    }


    //whether show copy btn
    private boolean isCanCopyFormula() {
        if ((Calculator.CalculatorState.RESULT == mCurrentState && (!TextUtils.isEmpty(mCommonTopFormulaEditText.getText())))) {
            return true;
        }
        return false;
    }

    //whether show paste btn
    private boolean isCanPaste() {

        if (!TextUtils.isEmpty(getClipBoardText()) && PatternUtil.getInstance().isContainNumber(getClipBoardText()) && (mCurrentState == Calculator.CalculatorState.INPUT || mCurrentState == Calculator.CalculatorState.ERROR || mCurrentState == Calculator.CalculatorState.RESULT)) {
            return true;
        }
        return false;
    }

    private boolean isCanCopyResult() {
        if (((!TextUtils.isEmpty(mCommonTopResultEditText.getText())) && Calculator.CalculatorState.ERROR != mCurrentState)) {
            return true;
        }
        return false;
    }

    private ClipboardManager clipboard;

    private String getClipBoardText() {
        if (clipboard.hasPrimaryClip()) {
            try {
                return clipboard.getPrimaryClip().getItemAt(0).getText().toString();
            } catch (NullPointerException e) {
                Log.i(TAG, "getClipBoardText catch a NullPointerException");
            }
        }
        return null;
    }

    private boolean isRegisted = false;
    private final BroadcastReceiver powerReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            final String action = intent.getAction();
            if (action.equals(Intent.ACTION_SCREEN_OFF)) {
                mContext.moveTaskToBack(true);
                if (isRegisted) {
                    try {
                        mContext.unregisterReceiver(this);
                        isRegisted = false;
                        mContext.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
                    } catch (Exception e) {
                        Log.d("Calculator", " Receiver not registered");
                    }
                }
            }
        }
    };

    public void onNewIntent(Intent intent) {//TODO ts
//        if (intent != null && intent.getBooleanExtra(SHOW_BY_LOCKED, false) && intent.getBooleanExtra(IS_SERCURE, false)) {
//            if (!isRegisted) {
//                IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
//                filter.setPriority(10000);
//                mContext.registerReceiver(powerReceiver, filter);
//                isRegisted = true;
//                mContext.getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
//            }
//            mCommonTopFormulaEditText.setText("");
//            cancelCopyPaste();
//        } else {
//            if (isRegisted) {
//                try {
//                    mContext.unregisterReceiver(powerReceiver);
//                    isRegisted = false;
//                    mContext.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
//                } catch (Exception e) {
//                }
//            }
//        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        /* MODIFIED-BEGIN by qiong.liu1, 2017-04-07,BUG-4452809*/
        mArrowView.setVisibility(View.VISIBLE);
        mCommonTopFormulaEditText.requestFocus();
        mCommonTopFormulaEditText.requestFocusFromTouch();
        dissMissPopWindow(); // MODIFIED by qiong.liu1, 2017-04-21,BUG-4452809
        resetCurrentUnit();
        /* MODIFIED-END by qiong.liu1,BUG-4452809*/
        if (powerReceiver != null && isRegisted) {
            try {
                mContext.unregisterReceiver(powerReceiver);
                isRegisted = false;
            } catch (Exception e) {
                Log.d("Calculator", " Receiver not registered");
            }
        }
        if (mCommonTopFormulaEditText != null && mFormulaTextWatcher != null) {
            mCommonTopFormulaEditText.removeTextChangedListener(mFormulaTextWatcher);
        }

    }

    private String JUMP_TO_SPLASH = "jumpToSplash";

    private void jumpTo(boolean b, Class<?> cls) {
        Intent mIntent = new Intent(mContext, cls);
        mIntent.putExtra(JUMP_TO_SPLASH, b);
        startActivity(mIntent);
//        finish();// TODO ts
    }

    private final String PREF = "first_pref";
    private final String KEY = "firstin";
    private SharedPreferences pref;

    private boolean isFirstIn() {
        pref = mContext.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        return pref.getBoolean(KEY, true);
    }

    @Override
    public void onHistorySelected(boolean isCollapse, ConvertHistoryItem item) {

        try {
            if (mWheelContainer != null) {
//                mWheelContainer.setVisibility(View.GONE);
                mArrowView.setVisibility(View.VISIBLE);
            }
            if (item != null) {
                mCurrentConvert = getConvertByName(item.convertName);
                mRecordConvertIndex = mCurrentConvert.getConvertIndex();
                int leftIndex = mCurrentConvert.getUnitIndexByName(item.fromName);
                int rightIndex = mCurrentConvert.getUnitIndexByName(item.toName);
                mCurrentConvert.setDefaultLeftUnitIndex(mCurrentConvert.getUnitIndexByName(item.fromName));
                mCurrentConvert.setDefaultRightUnitIndex(mCurrentConvert.getUnitIndexByName(item.toName));
                Log.i(TAG, "ConvertActivity=onHistorySelected mCurrentConvert=" + mCurrentConvert + ",item=" + item + ",leftIndex=" + leftIndex + ",rightIndex=" + rightIndex);
                setConvertState(mCurrentConvert);
                mConvertSlideAdapter.notifyDataSetChanged();
                updateSubConvertDatas(mCurrentConvert.getConvertIndex());
                performConvert();
                mCommonTopFormulaEditText.setText("0");
                mCommonBottomResultEditText.setText("0");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void changCalculatorView(float translaionY) {
        ((Calculator) mContext).hidGuideButton(translaionY);
        if (translaionY > 0) {
            mIsConverterShowHistory = true;
        } else {
            mIsConverterShowHistory = false;
        }
    }

    @Override
    public void hideWheel() {
        if (mWheelContainer != null) {
//            mWheelContainer.setVisibility(View.GONE);
            mArrowView.setVisibility(View.VISIBLE);
        }
    }

    /* MODIFIED-BEGIN by qiong.liu1, 2017-04-11,BUG-4452809*/
    @Override
    public void onConfigurationChanged() {
        if (mConvertPopWindow.isShowing()) {
            mArrowView.setVisibility(View.VISIBLE);
            mCommonTopFormulaEditText.requestFocus();
            mCommonTopFormulaEditText.requestFocusFromTouch();
            dissMissPopWindow(); // MODIFIED by qiong.liu1, 2017-04-21,BUG-4452809
            resetCurrentUnit();
        }
    }
    /* MODIFIED-END by qiong.liu1,BUG-4452809*/


    private class SetHistoryCopyStatue implements CommonHistoryAdapter.DataFromHistoryCallback {

        @Override
        public void setStatus(boolean mHistroyStatus) {
            mDataFromHistory = true;
        }

    }

    public void exchangeConvertAnim() {
        copyAnimData(true);
        showAnimView(true);
        Log.i(TAG, "ConvertActivity=exchangeConvertAnim=mTopFrom=" + mTopFrom + ",mTopTo=" + mTopTo + ",mBottomFrom=" + mBottomFrom + ",mBottomTo=" + mBottomTo + ",diff=" + (mSwitchLayoutTopAnim.getHeight() - mSwitchLayoutBottomAnim.getHeight()) / 2);

        ObjectAnimator topOutAnim = ObjectAnimator.ofFloat(mSwitchLayoutTopAnim, "translationY", mTopFrom, mTopTo);
        ObjectAnimator bottomOutAnim = ObjectAnimator.ofFloat(mSwitchLayoutBottomAnim, "translationY", mBottomFrom, mBottomTo);

        bottomOutAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                mUIHandler.sendEmptyMessage(EXCHANGE_CONVERT_ANIM_END);
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

    private void copyAnimData(boolean beforeSwitch) {

        try {
            if (beforeSwitch) {//before switch
                if (mCommonBottomResultEditText != null && mCommonBottomResultAnim != null) {
                    mCommonBottomResultAnim.setText(mCommonBottomResultEditText.getText().toString());
                }
                if (mCommonTopFormulaEditText != null && mCommonTopFormulaAnim != null) {
                    mCommonTopFormulaAnim.setText(mCommonTopFormulaEditText.getText().toString());
                }

                if (mConvertTopFullname != null && mConvertTopFullnameAnim != null && mConvertTopAbbreviation != null && mConvertTopAbbreviationAnim != null) {
                    mConvertTopFullnameAnim.setText(mConvertTopFullname.getText().toString());
                    mConvertTopAbbreviationAnim.setText(mConvertTopAbbreviation.getText().toString());
                }
                if (mConvertBottomFullname != null && mConvertBottomFullnameAnim != null && mConvertBottomAbbreviation != null && mConvertBottomAbbreviationAnim != null) {
                    mConvertBottomFullnameAnim.setText(mConvertBottomFullname.getText().toString());
                    mConvertBottomAbbreviationAnim.setText(mConvertBottomAbbreviation.getText().toString());
                }
            } else {//after switch
                String topFormulaText = mCommonTopFormulaEditText.getText().toString();
                String bottomResultText = mCommonBottomResultEditText.getText().toString();

                String topFullNameLabel = mConvertTopFullname.getText().toString();
                String topAbbreviationLabel = mConvertTopAbbreviation.getText().toString();

                String bottomFullNameLabel = mConvertBottomFullname.getText().toString();
                String bottomAbbreviationLabel = mConvertBottomAbbreviation.getText().toString();

                mCurrentConvert.setDefaultLeftUnitIndex(mCurrentConvert.getUnitIndexByName(bottomAbbreviationLabel));
                mCurrentConvert.setDefaultRightUnitIndex(mCurrentConvert.getUnitIndexByName(topAbbreviationLabel));
                mCalculatorWheelPicker.updateWheel(mCurrentConvert.getDefaultLeftUnitIndex(), mCurrentConvert.getDefaultRightUnitIndex(), mCurrentConvert.getUnit(), mCurrentConvert.getUnit());
                if (mShowAnimFromResult) {
                    if (mCommonTopFormulaEditText != null) {
                        mCommonTopFormulaEditText.setText(EXCHANGE_DEFAULT_VALUE);
                        if (!TextUtils.isEmpty(EXCHANGE_DEFAULT_VALUE)) {
                            setState(Calculator.CalculatorState.RESULT);
                        }
                    }
                } else {
                    if (mCommonBottomResultEditText != null) {
                        /* MODIFIED-BEGIN by qiong.liu1, 2017-04-25,BUG-4598868*/
                        mCommonBottomResultEditText.setText(bottomResultText);
                    }
                    if (mCommonTopFormulaEditText != null) {
                        mCommonTopFormulaEditText.setText(topFormulaText);
                        /* MODIFIED-END by qiong.liu1,BUG-4598868*/
                        if (!TextUtils.isEmpty(bottomResultText)) {
                            setState(Calculator.CalculatorState.RESULT);
                        }
                    }
                }
                if (mConvertTopFullname != null && mConvertTopAbbreviation != null) {
                    mConvertTopFullname.setText(bottomFullNameLabel);
                    mConvertTopAbbreviation.setText(bottomAbbreviationLabel);
                }
                if (mConvertBottomFullname != null && mConvertBottomAbbreviation != null) {
                    mConvertBottomFullname.setText(topFullNameLabel);
                    mConvertBottomAbbreviation.setText(topAbbreviationLabel);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void showAnimView(boolean isAnimViewVisible) {
        try {
            if (isAnimViewVisible) {
                mSwitchLayoutDividerAnim.setVisibility(View.VISIBLE);
                mSwitchLayoutAnim.setVisibility(View.VISIBLE);
                mCommonTopFormulaAnim.setVisibility(View.VISIBLE);
                mCommonBottomResultAnim.setVisibility(View.VISIBLE);
                mConvertTopFullnameAnim.setVisibility(View.VISIBLE);
                mConvertTopAbbreviationAnim.setVisibility(View.VISIBLE);
                mConvertBottomFullnameAnim.setVisibility(View.VISIBLE);
                mConvertBottomAbbreviationAnim.setVisibility(View.VISIBLE);

//                mCommonTopFormulaEditText.setVisibility(View.INVISIBLE);
                mCommonBottomResultEditText.setVisibility(View.INVISIBLE);
                mConvertTopFullname.setVisibility(View.INVISIBLE);
                mConvertTopAbbreviation.setVisibility(View.INVISIBLE);
                mConvertBottomFullname.setVisibility(View.INVISIBLE);
                mConvertBottomAbbreviation.setVisibility(View.INVISIBLE);
                mCommonTopBottomDivider.setVisibility(View.INVISIBLE);
            } else {
                mSwitchLayoutDividerAnim.setVisibility(View.GONE);
                mSwitchLayoutAnim.setVisibility(View.GONE);
                mCommonTopFormulaAnim.setVisibility(View.INVISIBLE);
                mCommonBottomResultAnim.setVisibility(View.INVISIBLE);
                mConvertTopFullnameAnim.setVisibility(View.INVISIBLE);
                mConvertTopAbbreviationAnim.setVisibility(View.INVISIBLE);
                mConvertBottomFullnameAnim.setVisibility(View.INVISIBLE);
                mConvertBottomAbbreviationAnim.setVisibility(View.INVISIBLE);
                mSwitchLayoutAnim.clearAnimation();
                mSwitchLayoutBottomAnim.clearAnimation();
                mSwitchLayoutTopAnim.clearAnimation();

                mCommonTopFormulaEditText.setVisibility(View.VISIBLE);
                mCommonBottomResultEditText.setVisibility(View.VISIBLE);
                mConvertTopFullname.setVisibility(View.VISIBLE);
                mConvertTopAbbreviation.setVisibility(View.VISIBLE);
                mConvertBottomFullname.setVisibility(View.VISIBLE);
                mConvertBottomAbbreviation.setVisibility(View.VISIBLE);
                mCommonTopBottomDivider.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void computeAnimHeight() {
        float topHeight, bottomHeight;
        if (Calculator.mScreenState == SCREENPORTFULL || Calculator.mScreenState == SCREENLANDFULL) {
            topHeight = getResources().getDimension(R.dimen.switch_layout_top_anim_height);
            bottomHeight = getResources().getDimension(R.dimen.switch_layout_bottom_height);
        } else {
            topHeight = getResources().getDimension(R.dimen.switch_layout_top_anim_height_split);
            bottomHeight = getResources().getDimension(R.dimen.switch_layout_bottom_height_split);
        }
        float paddingHeight = getResources().getDimension(R.dimen.convert_item_parent_padding);
        mTopFrom = 0.0f;
        mTopTo = topHeight - (topHeight - bottomHeight) / 2 - paddingHeight;
        mBottomFrom = 0.0f;
        mBottomTo = -topHeight + (topHeight - bottomHeight) / 2 + paddingHeight;
    }

    private void rotateAnim() {
        ObjectAnimator convertRotateAnim = ObjectAnimator.ofFloat(mExchangeBtn, "rotation", 0.0f, 360.0f);
        convertRotateAnim.setDuration(ANIM_TIME);
        AnimatorSet animSetXY = new AnimatorSet();
        animSetXY.play(convertRotateAnim);
        animSetXY.start();
    }

    public void requestCursor() {
        if (mCommonTopFormulaEditText != null) {
            mCommonTopFormulaEditText.setCursorVisible(true);
            mCommonTopFormulaEditText.setFocusable(true);
            mCommonTopFormulaEditText.setFocusableInTouchMode(true);
            mCommonTopFormulaEditText.requestFocus();
            mCommonTopFormulaEditText.requestFocusFromTouch();
            mCommonTopFormulaEditText.findFocus();
        }
    }

    private void cancelCursor() {
        if (mCommonTopFormulaEditText != null) {
            mCommonTopFormulaEditText.setCursorVisible(false);
            mCommonTopFormulaEditText.clearFocus();
        }
    }

    private static final int EXCHANGE_CONVERT_ANIM_END = 0;
    private static final int CONVERTER_SHOW_POPWINDOW = 1;
    Handler mUIHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case EXCHANGE_CONVERT_ANIM_END:
                    copyAnimData(false);
                    showAnimView(false);
                    break;
                case CONVERTER_SHOW_POPWINDOW:
                    showPopupWindow(mConvertTopItemLayout);
                    break;
            }
        }
    };

    private void resetPadViewPagerLayout() {
        try {
            float displayViewHeight = mDisplayView.getHeight();
            float recyclerViewHeight = mDisplayView.getCommonMyRecyclerView().getHeight();
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mCalculatorPadViewPager.getLayoutParams();
            params.removeRule(RelativeLayout.BELOW);
            params.topMargin = (int) (displayViewHeight - recyclerViewHeight);
            mCalculatorPadViewPager.setLayoutParams(params);
            mDisplayView.bringToFront();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private boolean isMoving = false;

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    /**
     * Show PopupWindow.
     *
     * @param view
     */
    private void showPopupWindow(View view) {
        /* MODIFIED-BEGIN by qiong.liu1, 2017-04-21,BUG-4452809*/
        if (Calculator.mScreenState == SCREENPORTFULL) {
            mConvertPopWindow.showAsDropDown(view, 0, -45);
        }else {
            mActivity.setShowExpandLayout(true);
            mCommonContainer.setPadding(15, 0, 0, 0);
            mConvertPopWindow.showAsDropDown(view, 0, -11);
        }
        mCommonTopContainer.setBackground(getResources().getDrawable(R.drawable.calculator_edittext_bg));
        /* MODIFIED-END by qiong.liu1,BUG-4452809*/
        cancelCursor();
    }

    private void setWheelItemData(WheelView wheel, int rightIndex, int leftIndex) {

        try {
            Log.i(TAG, "onScrollingFinished,leftIndex=" + mLeftIndex + ",rightIndex=" + mRightIndex + ",WHEEL=" + (ScrollWheelPickerView.WHEEL) wheel.getTag());
            mCurrentConvert.setDefaultLeftUnitIndex(leftIndex);
            mCurrentConvert.setDefaultRightUnitIndex(rightIndex);
            ScrollWheelPickerView.WHEEL wheelType = (ScrollWheelPickerView.WHEEL) wheel.getTag();
            switch (wheelType) {

                case LEFT_WHEEL:
                    String topFullName = mCurrentConvert.getDefaultLeftUnit().getFullName();
                    topFullName = topFullName.substring(0, 1).toUpperCase() + topFullName.substring(1);
                    mConvertTopFullname.setText(topFullName);
                    mConvertTopAbbreviation.setText(mCurrentConvert.getDefaultLeftUnit().getAbbreviation());
                    break;
                case RIGHT_WHEEL:
                    String bottomFullName = mCurrentConvert.getDefaultRightUnit().getFullName();
                    bottomFullName = bottomFullName.substring(0, 1).toUpperCase() + bottomFullName.substring(1);
                    mConvertBottomFullname.setText(bottomFullName);
                    mConvertBottomAbbreviation.setText(mCurrentConvert.getDefaultRightUnit().getAbbreviation());
                    break;
            }
            performConvert();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * when cancel button click in the convert wheel list
     * set the original unit back
     */
    private void resetCurrentUnit() {
        if (null != mWheelView) {
            try {
                mCurrentConvert.setDefaultLeftUnitIndex(mCurrentTopIndex);
                mCurrentConvert.setDefaultRightUnitIndex(mCurrentButtonIndex);
                mLeftIndex = mCurrentTopIndex;
                mRightIndex = mCurrentButtonIndex;
                String topFullName = mCurrentConvert.getDefaultLeftUnit().getFullName();
                topFullName = topFullName.substring(0, 1).toUpperCase() + topFullName.substring(1);
                mConvertTopFullname.setText(topFullName);
                mConvertTopAbbreviation.setText(mCurrentConvert.getDefaultLeftUnit().getAbbreviation());
                String bottomFullName = mCurrentConvert.getDefaultRightUnit().getFullName();
                bottomFullName = bottomFullName.substring(0, 1).toUpperCase() + bottomFullName.substring(1);
                mConvertBottomFullname.setText(bottomFullName);
                mConvertBottomAbbreviation.setText(mCurrentConvert.getDefaultRightUnit().getAbbreviation());
                performConvert();
                mCalculatorWheelPicker.updateWheel(mLeftIndex,mRightIndex,mCurrentConvert.getUnit(),mCurrentConvert.getUnit());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isNumeric(String str){
        Pattern pattern = Pattern.compile("-?[0-9]+.?[0-9]+");
        Matcher isNum = pattern.matcher(str);
        if( !isNum.matches() ){
            return false;
        }
        return true;
    }

}
