/*HISTORY
 *
 *Tag            Date         Author        Description
 *============== ============ =============== ==============================
 *BUGFIX-1941917  2016/04/13   kaifeng.lu    [Monkey][Calculator][Crash]com.tct.calculator
 ===========================================================================
 */
package com.tct.calculator.view.history;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.tct.calculator.CalculatorEditText;
import com.tct.calculator.R;
import com.tct.calculator.utils.Constant;
import com.tct.calculator.view.AnimationUtil;
import com.tct.calculator.view.interfaces.ChangeViewInParentCallBack;

/**
 * * create by xlli PR 996836 	[Calculator](new) calculation history
 * The display overlay is a container that intercepts touch events on top of:
 * 1. the display, i.e. the formula and result views
 * 2. the history view, which is revealed by dragging down on the display
 * <p/>
 * This overlay passes vertical scrolling events down to the history recycler view
 * when applicable.  If the user attempts to scroll up and the recycler is already
 * scrolled all the way up, then we intercept the event and collapse the history.
 */
public class CommonDisplayOverlay extends FrameLayout {
    /**
     * Closing the history with a fling will finish at least this fast (ms)
     */
    private static final float MIN_SETTLE_DURATION = 150f;

    /**
     * Do not settle overlay if velocity is less than this
     */
    private static float VELOCITY_SLOP = 0.1f;

    private static boolean DEBUG = false;
    private static final String TAG = "DisplayOverlay";
    private RelativeLayout mCommonTopEditTextLayout;
    private RelativeLayout mCommonBottomEditTextLayout;
    private View mCommonBottomHistoryBlankView;
    private View mCommonTopHistoryBlankView;
    private View mHistoryBlankView;
    private View mConvertBottomItemLayout, mConvertTopItemLayout;
    private View mWheelContainer;
    private View mCommonBottomLLFormulaResultContainer;
    private int mCommonBottomLLFormulaResultContainerHeight;
    private int mCommonTopEditTextLayoutHeight;
    private int mConvertBottomItemLayoutHeight, tempmConvertBottomItemLayoutHeight;
    private RelativeLayout mScaleBackgroundLayout;
    private ChangeViewInParentCallBack mChangeViewInParentCallBack;
    private View mCommonTopBottomDivider;

    public void setWheelView(View wheelContainer) {
        mWheelContainer = wheelContainer;

    }

    //    private RelativeLayout mCommonTopEditTextLayout;
    private View mActionBarView;

    public static enum DisplayMode {FORMULA, GRAPH}

    ;

    private CommonMyRecyclerView mRecyclerView;
    //    private CalculatorEditText mFormula;
//    private View mResult;

    private CalculatorEditText mCommonTopFormula;
    private View mCommonTopResult;
    private View mCommonBottomResult;
    private View mGraphLayout;
    private View mCloseGraphHandle;
    private View mMainDisplay;
    private DisplayMode mMode;
    private LinearLayoutManager mLayoutManager;
    private float mInitialMotionY;
    private float mLastMotionY;
    private float mTotalDeltaY;
    private float mLastDeltaY;
    private float mSecondLastDeltay;
    private float mInitialMotionX;
    private int mTouchSlop;
    private int mMaxTranslationInParent = -1;
    private VelocityTracker mVelocityTracker;
    private float mMinVelocity = -1;
    private int mParentHeight = -1;
    private int limitMaxTranslation;
    private float deleteheight;
    private int maxTx;
    private int maxTxpre;
    private final int MoveLimitX = 100;
    private float YRangeSlowMove;//if slowmove,Less Yrange reset panel,more


    /**
     * Reports when state changes to expanded or collapsed (partial is ignored)
     */
    public static interface TranslateStateListener {
        public void onTranslateStateChanged(TranslateState newState);
    }

    private TranslateStateListener mTranslateStateListener;

    public void setOnTranslateStateListener(TranslateStateListener listener) {
        this.mTranslateStateListener = listener;
    }

    public CommonDisplayOverlay(Context context) {
        super(context);
        setup();
    }

    private int mPosition = 0;

    public CommonDisplayOverlay(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DisplayOverlay);
        mPosition = a.getInteger(R.styleable.DisplayOverlay_calculatorType, 0);
        setup();
    }

    public CommonDisplayOverlay(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup();
    }

    public CommonDisplayOverlay(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setup();
    }

    private void setup() {
        ViewConfiguration vc = ViewConfiguration.get(getContext());
        mTouchSlop = vc.getScaledTouchSlop();
        YRangeSlowMove = this.getResources().getDimension(R.dimen.diaplyoverlay_y_range_slowmove);


    }

    public static enum TranslateState {
        EXPANDED, COLLAPSED, PARTIAL
    }

    private Constant.CalculatorType mCalculatorType;

    public void setCalculatorType(Constant.CalculatorType calculatorType) {
        this.mCalculatorType = calculatorType;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        Log.d("lxl", "onFinishInflate");
        mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView = (CommonMyRecyclerView) findViewById(R.id.common_historyRecycler);
        mCommonTopEditTextLayout = (RelativeLayout) findViewById(R.id.common_top_ll_formula_result);
        mCommonBottomEditTextLayout = (RelativeLayout) findViewById(R.id.common_bottom_ll_formula_result);
        mLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        //The method is Shielded, the reason is that split line of adapter from CalculatorFragment、CurrencyFragment and ConverterFragment is same.
        //resetDividerColor();
        mCommonTopFormula = (CalculatorEditText) findViewById(R.id.common_top_formula);
        mCommonTopResult = findViewById(R.id.common_top_result);
        mCommonBottomResult = findViewById(R.id.common_bottom_result);
        mGraphLayout = findViewById(R.id.graphLayout);
        mMainDisplay = findViewById(R.id.common_mainDisplay);
        mCloseGraphHandle = findViewById(R.id.closeGraphHandle);
        deleteheight = this.getResources().getDimension(R.dimen.delete_height);
        mCommonTopHistoryBlankView = findViewById(R.id.common_top_blank_space);
        mCommonBottomHistoryBlankView = findViewById(R.id.common_bottom_blank_space);

        mHistoryBlankView = findViewById(R.id.common_container);
        mConvertBottomItemLayout = findViewById(R.id.convert_bottom_item_layout);
        mConvertTopItemLayout = findViewById(R.id.convert_top_item_layout);

        mCommonBottomLLFormulaResultContainer = findViewById(R.id.common_bottom_ll_formula_result_container);
        mCommonTopBottomDivider = (View) findViewById(R.id.common_top_bottom_divider);
        mScaleBackgroundLayout = (RelativeLayout) findViewById(R.id.scale_background_layout);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mConvertBottomItemLayoutHeight = mConvertBottomItemLayout.getHeight();
                tempmConvertBottomItemLayoutHeight = mConvertBottomItemLayoutHeight;
                mCommonTopEditTextLayoutHeight = mCommonTopEditTextLayout.getHeight();
                mCommonBottomLLFormulaResultContainerHeight = mCommonBottomLLFormulaResultContainer.getHeight();
                Log.i(TAG, "===XXX===CommonDisplayOverlay=mConvertTopItemLayout=" + mConvertTopItemLayout);
            }
        }, 500);
    }

    private boolean isTouchSlop = false;
    private float mScale = 1.0f;
    private boolean isTouchEnd = false;

    @Override
    public boolean onInterceptTouchEvent(final MotionEvent ev) {
        Log.i(TAG, "===XXX===CommonDisplayOverlay=onInterceptTouchEvent");
        int action = MotionEventCompat.getActionMasked(ev);
        float y = ev.getRawY();
        float x = ev.getRawX();
        TranslateState state = getTranslateState();
        isTouchEnd = false;
        switch (action) {
            case MotionEvent.ACTION_DOWN:
//                Log.i(TAG, "===XXX===CommonDisplayOverlay=onInterceptTouchEvent ACTION_DOWN");
                isTouchSlop = true;
                if (isInWheelView(ev)) {
                    isTouchSlop = false;
                    mWheelContainer.dispatchGenericMotionEvent(ev);

                    return false;
                }
                mInitialMotionY = y;
                mLastMotionY = y;
                mInitialMotionX = x;
                break;
            case MotionEvent.ACTION_MOVE:

//                Log.i(TAG, "===XXX===CommonDisplayOverlay=onInterceptTouchEvent ACTION_MOVE=1");
                float dy = y - mInitialMotionY;
                float dx = x - mInitialMotionX;
                if (Math.abs(dy) < mTouchSlop) {
                    isTouchSlop = true;
//                    Log.i(TAG, "===XXX===CommonDisplayOverlay=onInterceptTouchEvent ACTION_MOVE=2");
                    return false;
                }
                if (isInWheelView(ev)) {
                    isTouchSlop = false;
                    mWheelContainer.dispatchGenericMotionEvent(ev);
//                    Log.i(TAG, "===XXX===CommonDisplayOverlay=onInterceptTouchEvent ACTION_MOVE=3");
                    return false;
                } else {
//                    Log.i(TAG, "===XXX===CommonDisplayOverlay=onInterceptTouchEvent ACTION_MOVE=4");
                    if (mHideWheelListener != null) {
                        mHideWheelListener.hideWheel();
                    }
                }
//                Log.i(TAG, "===XXX===CommonDisplayOverlay=onInterceptTouchEvent ACTION_MOVE=5");

                // in graph mode let move events apply to the graph,
                // unless the touch is on the "close handle"
                if (mMode == DisplayMode.GRAPH) {
                    return isInBounds(ev.getX(), ev.getY(), mCloseGraphHandle);
                }

                if (dy < 0) {
                    Log.d("lxl", "onIntercept dy < 0 isScrolledToEnd:" + isScrolledToEnd() + "    state:" + state + "   return:" + (isScrolledToEnd() && state != TranslateState.COLLAPSED));
                    //replace 1
                    if (state != TranslateState.COLLAPSED) {
                        if (isScrolledToEnd() || Math.abs(dx) > MoveLimitX) {
                            return true;
                        } else {
                            //when scrolled is not end,downpoint must in maindisplay area,can collapsed and scrollToMostRecent
                            if (mInitialMotionY > mRecyclerView.getHeight()) {
                                (new Handler()).postDelayed(new Runnable() {

                                    @Override
                                    public void run() {
                                        scrollToMostRecent();
                                    }

                                }, 500);
                                return true;
                            } else {
                                return false;
                            }

                        }
                    } else {
                        return false;
                    }
                    //replace 1
                    // 1 return isScrolledToEnd() && state != TranslateState.COLLAPSED;

                } else if (dy > 0 && Math.abs(dx) < MoveLimitX) {
                    return state != TranslateState.EXPANDED;
                }
                break;
            case MotionEvent.ACTION_UP:
                Log.i(TAG, "===XXX===CommonDisplayOverlay=onInterceptTouchEvent ACTION_UP 1 isTouchSlop=" + isTouchSlop);
                if (isTouchSlop) {
                }
                if (isInTopUnitView(ev)) {
                    if (mConvertTopItemLayout != null) {
                        mConvertTopItemLayout.performClick();
                    }
                }
                if (isInWheelView(ev)) {
                    isTouchSlop = false;
//                    Log.i(TAG, "===XXX===CommonDisplayOverlay=onInterceptTouchEvent ACTION_UP 2 isTouchSlop=" + isTouchSlop);
                    mWheelContainer.dispatchGenericMotionEvent(ev);

                    return false;
                }
                break;
        }
        return false;
    }

    private boolean isScrolledToEnd() {
        return mLayoutManager.findLastCompletelyVisibleItemPosition() ==
                mRecyclerView.getAdapter().getItemCount() - 1;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.i(TAG, "===XXX===CommonDisplayOverlay=onTouchEvent");
        if (isInWheelView(event)) {
            isTouchSlop = false;
            mWheelContainer.onTouchEvent(event);
            return false;
        }
        int action = MotionEventCompat.getActionMasked(event);
        initVelocityTrackerIfNotExists();
        mVelocityTracker.addMovement(event);
        Log.d("lxl", "onTouchEvent " + action);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                handleMove(event);
                break;
            case MotionEvent.ACTION_UP:
                handleUp(event);
                if (event.getRawY() > mInitialMotionY && mRecyclerView.getAdapter().getItemCount() == 1) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (getTranslateState() != TranslateState.COLLAPSED) collapseHistory();
                        }
                    }, 1000);
                }
                recycleVelocityTracker();
                break;
            case MotionEvent.ACTION_CANCEL:
                recycleVelocityTracker();
                break;
        }

        return true;
    }

    private void handleMove(MotionEvent event) {
//        Log.i(TAG, "===XXX===CommonDisplayOverlay handleMove");
        TranslateState state = getTranslateState();
        float y = event.getRawY();
        float dy = y - mLastMotionY;
        if (DEBUG) {
            Log.v(TAG, "handleMove y=" + y + ", dy=" + dy);
        }
        if (dy < 0 && state != TranslateState.COLLAPSED) {
            updateTranslation(dy);
        } else if (dy > 0 && state != TranslateState.EXPANDED) {
            updateTranslation(dy);
        }
        mSecondLastDeltay = mLastDeltaY;
        mLastDeltaY = dy;//quick to round,
        mLastMotionY = y;

    }

    private boolean isInTopUnitView(MotionEvent event) {
        try {
            float x = mConvertTopItemLayout.getX();
            float y = mConvertTopItemLayout.getY();
            float height = mConvertTopItemLayout.getHeight();
            float width = mConvertTopItemLayout.getWidth();
            float rightX = x + width;
            float bottomY = y + height;
            float touchX = event.getRawX();
            float touchY = event.getRawY();
//            Log.i(TAG, "===XXX===CommonDisplayOverlay isInTopUnitView=x=" + x + ", y=" + y + ",touchX=" + touchX + ",touchY=" + touchY + ",width=" + width + ",height=" + height);
            if (x <= touchX && y <= touchY && rightX >= touchX && bottomY >= touchY) {
//                Log.i(TAG, "===XXX===CommonDisplayOverlay isInTopUnitView=true==!!!");
                return true;
            }
//            Log.i(TAG, "===XXX===CommonDisplayOverlay isInTopUnitView=false==!!!");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean isInWheelView(MotionEvent event) {
        try {
            float x = mWheelContainer.getX();
            float y = mWheelContainer.getY();
            float height = mWheelContainer.getHeight();
            float width = mWheelContainer.getWidth();
            float rightX = x + width;
            float bottomY = y + height;
            float touchX = event.getX();
            float touchY = event.getY() - mRecyclerView.getHeight();
//            Log.i(TAG, "===XXX===CommonDisplayOverlay isInWheelView=x" + x + ", y=" + y + ",touchX=" + touchX + ",touchY=" + touchY + ",width=" + width + ",height=" + height);
            if (x <= touchX && y <= touchY && rightX >= touchX && bottomY >= touchY) {
//                Log.i(TAG, "===XXX===CommonDisplayOverlay isInWheelView=true==!!!");
                return true;
            }
//            Log.i(TAG, "===XXX===CommonDisplayOverlay isInWheelView=false==!!!");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void handleUp(MotionEvent event) {
//        Log.i(TAG, "===XXX===CommonDisplayOverlay handleUp");
        mTotalDeltaY = event.getRawY() - mInitialMotionY;
        mVelocityTracker.computeCurrentVelocity(1);
        float yvel = mVelocityTracker.getYVelocity();
        if (DEBUG) {
            Log.v(TAG, "handleUp yvel=" + yvel + ", mLastDeltaY=" + mLastDeltaY);
        }
        if (mSecondLastDeltay != 0 && ((mSecondLastDeltay > 0 && mLastDeltaY < 0) || (mSecondLastDeltay < 0 && mLastDeltaY > 0))) {
            mLastDeltaY = -mLastDeltaY;
        }//sometime mlastDeltaY has error,so to adapter
        TranslateState curState = getTranslateState();
        if (curState != TranslateState.PARTIAL) {
            // already settled
            if (mTranslateStateListener != null) {
                Log.d("lxl", "handleUp mTranslateStateListener");
                mTranslateStateListener.onTranslateStateChanged(curState);
            }
        } else if (Math.abs(yvel) > VELOCITY_SLOP) {
            // the sign on velocity seems unreliable, so use last delta to determine direction
            float destTx = ((mLastDeltaY >= 0)) ? getMaxTranslation(false) : 0;
            float velocity = Math.max(Math.abs(yvel), Math.abs(mMinVelocity));
//            Log.i(TAG, "===XXX===CommonDisplayOverlay handleUp=0");
            settleAt(destTx, velocity);
        } else if (yvel == 0) {//if hand step
            MaxShow(mLastDeltaY > 0, yvel);
        } else if (mLastDeltaY > 0) {
            //to down
            MaxShow(Math.abs(mTotalDeltaY) > YRangeSlowMove, yvel);
        } else {
            //to up
            MaxShow(Math.abs(mTotalDeltaY) < YRangeSlowMove, yvel);
        }
        mSecondLastDeltay = 0;
    }

    private void MaxShow(boolean isshow, float yvel) {
//        Log.i(TAG, "===XXX===CommonDisplayOverlay MaxShow，isshow=" + isshow + "=yvel" + yvel);
        float velocity = Math.max(Math.abs(yvel), Math.abs(mMinVelocity));
        if (isshow) {
            settleAt(getMaxTranslation(false), velocity);
        } else {
            settleAt(0, velocity);
        }
    }

    public void expandHistory() {
        settleAt(getMaxTranslation(false), mMinVelocity);
    }

    public void collapseHistory() {
        settleAt(0, mMinVelocity);
    }

    public int getDisplayHeight() {
        return mHistoryBlankView.getHeight();
    }

    /**
     * Smoothly translates the display overlay to the given target
     *
     * @param destTx target translation
     * @param yvel   velocity at point of release
     */
    private void settleAt(float destTx, float yvel) {
//        Log.i(TAG, "===XXX===CommonDisplayOverlay settleAt，destTx=" + destTx + "=yvel" + yvel);
        if (yvel != 0) {
            float dist = destTx - getTranslationY();
            //TS:kaifeng.lu 2016-01-22 Calculator BUGFIX_ 1534066  DEL
            float dt = Math.abs(dist / yvel);
            if (DEBUG) {
                Log.v(TAG, "settle display overlay yvel=" + yvel +
                        ", dt = " + dt);
            }
            ObjectAnimator anim =
                    ObjectAnimator.ofFloat(this, "translationY",
                            getTranslationY(), destTx);
            //TS:kaifeng.lu 2016-01-22 Calculator BUGFIX_ 1273635  MOD_S
            anim.setDuration((long) dt);  //TS:kaifeng.lu 2016-01-22 Calculator BUGFIX_1534066  MOD
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float clampedY = (float) animation.getAnimatedValue();
                    float factor = (1.0f - (float) 1 / 3 * (clampedY / getMaxTranslation(false)));
                    factor = factor > 1.0f ? 1.0f : factor;
                    scaleLayout(factor);

                }
            });
            //TS:kaifeng.lu 2016-01-22 Calculator BUGFIX_ 1273635  MOD_E
            anim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if (mTranslateStateListener != null) {
                        mTranslateStateListener.onTranslateStateChanged(getTranslateState());
                    }
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            });
            anim.start();
        }
    }

    /**
     * The distance that we are able to pull down the display to reveal history.
     */
    private int getMaxTranslation(boolean isRegetHeight) {
        if (mMaxTranslationInParent == -1 || mMaxTranslationInParent == 0 || isRegetHeight) {
            mMaxTranslationInParent = mRecyclerView.getMyMeasureHeight();
        }
        if (DEBUG) {
            Log.v(TAG, "mMaxTranslationInParent = " + mMaxTranslationInParent);
        }
        Log.i(TAG, "===XXX===CommonDisplayOverlay getMaxTranslation limitMaxTranslation=" + limitMaxTranslation + ",mMaxTranslationInParent=" + mMaxTranslationInParent);
        return limitMaxTranslation > mMaxTranslationInParent ? mMaxTranslationInParent : limitMaxTranslation;
    }

    private void getLimitMaxTranslation() {
        limitMaxTranslation = (int) (getParentHeight() - getDisplayHeight());
        Log.i(TAG, "===XXX===CommonDisplayOverlay getLimitMaxTranslation limitMaxTranslation=" + limitMaxTranslation);
    }

    private void updateTranslation(float dy) {
        float txY = getTranslationY() + dy;
        float clampedY = Math.min(Math.max(txY, 0), getMaxTranslation(false));
        setTranslationY(clampedY);
        float factor = (1.0f - (float) 1 / 3 * (clampedY / getMaxTranslation(false)));
        Log.i(TAG, "===XXX===CommonDisplayOverlay updateTranslation，dy=" + dy + ",txY=" + txY + ",clampedY=" + clampedY);
        scaleLayout(factor);
    }

    private void scaleLayout(float factor) {
        Log.i(TAG, "===XXX===CommonDisplayOverlay scaleLayout=factor=" + getTranslateState() + ",factor=" + factor);
        try {
            mScale = factor;
            //scale layout== start
//            tempmConvertBottomItemLayoutHeight = (int) (tempmConvertBottomItemLayoutHeight * factor);
//            mCommonTopEditTextLayout.setPivotX(getWidth());
//            mCommonTopEditTextLayout.setPivotY(0);
//            mCommonTopEditTextLayout.setScaleX(factor);
//            mCommonTopEditTextLayout.setScaleY(factor);
//
//            mCommonBottomEditTextLayout.setPivotX(getWidth());
//            mCommonBottomEditTextLayout.setPivotY(0);
//            mCommonBottomEditTextLayout.setScaleX(factor);
//            mCommonBottomEditTextLayout.setScaleY(factor);
//
//            mConvertTopItemLayout.setPivotX(0);
//            mConvertTopItemLayout.setPivotY(0);
//            mConvertTopItemLayout.setScaleX(factor);
//            mConvertTopItemLayout.setScaleY(factor);
//
//            mConvertBottomItemLayout.setPivotX(0);
//            mConvertBottomItemLayout.setPivotY(0);
//            mConvertBottomItemLayout.setScaleX(factor);
//            mConvertBottomItemLayout.setScaleY(factor);
//
//
//            mScaleBackgroundLayout.setPivotX(0);
//            mScaleBackgroundLayout.setPivotY(0);
//            mScaleBackgroundLayout.setScaleX(factor);
//            mScaleBackgroundLayout.setScaleY(factor);
//
//            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mCommonBottomLLFormulaResultContainer.getLayoutParams();
//            params.removeRule(RelativeLayout.BELOW);
//            mCommonBottomLLFormulaResultContainer.setTranslationY(-(mCommonTopEditTextLayoutHeight * (1 - factor) + mConvertBottomItemLayoutHeight * (1 - factor)));
//            mCommonTopBottomDivider.setTranslationY(-(mCommonTopEditTextLayoutHeight * (1 - factor) + mConvertBottomItemLayoutHeight * (1 - factor)) + mConvertBottomItemLayoutHeight * (1 - factor));
//            mConvertBottomItemLayout.setTranslationY(mConvertBottomItemLayoutHeight * (1 - factor));
//            mCommonBottomEditTextLayout.setTranslationY(mConvertBottomItemLayoutHeight * (1 - factor));
//            mScaleBackgroundLayout.setTranslationY(mConvertBottomItemLayoutHeight * (1 - factor));
//
//            if (factor == 1.0f) {
//                params.addRule(RelativeLayout.BELOW, R.id.common_top_bottom_divider);
//                mCommonBottomLLFormulaResultContainer.setTranslationY(0);
//                mCommonTopBottomDivider.setTranslationY(0);
//                mCommonBottomEditTextLayout.setTranslationY(0);
//                mConvertBottomItemLayout.setTranslationY(0);
//                mScaleBackgroundLayout.setTranslationY(0);
//                params.height = mCommonBottomLLFormulaResultContainerHeight;
//                mCommonBottomLLFormulaResultContainer.setLayoutParams(params);
//                mCommonBottomLLFormulaResultContainer.invalidate();
//            }
            //scale layout== end
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public TranslateState getTranslateState() {
        float txY = getTranslationY();
        if (txY <= 0) {
            return TranslateState.COLLAPSED;
        } else if (txY >= getMaxTranslation(false)) {
            return TranslateState.EXPANDED;
        } else {
            return TranslateState.PARTIAL;
        }
    }

    public CommonMyRecyclerView getHistoryView() {
        return mRecyclerView;
    }

    private void initVelocityTrackerIfNotExists() {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
    }

    private void recycleVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
        isTouchEnd = true;
    }

    private int getParentHeight() {
        if (mParentHeight < 0) {
            ViewGroup parent = (ViewGroup) getParent();
            mParentHeight = parent.getHeight();
        }
        return mParentHeight;
    }

    public void setMaxTranlation(int maxtx) {
        this.maxTx = maxtx;
    }

    /**
     * Set the size and offset of the history view / graph view
     * <p/>
     * We want the display+history to take up the full height of the parent minus some
     * predefined padding.  The normal way to do this would be to give the overlay a height
     * of match_parent minus some margin, and set an initial translation.  The issue with
     * this is that the display has a height of wrap content and the keypad fills the
     * remaining space, so we cannot determine the proper height for the history view until
     * after layout completes.
     * <p/>
     * To account for this, we make this method available to setup the history and graph
     * views after layout completes.
     */

    public void initializeHistoryAndGraphView(boolean isNoInitDisplayTag) {
        Log.i(TAG, "===XXX===CommonDisplayOverlay initializeHistoryAndGraphView，isNoInitDisplayTag=" + isNoInitDisplayTag);
        if (limitMaxTranslation == 0) {
            getLimitMaxTranslation();//init limitMax
        }
        maxTxpre = maxTx;
        if (maxTx < limitMaxTranslation) {
            Log.i(TAG, "===XXX===CommonDisplayOverlay initializeHistoryAndGraphView 111");
            maxTx = getMaxTranslation(!isNoInitDisplayTag);//if max less than limit ,to get max value
        }
        if (maxTxpre != maxTx) {
            Log.i(TAG, "===XXX===CommonDisplayOverlay initializeHistoryAndGraphView 222");
            MarginLayoutParams historyParams = (MarginLayoutParams) mRecyclerView.getLayoutParams();
            historyParams.height = maxTx;
            MarginLayoutParams graphParams = (MarginLayoutParams) mGraphLayout.getLayoutParams();
            graphParams.height = maxTx + getDisplayHeight();
            if (DEBUG) {
                Log.v(TAG, "Set history height to " + maxTx
                        + ", graph height to " + graphParams.height);
            }
            MarginLayoutParams overlayParams =
                    (MarginLayoutParams) getLayoutParams();
            overlayParams.topMargin = -maxTx;
            Log.i(TAG, "===XXX===CommonDisplayOverlay initializeHistoryAndGraphView topMargin=" + overlayParams.topMargin + ",historyParams.height=" + historyParams.height + ",graphParams.height=" + graphParams.height + ",maxTx=" + maxTx);
            requestLayout();
            scrollToMostRecent();

        }
        if (mMinVelocity <= 0) {
            Log.d("lxl", "mMinVelocity<0");
            int txDist = getMaxTranslation(false);
            mMinVelocity = txDist / MIN_SETTLE_DURATION;
        }
    }

    public void scrollToMostRecent() {
//        //TS:kaifeng.lu 2016-04-13 Calculator BUGFIX_ 1941917   MOD_S
//        if (mRecyclerView != null) {
//            mRecyclerView.scrollToPosition(mRecyclerView.getAdapter().getItemCount() - 1);
//        }
//        //TS:kaifeng.lu 2016-04-13 Calculator BUGFIX_ 1941917   MOD_E


        //TS:kaifeng.lu 2016-05-8 Calculator BUGFIX_1941917   MOD_S
        try {
            if (mRecyclerView.getAdapter() != null) {
                mRecyclerView.scrollToPosition(mRecyclerView.getAdapter().getItemCount() - 1);
                Log.i(TAG, "mRecyclerView.getAdapter().getItemCount() =" + mRecyclerView.getAdapter().getItemCount()); // MODIFIED by kaifeng.lu, 2016-06-02,BUG-2222521
            }
        } catch (NullPointerException ex) {
            Log.i(TAG, "mRecyclerView.getAdapter()  is  null");
        }
        //TS:kaifeng.lu 2016-05-8 Calculator BUGFIX_2049568   MOD_E
    }


    public void setTranslateStateListener(TranslateStateListener listener) {
        mTranslateStateListener = listener;
    }

    public TranslateStateListener getTranslateStateListener() {
        return mTranslateStateListener;
    }

    private boolean isInBounds(float x, float y, View v) {
        return y >= v.getTop() && y <= v.getBottom() &&
                x >= v.getLeft() && x <= v.getRight();
    }

    public void animateModeTransition() {
        switch (mMode) {
            case GRAPH:
                expandHistory();
                AnimationUtil.fadeOut(mMainDisplay);
                AnimationUtil.fadeIn(mGraphLayout);
                break;
            case FORMULA:
                collapseHistory();
                AnimationUtil.fadeIn(mMainDisplay);
                AnimationUtil.fadeOut(mGraphLayout);
                break;
        }
    }

    public void setMode(DisplayMode mode) {
        mMode = mode;
    }

    public DisplayMode getMode() {
        return mMode;
    }

    public CommonMyRecyclerView getCommonMyRecyclerView() {
        return mRecyclerView;
    }

    public void setActionBar(View view) {
        this.mActionBarView = view;
    }


    /**
     * CallBack  used to  hide (show) the guide button in calculator_activity
     */

    @Override
    public void setTranslationY(float translationY) {
        super.setTranslationY(translationY);
        mChangeViewInParentCallBack.changCalculatorView(translationY);
    }

    public void setChangeViewCallBack(ChangeViewInParentCallBack callBack) {
        mChangeViewInParentCallBack = callBack;
    }

    private void resetDividerColor() {
        mRecyclerView.addItemDecoration(new RecycleViewDivider(getContext(), R.drawable.history_divider));
    }

    private HideWheelListener mHideWheelListener;

    public void setHideWheelListener(HideWheelListener hideWheelListener) {
        this.mHideWheelListener = hideWheelListener;
    }

    public interface HideWheelListener {
        void hideWheel();
    }
}
