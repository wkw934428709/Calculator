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
/*  Comments : The class FMContentUri defines some constant quantity for data */
/*             base table columns , content uris...                           */
/*  File     : src/com/tct/fmradio/provider/FMContentUri.java              */
/*  Labels   :                                                                */
/* -------------------------------------------------------------------------- */
/* ========================================================================== */

package com.tct.calculator.data;

import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.ContactsContract;


public final class HistoryContentUri implements BaseColumns {

    private HistoryContentUri() {
    }

    public static final String AUTHORITY = "com.tct.calculator";
    public static final String HISTORY_FORMULA = "formula";
    public static final String HISTORY_RESULT = "result";
    public static final String HISTORY_TIMESTAP = "timestap";
    private static final String HISTORY_CURRENCY_TABLE_NAME = "currency_history";
    private static final String HISTORY_CONVERTER_TABLE_NAME = "converter_history";
    private static final String CURRENCY_RECORD_TABLE = "currency_record";
    public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.data";
    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.data";
    public static final String CONTENT_CURRENCY_HISTORY_TYPE = "vnd.android.cursor.dir/currency.history";
    public static final String CONTENT_CONVERTER_HISTROY_TYPE = "vnd.android.cursor.dir/converter.history";
    public static final String CONTENT_CURRENCY_RECORD_TYPE = "vnd.android.cursor.item/currency.record";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/items");
    public static final Uri CONTENT_CURRENCY_HISTORY_URI = Uri.parse("content://" + AUTHORITY + "/" + HISTORY_CURRENCY_TABLE_NAME);
    public static final Uri CONTENT_RECORD_URI = Uri.parse("content://" + AUTHORITY + "/" + CURRENCY_RECORD_TABLE);
    public static final Uri CONTENT_CONVERTER_HISTORY_URI = Uri.parse("content://" + AUTHORITY + "/" + HISTORY_CONVERTER_TABLE_NAME);

    /**
     * The  CurrencyRate record  Columns
     */
    public static final String CURRENCY_NAME = "currency_name";
    public static final String CURRENCY_FLAG_URI = "currency_flag";
    public static final String CURRENCY_TO_USD = "currency_tousd";
    public static final String CURRENCY_SIGNAL = "currency_signal";
    public static final String CURRENCY_ISLOCAL = "islocal";
    public static final String CURRENCY_SHORTER_FORM = "shorterform";
    public static final String CURRENCY_TIME = "currency_time";
    public static final String CURRENCY_AREA = "currency_area";
    public static final String CURRENCY_history_time = "currency_history_time"; // MODIFIED by qiong.liu1, 2017-05-04,BUG-4656997
    public static final String CRRRENCY_ALPHABET = "currency_alphabat";

    /**
     * The  CurrencyHistoryRecord Colums
     */
    public static final String CURRENCY_HISTORY_FROM = "fromcountry";
    public static final String CURRENCY_HISTORY_TO = "toconutry";
    public static final String CURRENCY_HISTORY_TIME_STAP = "timestap";

    /**
     * The ConverterHistoryRecord Colums
     */

    public static final String CONVERTER_HISTORY_FROM = "convert_from_unit";
    public static final String CONVERTER_HISTORY_TO = "convert_to_unit";
    public static final String CONVERTER_HISTORY_TYPE = "coverter_type";
    public static final String CONVERTER_HISTORY_IMG_ID = "coverter_img_id";
    public static final String CONVERTER_HISTORY_TIMESTAP = "coverter_time_stamp";

} // MODIFIED by qiong.liu1, 2017-05-04,BUG-4656997
