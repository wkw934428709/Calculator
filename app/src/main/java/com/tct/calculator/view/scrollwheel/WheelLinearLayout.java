package com.tct.calculator.view.scrollwheel;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Matrix;
import android.view.View;
import android.view.animation.Transformation;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tct.calculator.R;

public class WheelLinearLayout extends LinearLayout {

    private boolean enableScale = false;

    public WheelLinearLayout(Context context) {
        super(context);
        this.setStaticTransformationsEnabled(true);
    }

    public WheelLinearLayout(Context context, boolean enableScale) {
        super(context);
        this.setStaticTransformationsEnabled(true);
        this.enableScale = enableScale;
    }

    protected void updateTranslateScrollingOffset(int offset) {
        mTranslateOffset = offset;
    }

    /**
     * 这个方法主要对Child进行缩放处理。
     */
    protected boolean getChildStaticTransformation(View child, Transformation t) {
         /* 注意:
             1) 这里的父View和Child实质并没有被绘制在UI中，只不过借用了他们的draw()方法
           2) Child的高度值其实是固定的，Haier W910中为161.
             3) 示意图如下：
                 +----------------------@----------------------------+--------------------------------------
                 |                            ^                      |         ^                        ^
                 |                            |                      |         |                        |
               |                        |                   |     child.getHeight()/2      |
                 |                            |                      |         |                        |
               |                        |                   |        v                     |
                 +----------------------@----------------------------+--------------------------------  |
                 |                            |                      |         ^                        |
                 |                            |                      |         |                        |
                 |     计算scale的分子:child中心点Y轴值(加偏移)      |         |                        |
                 |              加上canvas的translate值            |         |                        |
                 |                            |                      |         |                        |
                 |                            |                      |         |         计算scale时的分母。child活动范围的一半
                 |  +-------------------------------------------+    |         |                        |
                 |  |                         |                 |    |         |                        |
               | |--here is one child--@----v-----------|   |        |                     |
               | |                                      |   |  parent.getHeight()/2        |
                 |  +-------------------------------------------+    |         |                        |
                 |                                                   |         |                        |
                 |                                                   |         |                        |
                 |                                                   |         |                        |
                 |                                                   |         |                        |
                 |                                                   |         |                        |
               |                                            |        V                     V
    -----------  |----------------------@-------------------------- -|--------------------------------------------
                 |                                                   |
                 |                                                   |
                 |                                                   |
                 |                                                   |
                 |                                                   |
                 |                                                   |
                 |                                                   |
                 |                                                   |
                 |                                                   |
                 |                                                   |
                 |                                                   |
                 |                                                   |
                 |                                                   |
                 |                                                   |
                 |                                                   |
                 |                                                   |
                 |                                                   |
                 |                                                   |
               +------------------@-------------------------+-------------------------------</pre>
         */


        // step 1:获得相关参数
        int parentHeight = this.getHeight();// 获得父View的高度
        int childHeight = child.getHeight();// 获得当前Child的高度
        float halfParentOffset = ((float) ((parentHeight + childHeight) >> 1));// 获得父View外加Child高度，并除以2。
        // 这个会在计算scale时做分母。

        // child中心点Y坐标与child.getHeight()/2的总和值等于child.getBottom().
        // 这里child.getBottom()请理解为child的中心点Y坐标跟child.getHeight()/2的总和
        float childLastBottom = ((float) child.getBottom() + mTranslateOffset);// Child的右下点Y轴值，但要加上canvas的translate值.

        // step 2: 计算child中心点Y坐标与child活动范围(父View高度加child.getHieight)上(下)边界的偏移量。
        float offset = 0;
        if (Float.compare(childLastBottom, halfParentOffset) > 0) {
            // 如果child的中心点大于父View的中心点，那么用父view的高度与一个child的高度的总和再减去当前child的Y轴最大值(即中心点Y坐标跟child.getHeight()/2的总和.当然这个值已经加了canvas偏移量)
            offset = parentHeight + childHeight - childLastBottom;
        } else {
            offset = childLastBottom;
        }

        // step 3: 计算缩放比例
        float scale = offset / halfParentOffset;
        if (Float.compare(scale, 0.0f) < 0) {
            scale = Math.abs(scale);
        }
        // change the color of the textview
        TextView fullName = (TextView)
                child.findViewById(R.id.full_name);
        TextView abbreviation = (TextView)
                child.findViewById(R.id.abbreviation);
        if (scale > 0.85) {
            if (isEnabled) {
                /* MODIFIED-BEGIN by qiong.liu1, 2017-04-01,BUG-3621966*/
                fullName.setTextColor(getResources().getColor(R.color.color_calculator_wheel_item));
                abbreviation.setTextColor(getResources().getColor(R.color.color_calculator_wheel_item));
            } else {
                fullName.setTextColor(Color.WHITE);
                abbreviation.setTextColor(Color.WHITE);
            }
        } else {
            fullName.setTextColor(getResources().getColor(R.color.color_calculator_wheel_item));
            abbreviation.setTextColor(getResources().getColor(R.color.color_calculator_wheel_item));
        }
//        if (Calculator.mScreenState == SCREENPORTQUTERTHREE
//                || Calculator.mScreenState == SCREENPORTHALF
//                || Calculator.mScreenState == SCREENPORTQUTER
//                || Calculator.mScreenState == SCREENLANDHALF) {
//            fullName.setTextSize(12);
//            abbreviation.setTextSize(16);
//        }else {
//            fullName.setTextSize(14);
//            abbreviation.setTextSize(18);
//        }
/* MODIFIED-END by qiong.liu1,BUG-3621966*/
//        }
        // step 4: 将缩放比例应用到Child的Transformation
        t.clear();
        t.setTransformationType(Transformation.TYPE_MATRIX);
        if (enableScale) {
            Matrix matrix = t.getMatrix();
            if (scale > 0.85) {
                matrix.setScale(1 + scale / 3f, 1 + scale / 3f, 0, ((float) childHeight) / 2.0f);
            } else {
                matrix.setScale(1, 1, 0, ((float) childHeight) / 2.0f);
            }
        }

        return true;
    }

    /**
     * 将某些Operate颜色置灰，不能滑动
     *
     * @param isEnbaled
     */
    public void setEnabledColor(boolean isEnbaled) {
        this.isEnabled = isEnbaled;
    }

    private boolean isEnabled = false;
    private int mTranslateOffset;
    //private static String TAG = WheelLinearLayout.class.getSimpleName();
}
