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

package com.tct.calculator.view.history;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;

import com.tct.calculator.convert.Convert;
import com.tct.calculator.data.HistoryContentUri;

import java.util.Vector;

public class ConvertHistory {
    private static final int MAX_ENTRIES = 10;
    private Vector<ConvertHistoryItem> mConvertEntries = new Vector<ConvertHistoryItem>();
    private int mPosition;
    private RecyclerView.Adapter mObserver;
    private ContentResolver mContentResolver;
    public static final String TAG = ConvertHistory.class.getName();
    private int mDBDeleteCount = -1;


    public ConvertHistory(ContentResolver contentResolver) {
        this.mContentResolver = contentResolver;
        clear();
    }

    public void clear() {
        Log.i(TAG, "===XXX===DB=ConvertHistory=clear");
        mConvertEntries.clear();
        mPosition = 0;
        notifyChanged();
    }

    public void setData(Vector<ConvertHistoryItem> mEntries) {
        Log.i(TAG, "===XXX===DB=ConvertHistory=setData");
        clear();
        this.mConvertEntries.addAll(mEntries);
        notifyChanged();

    }

    private void notifyChanged() {
        Log.i(TAG, "===XXX===DB=ConvertHistory=notifyChanged");
        if (mObserver != null) {
            mObserver.notifyDataSetChanged();
        }
    }

    public void setObserver(RecyclerView.Adapter observer) {
        mObserver = observer;
    }


    public void insertConvertHistory(Convert currentConvert) {
        try {
            if (mConvertHistoryHandler != null) {
                mConvertHistoryHandler.sendMessage(mConvertHistoryHandler.obtainMessage(ConvertHistoryHandler.CONVERT_HISTORY_INSERT, currentConvert));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ConvertHistory queryConvertHistory(boolean mIsStanderPoint) {
        Log.i(TAG, "===XXX===DB=ConvertHistory=queryConvertHistory");
        Cursor cursor = mContentResolver.query(HistoryContentUri.CONTENT_CONVERTER_HISTORY_URI, null, null, null, HistoryContentUri.CONVERTER_HISTORY_TIMESTAP);
        try {
            if (cursor != null) {
                int fromNameIndex = cursor.getColumnIndex(HistoryContentUri.CONVERTER_HISTORY_FROM);
                int toNameIndex = cursor.getColumnIndex(HistoryContentUri.CONVERTER_HISTORY_TO);
                int imgId = cursor.getColumnIndex(HistoryContentUri.CONVERTER_HISTORY_IMG_ID);
                int convertName = cursor.getColumnIndex(HistoryContentUri.CONVERTER_HISTORY_TYPE);
                int timestamp = cursor.getColumnIndex(HistoryContentUri.CONVERTER_HISTORY_TIMESTAP);
                Vector<ConvertHistoryItem> entries = new Vector<ConvertHistoryItem>();
                if (fromNameIndex != -1 && toNameIndex != -1) {
                    while (cursor.moveToNext()) {
                        ConvertHistoryItem item = new ConvertHistoryItem();
                        item.fromName = cursor.getString(fromNameIndex);
                        item.toName = cursor.getString(toNameIndex);
                        String imgIdString = cursor.getString(imgId);
                        if (!TextUtils.isEmpty(imgIdString)) {
                            item.imgId = Integer.parseInt(imgIdString);
                        }
                        item.convertName = cursor.getString(convertName);
                        item.timeStamp = cursor.getLong(timestamp);
                        Log.i(TAG, "===XXX===DB=ConvertHistory=queryConvertHistory item=" + item);
                        entries.add(item);
                    }
                    setData(entries);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return this;
    }

    public void deleteConvertHistory() {
        Log.i(TAG, "===XXX===DB=ConvertHistory=deleteConvertHistory");
        mContentResolver.delete(HistoryContentUri.CONTENT_CONVERTER_HISTORY_URI, "", null);
    }

    public void remove(ConvertHistoryItem he) {
        mConvertEntries.remove(he);
        mPosition--;
    }

    public Vector<ConvertHistoryItem> getEntries() {
        return mConvertEntries;
    }

    boolean moveToPrevious() {
        if (mPosition > 0) {
            --mPosition;
            return true;
        }
        return false;
    }

    boolean moveToNext() {
        if (mPosition < mConvertEntries.size() - 1) {
            ++mPosition;
            return true;
        }
        return false;
    }

    void update(String text) {
        current().toName = text;
    }

    private String getTo() {
        Log.i(TAG, "===XXX===DB=ConvertHistory=getTo");
        return current().toName;
    }

    private String getFrom() {
        Log.i(TAG, "===XXX===DB=ConvertHistory=getFrom");
        return current().fromName;
    }

    private ConvertHistoryItem current() {
        Log.i(TAG, "===XXX===DB=ConvertHistory=current");
        if (mConvertEntries != null) {
            return mConvertEntries.elementAt(mPosition);
        } else {
            return new ConvertHistoryItem();
        }
    }

    private ConvertHistoryHandler mConvertHistoryHandler;

    public void initConvertHistoryThread(HandlerThread convertThread) {
        mConvertHistoryHandler = new ConvertHistoryHandler(convertThread.getLooper());
    }

    private class ConvertHistoryHandler extends Handler {

        private static final int CONVERT_HISTORY_INSERT = 0;
        private static final int CONVERT_HISTORY_DELETE = 1;

        private ConvertHistoryHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case CONVERT_HISTORY_INSERT:
                    Log.i(TAG, "===XXX===DB=ConvertHistory=ConvertHistoryHandler=handleMessage=CONVERT_HISTORY_INSERT");
                    Convert currentConvert = (Convert) msg.obj;
                    ConvertHistoryItem hasHistory = checkSameHistoryRecord(currentConvert);
                    if (hasHistory != null) {//has same history record
                        if (!isHistoryRecoredOnTop(currentConvert)) {//Remove old and insert new
                            Log.i(TAG, "===XXX===DB=ConvertHistory=ConvertHistoryHandler=handleMessage=1");
                            removeOldRecord(hasHistory);
                            //====insert new
                            insertNewRecord(currentConvert);
                        }
                    } else {//no same record
                        int historySize = mConvertEntries.size();
                        if (historySize < MAX_ENTRIES) {
                            Log.i(TAG, "===XXX===DB=ConvertHistory=ConvertHistoryHandler=handleMessage=2");
                            insertNewRecord(currentConvert);
                        } else {
                            Log.i(TAG, "===XXX===DB=ConvertHistory=ConvertHistoryHandler=handleMessage=3");
                            ConvertHistoryItem first = mConvertEntries.get(0);
                            removeOldRecord(first);
                            //====insert new
                            insertNewRecord(currentConvert);
                        }
                    }
                    break;
                case CONVERT_HISTORY_DELETE:
                    break;
            }

        }

    }

    private void removeOldRecord(ConvertHistoryItem historyItem) {
        //====remove old
        final StringBuilder selection = new StringBuilder(HistoryContentUri.CONVERTER_HISTORY_TIMESTAP);
        selection.append(" = ");
        selection.append(historyItem.timeStamp);
        int index = 0;
        String newRecord = historyItem.convertName + "_" + historyItem.fromName + "_" + historyItem.toName;
        for (int i = 0; i < mConvertEntries.size(); i++) {
            ConvertHistoryItem item = mConvertEntries.get(i);
            String oldRecord = item.convertName + "_" + item.fromName + "_" + item.toName;
//            Log.i(TAG, "===XXX===DB=ConvertHistory=removeOldRecord=mConvertEntries.size()=" + mConvertEntries.size() + ",oldRecord=" + oldRecord + ",newRecord=" + newRecord);
            if (!TextUtils.isEmpty(oldRecord) && oldRecord.equalsIgnoreCase(newRecord)) {
                index = i;
                Log.i(TAG, "===XXX===DB=ConvertHistory=removeOldRecord=index=" + index);
                break;
            }
        }
        mConvertEntries.remove(index);
        mDBDeleteCount = mContentResolver.delete(HistoryContentUri.CONTENT_CONVERTER_HISTORY_URI, selection.toString(), null);
        Log.i(TAG, "deleted count " + mDBDeleteCount);
    }

    private void insertNewRecord(Convert currentConvert) {
        try {
            String fromName = currentConvert.getDefaultLeftUnit().getAbbreviation();
            String toName = currentConvert.getDefaultRightUnit().getAbbreviation();
            int imgId = currentConvert.getConvertHistoryImgResId();
            ConvertHistoryItem item = new ConvertHistoryItem(currentConvert.getConvertName(), fromName, toName, imgId, System.currentTimeMillis());
            mConvertEntries.insertElementAt(item, mConvertEntries.size());
            final ContentValues values = new ContentValues();
            values.put(HistoryContentUri.CONVERTER_HISTORY_FROM, item.fromName);
            values.put(HistoryContentUri.CONVERTER_HISTORY_TO, item.toName);
            values.put(HistoryContentUri.CONVERTER_HISTORY_IMG_ID, item.imgId);
            values.put(HistoryContentUri.CONVERTER_HISTORY_TYPE, item.convertName);
            values.put(HistoryContentUri.CONVERTER_HISTORY_TIMESTAP, item.timeStamp);
            Uri uri = mContentResolver.insert(HistoryContentUri.CONTENT_CONVERTER_HISTORY_URI, values);
            Log.i(TAG, "insert URI" + uri.toString());
            //==Update
            mPosition = mConvertEntries.size() - 1;
            mHistoryHandler.sendEmptyMessage(CONVERT_HISTORY_INSERT_UPDATE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isHistoryRecoredOnTop(Convert currentConvert) {
        try {
            if (currentConvert != null) {
                String fromName = currentConvert.getDefaultLeftUnit().getAbbreviation();
                String toName = currentConvert.getDefaultRightUnit().getAbbreviation();
                String newRecord = currentConvert.getConvertName() + "_" + fromName + "_" + toName;
                ConvertHistoryItem item = mConvertEntries.get(mConvertEntries.size() - 1);
                String oldRecord = item.convertName + "_" + item.fromName + "_" + item.toName;
                if (!TextUtils.isEmpty(oldRecord) && oldRecord.equalsIgnoreCase(newRecord)) {
                    Log.i(TAG, "===XXX===DB=ConvertHistory=isHistoryRecoredOnTop=true");
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private ConvertHistoryItem checkSameHistoryRecord(Convert currentConvert) {
        try {
            if (currentConvert != null) {
                String fromName = currentConvert.getDefaultLeftUnit().getAbbreviation();
                String toName = currentConvert.getDefaultRightUnit().getAbbreviation();
                String newRecord = currentConvert.getConvertName() + "_" + fromName + "_" + toName;
                for (ConvertHistoryItem item : mConvertEntries) {
                    String oldRecord = item.convertName + "_" + item.fromName + "_" + item.toName;
//                    Log.i(TAG, "===XXX===DB=ConvertHistory=checkSameHistoryRecord=oldRecord=" + oldRecord + ",newRecord=" + newRecord);
                    if (!TextUtils.isEmpty(oldRecord) && oldRecord.equalsIgnoreCase(newRecord)) {
                        Log.i(TAG, "===XXX===DB=ConvertHistory=checkSameHistoryRecord=true");
                        return item;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static final int CONVERT_HISTORY_INSERT_UPDATE = 0;
    private static final int CONVERT_HISTORY_DELETE_UPDATE = 1;
    private Handler mHistoryHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case CONVERT_HISTORY_INSERT_UPDATE:
                    notifyChanged();
                    break;
            }
        }
    };
}
