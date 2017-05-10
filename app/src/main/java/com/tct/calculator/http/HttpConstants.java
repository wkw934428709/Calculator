/* Copyright (C) 2016 Tcl Corporation Limited */
package com.tct.calculator.http;


/**
 * Created by user on 16-10-18.
 */
public class HttpConstants {
    public final static String REQUEST_URL = "https://xecdapi.xe.com/v1/";
    public final static String REQUEST_ACCOUNT_ID = "tctmobileinternationallimited116882674";
    public final static String REQUEST_APP_KEY = "p1ikb2blvrk1be3edo858iu1fm";

    public final static int ACCOUNT_INFO_REQUEST = 0;
    public final static int CONVERT_FROM_REQUEST = 1;
    public final static int CURRENCIES_JSON_REQUEST = 2;
    public final static int CONVERT_FROM_JSON_REQUEST = 3;
    public final static int CONVERT_TO_JSON_REQUEST = 4;
    public final static int HISTORIC_RATE_JSON_REQUEST = 5;
    public final static int HISTORIC_RATE_PERIOD_REQUEST = 6;

    public final static String ACCOUNT_INFO_SERVICE = "account_info/";
    public final static String CONVERT_TO_JSON_SERVICE = "convert_to.json/";
    public final static String CONVERT_FROM_SERVICE = "convert_from/";
    public final static String CURRENCIES_JSON_SERVICE = "currencies.json/";
    public final static String CONVERT_FROM_JSON_SERVICE = "convert_from.json/";
    public final static String HISTORIC_RATE_JSON_SERVICE = "historic_rate.json/";
    public final static String HISTORIC_RATE_PERIOD_SERVICE = "historic_rate/period/";

    public final static String ERROR_CONNECTION = "ConnectError";
    public final static String ERROR_UNKNOWN = "UnknownError";

    public final static String MESSENGER = "messenger";
    public final static String RESPONSE_SUCCESS = "success";
    public final static String RESPONSE_CODE = "code";
    public final static String RESPONSE_RESULT = "result";
    public final static String RESPONSE_ERROR = "Error";
    public final static String RESPONSE_MESSAGE = "message";

    public final static String KEY_REQUEST_ID = "request_id";
    public final static String KEY_TO = "to";
    public final static String KEY_FROM = "from";
    public final static String KEY_AMOUNT = "amount";
    public final static String KEY_OBSOLETE = "obsolete";
    public final static String KEY_DATE = "date";
    public final static String KEY_START_TIMES_TAMP = "start_timestamp";
    public final static String KEY_END_TIMES_TAMP = "end_timestamp";
    public final static String KEY_PER_PAGE_MAX = "per_page"; // MODIFIED by qiong.liu1, 2017-03-23,BUG-3621966

    /**
     * 0:General Server error
     * 1:Authentication Invalid Credentials
     * 2:Failed login Limit
     * 3:Monthly Rate Request Limit Exceeded
     * 4:Usage Restriction (Throttling)
     * 5:No Access to Historic Rate
     * 6:User Error
     * 7:No Rates for Requested Currency and Date
     * 8:Rates Not Available
     * 9:Free Trial Ended
     * 10:No Rates Available on requested dates
     * 11:Future Date
     * 12:Invalid Date Range
     * 13:Results Requested Exceeds Per Page Limit
     */
    public final static int[] RESPONSE_ERROR_CODE = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13};
}
