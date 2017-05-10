/*
* create by xlli PR 996836 	[Calculator](new) calculation history
* it is history's adapter
 */

package com.tct.calculator.view.history;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log; // MODIFIED by qiong.liu1, 2017-04-21,BUG-4452809
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tct.calculator.Calculator;
import com.tct.calculator.R;

import java.util.Vector;

public class CommonHistoryAdapter extends RecyclerView.Adapter {
    private final Context mContext;
    private final Vector<ConvertHistoryItem> mEntries;
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

    public interface CommonHistoryItemLongClickCallback {
        public void onCommonHistoryItemLongClickCallback(ConvertHistoryItem entry);
    }

    public interface HistoryHeadCallBack {
        public void onHistoryHeadSelected();
    }

    //PR 540691 don't collapse the history panel when long click history item to copy the result.->DELETE codes about longclick history item to collopse the history panel  28/8/2015 update by xiaolu.li
    public CommonHistoryAdapter(Context context, ConvertHistory history, HistoryHeadCallBack callbackbyHead) {
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
        public TextView historyItemTop;
        public TextView historyItemBottom;
        public ImageView historyImg;

        public NormalViewHolder(View v) {
            super(v);
            historyExpr = (TextView) v.findViewById(R.id.historyExpr);
            historyResult = (TextView) v.findViewById(R.id.historyResult);
            historyItemTop = (TextView) v.findViewById(R.id.history_item_top);
            historyItemBottom = (TextView) v.findViewById(R.id.history_item_bottom);
            historyImg = (ImageView) v.findViewById(R.id.common_history_img);
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
            CommonHistoryLine view = null;
            if(Calculator.mScreenState == Calculator.SCREENPORTFULL || Calculator.mScreenState == Calculator.SCREENLANDFULL) {
                view  =  (CommonHistoryLine) LayoutInflater.from(mContext)
                        .inflate(R.layout.common_history_entry, viewGroup, false);
            }else{
                view  =  (CommonHistoryLine) LayoutInflater.from(mContext)
                        .inflate(R.layout.common_history_entry_split, viewGroup, false);
            }

            return new NormalViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
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
            final CommonHistoryLine view = (CommonHistoryLine) normalViewHolder.itemView;
            final ConvertHistoryItem entry = mEntries.elementAt(position - 1);
            view.setAdapter(CommonHistoryAdapter.this);
            view.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    if (mCommonDisplayOverlay != null) {
                        mCommonDisplayOverlay.collapseHistory();
                    }
                    if (mHistoryItemSelectedInterface != null) {
                        mHistoryItemSelectedInterface.onHistorySelected(true, entry);
                    }
                }
            });


//            normalViewHolder.historyItemTop.setText(entry.fromName.substring(0, 1).toUpperCase() + entry.fromName.substring(1));
//            normalViewHolder.historyItemBottom.setText(entry.toName.substring(0, 1).toUpperCase() + entry.toName.substring(1));

            normalViewHolder.historyItemTop.setText(entry.fromName);
            normalViewHolder.historyItemBottom.setText(entry.toName);
            normalViewHolder.historyImg.setImageResource(entry.imgId);
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
    private CommonDisplayOverlay mCommonDisplayOverlay;

    public void setCommonDisplayOverlay(CommonDisplayOverlay commonDisplayOverlay) {
        this.mCommonDisplayOverlay = commonDisplayOverlay;
    }

    public interface HistoryItemSelectedInterface {
        void onHistorySelected(boolean isCollapse, ConvertHistoryItem item);
    }

    private HistoryItemSelectedInterface mHistoryItemSelectedInterface;

    public void setHistoryItemSelected(HistoryItemSelectedInterface itemSelectedInterface) {
        this.mHistoryItemSelectedInterface = itemSelectedInterface;

    }
}
