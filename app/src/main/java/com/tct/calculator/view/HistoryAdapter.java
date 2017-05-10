/*
* create by xlli PR 996836 	[Calculator](new) calculation history
* it is history's adapter
 */

package com.tct.calculator.view;

import android.content.Context;
import android.graphics.Color;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tct.calculator.Calculator;
import com.tct.calculator.R;

import java.util.Vector;

public class HistoryAdapter extends RecyclerView.Adapter {
    private final Context mContext;
    private final Vector<HistoryItem> mEntries;
    protected HistoryHeadCallBack mCallbackbyHead;
    /* MODIFIED-BEGIN by kaifeng.lu, 2016-08-16,BUG-2712192*/
    private DataFromHistoryCallback mHistoruCallback;

    private class VIEW_TYPES {
        public static final int Header = 1;
        public static final int Normal = 2;
    }

    public void setmHistoruCallback(DataFromHistoryCallback callback) {
        mHistoruCallback = callback;
    }
    /* MODIFIED-END by kaifeng.lu,BUG-2712192*/

    @Override
    public int getItemViewType(int position) {

        if (position == 0)
            return VIEW_TYPES.Header;
        else
            return VIEW_TYPES.Normal;

    }

    public interface HistoryItemLongClickCallback {
        public void onHistoryItemLongClickCallback(HistoryItem entry);
    }

    public interface HistoryHeadCallBack {
        public void onHistoryHeadSelected();
    }

    //PR 540691 don't collapse the history panel when long click history item to copy the result.->DELETE codes about longclick history item to collopse the history panel  28/8/2015 update by xiaolu.li
    public HistoryAdapter(Context context, History history, HistoryHeadCallBack callbackbyHead) {
        mContext = context;
        mEntries = history.getEntries();
        mCallbackbyHead = callbackbyHead;
    }

    private static class HeaderViewHolder extends RecyclerView.ViewHolder {

        public HeaderViewHolder(View v) {
            super(v);
        }
    }

    public static class NormalViewHolder extends RecyclerView.ViewHolder {
        public TextView historyExpr;
        public TextView historyResult;

        public NormalViewHolder(View v) {
            super(v);
            historyExpr = (TextView) v.findViewById(R.id.historyExpr);
            historyResult = (TextView) v.findViewById(R.id.historyResult);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int intType) {

        if (intType == VIEW_TYPES.Header) {
            View rowView = null;
            if(Calculator.mScreenState == Calculator.SCREENPORTFULL || Calculator.mScreenState == Calculator.SCREENLANDFULL) {
                rowView  = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.history_head, viewGroup, false);
            }else{
                rowView  = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.history_head_split, viewGroup, false);
            }
            return new HeaderViewHolder(rowView);
        } else {
            HistoryLine view =
                    (HistoryLine) LayoutInflater.from(mContext)
                            .inflate(R.layout.history_entry, viewGroup, false);
            return new NormalViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position == 0) {
            HeaderViewHolder headViewHolder = (HeaderViewHolder) holder;
            View view = headViewHolder.itemView;
            LinearLayout historyHead = (LinearLayout) view.findViewById(R.id.history_head);
            ImageView imageView = (ImageView) view.findViewById(R.id.delelte_iv);
            LinearLayout noHistory = (LinearLayout) view.findViewById(R.id.no_history);
            if (mEntries.size() == 0) {
                historyHead.setBackgroundColor(mContext.getResources().getColor(R.color.history_background_color));
                imageView.setVisibility(View.GONE);
                noHistory.setVisibility(View.VISIBLE);
            } else {
                historyHead.setBackgroundColor(mContext.getResources().getColor(R.color.historyhead_background));
                imageView.setVisibility(View.VISIBLE);
                noHistory.setVisibility(View.GONE);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mCallbackbyHead.onHistoryHeadSelected();
                    }
                });
            }

        } else {
            NormalViewHolder normalViewHolder = (NormalViewHolder) holder;
            final HistoryLine view = (HistoryLine) normalViewHolder.itemView;
            final HistoryItem entry = mEntries.elementAt(position - 1);
            view.setAdapter(HistoryAdapter.this);
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    //PR 540691 don't collapse the history panel when long click history item to copy the result.->DELETE codes about longclick history item to collopse the history panel  28/8/2015 update by xiaolu.li
                    view.copyContent(entry.result);
                    mHistoruCallback.setStatus(true); // MODIFIED by kaifeng.lu, 2016-08-16,BUG-2712192
                    return false;
                }
            });
            normalViewHolder.historyExpr.setText(entry.formula);
            normalViewHolder.historyResult.setText(entry.result);
        }
    }

    @Override
    public int getItemCount() {
        return mEntries.size() + 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    public Context getContext() {
        return mContext;
    }

    public void clear() {
        mEntries.clear();
    }


    /* MODIFIED-BEGIN by kaifeng.lu, 2016-08-16,BUG-2712192*/

    /**
     * to set the flag of  ClipboardManager'string is come from histroy_data
     */
    public interface DataFromHistoryCallback {

        void setStatus(boolean mHistroyStatus);
    }
    /* MODIFIED-END by kaifeng.lu,BUG-2712192*/
}
