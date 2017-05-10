/*HISTORY
 *
 *Tag            Date         Author        Description
 *============== ============ =============== ==============================
 *BUGFIX-1941917  2016/04/13   kaifeng.lu    [Monkey][Calculator][Crash]com.tct.calculator
 ===========================================================================
 */
package com.tct.calculator.view;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
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
import com.tct.calculator.adapter.CurrencyHistoryRecyclerView;
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
public class CurrencyDisplayOverlay extends FrameLayout {
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
    private View mHistoryBlankView;

    public static enum DisplayMode {FORMULA, GRAPH}

    ;

    private CurrencyHistoryRecyclerView mRecyclerView;
    private CalculatorEditText mFormula;
    private View mResult;
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
    private RelativeLayout mEditTextLayout;
    private ChangeViewInParentCallBack mChangeViewInParentCallBack;


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

    public CurrencyDisplayOverlay(Context context) {
        super(context);
        setup();
    }

    public CurrencyDisplayOverlay(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup();
    }

    public CurrencyDisplayOverlay(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup();
    }

    public CurrencyDisplayOverlay(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
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

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        Log.d("lxl", "onFinishInflate");
        mRecyclerView = (CurrencyHistoryRecyclerView) findViewById(R.id.historyRecycler);
        mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        //TS:kaifeng.lu 2016-01-22 Calculator BUGFIX_ 1273635  ADD_S
        mEditTextLayout= (RelativeLayout) findViewById(R.id.ll_formula_result);
        //TS:kaifeng.lu 2016-01-22 Calculator BUGFIX_ 1273635  ADD_E
        mLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mFormula = (CalculatorEditText) findViewById(R.id.formula);
        mResult = findViewById(R.id.result);
        mGraphLayout = findViewById(R.id.graphLayout);
        mMainDisplay = findViewById(R.id.mainDisplay);
        mCloseGraphHandle = findViewById(R.id.closeGraphHandle);
        deleteheight = this.getResources().getDimension(R.dimen.delete_height);
        //TS:kaifeng.lu 2016-01-22 Calculator BUGFIX_ 1273635  ADD_S
        mHistoryBlankView = findViewById(R.id.ll_formula_result);
        //TS:kaifeng.lu 2016-01-22 Calculator BUGFIX_ 1273635  ADD_E
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = MotionEventCompat.getActionMasked(ev);
        float y = ev.getRawY();
        float x = ev.getRawX();
        TranslateState state = getTranslateState();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mInitialMotionY = y;
                mLastMotionY = y;
                mInitialMotionX = x;
                break;
            case MotionEvent.ACTION_MOVE:
                float dy = y - mInitialMotionY;
                float dx = x - mInitialMotionX;
                if (Math.abs(dy) < mTouchSlop) {
                    return false;
                }

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

        }
        return false;
    }

    private boolean isScrolledToEnd() {
        return mLayoutManager.findLastCompletelyVisibleItemPosition() ==
                mRecyclerView.getAdapter().getItemCount() - 1;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
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
                if (event.getRawY()>mInitialMotionY && mRecyclerView.getAdapter().getItemCount() == 1 ) {
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

    private void handleUp(MotionEvent event) {
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
            anim.setDuration((long) dt );  //TS:kaifeng.lu 2016-01-22 Calculator BUGFIX_1534066  MOD
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float clampedY = (float) animation.getAnimatedValue();
                    float factor = (1.0f - (float) 1 / 3 * (clampedY / getMaxTranslation(false)));
                    factor = factor > 1.0f ? 1.0f : factor;
//                    mEditTextLayout.setPivotX(getWidth());
//                    mEditTextLayout.setPivotY(0);
//                    mEditTextLayout.setScaleX(factor);
//                    mEditTextLayout.setScaleY(factor);
//
//                    mHistoryBlankView.setPivotY(0);
//                    mHistoryBlankView.setScaleY(factor);


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
            /* MODIFIED-BEGIN by qiong.liu1, 2017-04-07,BUG-4452809*/
            Log.v(TAG, "limitMaxTranslation = " + limitMaxTranslation);
        }
        return limitMaxTranslation > mMaxTranslationInParent ? mMaxTranslationInParent : limitMaxTranslation;
    }

    private void getLimitMaxTranslation() {
        //TS:kaifeng.lu 2016-01-22 Calculator BUGFIX_ 1273635  MOD_S
        limitMaxTranslation = (int) (getParentHeight() - getDisplayHeight());
        /* MODIFIED-END by qiong.liu1,BUG-4452809*/
        //TS:kaifeng.lu 2016-01-22 Calculator BUGFIX_ 1273635  MOD_E
    }

    private void updateTranslation(float dy) {
        float txY = getTranslationY() + dy;
        float clampedY = Math.min(Math.max(txY, 0), getMaxTranslation(false));
        setTranslationY(clampedY);
        //TS:kaifeng.lu 2016-01-22 Calculator BUGFIX_ 1273635  ADD_S
        float factor = (1.0f-  (float)1/3 * (clampedY/getMaxTranslation(false)));
//        mEditTextLayout.setPivotX(getWidth());
//        mEditTextLayout.setPivotY(0);
//        mEditTextLayout.setScaleX(factor);
//        mEditTextLayout.setScaleY(factor);
//        mHistoryBlankView.setPivotY(0);
//        mHistoryBlankView.setScaleY(factor);
        //TS:kaifeng.lu 2016-01-22 Calculator BUGFIX_ 1273635  ADD_E
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

    public CurrencyHistoryRecyclerView getHistoryView() {
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
        if (limitMaxTranslation == 0) {
            getLimitMaxTranslation();//init limitMax
        }
        maxTxpre = maxTx;
        if (maxTx < limitMaxTranslation) {

            maxTx = getMaxTranslation(!isNoInitDisplayTag);//if max less than limit ,to get max value
        }
        if (maxTxpre != maxTx) {
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
            //TS:kaifeng.lu 2016-01-22 Calculator BUGFIX_ 1273635  ADD_S
            ViewGroup.LayoutParams blankParams = mHistoryBlankView.getLayoutParams();
            blankParams.height = mEditTextLayout.getHeight();
            //TS:kaifeng.lu 2016-01-22 Calculator BUGFIX_ 1273635  ADD_E
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
                if(mRecyclerView.getAdapter()!=null) {
                    mRecyclerView.scrollToPosition(mRecyclerView.getAdapter().getItemCount() - 1);
                    Log.i(TAG,"mRecyclerView.getAdapter().getItemCount() ="+mRecyclerView.getAdapter().getItemCount()); // MODIFIED by kaifeng.lu, 2016-06-02,BUG-2222521
                }
            }catch (NullPointerException ex){
                Log.i(TAG,"mRecyclerView.getAdapter()  is  null");
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




    /**
     *    CallBack  used to  hide (show) the guide button in calculator_activity
     */

    @Override
    public void setTranslationY(float translationY) {
        super.setTranslationY(translationY);
        if (mChangeViewInParentCallBack != null) {
            mChangeViewInParentCallBack.changCalculatorView(translationY);
        }
    }

    public void setChangeViewCallBack(ChangeViewInParentCallBack callBack){
        mChangeViewInParentCallBack = callBack;
    }
}
