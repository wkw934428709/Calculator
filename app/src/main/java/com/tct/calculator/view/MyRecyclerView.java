package com.tct.calculator.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowManager;

import com.tct.calculator.R;

import java.util.ArrayList;

/**
 * Created by xlli on 8/10/15.
 */
public class MyRecyclerView extends RecyclerView{
    public MyRecyclerView(Context context) {
        super(context);
    }
    private OnResizeListener mListener;
    private int mWindowWidth ;
    public interface OnResizeListener {
        void OnResize(int w, int h, int oldw, int oldh);
    }

    public void setOnResizeListener(OnResizeListener l) {
        mListener = l;
    }

    public MyRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //TS:kaifeng.lu 2016-02-17 Calculator BUGFIX_ 1594843  ADD_S
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mWindowWidth  = wm.getDefaultDisplay().getWidth();
        //TS:kaifeng.lu 2016-02-17 Calculator BUGFIX_ 1594843  ADD_E
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        if (mListener != null) {
            mListener.OnResize(w, h, oldw, oldh);
        }
    }
    public int getMyMeasureHeight(){
        int sum = 0;
        HistoryAdapter ha=(HistoryAdapter)this.getAdapter();
//        if(ha.getItemCount()==1){
//            return 0;//only head,dont't show history
//        }
/* MODIFIED-BEGIN by kaifeng.lu, 2016-06-15,BUG-2340496*/
//        ha.getItemCount();
        if(ha!=null) {
            for (int i = 0; i < ha.getItemCount(); i++) {
                ViewHolder vh = ha.onCreateViewHolder(this, ha.getItemViewType(i));
                ha.onBindViewHolder(vh, i);
                View view = vh.itemView;
                //TS:kaifeng.lu 2016-02-17 Calculator BUGFIX_ 1594843  MOD_S
                view.measure(MeasureSpec.makeMeasureSpec(mWindowWidth, MeasureSpec.EXACTLY), 0);
                //TS:kaifeng.lu 2016-02-17 Calculator BUGFIX_ 1594843  MOD_E
                sum = sum + view.getMeasuredHeight();
            }
            /* MODIFIED-END by kaifeng.lu,BUG-2340496*/
        }
        return sum;
    }

    private Adapter mAdapter ;



}
