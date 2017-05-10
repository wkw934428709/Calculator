/******************************************************************************/
/*                                                               Date:11/2012 */
/*                                PRESENTATION                                */
/*                                                                            */
/*       Copyright 2012 TCL Communication Technology Holdings Limited.        */
/*                                                                            */
/* This material is company confidential, cannot be reproduced in any form    */
/* without the written permission of TCL Communication Technology Holdings    */
/* Limited.                                                                   */
/*                                                                            */
/* -------------------------------------------------------------------------- */
/*  Author :  (Junjie.Qian)                                                   */
/*  Email  :   Junjie.Qian@tcl-mobile.com                                     */
/*  Role   :  FMRadio                                                         */
/*  Reference documents :                                                     */
/* -------------------------------------------------------------------------- */
/*  Comments : The class HistoryProvider can share the database for all applicatio */
/*             ns..                                                           */
/*  File     : src/com/tct/fmradio/provider/HistoryProvider.java                */
/*  Labels   :                                                                */
/* -------------------------------------------------------------------------- */
/* ========================================================================== */

package com.tct.calculator.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteCantOpenDatabaseException; // MODIFIED by kaifeng.lu, 2016-06-23,BUG-2384409
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;
import android.util.Log;

public class HistoryProvider extends ContentProvider {
    // public static final String AUTHORITY = "com.thunderst.radio";
    // public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
    // + "/items");
    private static final int DATABASE_VERSION = 1;
    private static final int ITEMS = 1;
    private static final int ITEMS_ID = 2;
    private static final int CALCULATOR_CODE = 3;
    private static final int CURRENCY_CODE = 4;
    private static final int CONVERTER_CODE = 5;
    private static final int CURRENCY_RECORD_CODE = 6;
    private static final String DATABASE_NAME = "history.db";
    private static final String HISTORY_TABLE_NAME = "history";
    private static final String HISTORY_CURRENCY_TABLE_NAME = "currency_history";
    private static final String HISTORY_CONVERTER_TABLE_NAME = "converter_history";
    private static final String CURRENCY_RECORD_TABLE = "currency_record";
    private static final String TAG = "HistoryProvider";
    private static final UriMatcher sUriMatcher;

    private DatabaseHelper mOpenHelper;

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(HistoryContentUri.AUTHORITY, "items", ITEMS);
        sUriMatcher.addURI(HistoryContentUri.AUTHORITY, "items/#", ITEMS_ID);
        sUriMatcher.addURI(HistoryContentUri.AUTHORITY, HISTORY_TABLE_NAME, CALCULATOR_CODE);
        sUriMatcher.addURI(HistoryContentUri.AUTHORITY, HISTORY_CURRENCY_TABLE_NAME, CURRENCY_CODE);
        sUriMatcher.addURI(HistoryContentUri.AUTHORITY, HISTORY_CONVERTER_TABLE_NAME, CONVERTER_CODE);
        sUriMatcher.addURI(HistoryContentUri.AUTHORITY, CURRENCY_RECORD_TABLE, CURRENCY_RECORD_CODE);
    }

    /************
     * content provider
     *****************************************************************************/
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        Log.i(TAG, "===XXX===DB=HistoryProvider=deleteConvertHistory");
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
            case ITEMS:
                count = db.delete(HISTORY_TABLE_NAME, selection, selectionArgs);
                break;

            case ITEMS_ID:
                String noteId = uri.getPathSegments().get(1);
                count = db.delete(HISTORY_TABLE_NAME, BaseColumns._ID + "=" + noteId
                                + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""),
                        selectionArgs);
                break;
            case CURRENCY_CODE:
                count = db.delete(HISTORY_CURRENCY_TABLE_NAME, selection, selectionArgs);
                break;
            case CONVERTER_CODE:
                count = db.delete(HISTORY_CONVERTER_TABLE_NAME, selection, selectionArgs);
                break;
            case CURRENCY_RECORD_CODE:
                count = db.delete(CURRENCY_RECORD_TABLE, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public String getType(Uri uri) {
        Log.i(TAG, "===XXX===DB=HistoryProvider=getType");
        switch (sUriMatcher.match(uri)) {
            case ITEMS:
                return HistoryContentUri.CONTENT_TYPE;
            case ITEMS_ID:
                return HistoryContentUri.CONTENT_ITEM_TYPE;
            case CURRENCY_CODE:
                return HistoryContentUri.CONTENT_CURRENCY_HISTORY_TYPE;
            case CONVERTER_CODE:
                return HistoryContentUri.CONTENT_CONVERTER_HISTROY_TYPE;
            case CURRENCY_RECORD_CODE:
                return HistoryContentUri.CONTENT_CURRENCY_RECORD_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Log.d(TAG, "===XXX===DB=HistoryProvider=insert: " + uri + " mOpenHelper " + mOpenHelper); // MODIFIED by qiong.liu1, 2017-03-20,BUG-3621966
//        if (sUriMatcher.match(uri) != ITEMS) {
//            throw new IllegalArgumentException("Unknown URI " + uri);
//        }
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        long rowId;
        switch (sUriMatcher.match(uri)) {
            case ITEMS:
                rowId = db.insert(HISTORY_TABLE_NAME, null, values);
                if (rowId > 0) {
                    Uri noteUri = ContentUris.withAppendedId(HistoryContentUri.CONTENT_URI, rowId);
                    getContext().getContentResolver().notifyChange(noteUri, null);
                    return noteUri;
                }
                break;
            case CURRENCY_CODE:
                rowId = db.insert(HISTORY_CURRENCY_TABLE_NAME, null, values);
                if (rowId > 0) {
                    Uri noteUri = ContentUris.withAppendedId(HistoryContentUri.CONTENT_CURRENCY_HISTORY_URI, rowId);
                    getContext().getContentResolver().notifyChange(noteUri, null);
                    return noteUri;
                }
                break;
            case CONVERTER_CODE:
                rowId = db.insert(HISTORY_CONVERTER_TABLE_NAME, null, values);
                if (rowId > 0) {
                    Uri noteUri = ContentUris.withAppendedId(HistoryContentUri.CONTENT_CONVERTER_HISTORY_URI, rowId);
                    getContext().getContentResolver().notifyChange(noteUri, null);
                    return noteUri;
                }
                break;
            case CURRENCY_RECORD_CODE:
                rowId = db.insert(CURRENCY_RECORD_TABLE, null, values);
                if (rowId > 0) {
                    Uri noteUri = ContentUris.withAppendedId(HistoryContentUri.CONTENT_RECORD_URI, rowId);
                    getContext().getContentResolver().notifyChange(noteUri, null);
                    return noteUri;
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        throw new SQLException("Failed to insert row into " + uri);
    }

    @Override
    public boolean onCreate() {
        Log.i(TAG, "===XXX===DB=HistoryProvider=onCreate"); // MODIFIED by qiong.liu1, 2017-03-20,BUG-3621966
        mOpenHelper = new DatabaseHelper(getContext());
        FileUtils.getInstance(getContext()).copyDBFromAssets();
        return true;

    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        /* MODIFIED-BEGIN by kaifeng.lu, 2016-06-23,BUG-2384409*/
        Log.i(TAG, "===XXX===DB=HistoryProvider=query uri " + uri); // MODIFIED by qiong.liu1, 2017-03-20,BUG-3621966
        Cursor cursor = null;
        SQLiteDatabase db = null;
        try {
            db = mOpenHelper.getReadableDatabase();

            switch (sUriMatcher.match(uri)) {
                case ITEMS:
                    cursor = db.query(HISTORY_TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                    break;
                case CURRENCY_CODE:
                    cursor = db.query(HISTORY_CURRENCY_TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                    break;
                case CONVERTER_CODE:
                    cursor = db.query(HISTORY_CONVERTER_TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                    break;
                case CURRENCY_RECORD_CODE:
                    cursor = db.query(CURRENCY_RECORD_TABLE, projection, selection, selectionArgs, null, null, sortOrder);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown URI " + uri);
            }
        } catch (SQLiteCantOpenDatabaseException e) {
            Log.e(TAG, "History happend SQLiteCantOpenDatabaseException" + e.toString());
            /* MODIFIED-END by kaifeng.lu,BUG-2384409*/
        }

        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        Log.i(TAG, "===XXX===DB=HistoryProvider=update");
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
            case ITEMS:
                count = db.update(HISTORY_TABLE_NAME, values, selection, selectionArgs);
                break;

            case ITEMS_ID:
                String noteId = uri.getPathSegments().get(1);
                count = db.update(HISTORY_TABLE_NAME, values, BaseColumns._ID + "=" + noteId
                                + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""),
                        selectionArgs);
                break;
            case CURRENCY_CODE:
                count = db.update(HISTORY_CURRENCY_TABLE_NAME, values, selection, selectionArgs);
                break;
            case CONVERTER_CODE:
                count = db.update(HISTORY_CONVERTER_TABLE_NAME, values, selection, selectionArgs);
                break;
            case CURRENCY_RECORD_CODE:
                count = db.update(CURRENCY_RECORD_TABLE, values, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    /**
     * This class helps open, create, and upgrade the database file.
     */
    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            Log.i(TAG, "===XXX===DB=DatabaseHelper");
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.i(TAG, "===XXX===DB=DatabaseHelper=onCreate");
            // calculator history table

            db.execSQL("CREATE TABLE " + HISTORY_TABLE_NAME + " (" + BaseColumns._ID
                    + " INTEGER PRIMARY KEY," + HistoryContentUri.HISTORY_FORMULA + " TEXT,"
                    + HistoryContentUri.HISTORY_RESULT + " TEXT, " + HistoryContentUri.HISTORY_TIMESTAP + " LONG" + ");");

            //  currency history table

            db.execSQL("CREATE TABLE " + HISTORY_CURRENCY_TABLE_NAME + " (" + BaseColumns._ID
                    + " INTEGER PRIMARY KEY," + HistoryContentUri.CURRENCY_HISTORY_FROM + " TEXT,"
                    + HistoryContentUri.CURRENCY_HISTORY_TO + " TEXT," + HistoryContentUri.HISTORY_TIMESTAP + " LONG" + "); ");

            // converter history table

            db.execSQL("CREATE TABLE " + HISTORY_CONVERTER_TABLE_NAME + " (" + BaseColumns._ID
                    + " INTEGER PRIMARY KEY," + HistoryContentUri.CONVERTER_HISTORY_FROM + " TEXT,"
                    + HistoryContentUri.CONVERTER_HISTORY_TO + " TEXT," + HistoryContentUri.CONVERTER_HISTORY_TYPE + " TEXT,"
                    + HistoryContentUri.CONVERTER_HISTORY_IMG_ID + " TEXT,"
                    + HistoryContentUri.CONVERTER_HISTORY_TIMESTAP + " TEXT" + ");");

            // converter record table

            db.execSQL("CREATE TABLE " + CURRENCY_RECORD_TABLE + " (" + BaseColumns._ID
                    /* MODIFIED-BEGIN by qiong.liu1, 2017-05-04,BUG-4656997*/
                    + " INTEGER PRIMARY KEY,"
                    + HistoryContentUri.CURRENCY_NAME + " TEXT,"
                    + HistoryContentUri.CURRENCY_FLAG_URI + " TEXT,"
                    + HistoryContentUri.CURRENCY_TO_USD + " TEXT,"
                    + HistoryContentUri.CURRENCY_SIGNAL + " TEXT,"
                    + HistoryContentUri.CURRENCY_ISLOCAL + " TEXT,"
                    + HistoryContentUri.CURRENCY_SHORTER_FORM + " TEXT,"
                    + HistoryContentUri.CURRENCY_TIME + " TEXT,"
                    + HistoryContentUri.CURRENCY_AREA + " TEXT,"
                    + HistoryContentUri.CRRRENCY_ALPHABET + " TEXT" + ");");
                    /* MODIFIED-END by qiong.liu1,BUG-4656997*/
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion
                    + ", which will destroy all old data");
            Log.i(TAG, "===XXX===DB=DatabaseHelper=onUpgrade");
        }
    }

}
