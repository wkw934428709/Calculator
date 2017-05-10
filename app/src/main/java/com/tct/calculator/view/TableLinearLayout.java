package com.tct.calculator.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by user on 17-1-18.
 */
public class TableLinearLayout extends View {

    private Paint mPaint;
    private final static int TABLECOUNT = 7;


    public TableLinearLayout(Context context) {
        super(context);
    }

    public TableLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TableLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public TableLinearLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mPaint = new Paint();
        mPaint.setColor(Color.WHITE);
        mPaint.setAlpha(50);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(5.0f);
        mPaint.setStyle(Paint.Style.STROKE);

        for(int i =1;i < TABLECOUNT;i++){
            canvas.drawLine(i*(getMeasuredWidth()/TABLECOUNT),0,i*(getMeasuredWidth()/TABLECOUNT),getMeasuredHeight(),mPaint);
            canvas.drawLine(0,i*(getMeasuredHeight()/TABLECOUNT),getMeasuredWidth(),i*(getMeasuredHeight()/TABLECOUNT),mPaint);
        }
    }
}
