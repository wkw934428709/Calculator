/*
* create by xlli PR 996836 	[Calculator](new) calculation history
* it is history's adapter
 */

package com.tct.calculator.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tct.calculator.Calculator;
import com.tct.calculator.R;
import com.tct.calculator.view.History;
import com.tct.calculator.view.HistoryItem;
import com.tct.calculator.view.HistoryLine;

import java.util.Vector;

public class CurrencyHistoryAdapter extends RecyclerView.Adapter implements View.OnClickListener {
    private final Context mContext;
    private Vector<HistoryItem> mEntries;
    protected HistoryHeadCallBack mCallbackbyHead;
    private DataFromHistoryCallback mHistoruCallback;

    private class VIEW_TYPES {
        public static final int Header = 1;
        public static final int Normal = 2;
    }

    public void setmHistoruCallback(DataFromHistoryCallback callback) {
        mHistoruCallback = callback;
    }

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

    public CurrencyHistoryAdapter(Context context, History history, HistoryHeadCallBack callbackbyHead) {
        mContext = context;
        mEntries = history.getEntries();
        mCallbackbyHead = callbackbyHead;
    }

    public void setHistoryData(History history) {
        mEntries = history.getEntries();
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
            historyExpr = (TextView) v.findViewById(R.id.currency_history_org);
            historyResult = (TextView) v.findViewById(R.id.currency_history_target);
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
                    .inflate(R.layout.currency_history_entry, viewGroup, false);
            view.setOnClickListener(this);
            return new NormalViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position == 0) {
            HeaderViewHolder headViewHolder = (HeaderViewHolder) holder;
            View view = headViewHolder.itemView;
            LinearLayout historyHead =(LinearLayout) view.findViewById(R.id.history_head);;

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
            view.setAdapter(CurrencyHistoryAdapter.this);


        /**
         *   cancel the function in Currency scream's history list for onLongClick action
         */

//            view.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View v) {
//                    view.copyContent(entry.result);
//                    if (mHistoruCallback != null) {
//                        mHistoruCallback.setStatus(true);
//                    }
//                    return false;
//                }
//            });

            normalViewHolder.historyExpr.setText(entry.formula);
            normalViewHolder.historyResult.setText(entry.result);
            // Save data to default tag.
            holder.itemView.setTag(entry.formula + "-" + entry.result);
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

    private OnCurrencyHistoryItemClickListener mOnItemClickListener;

    public interface OnCurrencyHistoryItemClickListener {
        void onItemClick(View view, String data);
    }

    public void setOnItemClickListener(OnCurrencyHistoryItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(v, (String) v.getTag());
        }
    }
}
