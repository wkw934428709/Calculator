package com.tct.calculator.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.tct.calculator.utils.Utils;

/**
 * Created by user on 17-1-19.
 */
public class CurrencyChartView extends View {


    private float[] data = {};


    public CurrencyChartView(Context context) {
        super(context);
    }

    public CurrencyChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CurrencyChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CurrencyChartView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public float[] getData() {
        return data;
    }

    public void setData(float[] data) {
        this.data = data;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        int length = data.length;
        if (data == null || length <= 1) {
            return;
        }

        Paint paint1 = new Paint();
        paint1.setAntiAlias(true);
        paint1.setColor(Color.WHITE);
        paint1.setStrokeWidth(5.0f);
        paint1.setStyle(Paint.Style.STROKE);
        Paint paint2 = new Paint();
        paint2.setStyle(Paint.Style.FILL);
        paint2.setColor(Color.WHITE);
        paint2.setAlpha(50);
        float xscale = getMeasuredWidth()/(length-1);
        float yscala = getMeasuredHeight()/(Utils.getArrayMax(data)- Utils.getArrayMin(data));
        Path path1 = new Path();
        path1.moveTo(0,getMeasuredHeight()-(data[0]-Utils.getArrayMin(data))*yscala);
        Path path2 = new Path();
        path2.moveTo(0,getMeasuredHeight()-(data[0]-Utils.getArrayMin(data))*yscala-5);
        for (int i = 1; i <length; i++) {
            if(i == length -1){
                path1.lineTo(getMeasuredWidth(), getMeasuredHeight() - (data[i] - Utils.getArrayMin(data)) * yscala);
                path2.lineTo(getMeasuredWidth(), getMeasuredHeight() - (data[i] - Utils.getArrayMin(data)) * yscala - 5);
            }else {
                path1.lineTo(i * xscale, getMeasuredHeight() - (data[i] - Utils.getArrayMin(data)) * yscala);
                path2.lineTo(i * xscale, getMeasuredHeight() - (data[i] - Utils.getArrayMin(data)) * yscala - 5);
            }
        }
        path2.lineTo(getMeasuredWidth(),getHeight());
        path2.lineTo(0,getMeasuredHeight());
        path2.close();
        canvas.drawPath(path1,paint1);
        canvas.drawPath(path2,paint2);
    }
}
