package com.tct.calculator.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import com.tct.calculator.R;
import com.tct.calculator.utils.Utils;

/**
 * Created by user on 16-9-28.
 */
public class WelcomeFragmentConverter extends Fragment {

    private ImageView aniImageView;
    private ScaleAnimation scaleAnimation;
    private int screenType = 1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        screenType = Utils.getMultiScreenType(getActivity());
        View view;
        switch (screenType){
            case 1://from 2/3~1
                view = inflater.inflate(R.layout.fragment_welcome_converter,container,false);
                break;
            case 0://from 1/2~2/3
                view = inflater.inflate(R.layout.splash_convert_over_half,container,false);
                break;
            case -1://from 1/4~1/2
                view = inflater.inflate(R.layout.splash_convert_half,container,false);
                break;
            default:
                view = inflater.inflate(R.layout.fragment_welcome_converter,container,false);
                break;
        }
        aniImageView = (ImageView) view.findViewById(R.id.convert_ani_imageview);
        return view;
    }

    public void initAnimation() {
        if (aniImageView == null){
            return;
        }
        if (aniImageView.getVisibility() == View.GONE) {
            aniImageView.setVisibility(View.VISIBLE);
        }
        switch (screenType){
            case 1://from 2/3~1
                scaleAnimation=new ScaleAnimation(0f,1f,0f,1f, Animation.RELATIVE_TO_SELF,0.42f,Animation.RELATIVE_TO_SELF,0.47f);
                break;
            case 0://from 1/2~2/3
                scaleAnimation=new ScaleAnimation(0f,1f,0f,1f, Animation.RELATIVE_TO_SELF,0.45f,Animation.RELATIVE_TO_SELF,0.47f);
                break;
            case -1://from 1/4~1/2
                scaleAnimation=new ScaleAnimation(0f,1f,0f,1f, Animation.RELATIVE_TO_SELF,0.45f,Animation.RELATIVE_TO_SELF,0.47f);
                break;
            default:
                scaleAnimation=new ScaleAnimation(0f,1f,0f,1f, Animation.RELATIVE_TO_SELF,0.42f,Animation.RELATIVE_TO_SELF,0.47f);
                break;
        }
        scaleAnimation.setDuration(600);
        //scaleAnimation.setStartOffset(100);
        aniImageView.startAnimation(scaleAnimation);
    }

    public void cancelAnimation() {
        if (aniImageView != null){
            aniImageView.setVisibility(View.GONE);
            if (scaleAnimation != null) {
                scaleAnimation.cancel();
            }
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser){
            initAnimation();
        }else {
            cancelAnimation();
        }
    }
}
