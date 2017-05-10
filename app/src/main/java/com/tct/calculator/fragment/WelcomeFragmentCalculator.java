package com.tct.calculator.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tct.calculator.R;
import com.tct.calculator.utils.Utils;

/**
 * Created by user on 16-9-28.
 */
public class WelcomeFragmentCalculator extends Fragment/* implements Animation.AnimationListener*/ {

    private Button btnTips, btnSelAdvanced, btnSelHistory;
    private ImageView imgAdvanced, imgAlpha, imgHistory;
    private TextView historyText, scientificText;
    private RelativeLayout animAdvanced, animHistory;

    private AnimationSet animSetTranLeft, animSetTranRight, animSetAlphaMax, animSetAlphaMin, animSetDown, animSetUp;
    private TranslateAnimation animTranLeft, animTranRight, animTranDown, animTranUp;
    private AlphaAnimation animAlphaMax, animAlphaMin, alpha0, alpha100;

    private ImageView mAnimationImage;
    private ScaleAnimation scaleAnimation;
    private Context mContext;
    private int screenType = 1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        screenType = Utils.getMultiScreenType(getActivity());
        View view;
        switch (screenType) {
            case 1://from 2/3~1
                view = inflater.inflate(R.layout.fragment_welcome_calculator, container, false);
                break;
            case 0://from 1/2~2/3
                view = inflater.inflate(R.layout.splash_calculate_over_half, container, false);
                break;
            case -1://from 1/4~1/2
                view = inflater.inflate(R.layout.splash_calculate_half, container, false);
                break;
            default:
                view = inflater.inflate(R.layout.fragment_welcome_calculator, container, false);
                break;
        }
        /*initViews(view);
        initAnim();*/
        mAnimationImage = (ImageView) view.findViewById(R.id.num_ani_imageview);
        return view;

    }

    private void initViews(View view) {
        animAdvanced = (RelativeLayout) view.findViewById(R.id.animAdvanced);
        animHistory = (RelativeLayout) view.findViewById(R.id.animHistory);
        imgAdvanced = (ImageView) view.findViewById(R.id.image_advanced);
        //TS:kaifeng.lu 2016-01-22 Calculator BUGFIX_ 1273635  ADD_S
        historyText = (TextView) view.findViewById(R.id.history_textview);
        scientificText = (TextView) view.findViewById(R.id.scientific_textview);
        //TS:kaifeng.lu 2016-01-22 Calculator BUGFIX_ 1273635  ADD_S
        imgAlpha = (ImageView) view.findViewById(R.id.image_advanced_bg_alpha);
        imgHistory = (ImageView) view.findViewById(R.id.image_history);
//        btnSelAdvanced= (Button) findViewById(R.id.select_point_advanced);
//        btnSelHistory= (Button) findViewById(R.id.select_point_history);
        btnTips = (Button) view.findViewById(R.id.tips_btn);
        //TS:kaifeng.lu 2015-12-08 Calculator BUGFIX_1052566  DEL_S
//        btnSelAdvanced.setOnClickListener(this);
//        btnSelHistory.setOnClickListener(this);
        //TS:kaifeng.lu 2015-12-08 Calculator BUGFIX_1052566  DEL_E
    }


    /*private void initAnim() {
        animSetTranLeft = new AnimationSet(true);
        animTranLeft = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.95f, Animation.RELATIVE_TO_SELF, 0.15f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f);
        animTranLeft.setDuration(2000);
        animTranLeft.setStartOffset(300);
        animTranLeft.setInterpolator(new AccelerateDecelerateInterpolator());
        animTranLeft.setAnimationListener(this);
        animSetTranLeft.addAnimation(animTranLeft);

        animSetTranRight =new AnimationSet(true);
        animTranRight=new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.15f, Animation.RELATIVE_TO_SELF, 0.95f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f);
        animTranRight.setDuration(2000);
        animTranRight.setInterpolator(new AccelerateDecelerateInterpolator());
        animSetTranRight.setStartOffset(300);
        animTranRight.setAnimationListener(this);
        animSetTranRight.addAnimation(animTranRight);

        animSetAlphaMax=new AnimationSet(true);
        animAlphaMax =new AlphaAnimation(0f,1.0f);
        animAlphaMax.setDuration(2000);
        animAlphaMax.setInterpolator(new AccelerateDecelerateInterpolator());
        animAlphaMax.setStartOffset(300);
        animAlphaMax.setAnimationListener(this);
        animSetAlphaMax.addAnimation(animAlphaMax);

        alpha0 =new AlphaAnimation(1.0f,0f);
        alpha0.setDuration(2000);
        alpha0.setInterpolator(new AccelerateDecelerateInterpolator());
        alpha0.setStartOffset(300);


        alpha100 = new AlphaAnimation(0f,1.0f);
        alpha100.setDuration(2000);
        alpha100.setInterpolator(new AccelerateDecelerateInterpolator());
        alpha100.setStartOffset(300);

        animSetAlphaMin=new AnimationSet(true);
        animAlphaMin=new AlphaAnimation(1.0f,0f);
        animAlphaMin.setDuration(2000);
        animAlphaMin.setInterpolator(new AccelerateDecelerateInterpolator());
        animAlphaMin.setStartOffset(300);
        animAlphaMin.setAnimationListener(this);
        animSetAlphaMin.addAnimation(animAlphaMin);


        animSetDown = new AnimationSet(true);
        animTranDown = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, -0.7255f, Animation.RELATIVE_TO_SELF, 0f);
        animTranDown.setDuration(2000);
        animTranDown.setStartOffset(300);
        animTranDown.setInterpolator(new AccelerateDecelerateInterpolator());
        animTranDown.setAnimationListener(this);
        animSetDown.addAnimation(animTranDown);

        animSetUp =new AnimationSet(true);
        animTranUp =new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, -0.7255f);
        animTranUp.setDuration(2000);
        animTranUp.setInterpolator(new AccelerateDecelerateInterpolator());
        animSetUp.setStartOffset(300);
        animTranUp.setAnimationListener(this);
        animSetUp.addAnimation(animTranUp);
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        if (animation.equals(animAlphaMax)){
            imgAdvanced.startAnimation(animSetTranRight);
            scientificText.startAnimation(alpha0);
            imgAlpha.startAnimation(animSetAlphaMin);
        }else if (animation.equals(animAlphaMin)){
            runAnimHistory();
        }else if (animation.equals(animTranDown)){
            imgHistory.startAnimation(animSetUp);
            historyText.startAnimation(alpha0);
        }else if (animation.equals(animTranUp)){
            runAnimAdvanced();
        }
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    private void runAnimAdvanced(){
//        btnSelHistory.setBackground(getDrawable(R.drawable.ic_point_off));
//        btnSelAdvanced.setBackground(getDrawable(R.drawable.ic_point_on));
        animHistory.setVisibility(View.GONE);
        animAdvanced.setVisibility(View.VISIBLE);
        imgAdvanced.startAnimation(animSetTranLeft);
        scientificText.startAnimation(alpha100);
        imgAlpha.startAnimation(animSetAlphaMax);
    }


    private void runAnimHistory(){
//        btnSelAdvanced.setBackground(getDrawable(R.drawable.ic_point_off));
//        btnSelHistory.setBackground(getDrawable(R.drawable.ic_point_on));
        animAdvanced.setVisibility(View.GONE);
        animHistory.setVisibility(View.VISIBLE);
        historyText.startAnimation(alpha100);
        imgHistory.startAnimation(animSetDown);
    }*/

    @Override
    public void onResume() {
        super.onResume();
        if (getUserVisibleHint()) {
            initAnimation();
        }
        //runAnimAdvanced();
    }

    public void initAnimation() {
        if (mAnimationImage == null) {
            return;
        }
        if (mAnimationImage.getVisibility() == View.GONE) {
            mAnimationImage.setVisibility(View.VISIBLE);
        }
        switch (screenType) {
            case -1://from 1/4~1/2
                scaleAnimation = new ScaleAnimation(0f, 1f, 0f, 1f, Animation.RELATIVE_TO_SELF, 0.6f, Animation.RELATIVE_TO_SELF, 0.35f);
                break;
            case 0://from 1/2~2/3
                scaleAnimation = new ScaleAnimation(0f, 1f, 0f, 1f, Animation.RELATIVE_TO_PARENT, 0.52f, Animation.RELATIVE_TO_PARENT, 0.35f);
                break;
            case 1://from 2/3~1
                scaleAnimation = new ScaleAnimation(0f, 1f, 0f, 1f, Animation.RELATIVE_TO_SELF, 0.7f, Animation.RELATIVE_TO_SELF, 0.35f);
                break;
            default:
                scaleAnimation = new ScaleAnimation(0f, 1f, 0f, 1f, Animation.RELATIVE_TO_SELF, 0.7f, Animation.RELATIVE_TO_SELF, 0.35f);
                break;
        }
        scaleAnimation.setDuration(600);
        //scaleAnimation.setStartOffset(100);
        mAnimationImage.startAnimation(scaleAnimation);
    }

    @Override
    public void onStop() {
        super.onStop();
        //cancelAllAnimation();
    }

    private void cancelAllAnimation() {
        animTranLeft.cancel();
        animTranRight.cancel();
        animTranDown.cancel();
        animTranUp.cancel();
        animAlphaMax.cancel();
        alpha0.cancel();
        alpha100.cancel();
        animAlphaMin.cancel();
    }

    public void cancelAnimation() {
        if (mAnimationImage != null) {
            mAnimationImage.setVisibility(View.GONE);
            if (scaleAnimation != null) {
                scaleAnimation.cancel();
            }
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            initAnimation();
        } else {
            cancelAnimation();
        }
    }
}
