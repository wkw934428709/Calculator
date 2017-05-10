/* Copyright (C) 2016 Tcl Corporation Limited */
package com.tct.calculator.adapter;

import com.tct.calculator.R;
import com.tct.calculator.data.CurrencyListBean;
import com.tct.calculator.fragment.CurrencyList_Fragment;
import android.text.TextUtils;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log; // MODIFIED by qiong.liu1, 2017-04-21,BUG-4452809
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ca.barrenechea.widget.recyclerview.decoration.StickyHeaderAdapter;

public class CurrencyListAdapter extends RecyclerView.Adapter<CurrencyListAdapter.ViewHolder> implements
    StickyHeaderAdapter<CurrencyListAdapter.HeaderHolder>, View.OnClickListener {

    public static final String HISTORY_TAG = "History";
    private LayoutInflater mInflater;
    private List<CurrencyListBean> mNameList;
    private char lastChar = '\u0000';
    private int DisplayIndex = 0;
    private OnRecyclerViewItemClickListener mOnItemClickListener = null;
    private CurrencyList_Fragment mCurrencyListFragment;

    public CurrencyListAdapter(Context context, List<CurrencyListBean> nameList, CurrencyList_Fragment currencyList_fragment) {
        mInflater = LayoutInflater.from(context);
        this.mNameList = nameList;
        this.mCurrencyListFragment = currencyList_fragment;
    }

    public void setNameList(List<CurrencyListBean> queryList) {
        this.mNameList = queryList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        final View view = mInflater.inflate(R.layout.currency_list_item, viewGroup, false);
        view.setOnClickListener(this);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        if (mNameList != null && mNameList.size() > 0) {
            viewHolder.item.setText(mNameList.get(position).mTitle);
            viewHolder.itemView.setTag(mNameList.get(position).mTitle);
            setFlag(viewHolder, position, mCurrencyListFragment.getCurrencyFlag());
        }
    }

    // Add flag.
    public void setFlag(ViewHolder viewHolder, int position, String currencySelectData) {
        if (mNameList != null && mNameList.size() > 0) {
            String[] countryUnit = mNameList.get(position).mTitle.split("-");
            if (countryUnit != null && countryUnit.length >= 2 && !TextUtils.isEmpty(currencySelectData)) {
                if (currencySelectData.equals(countryUnit[1].trim()) && !mNameList.get(position).mHeader.equals(HISTORY_TAG)) {
                    viewHolder.flag.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.flag.setVisibility(View.GONE);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return (mNameList != null) ? mNameList.size() : 0;
    }

    public long getHeaderId(int position) {
        if (mNameList == null ) {
            return -1;
        }
        char ch = mNameList.get(position).mHeader.charAt(0);
        if (lastChar == '\u0000') {
            lastChar = ch;
            return DisplayIndex;
        } else {
            if (lastChar == ch) {
                return DisplayIndex;
            } else {
                lastChar = ch;
                DisplayIndex++;
                return DisplayIndex;
            }
        }
    }

    public HeaderHolder onCreateHeaderViewHolder(ViewGroup parent) {
        final View view = mInflater.inflate(R.layout.currency_list_head, parent, false);
        return new HeaderHolder(view);
    }

    public void onBindHeaderViewHolder(HeaderHolder viewHolder, int position) {
        if (mNameList != null && mNameList.size() > 0) {
            if (mNameList.get(position).mHeader.equals(HISTORY_TAG)) {
                viewHolder.header.setText(mNameList.get(position).mHeader);
            } else {
                viewHolder.header.setText(mNameList.get(position).mHeader.charAt(0) + "");
            }
        }
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView item;
        public ImageView flag;

        public ViewHolder(View itemView) {
            super(itemView);
            item = (TextView) itemView.findViewById(R.id.currency_list_item_title);
            flag = (ImageView) itemView.findViewById(R.id.currency_list_item_flag);
        }
    }

    static class HeaderHolder extends RecyclerView.ViewHolder {
        public TextView header;

        public HeaderHolder(View itemView) {
            super(itemView);

            header = (TextView) itemView.findViewById(R.id.currency_list_item_head);
        }
    }

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, String data);
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(v, (String) v.getTag());
        }
    }
}
