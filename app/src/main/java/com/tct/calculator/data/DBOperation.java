package com.tct.calculator.data;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log; // MODIFIED by qiong.liu1, 2017-03-20,BUG-3621966

import com.tct.calculator.adapter.CurrencyListAdapter;
import com.tct.calculator.utils.Constant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DBOperation {

    private static final String TAG = "DBOperation"; // MODIFIED by qiong.liu1, 2017-04-07,BUG-4452809

    private ContentResolver mContentResolver;

    private static DBOperation mDBOperation;

    private Context mContext;

    public String updateTime; // MODIFIED by qiong.liu1, 2017-03-20,BUG-3621966


    private DBOperation(Context context) {
        this.mContext = context;
        mContentResolver = context.getContentResolver();
    }

    public static DBOperation getInstance(Context context) {
        if (null == mDBOperation) {
            mDBOperation = new DBOperation(context);
        }
        return mDBOperation;
    }


    /**
     * '
     * Query all currency list data.
     *
     * @return currency list screen data
     */
    public synchronized List<CurrencyListBean> queryAllCurrencyList() {
        Cursor cursor;
        /* MODIFIED-BEGIN by qiong.liu1, 2017-05-04,BUG-4656997*/
        String[] selectConditions = new String[]{HistoryContentUri.CURRENCY_NAME,
                HistoryContentUri.CURRENCY_SHORTER_FORM,
                HistoryContentUri.CURRENCY_TO_USD};
        cursor = mContentResolver.query(HistoryContentUri.CONTENT_RECORD_URI,
                selectConditions, null, null, null);
        if (cursor != null) {
            try {
                CurrencyListBean currencyListBean;
                List<CurrencyListBean> currencyListData = new ArrayList<>();
                cursor.moveToFirst();
                do {
                    String currency_name = cursor.getString(cursor.getColumnIndex(
                            HistoryContentUri.CURRENCY_NAME));
                    String currencyUnit = cursor.getString(cursor.getColumnIndex(
                            HistoryContentUri.CURRENCY_SHORTER_FORM));
                    /* MODIFIED-BEGIN by qiong.liu1, 2017-03-20,BUG-3621966*/
                    String currencyValue = cursor.getString(cursor.getColumnIndex(
                            HistoryContentUri.CURRENCY_TO_USD));
                            /* MODIFIED-END by qiong.liu1,BUG-4656997*/
                    String currencyCode = mContext.getResources().getString(mContext.getResources().getIdentifier(currency_name, "string", mContext.getPackageName()));

                    currencyListBean = new CurrencyListBean(currencyCode + " - " + currencyUnit + " - " + currencyValue, currencyUnit);
                    /* MODIFIED-END by qiong.liu1,BUG-3621966*/
                    currencyListData.add(currencyListBean);
                } while (cursor.moveToNext());
                Collections.sort(currencyListData);
                return currencyListData;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                    cursor = null;
                }
            }
        }
        return null;
    }

    /**
     * Query currency wheel data.
     *
     * @return currency wheel data
     */
    public synchronized ArrayList<Currency> queryCurrencyWheelData() {
        Cursor cursor;
        /* MODIFIED-BEGIN by qiong.liu1, 2017-03-20,BUG-3621966*/
        String[] selectConditions = new String[]{
                HistoryContentUri.CURRENCY_NAME,
                HistoryContentUri.CURRENCY_SHORTER_FORM,
                HistoryContentUri.CURRENCY_AREA,
                HistoryContentUri.CURRENCY_TO_USD,
                HistoryContentUri.CURRENCY_TIME};
                /* MODIFIED-END by qiong.liu1,BUG-3621966*/
        String args = HistoryContentUri.CURRENCY_AREA + "=?";
        String[] selectArgs = new String[]{"common"};
        cursor = mContentResolver.query(HistoryContentUri.CONTENT_RECORD_URI, selectConditions, args, selectArgs, null);
        if (cursor != null) {
            try {
                Currency currency;
                ArrayList<Currency> currencyWheelData = new ArrayList<>();
                cursor.moveToFirst();
                /* MODIFIED-BEGIN by qiong.liu1, 2017-03-20,BUG-3621966*/
                updateTime = cursor.getString(cursor.getColumnIndex(HistoryContentUri.CURRENCY_TIME));
                do {
                    String currency_name = cursor.getString(
                            cursor.getColumnIndex(HistoryContentUri.CURRENCY_NAME));
                    String currencyUnit = cursor.getString(
                            cursor.getColumnIndex(HistoryContentUri.CURRENCY_SHORTER_FORM));
                    String currencyCode = mContext.getResources().getString(mContext.getResources()
                            .getIdentifier(currency_name, "string", mContext.getPackageName()));
                    String currencyStr = cursor.getString(
                            cursor.getColumnIndex(HistoryContentUri.CURRENCY_TO_USD));
                    currency = new Currency(currencyCode, currencyUnit, currencyStr);
                    /* MODIFIED-END by qiong.liu1,BUG-3621966*/
                    currencyWheelData.add(currency);
                } while (cursor.moveToNext());
                return currencyWheelData;
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG,"queryCurrencyWheelData exception : " + e); // MODIFIED by qiong.liu1, 2017-04-21,BUG-4452809
            } finally {
                if (cursor != null) {
                    cursor.close();
                    cursor = null;
                }
            }
        }
        return null;
    }

    /**
     * Update currency wheel data when select data from currency list screen.
     */
    public synchronized void updateCurrencyWheelData(String currencyUnit) {
        try {
            String updateConditions = HistoryContentUri.CURRENCY_SHORTER_FORM + " = ?";
            ContentValues values = new ContentValues();
            values.put(HistoryContentUri.CURRENCY_AREA, "common");
            String[] updateData = null;
            if (!TextUtils.isEmpty(currencyUnit)) {
                updateData = currencyUnit.split("-");
            }
            if (updateData != null && updateData.length >= 2) {
                currencyUnit = updateData[1].trim();
                /* MODIFIED-BEGIN by qiong.liu1, 2017-03-20,BUG-3621966*/
                mContentResolver.update(HistoryContentUri.CONTENT_RECORD_URI,
                        values, updateConditions, new String[]{currencyUnit});
                        /* MODIFIED-END by qiong.liu1,BUG-3621966*/
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Query currency picture.
     *
     * @return picture url
     */
    public synchronized boolean queryCurrencyPicture(String shorterform, Currency currency) {
        Cursor cursor;
        /* MODIFIED-BEGIN by qiong.liu1, 2017-03-20,BUG-3621966*/
        String[] selectCloums = new String[]{HistoryContentUri.CURRENCY_NAME,
                HistoryContentUri.CURRENCY_SHORTER_FORM,
                HistoryContentUri.CURRENCY_FLAG_URI,
                HistoryContentUri.CURRENCY_SIGNAL,
                HistoryContentUri.CURRENCY_TO_USD};
        String args = HistoryContentUri.CURRENCY_SHORTER_FORM + " = ?";
        String[] selectArgs = new String[]{shorterform};
        cursor = mContentResolver.query(HistoryContentUri.CONTENT_RECORD_URI,
                selectCloums, args, selectArgs, null);
        if (cursor != null) {
            try {
                cursor.moveToFirst();
                do {
                    String pictureURL = cursor.getString(
                            cursor.getColumnIndex(HistoryContentUri.CURRENCY_FLAG_URI));
                    if (pictureURL == null){
                        pictureURL = "china";
                    }
                    String currencyVal = cursor.getString(
                            cursor.getColumnIndex(HistoryContentUri.CURRENCY_TO_USD));
                    if (currencyVal == null){
                        currencyVal = "1.0";
                    }
                    String signal = cursor.getString(
                            cursor.getColumnIndex(HistoryContentUri.CURRENCY_SIGNAL));
                    if (currency != null) {
                    	 currency.setCountryPicture(pictureURL);
                     	 currency.setCountrySignal(signal);
                        currency.setCountryCurrency(currencyVal);
                        return true;
                        /* MODIFIED-END by qiong.liu1,BUG-3621966*/
                    }
                } while (cursor.moveToNext());
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            } finally {
                if (cursor != null) {
                    cursor.close();
                    cursor = null;
                }
            }
        }
        return false;
    }

    /**
     * Clear all currency history data.
     */
    public void clearCurrencyHistoryData() {
        try {
            mContentResolver.delete(HistoryContentUri.CONTENT_CURRENCY_HISTORY_URI, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Query default currency.
     */
    public DefaultCurrency queryDefaultCurrency(String systemLanguageCode) {
        if (TextUtils.isEmpty(systemLanguageCode)) {
            return null;
        }

        // Deal default euro dollar.
        /* MODIFIED-BEGIN by qiong.liu1, 2017-03-20,BUG-3621966*/
        if (systemLanguageCode.equals(Constant.CURRENCY_ES)
                || systemLanguageCode.equals(Constant.CURRENCY_FR_BE)
                || systemLanguageCode.equals(Constant.CURRENCY_PT_BR)) {
                /* MODIFIED-END by qiong.liu1,BUG-3621966*/
            DefaultCurrency currency = new DefaultCurrency();
            currency.setCurrencyName(Constant.CURRENCY_EURO);
            currency.setCurrencyFlag(Constant.CURRENCY_EUROPEAN_UNION);
            currency.setCurrencyTousd("");
            currency.setCurrencySignal(Constant.CURRENCY_SINGAL);
            currency.setCurrencyIsLocal("");
            currency.setCurrencyShorterForm(Constant.CURRENCY_EUR);
            return currency;
        }

        Cursor cursor;
        String args = HistoryContentUri.CURRENCY_ISLOCAL + " = ?";
        String[] selectArgs = new String[]{systemLanguageCode};
        /* MODIFIED-BEGIN by qiong.liu1, 2017-03-20,BUG-3621966*/
        cursor = mContentResolver.query(HistoryContentUri.CONTENT_RECORD_URI,
                null, args, selectArgs, null);
        if (cursor != null && cursor.moveToFirst()) {
            try {
                DefaultCurrency defaultCurrency;
                do {
                    String currency_name = cursor.getString(cursor.getColumnIndex(
                            HistoryContentUri.CURRENCY_NAME));
                    String currency_flag = cursor.getString(cursor.getColumnIndex(
                            HistoryContentUri.CURRENCY_FLAG_URI));
                    String currency_tousd = cursor.getString(cursor.getColumnIndex(
                            HistoryContentUri.CURRENCY_TO_USD));
                    String currency_signal = cursor.getString(cursor.getColumnIndex(
                            HistoryContentUri.CURRENCY_SIGNAL));
                    String currency_isLocal = cursor.getString(cursor.getColumnIndex(
                            HistoryContentUri.CURRENCY_ISLOCAL));
                    String currency_shorterform = cursor.getString(cursor.getColumnIndex(
                            HistoryContentUri.CURRENCY_SHORTER_FORM));
                            /* MODIFIED-END by qiong.liu1,BUG-3621966*/

                    defaultCurrency = new DefaultCurrency();
                    defaultCurrency.setCurrencyName(currency_name);
                    defaultCurrency.setCurrencyFlag(currency_flag);
                    defaultCurrency.setCurrencyTousd(currency_tousd);
                    defaultCurrency.setCurrencySignal(currency_signal);
                    defaultCurrency.setCurrencyIsLocal(currency_isLocal);
                    defaultCurrency.setCurrencyShorterForm(currency_shorterform);
                } while (cursor.moveToNext());
                return defaultCurrency;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                    cursor = null;
                }
            }
        }
        return null;
    }

    /**
     * Query search data of currency list screen.
     */
    public ArrayList<CurrencyListBean> queryCurrencyListSearchData(String searchString) {
        if (TextUtils.isEmpty(searchString)) {
            return null;
        }
        Cursor cursor;
        /* MODIFIED-BEGIN by qiong.liu1, 2017-03-20,BUG-3621966*/
        String[] selectColumns = new String[]{
                HistoryContentUri.CURRENCY_NAME,
                HistoryContentUri.CURRENCY_SHORTER_FORM,
                HistoryContentUri.CURRENCY_TO_USD};
        String args = HistoryContentUri.CURRENCY_NAME + " like ?";
        String[] selectArgs = new String[]{searchString + "%"}; // MODIFIED by qiong.liu1, 2017-05-04,BUG-4656997
        cursor = mContentResolver.query(
                HistoryContentUri.CONTENT_RECORD_URI, selectColumns, args, selectArgs, null);
        if (cursor != null && cursor.moveToFirst()) {
            try {
                CurrencyListBean currencyListBean;
                ArrayList<CurrencyListBean> currencyListData = new ArrayList<>();

                do {
                    String currency_name = cursor.getString(
                            cursor.getColumnIndex(HistoryContentUri.CURRENCY_NAME));
                    String currencyUnit = cursor.getString(
                            cursor.getColumnIndex(HistoryContentUri.CURRENCY_SHORTER_FORM));
                    String currencyValue = cursor.getString(
                            cursor.getColumnIndex(HistoryContentUri.CURRENCY_TO_USD));
                    String currencyCode = mContext.getResources().getString(mContext.getResources()
                            .getIdentifier(currency_name, "string", mContext.getPackageName()));

                    currencyListBean = new CurrencyListBean(currencyCode
                            + " - " + currencyUnit + " - " + currencyValue, currencyUnit);
                            /* MODIFIED-END by qiong.liu1,BUG-3621966*/
                    currencyListData.add(currencyListBean);
                } while (cursor.moveToNext());
                Collections.sort(currencyListData);
                return currencyListData;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                    cursor = null;
                }
            }
        /* MODIFIED-BEGIN by qiong.liu1, 2017-05-04,BUG-4656997*/
        } else {
            ArrayList<CurrencyListBean> currencyListDataInShort = new ArrayList<>();
            currencyListDataInShort = queryCurrencyListSearchDataInShorForm(searchString);
            return currencyListDataInShort;
        }
        return null;
    }

    private ArrayList<CurrencyListBean> queryCurrencyListSearchDataInShorForm(String searchString) {
        if (TextUtils.isEmpty(searchString)) {
            return null;
        }
        Cursor cursor;
        String[] selectColumns = new String[]{
                HistoryContentUri.CURRENCY_NAME,
                HistoryContentUri.CURRENCY_SHORTER_FORM,
                HistoryContentUri.CURRENCY_TO_USD};
        String args = HistoryContentUri.CURRENCY_SHORTER_FORM + " like ?";
        String[] selectArgs = new String[]{"%" + searchString + "%"};
        cursor = mContentResolver.query(
                HistoryContentUri.CONTENT_RECORD_URI, selectColumns, args, selectArgs, null);
        if (cursor != null && cursor.moveToFirst()) {
            try {
                CurrencyListBean currencyListBean;
                ArrayList<CurrencyListBean> currencyListData = new ArrayList<>();
                do {
                    String currency_name = cursor.getString(
                            cursor.getColumnIndex(HistoryContentUri.CURRENCY_NAME));
                    String currencyUnit = cursor.getString(
                            cursor.getColumnIndex(HistoryContentUri.CURRENCY_SHORTER_FORM));
                    String currencyValue = cursor.getString(
                            cursor.getColumnIndex(HistoryContentUri.CURRENCY_TO_USD));
                    String currencyCode = mContext.getResources().getString(mContext.getResources()
                            .getIdentifier(currency_name, "string", mContext.getPackageName()));

                    currencyListBean = new CurrencyListBean(currencyCode
                            + " - " + currencyUnit + " - " + currencyValue, currencyUnit);
                    currencyListData.add(currencyListBean);
                } while (cursor.moveToNext());
                Collections.sort(currencyListData);
                return currencyListData;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                    cursor = null;
                }
            }
            /* MODIFIED-END by qiong.liu1,BUG-4656997*/
        }
        return null;
    }

    /**
     * Get history data for currency list.
     *
     * @return history data
     */
    public ArrayList<CurrencyListBean> queryHistoryCurrencyListData() {
        Cursor cursor;
        /* MODIFIED-BEGIN by qiong.liu1, 2017-03-20,BUG-3621966*/
        String[] selectColumns = new String[]{
                HistoryContentUri.CURRENCY_NAME,
                HistoryContentUri.CURRENCY_SHORTER_FORM,
                /* MODIFIED-BEGIN by qiong.liu1, 2017-05-04,BUG-4656997*/
                HistoryContentUri.CRRRENCY_ALPHABET,
                HistoryContentUri.CURRENCY_TO_USD};
        String args = HistoryContentUri.CRRRENCY_ALPHABET + " desc";
//        String[] selectArgs = new String[]{"true"};
        cursor = mContentResolver.query(HistoryContentUri.CONTENT_RECORD_URI, selectColumns, null,  null, args);
//                HistoryContentUri.CONTENT_RECORD_URI, selectColumns, args, selectArgs, null);

        if (cursor != null && cursor.moveToFirst()) {
            try {
                CurrencyListBean currencyListBean;
                ArrayList<CurrencyListBean> currencyListData = new ArrayList<>();
                int index = 0;
                do {
                    String currency_alphabat = cursor.getString(
                            cursor.getColumnIndex(HistoryContentUri.CRRRENCY_ALPHABET));
                    if (currency_alphabat == null) {
                        continue;
                    }
                    /* MODIFIED-END by qiong.liu1,BUG-4656997*/
                    String currency_name = cursor.getString(
                            cursor.getColumnIndex(HistoryContentUri.CURRENCY_NAME));
                    String currencyUnit = cursor.getString(
                            cursor.getColumnIndex(HistoryContentUri.CURRENCY_SHORTER_FORM));
                    String currencyValue = cursor.getString(
                            cursor.getColumnIndex(HistoryContentUri.CURRENCY_TO_USD));
                    String currencyCode = mContext.getResources().getString(mContext.getResources()
                            .getIdentifier(currency_name, "string", mContext.getPackageName()));

                    currencyListBean = new CurrencyListBean(currencyCode
                            + " - " + currencyUnit + " - " + currencyValue,
                            CurrencyListAdapter.HISTORY_TAG);
                            /* MODIFIED-END by qiong.liu1,BUG-3621966*/
                    currencyListData.add(currencyListBean);
                    /* MODIFIED-BEGIN by qiong.liu1, 2017-05-04,BUG-4656997*/
                    index++;
                } while (cursor.moveToNext() && (index < 10));
                /* MODIFIED-END by qiong.liu1,BUG-4656997*/
                Collections.sort(currencyListData);
                return currencyListData;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                    cursor = null;
                }
            }
        }
        return null;
    }

    /**
     * Update history currency data.
     *
     * @param data will be saved data
     */
    public void updateHistoryCurrencyListData(String data) {
        try {
            String updateConditions = HistoryContentUri.CURRENCY_SHORTER_FORM + " = ?";
            ContentValues values = new ContentValues();
/* MODIFIED-BEGIN by qiong.liu1, 2017-05-04,BUG-4656997*/
//            values.put(HistoryContentUri.CRRRENCY_ALPHABET, "true");
            values.put(HistoryContentUri.CRRRENCY_ALPHABET, System.currentTimeMillis());
            /* MODIFIED-END by qiong.liu1,BUG-4656997*/
            String[] updateData = null;
            if (!TextUtils.isEmpty(data) && data.contains("-")) {
                updateData = data.split("-");
            }
            if (updateData != null && updateData.length >= 2) {
                data = updateData[1].trim();
                mContentResolver.update(HistoryContentUri.CONTENT_RECORD_URI, values, updateConditions, new String[]{data});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* MODIFIED-BEGIN by qiong.liu1, 2017-03-23,BUG-3621966*/
    public boolean isCurrencyRecordInDbEmpty(){
        Cursor cursor;
        String[] selectColumns = new String[]{
                HistoryContentUri.CURRENCY_TO_USD};
        String args = HistoryContentUri.CURRENCY_SHORTER_FORM + " = ?";
        String[] selectArgs = new String[]{"USD"};
        cursor = mContentResolver.query(
                HistoryContentUri.CONTENT_RECORD_URI, selectColumns, args, selectArgs, null);
        if (cursor != null && cursor.moveToFirst()) {
            String currencyValue = cursor.getString(
                    cursor.getColumnIndex(HistoryContentUri.CURRENCY_TO_USD));
            return currencyValue == null;
        }else {
            return true;
        }
    }
    /* MODIFIED-END by qiong.liu1,BUG-3621966*/

    /* MODIFIED-BEGIN by qiong.liu1, 2017-04-21,BUG-4452809*/
    public synchronized String queryUpdateTime() {
        Cursor cursor;
        String[] selectConditions = new String[]{
                HistoryContentUri.CURRENCY_TIME};
        String args = HistoryContentUri.CURRENCY_SHORTER_FORM + "=?";
        String[] selectArgs = new String[]{"CNY"};
        cursor = mContentResolver.query(HistoryContentUri.CONTENT_RECORD_URI, selectConditions, args, selectArgs, null);
        if (cursor != null) {
            try {
                cursor.moveToFirst();
                updateTime = cursor.getString(cursor.getColumnIndex(HistoryContentUri.CURRENCY_TIME));
                return updateTime;
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG,"queryUpdateTime exception : " + e); // MODIFIED by qiong.liu1, 2017-04-07,BUG-4452809
            } finally {
                if (cursor != null) {
                    cursor.close();
                    cursor = null;
                }
            }
        }
        return null;
    }
    /* MODIFIED-END by qiong.liu1,BUG-4452809*/
}
