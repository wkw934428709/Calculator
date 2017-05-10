package com.tct.calculator.view.history;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.tct.calculator.R;

/**
 * it is history item layout
 * * create by xlli PR 996836 	[Calculator](new) calculation history
 */
public class CommonHistoryLine extends RelativeLayout {
    private ConvertHistoryItem mHistoryEntry;
    private ConvertHistory mHistory;
    private RecyclerView.Adapter mAdapter;

    public CommonHistoryLine(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public void copyContent(String content) {
        ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        clipboard.setPrimaryClip(ClipData.newPlainText(null, content));
        String toastText = String.format(getResources().getString(R.string.copy_result_toast), content);
        Toast.makeText(getContext(), toastText, Toast.LENGTH_SHORT).show();
    }

    private void removeContent() {
        mHistory.remove(mHistoryEntry);
        mAdapter.notifyDataSetChanged();
    }

    public ConvertHistoryItem getHistoryEntry() {
        return mHistoryEntry;
    }

    public void setHistoryEntry(ConvertHistoryItem historyEntry) {
        this.mHistoryEntry = historyEntry;
    }

    public ConvertHistory getHistory() {
        return mHistory;
    }

    public void setHistory(ConvertHistory history) {
        this.mHistory = history;
    }

    public RecyclerView.Adapter getAdapter() {
        return mAdapter;
    }

    public void setAdapter(RecyclerView.Adapter adapter) {
        this.mAdapter = adapter;
    }

}
