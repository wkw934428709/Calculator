package com.tct.calculator.view;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.tct.calculator.R;

/**
 * it is history item layout
 * * create by xlli PR 996836 	[Calculator](new) calculation history
 */
public class HistoryLine extends LinearLayout {
    private HistoryItem mHistoryEntry;
    private History mHistory;
    private RecyclerView.Adapter mAdapter;

    public HistoryLine(Context context, AttributeSet attrs) {
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

    public HistoryItem getHistoryEntry() {
        return mHistoryEntry;
    }

    public void setHistoryEntry(HistoryItem historyEntry) {
        this.mHistoryEntry = historyEntry;
    }

    public History getHistory() {
        return mHistory;
    }

    public void setHistory(History history) {
        this.mHistory = history;
    }

    public RecyclerView.Adapter getAdapter() {
        return mAdapter;
    }

    public void setAdapter(RecyclerView.Adapter adapter) {
        this.mAdapter = adapter;
    }

}
