/*
 * create by xlli PR 996836 	[Calculator](new) calculation history
 *
 * it is calculation history cache class.
 * * ==========================================================================
 *HISTORY
 *
 *Tag            Date         Author        Description
 *============== ============ =============== ==============================
 *BUGFIX_1269320   2015/12/28   kaifeng.lu      [Android 6.0][Calculator]It does not history when calculation repeated.
 ===========================================================================
 */

package com.tct.calculator.view;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask; // MODIFIED by kaifeng.lu, 2016-07-04,BUG-2340412
/* MODIFIED-BEGIN by qiong.liu1, 2017-03-20,BUG-3621966*/
import android.os.Handler;
import android.os.Message;
/* MODIFIED-END by qiong.liu1,BUG-3621966*/
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils; // MODIFIED by kaifeng.lu, 2016-07-14,BUG-2520616
import android.util.Log;

import com.tct.calculator.data.HistoryContentUri;

import java.util.Vector;

public class History {
    private static final int MAX_ENTRIES = 10;
    private Vector<HistoryItem> mEntries = new Vector<HistoryItem>();
    private int mPos;
    private RecyclerView.Adapter mObserver;
    private ContentResolver cr;
    public static final String TAG =  History.class.getName();
    private int mDBDeleteCount = -1; // MODIFIED by kaifeng.lu, 2016-07-04,BUG-2340412


    public History(ContentResolver cr) {
        this.cr = cr;
        clear();
    }

    public void clear() {
        mEntries.clear();
        //mEntries.add(new HistoryItem("","",System.currentTimeMillis()));
        mPos = 0;
        notifyChanged();
    }

    public void setData(Vector<HistoryItem> mEntries){
        clear();
        this.mEntries.addAll(mEntries);
        notifyChanged();

    }

    private void notifyChanged() {
        if(mObserver != null) {
            mObserver.notifyDataSetChanged();
        }
    }

    public void setObserver(RecyclerView.Adapter observer) {
        mObserver = observer;
    }


    void update(String text) {
        current().result = text;
    }

    public HistoryItem current() {
        if(mEntries!=null) {
            return mEntries.elementAt(mPos);
        }else{
            return new HistoryItem("","",System.currentTimeMillis());
        }
    }

    boolean moveToPrevious() {
        if(mPos > 0) {
            --mPos;
            return true;
        }
        return false;
    }

    boolean moveToNext() {
        if(mPos < mEntries.size() - 1) {
            ++mPos;
            return true;
        }
        return false;
    }

    public void enter(String formula, String result) {
//        current().result = "";
        if(mEntries.size() >= MAX_ENTRIES) {

            HistoryItem first = mEntries.get(0);
            /* MODIFIED-BEGIN by kaifeng.lu, 2016-07-04,BUG-2340412*/
            final StringBuilder selection = new StringBuilder(HistoryContentUri.HISTORY_TIMESTAP);
            selection.append(" = ");
            selection.append(first.timeStap);
            mEntries.remove(0);
//            int count = cr.delete(HistoryContentUri.CONTENT_URI,selection.toString(),null);
            new AsyncTask() {
                @Override
                protected Object doInBackground(Object[] params) {
                    mDBDeleteCount = cr.delete(HistoryContentUri.CONTENT_URI,selection.toString(),null);
                    Log.i(TAG, "deleted count " +  mDBDeleteCount );
                    return null;
                }
            }.execute();

        }
            //TS:kaifeng.lu 2015-12-23 Calculator BUGFIX_1269320 MOD_S
        if(!TextUtils.isEmpty(formula) && (mEntries.size() < 1 || !formula.equals(mEntries.elementAt(mEntries.size() - 1).formula))) {
            //TS:kaifeng.lu 2015-12-23 Calculator BUGFIX_1269320 MOD_S
            HistoryItem item = new HistoryItem(formula, result,System.currentTimeMillis());
            mEntries.insertElementAt(item, mEntries.size());
            final ContentValues values = new ContentValues();
            values.put(HistoryContentUri.HISTORY_FORMULA,item.formula);
            values.put(HistoryContentUri.HISTORY_RESULT,item.result);
            values.put(HistoryContentUri.HISTORY_TIMESTAP, item.timeStap);
            new AsyncTask(){
                @Override
                protected Object doInBackground(Object[] params) {
                    Uri uri =  cr.insert(HistoryContentUri.CONTENT_URI,values);
                    Log.i(TAG, "insert URI" + uri.toString());
                    return null;
                }

                @Override
                protected void onPostExecute(Object o) {
                    mPos = mEntries.size() - 1;
                    notifyChanged();
                }
            }.execute();
            /* MODIFIED-END by kaifeng.lu,BUG-2340412*/
        }

    }

    public void CurrencyEnter(String from, String to) {
        /* MODIFIED-BEGIN by qiong.liu1, 2017-03-20,BUG-3621966*/
        HistoryItem hasHistory = checkSameHistoryRecord(from, to);
        if (hasHistory != null) {
            if (!isHistoryRecoredOnTop(from, to)) {
                Log.d(TAG, "===XXX===DB=ConvertHistory=ConvertHistoryHandler=handleMessage=1");
                removeOldRecord(hasHistory);
                insertNewRecord(from, to);
            }
        } else {
            int historySize = mEntries.size();
            if (historySize < MAX_ENTRIES) {
                Log.d(TAG, "===XXX===DB=ConvertHistory=ConvertHistoryHandler=handleMessage=2");
                insertNewRecord(from, to);
            } else {
                Log.d(TAG, "===XXX===DB=ConvertHistory=ConvertHistoryHandler=handleMessage=3");
                HistoryItem first = mEntries.get(0);
                removeOldRecord(first);
                insertNewRecord(from, to);
            }
        }


//        if (mEntries.size() >= MAX_ENTRIES) {
//            HistoryItem first = mEntries.get(0);
//            final StringBuilder selection = new StringBuilder(HistoryContentUri.CURRENCY_HISTORY_TIME_STAP);
//            selection.append(" = ");
//            selection.append(first.timeStap);
//            mEntries.remove(0);
//            new AsyncTask() {
//                @Override
//                protected Object doInBackground(Object[] params) {
//                    mDBDeleteCount = cr.delete(HistoryContentUri.CONTENT_CURRENCY_HISTORY_URI, selection.toString(), null);
//                    Log.i(TAG, "deleted count " + mDBDeleteCount);
//                    return null;
//                }
//            }.execute();
//
//        }
//        if (!TextUtils.isEmpty(from) && (mEntries.size() < 1 || !from.equals(mEntries.elementAt(mEntries.size() - 1).formula))) {
//            HistoryItem item = new HistoryItem(from, to, System.currentTimeMillis());
//            mEntries.insertElementAt(item, mEntries.size());
//            final ContentValues values = new ContentValues();
//            values.put(HistoryContentUri.CURRENCY_HISTORY_FROM, item.formula);
//            values.put(HistoryContentUri.CURRENCY_HISTORY_TO, item.result);
//            values.put(HistoryContentUri.CURRENCY_HISTORY_TIME_STAP, item.timeStap);
//            new AsyncTask() {
//                @Override
//                protected Object doInBackground(Object[] params) {
//                    Uri uri = cr.insert(HistoryContentUri.CONTENT_CURRENCY_HISTORY_URI, values);
//                    Log.i(TAG, "insert URI" + uri.toString());
//                    return null;
//                }
//
//                @Override
//                protected void onPostExecute(Object o) {
//                    mPos = mEntries.size() - 1;
//                    notifyChanged();
//                }
//            }.execute();
//        }

    }

    private HistoryItem checkSameHistoryRecord(String from, String to) {
        try {
            if (from != null && to != null) {
                String fromName = from;
                String toName = to;
                String newRecord = fromName + "_" + toName;
                for (HistoryItem item : mEntries) {
                    String oldRecord = item.formula + "_" + item.result;
                    Log.d(TAG, "===XXX===DB=ConvertHistory=checkSameHistoryRecord=oldRecord=" + oldRecord + ",newRecord=" + newRecord);
                    if (!TextUtils.isEmpty(oldRecord) && oldRecord.equalsIgnoreCase(newRecord)) {
                        Log.d(TAG, "===XXX===DB=ConvertHistory=checkSameHistoryRecord=true");
                        return item;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean isHistoryRecoredOnTop(String from, String to) {
        try {
            if (from != null && to != null) {
                String fromName = from;
                String toName = to;
                String newRecord = fromName + "_" + toName;
                HistoryItem item = mEntries.get(mEntries.size() - 1);
                String oldRecord = item.formula + "_" + item.result;
                if (!TextUtils.isEmpty(oldRecord) && oldRecord.equalsIgnoreCase(newRecord)) {
                    Log.d(TAG, "===XXX===DB=ConvertHistory=isHistoryRecoredOnTop=true");
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void removeOldRecord(HistoryItem historyItem) {
        //====remove old
        final StringBuilder selection = new StringBuilder(HistoryContentUri.CURRENCY_HISTORY_TIME_STAP); // MODIFIED by qiong.liu1, 2017-03-27,BUG-3621966
        selection.append(" = ");
        selection.append(historyItem.timeStap);
        int index = 0;
        String newRecord = historyItem.formula + "_" + historyItem.result;
        for (int i = 0; i < mEntries.size(); i++) {
            HistoryItem item = mEntries.get(i);
            String oldRecord = item.formula + "_" + item.result;
//            Log.d(TAG, "===XXX===DB=ConvertHistory=removeOldRecord=mCurrencyEntries.size()=" + mCurrencyEntries.size() + ",oldRecord=" + oldRecord + ",newRecord=" + newRecord);
            if (!TextUtils.isEmpty(oldRecord) && oldRecord.equalsIgnoreCase(newRecord)) {
                index = i;
                Log.d(TAG, "===XXX===DB=ConvertHistory=removeOldRecord=index=" + index);
                break;
            }
        }
        mEntries.remove(index);
        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                mDBDeleteCount = cr.delete(HistoryContentUri.CONTENT_CURRENCY_HISTORY_URI, selection.toString(), null); // MODIFIED by qiong.liu1, 2017-03-27,BUG-3621966
                Log.i(TAG, "deleted count " + mDBDeleteCount);
                return null;
            }
        }.execute();
        Log.d(TAG, "deleted count " + mDBDeleteCount);
    }

    private void insertNewRecord(String from, String to) {
        try {
            String fromName = from;
            String toName = to;
            HistoryItem item = new HistoryItem(fromName, toName, System.currentTimeMillis());
            mEntries.insertElementAt(item, mEntries.size());
            final ContentValues values = new ContentValues();
            /* MODIFIED-BEGIN by qiong.liu1, 2017-03-27,BUG-3621966*/
            values.put(HistoryContentUri.CURRENCY_HISTORY_FROM, item.formula);
            values.put(HistoryContentUri.CURRENCY_HISTORY_TO, item.result);
            values.put(HistoryContentUri.CURRENCY_HISTORY_TIME_STAP, item.timeStap);
            new AsyncTask() {
                @Override
                protected Object doInBackground(Object[] params) {
                    Uri uri = cr.insert(HistoryContentUri.CONTENT_CURRENCY_HISTORY_URI, values);
                    /* MODIFIED-END by qiong.liu1,BUG-3621966*/
                    Log.i(TAG, "insert URI" + uri.toString());
                    return null;
                }

                @Override
                protected void onPostExecute(Object o) {
                    mPos = mEntries.size() - 1;
                    notifyChanged();
                }
            }.execute();
        /* MODIFIED-BEGIN by qiong.liu1, 2017-03-20,BUG-3621966*/
        } catch (Exception e) {
            e.printStackTrace();
            /* MODIFIED-END by qiong.liu1,BUG-3621966*/
        }
    }

    public String getResult() {
        return current().result;
    }

    public String getFormula() {
        return current().formula;
    }

    public void remove(HistoryItem he) {
        mEntries.remove(he);
        mPos--;
    }

    public Vector<HistoryItem> getEntries() {
        return mEntries;
    }
}
