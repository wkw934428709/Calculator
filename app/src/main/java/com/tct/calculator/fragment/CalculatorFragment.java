package com.tct.calculator.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
/* MODIFIED-BEGIN by kaifeng.lu, 2016-11-04,BUG-3005276*/
import android.content.pm.ActivityInfo;
import android.content.pm.ConfigurationInfo;
import android.content.res.Configuration;
/* MODIFIED-END by kaifeng.lu,BUG-3005276*/
import android.database.Cursor;
import android.graphics.Color; // MODIFIED by qiong.liu1, 2017-03-27,BUG-3621966
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
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
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tct.calculator.Calculator;
import com.tct.calculator.Calculator.ScreenState;
import com.tct.calculator.CalculatorEditText;
import com.tct.calculator.CalculatorExpressionBuilder;
import com.tct.calculator.CalculatorExpressionEvaluator;
import com.tct.calculator.CalculatorExpressionTokenizer;
import com.tct.calculator.CalculatorPadViewPager;
import com.tct.calculator.R;
import com.tct.calculator.data.HistoryContentUri;
import com.tct.calculator.data.PatternUtil;
import com.tct.calculator.utils.Constant;
/* MODIFIED-BEGIN by kaifeng.lu, 2016-11-17,BUG-3005276*/
import com.tct.calculator.utils.CopyPasteUtils;
import com.tct.calculator.utils.CopyPasteUtils;
/* MODIFIED-END by kaifeng.lu,BUG-3005276*/
import com.tct.calculator.utils.Utils;
import com.tct.calculator.view.DisplayOverlay;
import com.tct.calculator.view.History;
import com.tct.calculator.view.HistoryAdapter;
import com.tct.calculator.view.HistoryItem;
import com.tct.calculator.view.interfaces.ChangeViewInParentCallBack;
import com.tct.calculator.view.interfaces.ICalculatorInterfaces;

import org.javia.arity.SyntaxException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static com.tct.calculator.Calculator.ScreenState.*;

/**
 * Created by user on 16-9-28.
 */
/* MODIFIED-BEGIN by qiong.liu1, 2017-04-07,BUG-4452809*/
public class CalculatorFragment extends Fragment implements
        CalculatorEditText.OnTextSizeChangeListener,
        CalculatorExpressionEvaluator.EvaluateCallback, View.OnLongClickListener ,
        View.OnClickListener ,ChangeViewInParentCallBack , ICalculatorInterfaces{
        /* MODIFIED-END by qiong.liu1,BUG-4452809*/

    private static final String NAME = "com.tct.calculator";
    // instance state keys
    private static final String KEY_CURRENT_STATE = NAME + "_currentState";
    private static final String KEY_CURRENT_EXPRESSION = NAME + "_currentExpression";
    //PR1012727 The display of app is not match with it in recent key by tingma at 2015-06-02
    private static final String KEY_CURRENT_VIEWPAGER = "current_viewpager";
    private static final String KEY_DISPLAY_MODE = NAME + "_displayMode";
    private static final String TAG = "Calculator";// MODIFIED by kaifeng.lu, 2016-03-23,BUG-1722335
    private static final String KEY_CURRENT_TRANSLATE_STATE = NAME + "_currentTranslateState";
    private static final String KEY_CURRENT_EVALUATOR_MODE = NAME + "_currentEvaluatorMode";
    private static final String KEY_CURRENT_EVALUATOR_IS_EXPAND = NAME + "_currentEvaluatorIsExpand"; // MODIFIED by qiong.liu1, 2017-05-02,BUG-4598039
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
    private static final String CALCULATOR_FORMULA_STATE = "calculator_formula_state";
    private static final String FORMULA_PASTE_SHOW = "formula_paste_show";
    private static final String FORMULA_COPY_SHOW = "formula_copy_show";
    private static final String RESULT_COPY_SHOW = "result_copy_show";
    private static final String FORMULA_TEXT = "formula_text";
    /* MODIFIED-BEGIN by qiong.liu1, 2017-04-13,BUG-4452809*/
    private static final String NEED_EXPAND = "nedd_expand";
    private static final String IS_ERROR = "is_error";
    /* MODIFIED-END by qiong.liu1,BUG-4452809*/
    private final String SHOW_BY_LOCKED = "showByLocked";
    private final String IS_SERCURE = "IsSecure";
    public History mHistory;
    private HistoryAdapter mHistoryAdapter;
    /* MODIFIED-BEGIN by kaifeng.lu, 2016-08-16,BUG-2712192*/
    private SetHistoryCopyStatue mSetHistoryCopyStatue;
    //TS:kaifeng.lu 2015-12-30 Calculator BUGFIX_1271732 ADD_S
    private boolean mDataFromHistory = false;
    /* MODIFIED-END by kaifeng.lu,BUG-2712192*/

    private Display mDisplay;
    private View mFormatView;
    private int evaluateCount = 0; // MODIFIED by kaifeng.lu, 2016-06-02,BUG-2222411

    /* to handle the states when scream resume */
    /* MODIFIED-BEGIN by kaifeng.lu, 2016-11-17,BUG-3005276*/
    private final static String  CalculatorStateSFLAG = "calculator_states_flag";
    private final static int CalculatorStateSRESULT =  0x0001;
    private final static int CalculatorStateSERROR =  0x0002;
    private final static int CalculatorStateSEVALUATE =  0x0003;
    private final static int CalculatorStateSINPUT =  0x0004;
    private boolean mIsResultState = false;
    private CopyPasteUtils mCopyPasteUtils = CopyPasteUtils.getInstance();
    /* MODIFIED-END by kaifeng.lu,BUG-3005276*/
    private Constant.CalculatorState mCalculateState;



//    to control the screen viewsize in split modle
    private int mScreenState;
    private final static int SCREENPORTFULL = 0x0001;
    private final static int SCREENPORTQUTERTHREE  = 0x0002;
    private final static int SCREENPORTHALF = 0x0003;
    private final static int SCREENPORTQUTER = 0x0004;
    private final static int SCREENLANDFULL = 0x0005;
    private final static int SCREENLANDHALF = 0x0006;

    private String mCurrentFormulate ;
    private static boolean mIsErrorStates; // MODIFIED by qiong.liu1, 2017-04-13,BUG-4452809
    private boolean mIsExpand;

    @Override
    public void changCalculatorView(float translaionY) {
        if(mContext != null){
            ((Calculator)mContext).hidGuideButton(translaionY);
        /* MODIFIED-BEGIN by kaifeng.lu, 2016-11-04,BUG-3005276*/
        }else{

        }
        /* MODIFIED-END by kaifeng.lu,BUG-3005276*/
    }

    @Override
    public void setCalculateText(Editable calculateText) {
        mCalculateString = calculateText;
    }

    @Override
    public void setResultText(Editable resultText) {
        mResultEditString = resultText;
    }

    @Override
    public void setCalculateLongclickState(boolean isLongclick) {
        isTempCalculateLongClick = isLongclick;
    }

    @Override
    public void setResultLongclickState(boolean isLongclick) {
        isTempResultLongClicked = isLongclick;
    }

    @Override
    public void setCalulateState(int mode) {
        mTempCurrentState = Constant.CalculatorState.values()[mode];
    }

    @Override
    public void setCopyPasteState(boolean state) {
        copyPasteState = state;
    }

    @Override
    public void setHistory(History history) {
        if (mHistory == null) {
            mHistory = new History(cr);
        } else {
            mHistory.setData(history.getEntries());
        }
    }

    @Override
    public void setRadVisibility(int visiVisbility) {
        mRadVisibility = visiVisbility;
    }

    private final TextWatcher mFormulaTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int start, int count, int after) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            Log.i(TAG, "afterTextChanged start " + System.currentTimeMillis()); // MODIFIED by kaifeng.lu, 2016-05-25,BUG-2162730

            /* MODIFIED-BEGIN by kaifeng.lu, 2016-09-22,BUG-2960280*/
            Log.i(TAG, "afterTextChanged: " + editable.toString());
            if (editable != null && mFormulaEditText != null) {
                mFormulaEditText.removeTextChangedListener(this);
                mFormulaEditText.setText(getCommaString(editable.toString()));
                mFormulaEditText.addTextChangedListener(this);
            }

            /* MODIFIED-END by kaifeng.lu,BUG-2960280*/
            //TS:kaifeng.lu 2016-04-27 Calculator BUGFIX_ 1999448  ADD_S
            Log.i(TAG, "---afterTextChanged---");// MODIFIED by kaifeng.lu, 2016-03-23,BUG-1722335
           /* setState(Constant.CalculatorState.INPUT);
            mEvaluator.evaluate(editable, Calculator.this);*/
            if ((mCurrentState == Constant.CalculatorState.EVALUATE && editable.toString().contains("e")) || (mCurrentState == Constant.CalculatorState.RESULT)) {
                setState(Constant.CalculatorState.INPUT);
                if (mResultEditText.getText().length() != 0) {
                    mResultEditText.setText("");
                }
            } else {
                setState(Constant.CalculatorState.INPUT);
                mEvaluator.evaluate(editable.toString().replaceAll(" ", "").replaceAll(",", "."), CalculatorFragment.this);
            }

            //TS:kaifeng.lu 2016-04-27 Calculator BUGFIX_ 1999448  ADD_E
            Log.v(TAG, "mResultEditText = " + mResultEditText.getText());
            Log.v(TAG, " editable.toString() = " + editable.toString());
            Log.i(TAG, "Constant.CalculatorState.now = " + Constant.CalculatorState.values()); // MODIFIED by kaifeng.lu, 2016-11-17,BUG-3005276

            Log.i(TAG, "afterTextChanged start " + System.currentTimeMillis()); // MODIFIED by kaifeng.lu, 2016-05-25,BUG-2162730

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
            /* MODIFIED-BEGIN by kaifeng.lu, 2016-11-17,BUG-3005276*/
            final boolean isEdited = mCurrentState == Constant.CalculatorState.INPUT
                    || mCurrentState == Constant.CalculatorState.ERROR;
            return new CalculatorExpressionBuilder(source, mTokenizer, isEdited);
        }
    };
    public Constant.CalculatorState mCurrentState;
    /* MODIFIED-END by kaifeng.lu,BUG-3005276*/
    private CalculatorExpressionTokenizer mTokenizer;
    private CalculatorExpressionEvaluator mEvaluator;
    private DisplayOverlay mDisplayView;
    public CalculatorEditText mFormulaEditText;
    public CalculatorEditText mResultEditText;
    private CalculatorPadViewPager mPadViewPager;
    private View mDeleteButton;
    private View mEqualButton;
    private View mClearButton;
    private SharedPreferences pref;
    private Button formulaCopyBtn, formulaPasteBtn, resultCopyBtn;
    public boolean copyPasteState;
    private ClipboardManager clipboard;
    private Button tipsBtn;

    private TextView degTv;
    public TextView radTv;
    private Button invBtn, radBtn, degBtn, sinBtn, asinBtn, cosBtn, acosBtn, tanBtn, atanBtn, lnBtn, naturExpBtn, logBtn, comExpBtn, sqrtBtn, sqrBtn, mBtnExPow;
    private Button mBtn0 ,mBtn1 ,mBtn2 ,mBtn3 ,mBtn4 ,mBtn5 ,mBtn6 ,mBtn7 ,mBtn8 ,mBtn9 ;
    private View mBtndiv,mBtndel,mBtnmul,mBtnclear,mBtnsub,mBtnexpend,mBtnpoint,mBtnadd,mBtneq,mBtnClear2;
    private View mPad_advence;
    private View mBtnex0 ,mBtnex1 ,mBtnex2 ,mBtnex3 ,mBtnex4 ,mBtnex5 ,mBtnex6 ,mBtnex7 ,mBtnex8 ,mBtnex9 ;
    private View mBtnExPer,mBtnExPi,mBtnExE,mBtnExFact,mBtnExDel,mBtnExLparen,mBtnExRparen,mBtnBackAdvenced;
    private View  mBtnexdiv,mBtnexsub,mBtnexmul,mBtnexpoint,mBtnexadd,mBtnexeq;
    private static String[] buttonTexts = {"ln(", "log(", "sin(", "cos(", "tan(", "exp(", "sin⁻¹(", "cos⁻¹(", "tan⁻¹("};
    private static boolean inv = false;
    private static boolean needExpand = false;
    private boolean isInitialize = false;
    private boolean isFormulaCopyShow = false;
    private boolean isFormulaPasteShow = false;
    private boolean isResultCopyShow = false;

    ContentResolver cr;
    private View mCurrentButton;
    private Animator mCurrentAnimator;
    //PR1012727 The display of app is not match with it in recent key by tingma at 2015-06-02
    private int mCurrentViewPager = 0, mCurrentTranslate = 0, mCurrentEvaMode = 0;
    private FrameLayout.LayoutParams mLayoutParams =
            new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, 0);
    private Context mContext;
    //TS:kaifeng.lu 2015-12-23 Calculator BUGFIX_1202285 ADD_S
    public boolean isFormulaLongClicked = false;
    public boolean isResultLongClicked = false;
    //TS:kaifeng.lu 2015-12-23 Calculator BUGFIX_1202285 ADD_E
    private boolean isRegisted = false;
    private float mMaxScreemHeight = 0;
    private int mCreatCount = 0;
    private Button mPoint;
    private boolean mIsStanderPoint = true;

    /* MODIFIED-BEGIN by kaifeng.lu, 2016-11-04,BUG-3005276*/
    public Editable mCalculateString;
    public Editable mResultEditString;
    private boolean isTempCalculateLongClick;
    private boolean isTempResultLongClicked;
    private int mRadVisibility;
    private Constant.CalculatorState mTempCurrentState; // MODIFIED by kaifeng.lu, 2016-11-17,BUG-3005276
    private Typeface mNormalTf;

    /**
     * Update data from landscape fragment
     */
    private void updateFragmentState(){
        if (isInitialize && mHistory != null
                && mCalculateString != null && mResultEditString != null) {
            // Reset state
            cancelCopyPaste();

            // Set rad and deg button visibility
            if (mRadVisibility == View.VISIBLE) {
                radClick();
            } else {
                degClick();
            }

            // Set string from landscape fragment
            mFormulaEditText.setText(mCalculateString);
            mResultEditText.setText(mResultEditString);
            mCurrentState = mTempCurrentState; // MODIFIED by kaifeng.lu, 2016-11-17,BUG-3005276

            // If the formulate is longclicked
            if (isTempCalculateLongClick) {
                displayFormulaCopyPasteButton(true);
                mFormulaEditText.setPressed(true);
                Selection.selectAll(mFormulaEditText.getText());
                isFormulaLongClicked = true;
                copyPasteState = true;
            /* MODIFIED-BEGIN by kaifeng.lu, 2016-11-17,BUG-3005276*/
            }

            // Copy result
            if (isTempResultLongClicked && mCurrentState == Constant.CalculatorState.RESULT && !mCopyPasteUtils.isCanPaste(mCurrentState)) {
                clipboard.setPrimaryClip(ClipData.newPlainText(null, mFormulaEditText.getText()));
                Toast.makeText(mContext, mContext.getString(R.string.copy_result_toast), Toast.LENGTH_SHORT).show();
            }

            // If the result is longclicked
            if (isTempResultLongClicked) {
            /* MODIFIED-END by kaifeng.lu,BUG-3005276*/
                isResultLongClicked = true;
                copyPasteState = true;
                Selection.selectAll(mResultEditText.getText());
                mResultEditText.setPressed(true);
                displayResultCopyButton(true);
            }

            // Reset the history
            HistoryAdapter historyAdapter = (HistoryAdapter) mDisplayView.getHistoryView().getAdapter();
            if (historyAdapter != null) {
                // If the history number is 0, inform recycler view
                if (mHistory.getEntries().size() == 0) {
                    cr.delete(HistoryContentUri.CONTENT_URI, "", null);
                    mDisplayView.setMaxTranlation(-1);
                    mDisplayView.initializeHistoryAndGraphView(false);
                } else { // Inform recyclerview
                    mDisplayView.scrollToMostRecent();
                    mDisplayView.initializeHistoryAndGraphView(false);
                }
            }

            // Reinitialize
            mCalculateString = null;
            mResultEditString = null;
            isTempResultLongClicked = false;
            isTempResultLongClicked = false;
        }
    }

    public CalculatorFragment() {}


    /**
     * When landscaping, call this function
     * @param hidd
     */
    @Override
    public void onHiddenChanged(boolean hidd){
        Calculator activity = (Calculator) getActivity();

//        if(activity!=null && activity.mCurrentFragment!=activity.CALCULATORLANDSCAPE) {
//            getActivity().setRequestedOrientation(hidd ?
//                    ActivityInfo.SCREEN_ORIENTATION_NOSENSOR
//                    : ActivityInfo.SCREEN_ORIENTATION_SENSOR);
//        }

        if(!hidd)
            updateFragmentState();
    }
    /* MODIFIED-END by kaifeng.lu,BUG-3005276*/

    @Nullable
    @Override
    public View  onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =null;
        if(null != savedInstanceState) {
            isFormulaCopyShow = savedInstanceState.getBoolean(FORMULA_COPY_SHOW, false);
            isFormulaPasteShow = savedInstanceState.getBoolean(FORMULA_PASTE_SHOW, false);
            isResultCopyShow = savedInstanceState.getBoolean(RESULT_COPY_SHOW, false);
            mCalculateState = (Constant.CalculatorState) savedInstanceState.getSerializable(CALCULATOR_FORMULA_STATE);
            mCurrentFormulate = savedInstanceState.getString(FORMULA_TEXT);
            /* MODIFIED-BEGIN by qiong.liu1, 2017-05-02,BUG-4598039*/
            mCurrentEvaMode = savedInstanceState.getInt(KEY_CURRENT_EVALUATOR_MODE, 0);
            mIsExpand = savedInstanceState.getBoolean(KEY_CURRENT_EVALUATOR_IS_EXPAND, false);
        } else {
            restoreStateFromArguments();
            /* MODIFIED-END by qiong.liu1,BUG-4598039*/
        }
        if(Calculator.mScreenState == SCREENPORTFULL) {
            view = inflater.inflate(R.layout.fragment_calculator, container, false);
        }else if(Calculator.mScreenState == SCREENPORTQUTERTHREE){
            view = inflater.inflate(R.layout.fragment_calculator_halfscreen, container, false);
        }else if(Calculator.mScreenState == SCREENPORTHALF || mScreenState == SCREENPORTQUTER){
            view = inflater.inflate(R.layout.fragment_calculator_halfscreen, container, false);
        }else if(Calculator.mScreenState == SCREENLANDFULL){
            view = inflater.inflate(R.layout.fragment_calculator_landscape, container, false);
        }else if(Calculator.mScreenState == SCREENLANDHALF){
            view = inflater.inflate(R.layout.fragment_calculator_halfscreen, container, false);
        }
        Log.d(TAG, " Calculator onCreateView " + Calculator.mScreenState); // MODIFIED by qiong.liu1, 2017-03-30,BUG-3621966
        intView(view);
        findViews(view);
        mFormulaEditText.setText(mCurrentFormulate);
        return view;
    }


    protected void intView(View view) {
        mContext = this.getActivity();
        cr = mContext.getContentResolver();
        mDisplayView = (DisplayOverlay) view.findViewById(R.id.display);
        mDisplayView.setChangeViewCallBack(this);
        mFormulaEditText = (CalculatorEditText) view.findViewById(R.id.formula);
        mResultEditText = (CalculatorEditText) view.findViewById(R.id.result);
        mPadViewPager = (CalculatorPadViewPager) view.findViewById(R.id.pad_pager);
        formulaCopyBtn = (Button) view.findViewById(R.id.formula_copy_btn);
        formulaPasteBtn = (Button) view.findViewById(R.id.formula_paste_btn);
        resultCopyBtn = (Button) view.findViewById(R.id.result_copy_btn);
        clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        tipsBtn = (Button) view.findViewById(R.id.tips_btn);
        mPoint = (Button) view.findViewById(R.id.dec_point);
        mPad_advence = view.findViewById(R.id.calculator_advenced);
        radTv = (TextView) mDisplayView.findViewById(R.id.tv_rad);
        degTv = (TextView) mDisplayView.findViewById(R.id.tv_deg);
//        mClearButton = view.findViewById(R.id.clr);
        mEqualButton = view.findViewById(R.id.eq);
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
        mFormatView = view.findViewById(R.id.ll_formula_result);


        mBtndiv =  view.findViewById(R.id.op_div);
        if(Calculator.mScreenState != SCREENLANDFULL){
            mBtndel =  view.findViewById(R.id.del);
            mBtndel.setOnClickListener(this);
            mBtndel.setOnLongClickListener(this);
        }
        mBtnmul =  view.findViewById(R.id.op_mul);
        mBtnclear =  view.findViewById(R.id.clear);
        mBtnClear2 = view.findViewById(R.id.del2);
        mBtnsub =  view.findViewById(R.id.op_sub);
        mBtnexpend =  view.findViewById(R.id.expend);
        mBtnpoint =  view.findViewById(R.id.dec_point);
        mBtnadd =  view.findViewById(R.id.op_add);
        mBtneq =  view.findViewById(R.id.eq);

        mBtndiv.setOnClickListener(this);

        mBtnmul.setOnClickListener(this);
        mBtnclear.setOnClickListener(this);
        mBtnClear2.setOnClickListener(this);
        mBtnsub.setOnClickListener(this);
        mBtnexpend.setOnClickListener(this);
        mBtnpoint.setOnClickListener(this);
        mBtnadd.setOnClickListener(this);
        mBtneq.setOnClickListener(this);
        mBtnclear.setOnLongClickListener(this);
        resultCopyBtn.setOnClickListener(this);
        formulaCopyBtn.setOnClickListener(this);
        formulaPasteBtn.setOnClickListener(this);

        mFormulaEditText.setOnClickListener(this);
        mResultEditText.setOnClickListener(this);


        mPadViewPager.setCalculatorType(Constant.CalculatorType.CALCULATOR);
        if (mPoint.getText().equals(",")) {
            mIsStanderPoint = false;
        }

        try {
            if (mEqualButton == null || mEqualButton.getVisibility() != View.VISIBLE) {
                mEqualButton = view.findViewById(R.id.pad_operator).findViewById(R.id.eq);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        mTokenizer = new CalculatorExpressionTokenizer(this.getActivity());
        mEvaluator = new CalculatorExpressionEvaluator(mTokenizer);

        mEvaluator.evaluate(mFormulaEditText.getText().toString().replaceAll(" ", "").replaceAll(",", "."), this);

        mFormulaEditText.setEditableFactory(mFormulaEditableFactory);
        mFormulaEditText.addTextChangedListener(mFormulaTextWatcher);
        mFormulaEditText.setOnKeyListener(mFormulaOnKeyListener);
        mFormulaEditText.setOnTextSizeChangeListener(this);
        mFormulaEditText.setOnLongClickListener(this);
        mResultEditText.setOnLongClickListener(this);

        mDisplayView.bringToFront();
        DisplayOverlay.DisplayMode displayMode = DisplayOverlay.DisplayMode.FORMULA;

        mDisplayView.setMode(displayMode);
        mDisplayView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        if (mDisplayView.getHeight() > 0) {
                            mDisplayView.initializeHistoryAndGraphView(true);
                            if (mDisplayView.getMode() == DisplayOverlay.DisplayMode.GRAPH) {
                                //  mGraphController.startGraph(mFormulaEditText.getText());
                            }
                        }
                    }
                });
        mDisplayView.setTranslateStateListener(new DisplayOverlay.TranslateStateListener() {
            @Override
            public void onTranslateStateChanged(DisplayOverlay.TranslateState newState) {
                cancelCopyPaste();
                /* MODIFIED-BEGIN by qiong.liu1, 2017-04-13,BUG-4452809*/
                if (newState == DisplayOverlay.TranslateState.EXPANDED) {
                    needExpand = true;
                } else if (newState == DisplayOverlay.TranslateState.COLLAPSED) {
                    needExpand = false;
                } else {
                    // TranslateState is PARTIAL, we don't need to change the value of SharedPreferences.
                }
                /* MODIFIED-END by qiong.liu1,BUG-4452809*/
            }
        });

        //PR540014 START clicking number's panel close scientic's panel when scientic 's panel  totally show.27/8/2015 update by xiaolu.li
        if (mPadViewPager != null) {
            ((CalculatorPadViewPager) mPadViewPager).setOnTouchUpFirstPagerOnBackgroundListener(new CalculatorPadViewPager.onTouchUpFirstPagerOnBackgroundListener() {
                @Override
                public void onTouchUpFirstPager() {
                    if (mPadViewPager.getCurrentItem() == 1) {
                        // Otherwise, select the previous pad.
                        mPadViewPager.setCurrentItem(mPadViewPager.getCurrentItem() - 1);
                    }
                }

                /* MODIFIED-BEGIN by qiong.liu1, 2017-04-14,BUG-4452809*/
                @Override
                public void onDEGModeSelect() {
                    degClick();
                }
                /* MODIFIED-END by qiong.liu1,BUG-4452809*/
            });
        }

        mFormulaEditText.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View view, DragEvent dragEvent) {
                if (dragEvent.getAction() == DragEvent.ACTION_DROP) {

                    if (!TextUtils.isEmpty(dragEvent.getClipData().getItemAt(0).getText())) {
                        mFormulaEditText.setText(getCommaString(dragEvent.getClipData().getItemAt(0).getText().toString()));
                    }
                }
                return true;
            }
        });

//        if(Calculator.mScreenState == SCREENLANDFULL) {
//            Utils.initEqualsBtnSize(mEqualButton, getActivity(), false);
//            Utils.initEqualsBtnSize(mBtneq, getActivity(), false);
//        }
        /* MODIFIED-BEGIN by qiong.liu1, 2017-05-02,BUG-4598039*/
        if (mCurrentEvaMode == 1 && mIsExpand) {
            mPadViewPager.setCurrentItem(1);
            mEvaluator.setEvaluatorMode(CalculatorExpressionEvaluator.EvaluatorMode.RADIUS);
        }
        /* MODIFIED-END by qiong.liu1,BUG-4598039*/
    }

    private void findViews(View view) {
        isInitialize = true;
        radBtn= (Button) view.findViewById(R.id.op_rad);
        degBtn= (Button) view.findViewById(R.id.op_deg);
        invBtn= (Button) view.findViewById(R.id.op_inv);

        mNormalTf= invBtn.getTypeface();

        degBtn= (Button) view.findViewById(R.id.op_deg);
        radBtn= (Button) view.findViewById(R.id.op_rad);
        sinBtn= (Button) view.findViewById(R.id.fun_sin);
        asinBtn= (Button) view.findViewById(R.id.fun_arcsin);
        cosBtn= (Button) view.findViewById(R.id.fun_cos);

        acosBtn= (Button) view.findViewById(R.id.fun_arccos);
        tanBtn= (Button) view.findViewById(R.id.fun_tan);
        atanBtn= (Button) view.findViewById(R.id.fun_arctan);
        lnBtn= (Button) view.findViewById(R.id.fun_ln);
        naturExpBtn= (Button) view.findViewById(R.id.fun_naturExp);

        logBtn= (Button) view.findViewById(R.id.fun_log);
        comExpBtn= (Button) view.findViewById(R.id.fun_comExp);
        sqrtBtn= (Button) view.findViewById(R.id.op_sqrt);
        sqrBtn= (Button) view.findViewById(R.id.fun_sqr);

        if(Calculator.mScreenState != SCREENLANDFULL) {
            mBtnex0 = (Button) mPad_advence.findViewById(R.id.digit_0);
            mBtnex1 = (Button) mPad_advence.findViewById(R.id.digit_1);
            mBtnex2 = (Button) mPad_advence.findViewById(R.id.digit_2);
            mBtnex3 = (Button) mPad_advence.findViewById(R.id.digit_3);
            mBtnex4 = (Button) mPad_advence.findViewById(R.id.digit_4);
            mBtnex5 = (Button) mPad_advence.findViewById(R.id.digit_5);
            mBtnex6 = (Button) mPad_advence.findViewById(R.id.digit_6);
            mBtnex7 = (Button) mPad_advence.findViewById(R.id.digit_7);
            mBtnex8 = (Button) mPad_advence.findViewById(R.id.digit_8);
            mBtnex9 = (Button) mPad_advence.findViewById(R.id.digit_9);
            mBtnexdiv = mPad_advence.findViewById(R.id.op_div);
            mBtnexsub = mPad_advence.findViewById(R.id.op_sub);
            mBtnexmul = mPad_advence.findViewById(R.id.op_mul);
            mBtnexpoint = mPad_advence.findViewById(R.id.dec_point);
            mBtnexadd = mPad_advence.findViewById(R.id.op_add);
            mBtnexeq = mPad_advence.findViewById(R.id.eq);
            mBtnExDel = mPad_advence.findViewById(R.id.del);
            mBtnBackAdvenced = mPad_advence.findViewById(R.id.expand_bt);

            mBtnex0.setOnClickListener(this);
            mBtnex1.setOnClickListener(this);
            mBtnex2.setOnClickListener(this);
            mBtnex3.setOnClickListener(this);
            mBtnex4.setOnClickListener(this);
            mBtnex5.setOnClickListener(this);
            mBtnex6.setOnClickListener(this);
            mBtnex7.setOnClickListener(this);
            mBtnex8.setOnClickListener(this);
            mBtnex9.setOnClickListener(this);
            mBtnexdiv.setOnClickListener(this);
            mBtnexsub.setOnClickListener(this);
            mBtnexmul.setOnClickListener(this);
            mBtnexpoint.setOnClickListener(this);
            mBtnexadd.setOnClickListener(this);
            mBtnexeq.setOnClickListener(this);
            mBtnExDel.setOnClickListener(this);
            mBtnBackAdvenced.setOnClickListener(this);

        }

        mBtnExPer = mPad_advence.findViewById(R.id.op_per);
        mBtnExPi = mPad_advence.findViewById(R.id.const_pi);
        mBtnExE = mPad_advence.findViewById(R.id.const_e);
        mBtnExFact = mPad_advence.findViewById(R.id.op_fact);

        mBtnExPow = (Button) mPad_advence.findViewById(R.id.op_pow);
        mBtnExLparen = mPad_advence.findViewById(R.id.lparen);
        mBtnExRparen = mPad_advence.findViewById(R.id.rparen);


        radBtn.setOnClickListener(this);
        degBtn.setOnClickListener(this);
        invBtn.setOnClickListener(this);

        degBtn.setOnClickListener(this);
        radBtn.setOnClickListener(this);
        sinBtn.setOnClickListener(this);
        asinBtn.setOnClickListener(this);
        cosBtn.setOnClickListener(this);

        acosBtn.setOnClickListener(this);
        tanBtn.setOnClickListener(this);
        atanBtn.setOnClickListener(this);
        lnBtn.setOnClickListener(this);
        naturExpBtn.setOnClickListener(this);

        logBtn.setOnClickListener(this);
        comExpBtn.setOnClickListener(this);
        sqrtBtn.setOnClickListener(this);
        sqrBtn.setOnClickListener(this);

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


        mBtnExPer.setOnClickListener(this);
        mBtnExPi.setOnClickListener(this);
        mBtnExE.setOnClickListener(this);
        mBtnExFact.setOnClickListener(this);
        mBtnExPow.setOnClickListener(this);
        mBtnExLparen.setOnClickListener(this);
        mBtnExRparen.setOnClickListener(this);


        updateFragmentState();
    }


    public void onArea2Click(View view) {
        switch (view.getId()) {
            case R.id.formula_copy_btn:
                if (mIsStanderPoint) {
                    clipboard.setPrimaryClip(ClipData.newPlainText(null, mFormulaEditText.getText().toString().replaceAll(" ", "").replaceAll(",", ".")));
                } else {
                    clipboard.setPrimaryClip(ClipData.newPlainText(null, mFormulaEditText.getText().toString().replaceAll(" ", "").replaceAll("\\.", ",")));
                }
                /* MODIFIED-END by kaifeng.lu,BUG-2960280*/

                /* MODIFIED-BEGIN by kaifeng.lu, 2016-08-16,BUG-2712192*/
                mDataFromHistory = false;
                mDisplayView.collapseHistory();
                break;
            case R.id.formula_paste_btn:
                if (clipboard.hasPrimaryClip()) {
                    // PR568561,fixed by boyang@tcl.com,2015-10-15,begin.
                    if (mCurrentState == Constant.CalculatorState.RESULT && !TextUtils.isEmpty(mFormulaEditText.getText()) && !mDataFromHistory) { // MODIFIED by kaifeng.lu, 2016-11-17,BUG-3005276
                    /* MODIFIED-END by kaifeng.lu,BUG-2712192*/
                        reveal(mCurrentButton, R.color.calculator_accent_color, new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                mFormulaEditText.getEditableText().clear();
/* MODIFIED-BEGIN by kaifeng.lu, 2016-09-22,BUG-2960280*/
                                mFormulaEditText.append(clipboard.getPrimaryClip().getItemAt(0).getText().toString());
                            }
                        });
                    } else {
                        //TS: lin-zhou 2016-09-27 Calculator BUGFIX-2999162 MOD-Start
                        if (mDataFromHistory) {
                            setState(Constant.CalculatorState.INPUT); // MODIFIED by kaifeng.lu, 2016-11-17,BUG-3005276
                        }
                        //TS: lin-zhou 2016-09-27 Calculator BUGFIX-2999162 MOD-End
                        Editable editable = mFormulaEditText.getText();
                        int length = editable.length();
                        if (length > 0 && Character.isDigit(editable.charAt(length - 1)))
                            mFormulaEditText.append(getString(R.string.op_mul));
                        mFormulaEditText.append(clipboard.getPrimaryClip().getItemAt(0).getText().toString());
                    }
                    // PR568561,fixed by boyang@tcl.com,2015-10-15,end.
                }
                mDisplayView.collapseHistory();
                break;
            case R.id.result_copy_btn:
                if (mIsStanderPoint) {
                    clipboard.setPrimaryClip(ClipData.newPlainText(null, mResultEditText.getText().toString().replaceAll(",", ".").replaceAll(" ", "")));
                } else {
                    clipboard.setPrimaryClip(ClipData.newPlainText(null, mResultEditText.getText().toString().replaceAll("\\.", ",").replaceAll(" ", "")));
                }
                /* MODIFIED-END by kaifeng.lu,BUG-2960280*/
                displayResultCopyButton(false);
                mDataFromHistory = false;
                mDisplayView.collapseHistory();
                break;
            case R.id.result:
            case R.id.formula:
                if (mPadViewPager != null && mPadViewPager.getCurrentItem() > 0 && mDisplayView.getTranslateState() == DisplayOverlay.TranslateState.COLLAPSED) {
                    //PR536104  [Android 5.1][Calculator_v5.2.1.2.0303.0]The scientific calculator panel can slide back after swipe down the history of calculations.update by xiaolu.li AUG 25 2015
                    mPadViewPager.setCurrentItem(mPadViewPager.getCurrentItem() - 1);
                    mIsExpand = false; // MODIFIED by qiong.liu1, 2017-05-02,BUG-4598039
                }
                break;
            default:
                break;
        }
        cancelCopyPaste();
    }

    public void cancelCopyPaste() {
        //TS:kaifeng.lu 2015-12-23 Calculator BUGFIX_1202285 MOD_S
        isFormulaLongClicked = false;
        isResultLongClicked = false;
        displayFormulaCopyPasteButton(false);//when displayview state change, copy&paste must visibility change to gone
        displayResultCopyButton(false);
        mFormulaEditText.setPressed(false);
        mResultEditText.setPressed(false);
        //TS:kaifeng.lu 2015-12-23 Calculator BUGFIX_1202285 MOD_E
        Selection.removeSelection(mFormulaEditText.getText());
        Selection.removeSelection(mResultEditText.getText());
        copyPasteState = false;
    }

    public void displayFormulaCopyPasteButton(boolean show) {
        /* MODIFIED-BEGIN by kaifeng.lu, 2016-11-17,BUG-3005276*/
        formulaPasteBtn.setVisibility(show && mCopyPasteUtils.isCanPaste(mCurrentState) ? View.VISIBLE : View.GONE);
        formulaCopyBtn.setVisibility(show && mCopyPasteUtils.isCanCopyFormula(mCurrentState, mFormulaEditText) ? View.VISIBLE : View.GONE); // MODIFIED by kaifeng.lu, 2016-06-07,BUG-2281741
        /* MODIFIED-END by kaifeng.lu,BUG-3005276*/
    }

    public void displayResultCopyButton(boolean show) {
        resultCopyBtn.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onResume() {
        Log.i(TAG, "onResume start " + System.currentTimeMillis()); // MODIFIED by kaifeng.lu, 2016-05-25,BUG-2162730
        super.onResume();

        mHistory = getHistory();

        mHistoryAdapter = new HistoryAdapter(this.getActivity(), mHistory
                , new HistoryAdapter.HistoryHeadCallBack() {
            //PR 540691 don't collapse the history panel when long click history item to copy the result.->DELETE codes about longclick history item to collopse the history panel  28/8/2015 update by xiaolu.li
            @Override
            public void onHistoryHeadSelected() {

                mDisplayView.collapseHistory();
                (new Handler()).postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        Log.e("kftest","delete");
                        cr.delete(HistoryContentUri.CONTENT_URI, "", null);
                        ((HistoryAdapter) mDisplayView.getHistoryView().getAdapter()).clear();
                        mHistory.clear();
                        ((HistoryAdapter) mDisplayView.getHistoryView().getAdapter()).notifyDataSetChanged();
                        mDisplayView.setMaxTranlation(-1);
                        mDisplayView.initializeHistoryAndGraphView(false);
                    }

                }, 300);

            }
        });
        /* MODIFIED-BEGIN by kaifeng.lu, 2016-08-16,BUG-2712192*/
        mSetHistoryCopyStatue = new SetHistoryCopyStatue();
        mHistoryAdapter.setmHistoruCallback(mSetHistoryCopyStatue);
        /* MODIFIED-END by kaifeng.lu,BUG-2712192*/
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
        }
        needExpand = false; // MODIFIED by qiong.liu1, 2017-04-13,BUG-4452809
        //TS:kaifeng.lu 2015-11-27 Calculator BUGFIX_967237 ADD_S
        try {
            if (mDisplayView.getHistoryView().getAdapter().getItemCount() == 1) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mDisplayView.getTranslateState() != DisplayOverlay.TranslateState.COLLAPSED)
                            mDisplayView.collapseHistory();
                    }
                }, 1000);
            }
        } catch (NullPointerException e) {
            Log.d("Calcultor", "NullPointerException to close history");
        }
        //TS:kaifeng.lu 2015-11-27 Calculator BUGFIX_967237 ADD_S
        if (mEvaluator.getEvaluatorMode() == CalculatorExpressionEvaluator.EvaluatorMode.RADIUS) {
            radTv.setVisibility(View.VISIBLE);
            degBtn.setVisibility(View.VISIBLE);
            radBtn.setVisibility(View.GONE);
        }
        //TS:kaifeng.lu 2015-12-23 Calculator BUGFIX_1202285 ADD_S


        /* MODIFIED-BEGIN by kaifeng.lu, 2016-09-22,BUG-2960280*/
        if (!TextUtils.isEmpty(mFormulaEditText.getText())) {
            String currentStr;
            if (mIsStanderPoint) {
                currentStr = mFormulaEditText.getText().toString().replaceAll(",", ".");
            } else {
                currentStr = mFormulaEditText.getText().toString().replaceAll("\\.", ",");
            }
            mFormulaEditText.setText(currentStr);
            /* MODIFIED-BEGIN by qiong.liu1, 2017-04-13,BUG-4452809*/
            if (mIsErrorStates) {
                setState(Constant.CalculatorState.ERROR);
            }
            /* MODIFIED-END by qiong.liu1,BUG-4452809*/
        }
        /* MODIFIED-END by kaifeng.lu,BUG-2960280*/

        if (!TextUtils.isEmpty(mFormulaEditText.getText())) {
            mEvaluator.evaluate(mFormulaEditText.getText().toString().replaceAll(" ", "").replaceAll(",", "."), this);
        }

        //TS:kaifeng.lu 2015-12-23 Calculator BUGFIX_1202285 ADD_E

        Log.i(TAG, "onResume end " + System.currentTimeMillis()); // MODIFIED by kaifeng.lu, 2016-05-25,BUG-2162730
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();

        if(mIsResultState){
            setState(Constant.CalculatorState.RESULT); // MODIFIED by kaifeng.lu, 2016-11-17,BUG-3005276
        }
        if (isFormulaLongClicked) {
            copyPasteState = true;
            mFormulaEditText.setPressed(true);
            Selection.selectAll(mFormulaEditText.getText());
            displayFormulaCopyPasteButton(true);
        }
        if (isResultLongClicked) {
            copyPasteState = true;
            mResultEditText.setPressed(true);
            Selection.selectAll(mResultEditText.getText());
            displayResultCopyButton(true);
        }

        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(i == KeyEvent.KEYCODE_BACK && keyEvent.getAction() == KeyEvent.ACTION_UP){
                    if (mDisplayView.getTranslateState() == DisplayOverlay.TranslateState.EXPANDED) {
                        mDisplayView.collapseHistory();
                        // PR708792 cannot show recent history again since history has been shown and swipe down and click back.Fix by boyang@tcl.com,2015-10-14 begin
                        mDisplayView.scrollToMostRecent();
                        // PR708792 cannot show recent history again since history has been shown and swipe down and click back.Fix by boyang@tcl.com,2015-10-14 end
                        return true;
                    }
                    //longclick result ,so just hide
                    if (copyPasteState) {
                        cancelCopyPaste();
                        return true;
                    }
                    if (mPadViewPager == null || mPadViewPager.getCurrentItem() != 0) {

                        mPadViewPager.setCurrentItem(mPadViewPager.getCurrentItem() - 1);
                        return true;
                    }
                }
                return false;
            }
        });
        if (mCalculateState != null){
            mCurrentState = mCalculateState;
            mCalculateState = null;
        }
//        showCurrentState();
//        setState(mCurrentState); // MODIFIED by qiong.liu1, 2017-04-13,BUG-4452809
//        showCurrentState();
        if (isFormulaLongClicked || isFormulaPasteShow || isFormulaCopyShow) {
            copyPasteState = true;
            mFormulaEditText.setPressed(true);
            Selection.selectAll(mFormulaEditText.getText());
            displayFormulaCopyPasteButton(true);
        }

        if (isResultLongClicked || isResultCopyShow) {
            copyPasteState = true;
            mResultEditText.setPressed(true);
            Selection.selectAll(mResultEditText.getText());
            displayResultCopyButton(true);
        }

        isResultCopyShow = false;
        isFormulaCopyShow = false;
        isFormulaPasteShow = false;
        requestCursor(); // MODIFIED by qiong.liu1, 2017-05-05,BUG-4639606
    }

    //TS:kaifeng.lu 2016-01-11 Calculator BUGFIX_1392730  ADD_E
    private History getHistory() {
        Log.i(TAG, "getHistory start " + System.currentTimeMillis()); // MODIFIED by kaifeng.lu, 2016-05-25,BUG-2162730
        History history = new History(cr);
        Cursor cursor = null;
        cursor = cr.query(HistoryContentUri.CONTENT_URI, null, null, null, HistoryContentUri.HISTORY_TIMESTAP);
        if (cursor != null) {

            int formulaIndex = cursor.getColumnIndex(HistoryContentUri.HISTORY_FORMULA);
            int resultIndex = cursor.getColumnIndex(HistoryContentUri.HISTORY_RESULT);
            int timestap = cursor.getColumnIndex(HistoryContentUri.HISTORY_TIMESTAP);
            Vector<HistoryItem> mEntries = new Vector<HistoryItem>();
            if (formulaIndex != -1 && resultIndex != -1) {
                while (cursor.moveToNext()) {
                    HistoryItem item = new HistoryItem();
                    if (mIsStanderPoint) {
                        item.formula = cursor.getString(formulaIndex).replaceAll(",", ".");
                        item.result = cursor.getString(resultIndex).replaceAll(",", ".");
                    } else {
                        item.formula = cursor.getString(formulaIndex).toString().replaceAll("\\.", ",");
                        item.result = cursor.getString(resultIndex).toString().replaceAll("\\.", ",");
                    }
                    item.timeStap = cursor.getLong(timestap);
                    mEntries.add(item);
                }
                history.setData(mEntries);
            }
            //TS:kaifeng.lu 2015-11-30 EMAIL BUGFIX_983830 ADD_S
            cursor.close();
            //TS:kaifeng.lu 2015-11-30 EMAIL BUGFIX_983830 ADD_E
        }
        Log.i(TAG, "getHistory end " + System.currentTimeMillis()); // MODIFIED by kaifeng.lu, 2016-05-25,BUG-2162730
        return history;
    }

    /* MODIFIED-BEGIN by qiong.liu1, 2017-04-13,BUG-4452809*/
    private boolean restoreStateFromArguments() {
        Bundle b = getArguments();
        if (b != null) {
            isFormulaCopyShow = b.getBoolean(FORMULA_COPY_SHOW, false);
            isFormulaPasteShow = b.getBoolean(FORMULA_PASTE_SHOW, false);
            isResultCopyShow = b.getBoolean(RESULT_COPY_SHOW, false);
            mCalculateState = (Constant.CalculatorState) b.getSerializable(CALCULATOR_FORMULA_STATE);
            mCurrentFormulate = b.getString(FORMULA_TEXT);
            needExpand = b.getBoolean(NEED_EXPAND, false);
            mIsErrorStates = b.getBoolean(IS_ERROR, false);
            /* MODIFIED-BEGIN by qiong.liu1, 2017-05-02,BUG-4598039*/
            mCurrentEvaMode = b.getInt(KEY_CURRENT_EVALUATOR_MODE, 0);
            mIsExpand = b.getBoolean(KEY_CURRENT_EVALUATOR_IS_EXPAND, false);
            /* MODIFIED-END by qiong.liu1,BUG-4598039*/
            return true;
        }
        return false;
    }
    /* MODIFIED-END by qiong.liu1,BUG-4452809*/

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, " CalculatorFragment onDestoy "); // MODIFIED by qiong.liu1, 2017-03-30,BUG-3621966
        if (mDisplayView != null) {
            mDisplayView.collapseHistory();
        }
        //TS:kaifeng.lu 2015-12-31 Calculator BUGFIX_ 1202273  MOD_E
        if (mFormulaEditText != null && mFormulaTextWatcher != null) {
            mFormulaEditText.removeTextChangedListener(mFormulaTextWatcher);
        }
        /* MODIFIED-BEGIN by qiong.liu1, 2017-04-13,BUG-4452809*/
        saveStateToArguments();
    }

    private void saveStateToArguments() {
        Bundle outState = getArguments();
        if (outState != null) {
            saveStateFlag(outState);
            saveDataAndDataState(outState);
            outState.putBoolean(NEED_EXPAND, needExpand);
            outState.putBoolean(IS_ERROR, mCurrentState == Constant.CalculatorState.ERROR);
            /* MODIFIED-BEGIN by qiong.liu1, 2017-05-02,BUG-4598039*/
            outState.putInt(KEY_CURRENT_EVALUATOR_MODE,mEvaluator.getEvaluatorMode().ordinal());
            outState.putBoolean(KEY_CURRENT_EVALUATOR_IS_EXPAND, mIsExpand);
            /* MODIFIED-END by qiong.liu1,BUG-4598039*/
        }
    }
    /* MODIFIED-END by qiong.liu1,BUG-4452809*/

    //TS:kaifeng.lu 2015-12-30 Calculator BUGFIX_1271732 ADD_E


    /* MODIFIED-BEGIN by kaifeng.lu, 2016-11-17,BUG-3005276*/
    private void setState(Constant.CalculatorState state) {
        if (mCurrentState != state) {
            mCurrentState = state;

            if (state == Constant.CalculatorState.RESULT || state == Constant.CalculatorState.ERROR) {
            /* MODIFIED-END by kaifeng.lu,BUG-3005276*/
//                mDeleteButton.setVisibility(View.GONE);
//                mClearButton.setVisibility(View.VISIBLE);
            } else {
//                mDeleteButton.setVisibility(View.VISIBLE);
//                mClearButton.setVisibility(View.GONE);
            }

            if (state == Constant.CalculatorState.ERROR) { // MODIFIED by kaifeng.lu, 2016-11-17,BUG-3005276
                final int errorColor = getResources().getColor(R.color.calculator_error_color);
                mFormulaEditText.setTextColor(errorColor);
                mResultEditText.setTextColor(errorColor);
//                getWindow().setStatusBarColor(errorColor);
            } else {
                mFormulaEditText.setTextColor(
                        getResources().getColor(R.color.display_formula_text_color));
                mResultEditText.setTextColor(
                        getResources().getColor(R.color.display_result_text_color));
//                getWindow().setStatusBarColor(
//                        getResources().getColor(R.color.calculator_accent_color));
            }
        }
    }

    private void inverseFunctions(boolean needInv) {
        Button[] norButtons = new Button[]{sinBtn, cosBtn, tanBtn, lnBtn, logBtn, sqrtBtn};
        Button[] invButtons = new Button[]{asinBtn, acosBtn, atanBtn, naturExpBtn, comExpBtn, sqrBtn};
        for (Button btn : norButtons) {
            btn.setVisibility(needInv ? View.GONE : View.VISIBLE);
        }
        for (Button btn : invButtons) {
            btn.setVisibility(needInv ? View.VISIBLE : View.GONE);
        }
        invBtn.setTypeface(needInv ? Typeface.DEFAULT_BOLD : mNormalTf);
        /* MODIFIED-BEGIN by qiong.liu1, 2017-03-27,BUG-3621966*/
        invBtn.setTextColor(needInv ? getResources().getColor(R.color.color_green)
                : Color.WHITE);
                /* MODIFIED-END by qiong.liu1,BUG-3621966*/
        mBtnExPow.setTypeface(needInv ? Typeface.DEFAULT_BOLD : mNormalTf);
    }


    @Override
    public boolean onLongClick(View view) {
        mCurrentButton = view;
        switch (view.getId()) {
            case R.id.clear:
                onClear();
                return true;
            case R.id.del:
                onClear();
                return true;
            case R.id.formula:
                /* MODIFIED-BEGIN by kaifeng.lu, 2016-11-17,BUG-3005276*/
                if (((mCopyPasteUtils.isCanCopyFormula(mCurrentState, mFormulaEditText) || mCopyPasteUtils.isCanPaste(mCurrentState) && !copyPasteState))) {
                    isFormulaLongClicked = true;
                    copyPasteState = true;
                    Selection.selectAll(mFormulaEditText.getText());
                    displayFormulaCopyPasteButton(true);
                }
                return true;
            case R.id.result:
                if (mCurrentState == Constant.CalculatorState.RESULT && !mCopyPasteUtils.isCanPaste(mCurrentState)) {//PR546179 modify paste logic.update by xiaolu.li 8/31/2015
                    clipboard.setPrimaryClip(ClipData.newPlainText(null, mFormulaEditText.getText()));
                    Toast.makeText(mContext, mContext.getString(R.string.copy_result_toast), Toast.LENGTH_SHORT).show();
                    return true;
                } else if (mCopyPasteUtils.isCanCopyResult(mCurrentState, mResultEditText) && !copyPasteState) {//PR546179 modify paste logic.update by xiaolu.li 8/31/2015
                /* MODIFIED-END by kaifeng.lu,BUG-3005276*/
                    isResultLongClicked = true;
                    copyPasteState = true;
                    Selection.selectAll(mResultEditText.getText());
                    displayResultCopyButton(true);
                    return true;
                }


        }
        return true;
    }

    //Modified it by hong.zhan for PR817289 about fix to support exttra feature for test 20141028 begin
    @Override
    public void onEvaluate(String expr, String result, int errorResourceId) {
        Log.i(TAG, "onEvaluate start" + System.currentTimeMillis()); // MODIFIED by kaifeng.lu, 2016-05-25,BUG-2162730
        Log.i(TAG, "---onEvaluate---");// MODIFIED by kaifeng.lu, 2016-03-23,BUG-1722335
        Log.i(TAG, "onEvaluate start" + System.currentTimeMillis() + "evaluateCount = " + evaluateCount);
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
        if (mCurrentState == Constant.CalculatorState.INPUT) { // MODIFIED by kaifeng.lu, 2016-11-17,BUG-3005276
            mResultEditText.setText(commaResult);
        } else if (errorResourceId != INVALID_RES_ID) {
            onError(errorResourceId);
        } else if (!TextUtils.isEmpty(result)) {
            //liu.zheng
            /*if(com.tct.feature.Global.TCT_FEATURE_BOARD_TABLET){
                exttraFeature(mFormulaEditText.getText().toString(),result);
            }else{*/
            //TS:kaifeng.lu 2016-03-29 Calculator BUGFIX_1868303  MOD_S
            if (mHistory != null) {
                mHistory.enter(mFormulaEditText.getText().toString(), commaResult);//PR535661 [Android 5.1][ Calculator_v5.2.1.2.0303.0] The “X”and “÷“ show as ”*“”/“ on history screen. update by xiaolu.li AUG 24 2015
            }
            //TS:kaifeng.lu 2016-03-29 Calculator BUGFIX_1868303  MOD_E
            mDisplayView.scrollToMostRecent();
            onResult(result);
            mDisplayView.initializeHistoryAndGraphView(false);
        } else if (mCurrentState == Constant.CalculatorState.EVALUATE) { // MODIFIED by kaifeng.lu, 2016-11-17,BUG-3005276
            // The current expression cannot be evaluated -> return to the input state.
            //TS:kaifeng.lu 2015-12-07 Calculator BUGFIX_982004  MOD_S
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
                //TS:kaifeng.lu 2015-12-28 Calculator BUGFIX_1239024  ADD_S
                resultStr = mTokenizer.evaluate2Local(resultStr);
                //TS:kaifeng.lu 2015-12-28 Calculator BUGFIX_1239024  ADD_S
                onResult(resultStr);
            } catch (SyntaxException | NullPointerException e) {
                e.printStackTrace();
            }
//            setState(Constant.CalculatorState.RESULT); // MODIFIED by kaifeng.lu, 2016-11-17,BUG-3005276
            //TS:kaifeng.lu 2015-12-07 Calculator BUGFIX_982004  MOD_E
        }
        Log.i(TAG, "onEvaluate end " + System.currentTimeMillis()); // MODIFIED by kaifeng.lu, 2016-05-25,BUG-2162730
        Log.i(TAG, "onEvaluate start" + System.currentTimeMillis() + "evaluateCount = " + ++evaluateCount);
    }
//Modified it by hong.zhan for PR817289 about fix to support exttra feature for test 20141028 end

    @Override
    public void onTextSizeChanged(final TextView textView, float oldSize) {
        if (mCurrentState != Constant.CalculatorState.INPUT) { // MODIFIED by kaifeng.lu, 2016-11-17,BUG-3005276
            // Only animate text changes that occur from user input.
            return;
        }
        Log.i(TAG, "---onTextSizeChanged---begin");// MODIFIED by kaifeng.lu, 2016-03-23,BUG-1722335
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
        Log.i(TAG, "---onTextSizeChanged---end");// MODIFIED by kaifeng.lu, 2016-03-23,BUG-1722335
    }

    private void onEquals() {
        //TS:kaifeng.lu 2016-04-27 Calculator BUGFIX_ 1999448  MOD_S
        if (mFormulaEditText.getText().toString().contains("e") || mCurrentState == Constant.CalculatorState.INPUT) {
            setState(Constant.CalculatorState.EVALUATE);
            mEvaluator.evaluate(mFormulaEditText.getText().toString().replace(" ", "").replaceAll(",", "."), this);
        }
        //TS:kaifeng.lu 2016-04-27 Calculator BUGFIX_ 1999448  MOD_E
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


    private void reveal(View sourceView, int colorRes, Animator.AnimatorListener listener) {
        final View revealView = new View(this.getActivity());

        mLayoutParams.height = mFormatView.getHeight();
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
        final int revealCenterX =  mDisplayView.getWidth();
        final int revealCenterY = mDisplayView.getDisplayHeight();

        final double x_2 = Math.pow(mDisplayView.getWidth(), 2);
//        final double x2_2 = Math.pow(mDisplayView.getDisplayHeight() - revealCenterX, 2);
        final double y_2 = Math.pow(mDisplayView.getDisplayHeight() , 2);
//        final float revealRadius = (float) Math.max(Math.sqrt(x1_2 + y_2), Math.sqrt(x2_2 + y_2));
        final float revealRadius = (float) Math.sqrt(x_2+y_2);
        //TS:kaifeng.lu 2015-12-07 Calculator BUGFIX_966467  MOD_E

        //TS:kaifeng.lu 2016-05-17 Calculator BUGFIX-2157429  MOD_S
        try{
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

        }catch (IllegalStateException e){
            Log.i(TAG,"revealView has happend IllegalStateException");
        }
        //TS:kaifeng.lu 2016-05-17 Calculator BUGFIX-2157429  MOD_E
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

    private void onError(final int errorResourceId) {
        if (mCurrentState != Constant.CalculatorState.EVALUATE) { // MODIFIED by kaifeng.lu, 2016-11-17,BUG-3005276
            // Only animate error on evaluate.
            mResultEditText.setText(errorResourceId);
            return;
        }

        reveal(mCurrentButton, R.color.calculator_error_color, new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                setState(Constant.CalculatorState.ERROR); // MODIFIED by kaifeng.lu, 2016-11-17,BUG-3005276
                mResultEditText.setText(errorResourceId);
            }
        });
    }

    private void onResult(final String result) {
        Log.i(TAG, "---onResult---");// MODIFIED by kaifeng.lu, 2016-03-23,BUG-1722335
// Calculate the values needed to perform the scale and translation animations,
// accounting for how the scale will affect the final position of the text.
        final String commaResult = getCommaString(result);
        final float resultScale =
                mFormulaEditText.getVariableTextSize(commaResult) / mResultEditText.getTextSize();
        final float resultTranslationX = (1.0f - resultScale) *
                (mResultEditText.getWidth() / 2.0f - mResultEditText.getPaddingEnd());
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
                setState(Constant.CalculatorState.RESULT); // MODIFIED by kaifeng.lu, 2016-11-17,BUG-3005276

                mCurrentAnimator = null;
            }
        });

        mCurrentAnimator = animatorSet;
        animatorSet.start();
    }

    //Added it by hong.zhan for PR817289 about fix to support exttra feature for test 20141028 begin
    private String getBuildDate() {
        File file = new File("/proc/version");
        BufferedReader reader = null;
        String buildDate = "";
        try {
            reader = new BufferedReader(new FileReader(file));
            buildDate = reader.readLine().split("SMP PREEMPT ")[1];
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        return buildDate;
    }

    //liu.zheng
    /*private void exttraFeature(final String expr,final String result){
        if (expr.equals(MMI_AUTOTEST)){
            try {
                Intent i = new Intent(Intent.ACTION_MAIN);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.setClassName("com.android.mmi", "com.android.mmi.MMITest");
                this.startActivity(i);
            } catch (android.content.ActivityNotFoundException e) {
                // TODO: handle exception
                Log.d("Calcultor", "no activity to handle MMITest.");
            }
            // MODIFIED-BEGIN by kaifeng.lu, 2016-11-17, BUG-3005276
            setState(Constant.CalculatorState.RESULT);
        }else if (expr.equals(MMI_AGPS)){
            Intent intent = new Intent("android.provider.Telephony.SECRET_CODE",
            Uri.parse("android_secret_code://4636"));
            this.sendBroadcast(intent);
            setState(Constant.CalculatorState.RESULT);
            // MODIFIED-END by kaifeng.lu, BUG-3005276
        }else if (expr.equals(MMI_IMEIVERSION)) {
            String boot_ver = SystemProperties.get("ro.tct.boot.ver");
            String sys_ver = SystemProperties.get("ro.tct.sys.ver");
            String custpack_ver = SystemProperties.get("ro.tct.cust.ver");
            String mod_ver = SystemProperties.get("ro.tct.non.ver");
            String recovery_ver = SystemProperties.get("ro.tct.reco.ver");
            String sw_version =  boot_ver+'\n'+
                        sys_ver+'\n'+
                        recovery_ver+'\n'+
                        mod_ver;
            if (custpack_ver != null && !custpack_ver.contains("????????")) {
                sw_version +='\n'+custpack_ver;
            }
            AlertDialog alert = new AlertDialog.Builder(this)
                                 .setTitle("Image Mapping")
                                 .setMessage(sw_version)
                                 .setPositiveButton(android.R.string.ok, null)
                                 .setCancelable(false)
                                 .show();
            // MODIFIED-BEGIN by kaifeng.lu, 2016-11-17, BUG-3005276
            setState(Constant.CalculatorState.RESULT);
        }else if (expr.equals(MMI_LOGTOOL)){
            Intent intent = new Intent("android.provider.Telephony.SECRET_CODE",
            Uri.parse("android_secret_code://0574"));
            this.sendBroadcast(intent);
            setState(Constant.CalculatorState.RESULT);
            // MODIFIED-END by kaifeng.lu, BUG-3005276
        }else if (expr.equals(MMI_BUILDVERSION)){
            String version = SystemProperties.get("ro.build.description");
            String message = "";
            if(!TextUtils.isEmpty(version)){
                    version = version.replaceFirst("update_", "");
                    message = "Version : " + version;
            }else{
                    message = "Version : null";
            }
            String date = "Date      : " + getBuildDate();
            message = message + "\n" + date;
            new AlertDialog.Builder(this)
                     .setMessage(message)
                     .setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                     public void onClick(DialogInterface arg0, int arg1) {
                                            arg0.dismiss();
                                     }
                                     })
                     .show();
            setState(Constant.CalculatorState.RESULT); // MODIFIED by kaifeng.lu, 2016-11-17,BUG-3005276
        }else{
            onResult(result);
        }
    }*/
    //Added it by hong.zhan for PR817289 about fix to support exttra feature for test 20141028 end

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


    void showCalulatorState() {
        /* MODIFIED-BEGIN by kaifeng.lu, 2016-11-17,BUG-3005276*/
        if (mCurrentState == Constant.CalculatorState.INPUT) {
            Log.i(TAG, "Current statue is INPUT");
        }
        if (mCurrentState == Constant.CalculatorState.EVALUATE) {
            Log.i(TAG, "Current statue is EVALUATE");
        }
        if (mCurrentState == Constant.CalculatorState.RESULT) {
            Log.i(TAG, "Current statue is RESULT");
        }
        if (mCurrentState == Constant.CalculatorState.ERROR) {
        /* MODIFIED-END by kaifeng.lu,BUG-3005276*/
            Log.i(TAG, "Current statue is ERROR");
        }
    }

    /**
     * When click radBtn, call this function
     */
    public void radClick(){
        // change to radius
        radTv.setVisibility(View.VISIBLE);
        degTv.setVisibility(View.GONE);
        degBtn.setVisibility(View.VISIBLE);
        radBtn.setVisibility(View.GONE);
        mEvaluator.setEvaluatorMode(CalculatorExpressionEvaluator.EvaluatorMode.RADIUS);
        mEvaluator.evaluate(mFormulaEditText.getText().toString().replaceAll(" ", "").replaceAll(",", "."), this);
    }

    /**
     * When click regBtn, call this function
     */
    public void degClick(){
        radTv.setVisibility(View.GONE);
        radBtn.setVisibility(View.VISIBLE);
        degBtn.setVisibility(View.GONE);
        mEvaluator.setEvaluatorMode(CalculatorExpressionEvaluator.EvaluatorMode.DEGREE);
        mEvaluator.evaluate(mFormulaEditText.getText().toString().replaceAll(" ", "").replaceAll(",", "."), this);
    }

    @Override
    public void onClick(View view) {
        mCurrentButton = view;
        mDisplayView.collapseHistory();
        ((Calculator)this.getActivity()).coolapseButton();
        cancelCopyPaste();
        switch (view.getId()) {

            case R.id.clear:
                if(mCurrentState == Constant.CalculatorState.RESULT || mCurrentState == Constant.CalculatorState.ERROR){ // MODIFIED by kaifeng.lu, 2016-11-17,BUG-3005276
                    onClear();
                }else{
                    onDelete();
                }
                break;
            case R.id.del2:
                onClear();
                break;
            case R.id.del:
                if(mCurrentState == Constant.CalculatorState.RESULT || mCurrentState == Constant.CalculatorState.ERROR){ // MODIFIED by kaifeng.lu, 2016-11-17,BUG-3005276
                    onClear();
                }else{
                    onDelete();
                }
                break;
            case R.id.expend:
                mPadViewPager.setCurrentItem(mPadViewPager.getCurrentItem()+1);
                mIsExpand = true; // MODIFIED by qiong.liu1, 2017-05-02,BUG-4598039
                break;
            case R.id.formula_copy_btn:
//                if((mCurrentState == Constant.CalculatorState.RESULT)){ // MODIFIED by kaifeng.lu, 2016-11-17,BUG-3005276
//                    clipboard.setPrimaryClip(ClipData.newPlainText(null, mFormulaEditText.getText()));//PR546179 modify paste logic.update by xiaolu.li 8/31/2015
//                }else {
//                    clipboard.setPrimaryClip(ClipData.newPlainText(null, mResultEditText.getText()));
//                }
                /* MODIFIED-BEGIN by kaifeng.lu, 2016-09-22,BUG-2960280*/
                if (mIsStanderPoint) {
                    clipboard.setPrimaryClip(ClipData.newPlainText(null, mFormulaEditText.getText().toString().replaceAll(" ", "").replaceAll(",", ".")));
                } else {
                    clipboard.setPrimaryClip(ClipData.newPlainText(null, mFormulaEditText.getText().toString().replaceAll(" ", "").replaceAll("\\.", ",")));
                }
                /* MODIFIED-END by kaifeng.lu,BUG-2960280*/

                /* MODIFIED-BEGIN by kaifeng.lu, 2016-08-16,BUG-2712192*/
                mDataFromHistory = false;
                mDisplayView.collapseHistory();
                break;
            case R.id.formula_paste_btn:
                if (clipboard.hasPrimaryClip()) {
                    // PR568561,fixed by boyang@tcl.com,2015-10-15,begin.
                    if (mCurrentState == Constant.CalculatorState.RESULT && !TextUtils.isEmpty(mFormulaEditText.getText()) && !mDataFromHistory) { // MODIFIED by kaifeng.lu, 2016-11-17,BUG-3005276
                    /* MODIFIED-END by kaifeng.lu,BUG-2712192*/
                        reveal(mCurrentButton, R.color.calculator_accent_color, new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                mFormulaEditText.getEditableText().clear();
/* MODIFIED-BEGIN by kaifeng.lu, 2016-09-22,BUG-2960280*/
//                                mFormulaEditText.append(getCommaString(PatternUtil.getInstance().getVaildPaster(clipboard.getPrimaryClip().getItemAt(0).getText().toString())));
                                mFormulaEditText.append(clipboard.getPrimaryClip().getItemAt(0).getText().toString());
                            }
                        });
                    } else {
                        //TS: lin-zhou 2016-09-27 Calculator BUGFIX-2999162 MOD-Start
                        if (mDataFromHistory) {
                            setState(Constant.CalculatorState.INPUT); // MODIFIED by kaifeng.lu, 2016-11-17,BUG-3005276
                        }
                        //TS: lin-zhou 2016-09-27 Calculator BUGFIX-2999162 MOD-End
                        Editable editable = mFormulaEditText.getText();
                        int length = editable.length();
                        if (length > 0 && Character.isDigit(editable.charAt(length - 1)))
                            mFormulaEditText.append(getString(R.string.op_mul));
//                        mFormulaEditText.append(getCommaString(PatternUtil.getInstance().getVaildPaster(clipboard.getPrimaryClip().getItemAt(0).getText().toString())));//PR1075592 don't show paster button when clipboard hasn't vaild paster content Update by xiaolu.li 26/8/2015
                        mFormulaEditText.append(clipboard.getPrimaryClip().getItemAt(0).getText().toString());
                    }
                    // PR568561,fixed by boyang@tcl.com,2015-10-15,end.
                }
                mDisplayView.collapseHistory();
                break;
            case R.id.expand_bt:
                if(mPadViewPager.getCurrentItem()== 1){
                    mPadViewPager.setCurrentItem(0);
                }
                mIsExpand = false; // MODIFIED by qiong.liu1, 2017-05-02,BUG-4598039
                break;
            case R.id.result_copy_btn:
                if (mIsStanderPoint) {
                    clipboard.setPrimaryClip(ClipData.newPlainText(null, mResultEditText.getText().toString().replaceAll(",", ".").replaceAll(" ", "")));
                } else {
                    clipboard.setPrimaryClip(ClipData.newPlainText(null, mResultEditText.getText().toString().replaceAll("\\.", ",").replaceAll(" ", "")));
                }
                /* MODIFIED-END by kaifeng.lu,BUG-2960280*/
                displayResultCopyButton(false);
                mDataFromHistory = false;
                mDisplayView.collapseHistory();
                break;
            case R.id.result:
            case R.id.formula:
                if (mPadViewPager != null && mPadViewPager.getCurrentItem() > 0 && mDisplayView.getTranslateState() == DisplayOverlay.TranslateState.COLLAPSED) {
                    //PR536104  [Android 5.1][Calculator_v5.2.1.2.0303.0]The scientific calculator panel can slide back after swipe down the history of calculations.update by xiaolu.li AUG 25 2015
                    mPadViewPager.setCurrentItem(mPadViewPager.getCurrentItem() - 1);
                }
                break;
            case R.id.op_inv:
                // inverse functions
                inv = !inv;
                //TS:jin.dong 2015-12-03 Calculator  BUGFIX_904574  ADD_S
                view.setSelected(inv);
                //TS:jin.dong 2015-12-03 Calculator  BUGFIX_904574  ADD_E
                inverseFunctions(inv);
                break;
            case R.id.op_rad:
                // change to radius
                radClick();
                break;
            case R.id.op_deg:
                // change to degree
                degClick();
                break;
            case R.id.eq:
                onEquals();
                break;
            case R.id.clr:
                onClear();
                break;
            case R.id.fun_cos:
            case R.id.fun_ln:
            case R.id.fun_log:
            case R.id.fun_sin:
            case R.id.fun_tan:
            case R.id.fun_arccos:
            case R.id.fun_arcsin:
            case R.id.fun_arctan:
                // Add left parenthesis after functions.
                /* MODIFIED-BEGIN by lin-zhou, 2016-06-16,BUG-2282670*/
                /* MODIFIED-BEGIN by kaifeng.lu, 2016-06-29,BUG-2398506*/
                if (mCurrentState != Constant.CalculatorState.RESULT) {
                    mFormulaEditText.append(((Button) view).getText() + getString(R.string.lparen));
                } else {
                    onDelete();
                    mFormulaEditText.setText(((Button) view).getText() + getString(R.string.lparen));
                }
                /* MODIFIED-END by kaifeng.lu,BUG-2398506*/
                /* MODIFIED-END by lin-zhou,BUG-2282670*/
                break;
            case R.id.fun_naturExp:
                mFormulaEditText.append(getString(R.string.op_exp) + getString(R.string.lparen));
                break;
            case R.id.fun_comExp:
                mFormulaEditText.append("10" + getString(R.string.op_pow));
                break;
            case R.id.fun_sqr:
                //TS: lin-zhou 2016-09-27 Calculator BUGFIX-3000477 MOD-Start
                setState(Constant.CalculatorState.INPUT); // MODIFIED by kaifeng.lu, 2016-11-17,BUG-3005276
                //TS: lin-zhou 2016-09-27 Calculator BUGFIX-3000477 MOD-End
                mFormulaEditText.append(getString(R.string.op_sqr));
                break;

            //PR888211 Should not be can input "+" button by ting.ma at 2015.01.12 begin
            case R.id.op_add:
            case R.id.op_mul:
            case R.id.op_div:
                String mFormulaEditTextStr = mFormulaEditText.getText().toString()
                        .trim();
                String op_sub = getResources().getString(R.string.op_sub);
                if (mFormulaEditTextStr.length() == 1
                        && op_sub.equals(mFormulaEditTextStr)) {
                    break;
                }
                /* MODIFIED-BEGIN by kaifeng.lu, 2016-06-23,BUG-2396079*/
                setState(Constant.CalculatorState.INPUT);
                mFormulaEditText.append(((Button) view).getText());
                break;
                /* MODIFIED-END by kaifeng.lu,BUG-2396079*/

//PR888211 Should not be can input "+" button by ting.ma at 2015.01.12 end
            /* MODIFIED-BEGIN by kaifeng.lu, 2016-06-16,BUG-2285401*/
            case R.id.dec_point:
                /* MODIFIED-BEGIN by kaifeng.lu, 2016-06-29,BUG-2398506*/
                if (mCurrentState == Constant.CalculatorState.RESULT) {
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
                /* MODIFIED-END by kaifeng.lu,BUG-2285401*/
            default:

                /* MODIFIED-BEGIN by kaifeng.lu, 2016-11-17,BUG-3005276*/
                if (mCurrentState != Constant.CalculatorState.RESULT) {
                    mFormulaEditText.append(((Button) view).getText());
                } else {
                    setState(Constant.CalculatorState.INPUT);
                    /* MODIFIED-END by kaifeng.lu,BUG-3005276*/
                    mFormulaEditText.append(((Button) view).getText());
                }

                break;
        }
        cancelCopyPaste();
    }

    /* MODIFIED-BEGIN by kaifeng.lu, 2016-08-16,BUG-2712192*/
    private class SetHistoryCopyStatue implements HistoryAdapter.DataFromHistoryCallback {
        @Override
        public void setStatus(boolean mHistroyStatus) {
            mDataFromHistory = true;
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        saveStateFlag(outState);
        saveDataAndDataState(outState);
        mIsErrorStates = mCurrentState == Constant.CalculatorState.ERROR ? true : false; // MODIFIED by qiong.liu1, 2017-04-13,BUG-4452809
        super.onSaveInstanceState(outState);

    }

    private void saveStateFlag(Bundle outState){

        /* MODIFIED-BEGIN by kaifeng.lu, 2016-11-17,BUG-3005276*/
        outState.putSerializable(CALCULATOR_FORMULA_STATE, mCurrentState);
        if (formulaPasteBtn.getVisibility() == View.VISIBLE) {
            outState.putBoolean(FORMULA_PASTE_SHOW, true);
        } else {
            outState.putBoolean(RESULT_COPY_SHOW, false);
        }
        if (formulaCopyBtn.getVisibility() == View.VISIBLE) {
            outState.putBoolean(FORMULA_COPY_SHOW, true);
        } else {
            outState.putBoolean(RESULT_COPY_SHOW, false);
        }
        if (resultCopyBtn.getVisibility() == View.VISIBLE) {
            outState.putBoolean(RESULT_COPY_SHOW, true);
        } else {
            outState.putBoolean(RESULT_COPY_SHOW, false);
        }
    }



    private void showCurrentState(){
        if(mCurrentState == Constant.CalculatorState.RESULT){
        }else if(mCurrentState == Constant.CalculatorState.ERROR){
        }else if(mCurrentState == Constant.CalculatorState.EVALUATE){
        /* MODIFIED-END by kaifeng.lu,BUG-3005276*/
        }else{}
    }


    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if(savedInstanceState != null){
            /* MODIFIED-BEGIN by kaifeng.lu, 2016-11-17,BUG-3005276*/
            mCurrentState = (Constant.CalculatorState) savedInstanceState.getSerializable(CALCULATOR_FORMULA_STATE);
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        if(mCurrentState == Constant.CalculatorState.RESULT){ // MODIFIED by kaifeng.lu, 2016-11-17,BUG-3005276
            mIsResultState = true;
        }
    }

    protected void initView(View view) {
        mContext = this.getActivity();
        cr = mContext.getContentResolver();
        mDisplayView = (DisplayOverlay) view.findViewById(R.id.display);
        mDisplayView.setChangeViewCallBack(this);
        mFormulaEditText = (CalculatorEditText) view.findViewById(R.id.formula);
        mResultEditText = (CalculatorEditText) view.findViewById(R.id.result);
        mPadViewPager = (CalculatorPadViewPager) view.findViewById(R.id.pad_pager);
        formulaCopyBtn = (Button) view.findViewById(R.id.formula_copy_btn);
        formulaPasteBtn = (Button) view.findViewById(R.id.formula_paste_btn);
        resultCopyBtn = (Button) view.findViewById(R.id.result_copy_btn);
        clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        tipsBtn = (Button) view.findViewById(R.id.tips_btn);
        mPoint = (Button) view.findViewById(R.id.dec_point);
        radTv = (TextView) mDisplayView.findViewById(R.id.tv_rad);
        degTv = (TextView) mDisplayView.findViewById(R.id.tv_deg);
        mEqualButton = view.findViewById(R.id.eq);
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
        mFormatView = view.findViewById(R.id.ll_formula_result);

        // mBtndiv,mBtndel,mBtnmul,mBtndel2,mBtnsub,mBtnexpend,mBtnpoint,mBtnadd,mBtneq;
        mBtndiv =  view.findViewById(R.id.op_div);
        mBtnmul =  view.findViewById(R.id.op_mul);
        mBtnclear =  view.findViewById(R.id.clear);
        mBtnsub =  view.findViewById(R.id.op_sub);
        mBtnpoint =  view.findViewById(R.id.dec_point);
        mBtnadd =  view.findViewById(R.id.op_add);
        mBtneq =  view.findViewById(R.id.eq);

        mBtndiv.setOnClickListener(this);
        mBtnmul.setOnClickListener(this);
        mBtnclear.setOnClickListener(this);
        mBtnsub.setOnClickListener(this);
        mBtnpoint.setOnClickListener(this);
        mBtnadd.setOnClickListener(this);
        mBtneq.setOnClickListener(this);
        mBtnclear.setOnLongClickListener(this);
        resultCopyBtn.setOnClickListener(this);
        formulaCopyBtn.setOnClickListener(this);
        formulaPasteBtn.setOnClickListener(this);

        mFormulaEditText.setOnClickListener(this);
        mResultEditText.setOnClickListener(this);

        mPadViewPager.setCalculatorType(Constant.CalculatorType.CALCULATOR);
        if (mPoint.getText().equals(",")) {
            mIsStanderPoint = false;
        }

        try {
            if (mEqualButton == null || mEqualButton.getVisibility() != View.VISIBLE) {
                mEqualButton = view.findViewById(R.id.pad_operator).findViewById(R.id.eq);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        mTokenizer = new CalculatorExpressionTokenizer(this.getActivity());
        mEvaluator = new CalculatorExpressionEvaluator(mTokenizer);
        mEvaluator.evaluate(mFormulaEditText.getText().toString().replaceAll(" ", "").replaceAll(",", "."), this);

        mFormulaEditText.setEditableFactory(mFormulaEditableFactory);
        mFormulaEditText.addTextChangedListener(mFormulaTextWatcher);
        mFormulaEditText.setOnKeyListener(mFormulaOnKeyListener);
        mFormulaEditText.setOnTextSizeChangeListener(this);
        mFormulaEditText.setOnLongClickListener(this);
        mResultEditText.setOnLongClickListener(this);
        mDisplayView.bringToFront();
        DisplayOverlay.DisplayMode displayMode = DisplayOverlay.DisplayMode.FORMULA;
        mDisplayView.setMode(displayMode);
        mDisplayView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        if (mDisplayView.getHeight() > 0) {
                            mDisplayView.initializeHistoryAndGraphView(true);
                            if (mDisplayView.getMode() == DisplayOverlay.DisplayMode.GRAPH) {
                            }
                        }
                    }
                });
        mDisplayView.setTranslateStateListener(new DisplayOverlay.TranslateStateListener() {
            @Override
            public void onTranslateStateChanged(DisplayOverlay.TranslateState newState) {
                cancelCopyPaste();
            }
        });

        if (mPadViewPager != null) {
            ((CalculatorPadViewPager) mPadViewPager).setOnTouchUpFirstPagerOnBackgroundListener(new CalculatorPadViewPager.onTouchUpFirstPagerOnBackgroundListener() {

                @Override
                public void onTouchUpFirstPager() {
                    if (mPadViewPager.getCurrentItem() == 1) {
                        // Otherwise, select the previous pad.
                        mPadViewPager.setCurrentItem(mPadViewPager.getCurrentItem() - 1);
                    }
                }

                /* MODIFIED-BEGIN by qiong.liu1, 2017-04-14,BUG-4452809*/
                @Override
                public void onDEGModeSelect() {

                }
                /* MODIFIED-END by qiong.liu1,BUG-4452809*/
            });
        }
        Log.i(TAG, "onCreate end" + System.currentTimeMillis());

        mFormulaEditText.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View view, DragEvent dragEvent) {
                if (dragEvent.getAction() == DragEvent.ACTION_DROP) {

                    if (!TextUtils.isEmpty(dragEvent.getClipData().getItemAt(0).getText())) {
                        mFormulaEditText.setText(getCommaString(dragEvent.getClipData().getItemAt(0).getText().toString()));
                    }
                }
                return true;
            }
        });
    }

    /* MODIFIED-BEGIN by qiong.liu1, 2017-05-04,BUG-4656997*/
    public void requestCursor() {
        if (mFormulaEditText != null) {
            mFormulaEditText.setFocusable(true);
            mFormulaEditText.setFocusableInTouchMode(true);
            mFormulaEditText.requestFocus();
            mFormulaEditText.requestFocusFromTouch();
            mFormulaEditText.findFocus();
        }
    }
    /* MODIFIED-END by qiong.liu1,BUG-4656997*/


    private void findLnadViews(View view) {
        isInitialize = true;
        degBtn = (Button) view.findViewById(R.id.op_deg);
        invBtn= (Button) view.findViewById(R.id.op_inv);
        radBtn= (Button) view.findViewById(R.id.op_rad);

        sinBtn= (Button) view.findViewById(R.id.fun_sin);
        asinBtn = (Button) view.findViewById(R.id.fun_arcsin);
        acosBtn = (Button) view.findViewById(R.id.fun_arccos);
        atanBtn = (Button) view.findViewById(R.id.fun_arctan);
        cosBtn= (Button) view.findViewById(R.id.fun_cos);
        tanBtn= (Button) view.findViewById(R.id.fun_tan);

        lnBtn= (Button) view.findViewById(R.id.fun_ln);
        logBtn= (Button) view.findViewById(R.id.fun_log);
        sqrtBtn= (Button) view.findViewById(R.id.op_sqrt);
        sqrBtn = (Button) view.findViewById(R.id.fun_sqr);
        comExpBtn = (Button) view.findViewById(R.id.fun_comExp);
        naturExpBtn = (Button) view.findViewById(R.id.fun_naturExp);

        mBtnexdiv = view.findViewById(R.id.op_div);
        mBtnexsub = view.findViewById(R.id.op_sub);
        mBtnexmul = view.findViewById(R.id.op_mul);
        mBtnexpoint = view.findViewById(R.id.dec_point);
        mBtnexadd = view.findViewById(R.id.op_add);
        mBtnexeq = view.findViewById(R.id.eq);

        mBtnExPer = view.findViewById(R.id.op_per);
        mBtnExPi = view.findViewById(R.id.const_pi);
        mBtnExE = view.findViewById(R.id.const_e);
        mBtnExFact = view.findViewById(R.id.op_fact);
        mBtnExPow = (Button) view.findViewById(R.id.op_pow);
        mBtnExLparen = view.findViewById(R.id.lparen);
        mBtnExRparen = view.findViewById(R.id.rparen);

        invBtn.setOnClickListener(this);
        degBtn.setOnClickListener(this);
        radBtn.setOnClickListener(this);

        sinBtn.setOnClickListener(this);
        asinBtn.setOnClickListener(this);
        acosBtn.setOnClickListener(this);
        atanBtn.setOnClickListener(this);
        acosBtn.setOnClickListener(this);
        cosBtn.setOnClickListener(this);
        tanBtn.setOnClickListener(this);

        lnBtn.setOnClickListener(this);
        logBtn.setOnClickListener(this);
        sqrtBtn.setOnClickListener(this);
        sqrBtn.setOnClickListener(this);
        comExpBtn.setOnClickListener(this);
        naturExpBtn.setOnClickListener(this);

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


        mBtnexdiv.setOnClickListener(this);
        mBtnexsub.setOnClickListener(this);
        mBtnexmul.setOnClickListener(this);
        mBtnexpoint.setOnClickListener(this);
        mBtnexadd.setOnClickListener(this);
        mBtnexeq.setOnClickListener(this);

        //        mBtnExPer,mBtnExPi,mBtnExE,mBtnExFact,mBtnExDel,mBtnExPow,mBtnExLparen,mBtnExRparen,mBtnBackAdvenced;
        mBtnExPer.setOnClickListener(this);
        mBtnExPi.setOnClickListener(this);
        mBtnExE.setOnClickListener(this);
        mBtnExFact.setOnClickListener(this);
        mBtnExPow.setOnClickListener(this);
        mBtnExLparen.setOnClickListener(this);
        mBtnExRparen.setOnClickListener(this);

        updateFragmentState();
    }


    protected void initLandView(View view) {
        mContext = this.getActivity();
        cr = mContext.getContentResolver();
        mDisplayView = (DisplayOverlay) view.findViewById(R.id.display);
        mDisplayView.setChangeViewCallBack(this);
        mFormulaEditText = (CalculatorEditText) view.findViewById(R.id.formula);
        mResultEditText = (CalculatorEditText) view.findViewById(R.id.result);
        mPadViewPager = (CalculatorPadViewPager) view.findViewById(R.id.pad_pager);
        formulaCopyBtn = (Button) view.findViewById(R.id.formula_copy_btn);
        formulaPasteBtn = (Button) view.findViewById(R.id.formula_paste_btn);
        resultCopyBtn = (Button) view.findViewById(R.id.result_copy_btn);
        clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        tipsBtn = (Button) view.findViewById(R.id.tips_btn);
        mPoint = (Button) view.findViewById(R.id.dec_point);
        radTv = (TextView) mDisplayView.findViewById(R.id.tv_rad);
        degTv = (TextView) mDisplayView.findViewById(R.id.tv_deg);
        mEqualButton = view.findViewById(R.id.eq);
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
        mFormatView = view.findViewById(R.id.ll_formula_result);

        // mBtndiv,mBtndel,mBtnmul,mBtndel2,mBtnsub,mBtnexpend,mBtnpoint,mBtnadd,mBtneq;
        mBtndiv =  view.findViewById(R.id.op_div);
        mBtnmul =  view.findViewById(R.id.op_mul);
        mBtnclear =  view.findViewById(R.id.clear);
        mBtnsub =  view.findViewById(R.id.op_sub);
        mBtnpoint =  view.findViewById(R.id.dec_point);
        mBtnadd =  view.findViewById(R.id.op_add);
        mBtneq =  view.findViewById(R.id.eq);

        mBtndiv.setOnClickListener(this);
        mBtnmul.setOnClickListener(this);
        mBtnclear.setOnClickListener(this);
        mBtnsub.setOnClickListener(this);
        mBtnpoint.setOnClickListener(this);
        mBtnadd.setOnClickListener(this);
        mBtneq.setOnClickListener(this);
        mBtnclear.setOnLongClickListener(this);
        resultCopyBtn.setOnClickListener(this);
        formulaCopyBtn.setOnClickListener(this);
        formulaPasteBtn.setOnClickListener(this);

        mFormulaEditText.setOnClickListener(this);
        mResultEditText.setOnClickListener(this);

        mPadViewPager.setCalculatorType(Constant.CalculatorType.CALCULATOR);
        if (mPoint.getText().equals(",")) {
            mIsStanderPoint = false;
        }

        try {
            if (mEqualButton == null || mEqualButton.getVisibility() != View.VISIBLE) {
                mEqualButton = view.findViewById(R.id.pad_operator).findViewById(R.id.eq);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        mTokenizer = new CalculatorExpressionTokenizer(this.getActivity());
        mEvaluator = new CalculatorExpressionEvaluator(mTokenizer);
        mEvaluator.evaluate(mFormulaEditText.getText().toString().replaceAll(" ", "").replaceAll(",", "."), this);

        mFormulaEditText.setEditableFactory(mFormulaEditableFactory);
        mFormulaEditText.addTextChangedListener(mFormulaTextWatcher);
        mFormulaEditText.setOnKeyListener(mFormulaOnKeyListener);
        mFormulaEditText.setOnTextSizeChangeListener(this);
        mFormulaEditText.setOnLongClickListener(this);
        mResultEditText.setOnLongClickListener(this);
        mDisplayView.bringToFront();
        DisplayOverlay.DisplayMode displayMode = DisplayOverlay.DisplayMode.FORMULA;
        mDisplayView.setMode(displayMode);
        mDisplayView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        if (mDisplayView.getHeight() > 0) {
                            mDisplayView.initializeHistoryAndGraphView(true);
                            if (mDisplayView.getMode() == DisplayOverlay.DisplayMode.GRAPH) {
                            }
                        }
                    }
                });
        mDisplayView.setTranslateStateListener(new DisplayOverlay.TranslateStateListener() {
            @Override
            public void onTranslateStateChanged(DisplayOverlay.TranslateState newState) {
                cancelCopyPaste();
            }
        });

        if (mPadViewPager != null) {
            ((CalculatorPadViewPager) mPadViewPager).setOnTouchUpFirstPagerOnBackgroundListener(new CalculatorPadViewPager.onTouchUpFirstPagerOnBackgroundListener() {

                @Override
                public void onTouchUpFirstPager() {
                    if (mPadViewPager.getCurrentItem() == 1) {
                        // Otherwise, select the previous pad.
                        mPadViewPager.setCurrentItem(mPadViewPager.getCurrentItem() - 1);
                    }
                }

                /* MODIFIED-BEGIN by qiong.liu1, 2017-04-14,BUG-4452809*/
                @Override
                public void onDEGModeSelect() {

                }
                /* MODIFIED-END by qiong.liu1,BUG-4452809*/
            });
        }
        Log.i(TAG, "onCreate end" + System.currentTimeMillis());

        mFormulaEditText.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View view, DragEvent dragEvent) {
                if (dragEvent.getAction() == DragEvent.ACTION_DROP) {

                    if (!TextUtils.isEmpty(dragEvent.getClipData().getItemAt(0).getText())) {
                        mFormulaEditText.setText(getCommaString(dragEvent.getClipData().getItemAt(0).getText().toString()));
                    }
                }
                return true;
            }
        });
    }


    protected void saveDataAndDataState(Bundle bundle){
           bundle.putString(FORMULA_TEXT,mFormulaEditText.getText().toString());
           /* MODIFIED-BEGIN by qiong.liu1, 2017-05-02,BUG-4598039*/
           bundle.putInt(KEY_CURRENT_EVALUATOR_MODE, mEvaluator.getEvaluatorMode().ordinal());
           bundle.putBoolean(KEY_CURRENT_EVALUATOR_IS_EXPAND, mIsExpand);
           /* MODIFIED-END by qiong.liu1,BUG-4598039*/
    }

}
