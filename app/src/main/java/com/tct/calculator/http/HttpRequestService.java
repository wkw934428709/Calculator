/* Copyright (C) 2016 Tcl Corporation Limited */
package com.tct.calculator.http;

import android.app.IntentService;
/* MODIFIED-BEGIN by qiong.liu1, 2017-03-20,BUG-3621966*/
import android.app.Notification;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
/* MODIFIED-BEGIN by qiong.liu1, 2017-03-23,BUG-3621966*/
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
/* MODIFIED-END by qiong.liu1,BUG-3621966*/
import android.text.TextUtils;
//import org.apache.commons.codec.binary.Base64;
import android.util.Base64;
import android.util.Log;

import com.tct.calculator.data.HistoryContentUri;
import com.tct.calculator.data.HistoryProvider;
import com.tct.calculator.view.interfaces.ChangeGraphViewListener; // MODIFIED by qiong.liu1, 2017-03-23,BUG-3621966

import org.json.JSONArray;
/* MODIFIED-END by qiong.liu1,BUG-3621966*/
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpRetryException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
/* MODIFIED-BEGIN by qiong.liu1, 2017-03-23,BUG-3621966*/
import java.util.ArrayList;
import java.util.List;
/* MODIFIED-END by qiong.liu1,BUG-3621966*/

/* MODIFIED-BEGIN by qiong.liu1, 2017-03-20,BUG-3621966*/
import static com.tct.calculator.utils.SharedPreferencesHelper.SHARED_PREFERENCES_CONVERT_RECORDER;
import static com.tct.calculator.utils.SharedPreferencesHelper.WHEN_UPDATE;
/* MODIFIED-END by qiong.liu1,BUG-3621966*/

public class HttpRequestService extends IntentService {

    private final static String TAG = "HttpRequestService";

    private final static boolean EXTRA_DEBUGGING = true;

    private static final String ENCODE_CHARSET = "UTF-8";

    private final static int CONNECTION_TIMEOUT = 30000; // MODIFIED by qiong.liu1, 2017-03-20,BUG-3621966

    private static final String[] requestNames = {
        "ACCOUNT_INFO_REQUEST",
        "CONVERT_FROM_REQUEST",
        "CURRENCIES_JSON_REQUEST",
        "CONVERT_FROM_JSON_REQUEST",
        "CONVERT_TO_JSON_REQUEST",
        "HISTORIC_RATE_JSON_REQUEST",
        "HISTORIC_RATE_PERIOD_REQUEST",
    };

    /* MODIFIED-BEGIN by qiong.liu1, 2017-03-20,BUG-3621966*/
    private ChangeGraphViewListener mChangeGrapViewListener;

    public HttpRequestService() {
        super("HttpRequestService");
    }

    private void just4test() {
        String responseString = "{\"terms\":\"http://www.xe.com/legal/dfs.php\",\"privacy\":\"http://www.xe.com/privacy.php\",\"from\":\"USD\",\"amount\":1.0,\"timestamp\":\"2017-02-21T16:00:00Z\",\"to\":[{\"quotecurrency\":\"AED\",\"mid\":3.6728601056},{\"quotecurrency\":\"AFN\",\"mid\":66.6800012207},{\"quotecurrency\":\"ALL\",\"mid\":128.2300372887},{\"quotecurrency\":\"AMD\",\"mid\":486.8549172347},{\"quotecurrency\":\"ANG\",\"mid\":1.7760001701},{\"quotecurrency\":\"AOA\",\"mid\":165.7541898515},{\"quotecurrency\":\"ARS\",\"mid\":15.7096468941},{\"quotecurrency\":\"AUD\",\"mid\":1.3039393241},{\"quotecurrency\":\"AWG\",\"mid\":1.79},{\"quotecurrency\":\"AZN\",\"mid\":1.7853141841},{\"quotecurrency\":\"BAM\",\"mid\":1.8562434898},{\"quotecurrency\":\"BBD\",\"mid\":2.0},{\"quotecurrency\":\"BDT\",\"mid\":80.1009842148},{\"quotecurrency\":\"BGN\",\"mid\":1.8558003247},{\"quotecurrency\":\"BHD\",\"mid\":0.3768450005},{\"quotecurrency\":\"BIF\",\"mid\":1697.6360351562},{\"quotecurrency\":\"BMD\",\"mid\":1.0},{\"quotecurrency\":\"BND\",\"mid\":1.4211406132},{\"quotecurrency\":\"BOB\",\"mid\":6.9438960098},{\"quotecurrency\":\"BRL\",\"mid\":3.096460964},{\"quotecurrency\":\"BSD\",\"mid\":1.0},{\"quotecurrency\":\"BTN\",\"mid\":66.9404770168},{\"quotecurrency\":\"BWP\",\"mid\":10.4576606966},{\"quotecurrency\":\"BYN\",\"mid\":1.8747985458},{\"quotecurrency\":\"BYR\",\"mid\":18747.9854578164},{\"quotecurrency\":\"BZD\",\"mid\":2.0153496401},{\"quotecurrency\":\"CAD\",\"mid\":1.3137278653},{\"quotecurrency\":\"CDF\",\"mid\":1267.599999223},{\"quotecurrency\":\"CHF\",\"mid\":1.0095302211},{\"quotecurrency\":\"CLP\",\"mid\":643.9001604812},{\"quotecurrency\":\"CNY\",\"mid\":6.8828953973},{\"quotecurrency\":\"COP\",\"mid\":2901.8559946863},{\"quotecurrency\":\"CRC\",\"mid\":560.2425832363},{\"quotecurrency\":\"CUC\",\"mid\":1.0},{\"quotecurrency\":\"CUP\",\"mid\":26.5},{\"quotecurrency\":\"CVE\",\"mid\":104.7910976258},{\"quotecurrency\":\"CZK\",\"mid\":25.6464659552},{\"quotecurrency\":\"DJF\",\"mid\":178.586},{\"quotecurrency\":\"DKK\",\"mid\":7.0546385868},{\"quotecurrency\":\"DOP\",\"mid\":46.8269731657},{\"quotecurrency\":\"DZD\",\"mid\":110.6727931763},{\"quotecurrency\":\"EGP\",\"mid\":15.92909171},{\"quotecurrency\":\"ERN\",\"mid\":15.3620007692},{\"quotecurrency\":\"ETB\",\"mid\":22.6482611797},{\"quotecurrency\":\"EUR\",\"mid\":0.9490822259},{\"quotecurrency\":\"FJD\",\"mid\":2.054411204},{\"quotecurrency\":\"FKP\",\"mid\":0.8020182372},{\"quotecurrency\":\"GBP\",\"mid\":0.8020182372},{\"quotecurrency\":\"GEL\",\"mid\":2.6199141245},{\"quotecurrency\":\"GGP\",\"mid\":0.8020182372},{\"quotecurrency\":\"GHS\",\"mid\":4.5387954432},{\"quotecurrency\":\"GIP\",\"mid\":0.8020182372},{\"quotecurrency\":\"GMD\",\"mid\":44.54},{\"quotecurrency\":\"GNF\",\"mid\":9286.0},{\"quotecurrency\":\"GTQ\",\"mid\":7.3820996378},{\"quotecurrency\":\"GYD\",\"mid\":208.043980293},{\"quotecurrency\":\"HKD\",\"mid\":7.7607432757},{\"quotecurrency\":\"HNL\",\"mid\":23.5393207563},{\"quotecurrency\":\"HRK\",\"mid\":7.0732116374},{\"quotecurrency\":\"HTG\",\"mid\":68.2500216694},{\"quotecurrency\":\"HUF\",\"mid\":291.4148915563},{\"quotecurrency\":\"IDR\",\"mid\":13380.8011092839},{\"quotecurrency\":\"ILS\",\"mid\":3.7010382943},{\"quotecurrency\":\"IMP\",\"mid\":0.8020182372},{\"quotecurrency\":\"INR\",\"mid\":66.9404770168},{\"quotecurrency\":\"IQD\",\"mid\":1182.1},{\"quotecurrency\":\"IRR\",\"mid\":32422.1},{\"quotecurrency\":\"ISK\",\"mid\":110.548},{\"quotecurrency\":\"JEP\",\"mid\":0.8020182372},{\"quotecurrency\":\"JMD\",\"mid\":128.3317224871},{\"quotecurrency\":\"JOD\",\"mid\":0.7090499772},{\"quotecurrency\":\"JPY\",\"mid\":113.6297055878},{\"quotecurrency\":\"KES\",\"mid\":103.5904639759},{\"quotecurrency\":\"KGS\",\"mid\":69.1037246908},{\"quotecurrency\":\"KHR\",\"mid\":4014.4000000009},{\"quotecurrency\":\"KMF\",\"mid\":466.9178472327},{\"quotecurrency\":\"KPW\",\"mid\":131.1510926903},{\"quotecurrency\":\"KRW\",\"mid\":1143.946885193},{\"quotecurrency\":\"KWD\",\"mid\":0.3057300166},{\"quotecurrency\":\"KYD\",\"mid\":0.8199999967},{\"quotecurrency\":\"KZT\",\"mid\":316.0369248635},{\"quotecurrency\":\"LAK\",\"mid\":8210.5},{\"quotecurrency\":\"LBP\",\"mid\":1511.2200195312},{\"quotecurrency\":\"LKR\",\"mid\":151.8199415493},{\"quotecurrency\":\"LRD\",\"mid\":91.2},{\"quotecurrency\":\"LSL\",\"mid\":13.1441507534},{\"quotecurrency\":\"LYD\",\"mid\":1.4277600136},{\"quotecurrency\":\"MAD\",\"mid\":10.1207840895},{\"quotecurrency\":\"MDL\",\"mid\":19.8879738102},{\"quotecurrency\":\"MGA\",\"mid\":2982.0},{\"quotecurrency\":\"MKD\",\"mid\":58.3607978037},{\"quotecurrency\":\"MMK\",\"mid\":1371.3555631821},{\"quotecurrency\":\"MNT\",\"mid\":2475.8902305268},{\"quotecurrency\":\"MOP\",\"mid\":7.9935655739},{\"quotecurrency\":\"MRO\",\"mid\":357.8},{\"quotecurrency\":\"MUR\",\"mid\":35.8138732678},{\"quotecurrency\":\"MVR\",\"mid\":15.422000061},{\"quotecurrency\":\"MWK\",\"mid\":726.6319824219},{\"quotecurrency\":\"MXN\",\"mid\":20.485160442},{\"quotecurrency\":\"MYR\",\"mid\":4.4578999411},{\"quotecurrency\":\"MZN\",\"mid\":70.7520019531},{\"quotecurrency\":\"NAD\",\"mid\":13.1441507534},{\"quotecurrency\":\"NGN\",\"mid\":317.5},{\"quotecurrency\":\"NIO\",\"mid\":29.5292040876},{\"quotecurrency\":\"NOK\",\"mid\":8.3547106298},{\"quotecurrency\":\"NPR\",\"mid\":107.0639739363},{\"quotecurrency\":\"NZD\",\"mid\":1.397600263},{\"quotecurrency\":\"OMR\",\"mid\":0.3848500122},{\"quotecurrency\":\"PAB\",\"mid\":1.0},{\"quotecurrency\":\"PEN\",\"mid\":3.2483556114},{\"quotecurrency\":\"PGK\",\"mid\":3.1745881583},{\"quotecurrency\":\"PHP\",\"mid\":50.2924193011},{\"quotecurrency\":\"PKR\",\"mid\":104.8100024414},{\"quotecurrency\":\"PLN\",\"mid\":4.0845437266},{\"quotecurrency\":\"PYG\",\"mid\":5580.7001837032},{\"quotecurrency\":\"QAR\",\"mid\":3.6410400259},{\"quotecurrency\":\"RON\",\"mid\":4.2862945493},{\"quotecurrency\":\"RSD\",\"mid\":117.5654074713},{\"quotecurrency\":\"RUB\",\"mid\":57.6891122438},{\"quotecurrency\":\"RWF\",\"mid\":822.2879882813},{\"quotecurrency\":\"SAR\",\"mid\":3.7496854803},{\"quotecurrency\":\"SBD\",\"mid\":7.7972709552},{\"quotecurrency\":\"SCR\",\"mid\":13.4144002075},{\"quotecurrency\":\"SDG\",\"mid\":6.68},{\"quotecurrency\":\"SEK\",\"mid\":8.9878768946},{\"quotecurrency\":\"SGD\",\"mid\":1.4211406132},{\"quotecurrency\":\"SHP\",\"mid\":0.8020182372},{\"quotecurrency\":\"SLL\",\"mid\":7454.1373134328},{\"quotecurrency\":\"SOS\",\"mid\":578.0},{\"quotecurrency\":\"SPL\",\"mid\":0.1666666667},{\"quotecurrency\":\"SRD\",\"mid\":7.5126948503},{\"quotecurrency\":\"STD\",\"mid\":23279.9424293903},{\"quotecurrency\":\"SVC\",\"mid\":8.75},{\"quotecurrency\":\"SYP\",\"mid\":214.3498661385},{\"quotecurrency\":\"SZL\",\"mid\":13.1441507534},{\"quotecurrency\":\"THB\",\"mid\":35.0484320559},{\"quotecurrency\":\"TJS\",\"mid\":7.9549115613},{\"quotecurrency\":\"TMT\",\"mid\":3.5},{\"quotecurrency\":\"TND\",\"mid\":2.2995638938},{\"quotecurrency\":\"TOP\",\"mid\":2.2385875242},{\"quotecurrency\":\"TRY\",\"mid\":3.6310616892},{\"quotecurrency\":\"TTD\",\"mid\":6.7320425712},{\"quotecurrency\":\"TVD\",\"mid\":1.3039393241},{\"quotecurrency\":\"TWD\",\"mid\":30.7464331352},{\"quotecurrency\":\"TZS\",\"mid\":2232.1972956664},{\"quotecurrency\":\"UAH\",\"mid\":27.1249976382},{\"quotecurrency\":\"UGX\",\"mid\":3586.8039490217},{\"quotecurrency\":\"USD\",\"mid\":1.0},{\"quotecurrency\":\"UYU\",\"mid\":28.3009909123},{\"quotecurrency\":\"UZS\",\"mid\":3356.8311513931},{\"quotecurrency\":\"VEF\",\"mid\":9.9829996338},{\"quotecurrency\":\"VND\",\"mid\":22811.7},{\"quotecurrency\":\"VUV\",\"mid\":106.730000326},{\"quotecurrency\":\"WST\",\"mid\":2.5385572139},{\"quotecurrency\":\"XAF\",\"mid\":622.5571296436},{\"quotecurrency\":\"XAG\",\"mid\":0.0555249614},{\"quotecurrency\":\"XAU\",\"mid\":8.102962E-4},{\"quotecurrency\":\"XBT\",\"mid\":9.093438E-4},{\"quotecurrency\":\"XCD\",\"mid\":2.7000000027},{\"quotecurrency\":\"XDR\",\"mid\":0.7405999883},{\"quotecurrency\":\"XOF\",\"mid\":622.5571296436},{\"quotecurrency\":\"XPD\",\"mid\":0.0012914336},{\"quotecurrency\":\"XPF\",\"mid\":113.2556355469},{\"quotecurrency\":\"XPT\",\"mid\":0.0010025805},{\"quotecurrency\":\"YER\",\"mid\":250.025},{\"quotecurrency\":\"ZAR\",\"mid\":13.1441507534},{\"quotecurrency\":\"ZMW\",\"mid\":9.7689877532},{\"quotecurrency\":\"ZWD\",\"mid\":361.9}]}";
        Bundle bundle = new Bundle();
        bundle.putInt(HttpConstants.KEY_REQUEST_ID,4);
        try {
            handleJSONResponse(new JSONObject(responseString), bundle);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    /* MODIFIED-BEGIN by qiong.liu1, 2017-03-27,BUG-3621966*/
    public void onCreate() {
        super.onCreate();
    }

    @Override
    /* MODIFIED-END by qiong.liu1,BUG-3621966*/
    protected void onHandleIntent(Intent intent) {
        Log.e(TAG, "HttpRequestService onHandleIntent "  );
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo netInfo = cm.getActiveNetworkInfo();
                if (netInfo != null && netInfo.isConnected()) {
                    onHandleRequest(bundle);
                } else {
                    handleException(bundle, HttpConstants.ERROR_CONNECTION);
                    Log.w(TAG, "onHandleIntent() No network connection available.");
                }
            }
        }
    }

    private void logRequestId(Bundle bundle) {
        if (EXTRA_DEBUGGING) {
            int requestId = bundle.getInt(HttpConstants.KEY_REQUEST_ID, -1);
            String packageName = HttpUtils.getCallingPackageName(this);
            packageName = packageName == null ? "" : " from \"" + packageName + "\"";
            if (requestId >= requestNames.length) {
                Log.e(TAG, "Unknown request ID " + requestId + " passed" + packageName);
            } else if (requestId < 0) {
                Log.e(TAG, "No request ID was provided" + packageName);
            } else {
                Log.d(TAG, "Handling " + requestNames[requestId] + " (" + requestId + ")" + packageName);
            }
        }
    }

    private boolean handleErrorResponse(InputStream errorStream, final Bundle bundleRequest, int errorCode) {
        boolean needsRetry = false;
        Bundle errorBundle = new Bundle();
        errorBundle.putInt(HttpConstants.RESPONSE_CODE, errorCode);
        errorBundle.putBoolean(HttpConstants.RESPONSE_SUCCESS, false);

        if (errorStream != null) {
            try {
                InputStreamReader isr = new InputStreamReader(errorStream);
                BufferedReader rd = new BufferedReader(isr);
                String line;
                StringBuilder response = new StringBuilder();
                while ((line = rd.readLine()) != null) {
                    response.append(line);
                    response.append('\r');
                }
                isr.close();
                rd.close();

                Log.w(TAG, "handleErrorResponse() Error code " + errorCode + " received: \"" + response.toString() + "\"");
                if (errorCode == 550
                    && (response.toString().contains("504") || response.toString().contains("502"))) {
                    Log.e(TAG, "HTTP-ERROR: Detected \"504 Gateway Time-out\", retrying");
                    needsRetry = true;
                } else if (response.toString().trim().startsWith("{")) {
                    JSONObject jsonObject = new JSONObject(response.toString());
                    int responseCode = jsonObject.getInt(HttpConstants.RESPONSE_CODE);
                    errorBundle.putString(HttpConstants.RESPONSE_RESULT, String.valueOf(responseCode));
                    errorBundle.putString(HttpConstants.RESPONSE_MESSAGE, jsonObject.getString(HttpConstants.RESPONSE_MESSAGE));
                    for (int i = 0; i < HttpConstants.RESPONSE_ERROR_CODE.length - 1; i++) {
                        if (HttpConstants.RESPONSE_ERROR_CODE[i] != responseCode) {
                            continue;
                        }
                        Log.d(TAG, "HTTP-RESPONSE-CODE :" + responseCode);
                    }
                }
            } catch (Exception e) {
                Log.w(TAG, e.getClass().getSimpleName() + " received while handling error response: " + Log.getStackTraceString(e));
                errorBundle.putString(HttpConstants.RESPONSE_ERROR,
                    HttpConstants.ERROR_CONNECTION);
            }
        } else {
            Log.w(TAG, "handleErrorResponse() Error code " + errorCode + " received with no error stream");
        }
        if (!needsRetry) {
            sendReplyMessage(bundleRequest, errorBundle);
        }
        return needsRetry;
    }

    private void onHandleRequest(Bundle bundle) {
        logRequestId(bundle);

        String requestURL = HttpConstants.REQUEST_URL;
        int REQUEST_KEY = bundle.getInt(HttpConstants.KEY_REQUEST_ID, -1);
        switch (REQUEST_KEY) {
            case -1:
                handleErrorResponse(null, bundle, -1);
                return;

            case HttpConstants.ACCOUNT_INFO_REQUEST:
                requestURL = requestURL.concat(HttpConstants.ACCOUNT_INFO_SERVICE);
                break;

            case HttpConstants.CONVERT_FROM_REQUEST:
                requestURL = requestURL.concat(HttpConstants.CONVERT_FROM_SERVICE);
                requestURL = constructRequestURL(requestURL, bundle);
                break;

            case HttpConstants.CURRENCIES_JSON_REQUEST:
                requestURL = requestURL.concat(HttpConstants.CURRENCIES_JSON_SERVICE);
                requestURL = constructRequestURL(requestURL, bundle);
                break;

            case HttpConstants.CONVERT_FROM_JSON_REQUEST:
                requestURL = requestURL.concat(HttpConstants.CONVERT_FROM_JSON_SERVICE);
                requestURL = constructRequestURL(requestURL, bundle);
                break;

            case HttpConstants.CONVERT_TO_JSON_REQUEST:
                requestURL = requestURL.concat(HttpConstants.CONVERT_TO_JSON_SERVICE);
                requestURL = constructRequestURL(requestURL, bundle);
                break;

            case HttpConstants.HISTORIC_RATE_JSON_REQUEST:
                requestURL = requestURL.concat(HttpConstants.HISTORIC_RATE_JSON_SERVICE);
                requestURL = constructRequestURL(requestURL, bundle);
                break;

            case HttpConstants.HISTORIC_RATE_PERIOD_REQUEST:
                requestURL = requestURL.concat(HttpConstants.HISTORIC_RATE_PERIOD_SERVICE);
                requestURL = constructRequestURL(requestURL, bundle);
                break;
        }

        int connectionTimeout = CONNECTION_TIMEOUT;
        int readTimeout = CONNECTION_TIMEOUT;
        int retriesToGo = 3;
        boolean retry;

        do {
            retry = false;
            try {
                if (EXTRA_DEBUGGING) Log.d(TAG, "Querying " + requestURL);

                URL url = new URL(requestURL);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setConnectTimeout(connectionTimeout);
                urlConnection.setReadTimeout(readTimeout);
                urlConnection.setDoInput(true);
//                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("GET");

                urlConnection.setRequestProperty("Authorization", "Basic " + getAuthString(bundle));
//                urlConnection.setRequestProperty("Authorization", "Basic "
//                        + "dGN0bW9iaWxlaW50ZXJuYXRpb25hbGxpbWl0ZWQxMTY4ODI2NzQ6cDFpa2IyYmx2cmsxYmUzZWRvODU4aXUxZm0=");

                long starttime = System.currentTimeMillis();
                int errorCode = urlConnection.getResponseCode();
                long stoptime = System.currentTimeMillis();


                if (EXTRA_DEBUGGING) {
                    Log.d(TAG, "Received HTTP code " + errorCode + " after " + Long.toString(stoptime - starttime) + " milliseconds");
                }
                if (errorCode == HttpURLConnection.HTTP_OK) {
                    InputStream is = urlConnection.getInputStream();
                    InputStreamReader isr = new InputStreamReader(is);
                    BufferedReader rd = new BufferedReader(isr);
                    StringBuilder response = new StringBuilder();
                    char[] buffer = new char[512];
                    int bufsize;
                    while ((bufsize = rd.read(buffer)) > 0) {
                        if (EXTRA_DEBUGGING)
//                            Log.d(TAG, "buffer (" + bufsize + " bytes): " + new String(buffer, 0, bufsize)); // MODIFIED by qiong.liu1, 2017-03-23,BUG-3621966
                        response.append(buffer, 0, bufsize);
                    }
                    isr.close();
                    rd.close();

                    String responseString = response.toString();
                    if (EXTRA_DEBUGGING)
                        Log.d(TAG, "Read a total of " + responseString.length() + " bytes");
                    try {
                        handleJSONResponse(new JSONObject(responseString), bundle);
                    } catch (JSONException e) {
                        Log.w(TAG, "Error parsing JSON response " + e); // MODIFIED by qiong.liu1, 2017-03-23,BUG-3621966
                        handleException(bundle, HttpConstants.ERROR_UNKNOWN);
                    }
                } else {
                    InputStream es = urlConnection.getErrorStream();
                    retry = handleErrorResponse(es, bundle, errorCode);
                    Log.d(TAG, "retry " + retry); // MODIFIED by qiong.liu1, 2017-03-20,BUG-3621966
                }
            } catch (SocketTimeoutException e) {
                Log.e(TAG, "Operation timed out with connectionTimeout=" + connectionTimeout + ", readTimeout=" + readTimeout);
                connectionTimeout += 15000;
                readTimeout += 15000;
                retry = true;
            } catch (HttpRetryException e) {
                retry = true;
            } catch (IOException e) {
                Log.e(TAG, e.getClass().getSimpleName() + " received while handling request: " + Log.getStackTraceString(e));
                handleException(bundle, HttpConstants.ERROR_CONNECTION);
            }
            if (retry && retriesToGo-- <= 0) {
                Log.w(TAG, "Failed to get data after 3 retries");
                handleException(bundle, HttpConstants.ERROR_CONNECTION);
                retry = false;
            }
            if (retry) {
                Log.d(TAG, "Retrying...");
            }
        } while (retry);
    }

    private String constructRequestURL(String requestURL, Bundle requestBundle) {
        StringBuilder requestBuilder;
        switch (requestBundle.getInt(HttpConstants.KEY_REQUEST_ID, -1)) {
            case -1:
                return null;
            case HttpConstants.CONVERT_FROM_REQUEST:
                requestBuilder = new StringBuilder();
                requestBuilder.append("?to=").append(requestBundle.getString(HttpConstants.KEY_TO));
                return requestURL.concat(requestBuilder.toString());
            case HttpConstants.CURRENCIES_JSON_REQUEST:
                requestBuilder = new StringBuilder();
                requestBuilder.append("?obsolete=").append(requestBundle.getString(HttpConstants.KEY_OBSOLETE));
                return requestURL.concat(requestBuilder.toString());
            case HttpConstants.CONVERT_FROM_JSON_REQUEST:
            case HttpConstants.CONVERT_TO_JSON_REQUEST:
                requestBuilder = new StringBuilder();
                /* MODIFIED-BEGIN by qiong.liu1, 2017-03-20,BUG-3621966*/
                requestBuilder.append("?from=").append(requestBundle.getString(HttpConstants.KEY_FROM)).
                    append("&to=").append(requestBundle.getString(HttpConstants.KEY_TO))
                    .append("&amount=")
                    .append(requestBundle.getString(HttpConstants.KEY_AMOUNT));
                    /* MODIFIED-END by qiong.liu1,BUG-3621966*/
                return requestURL.concat(requestBuilder.toString());
            case HttpConstants.HISTORIC_RATE_JSON_REQUEST:
                requestBuilder = new StringBuilder();
                /* MODIFIED-BEGIN by qiong.liu1, 2017-03-23,BUG-3621966*/
                requestBuilder.append("?from=").append(requestBundle.getString(HttpConstants.KEY_FROM))
                        .append("&date=").append(requestBundle.getString(HttpConstants.KEY_DATE))
                    .append("&to=").append(requestBundle.getString(HttpConstants.KEY_TO));
                return requestURL.concat(requestBuilder.toString());
            case HttpConstants.HISTORIC_RATE_PERIOD_REQUEST:
                requestBuilder = new StringBuilder();
                requestBuilder.append("?from=").append(requestBundle.getString(HttpConstants.KEY_FROM))
                        .append("&to=")
                        .append(requestBundle.getString(HttpConstants.KEY_TO))
                        .append("&start_timestamp=")
                        .append(requestBundle.getString(HttpConstants.KEY_START_TIMES_TAMP))
                        .append("&end_timestamp=")
                        .append(requestBundle.getString(HttpConstants.KEY_END_TIMES_TAMP))
                        .append("&per_page=")
                        .append(requestBundle.getString(HttpConstants.KEY_PER_PAGE_MAX));
                        /* MODIFIED-END by qiong.liu1,BUG-3621966*/
                return requestURL.concat(requestBuilder.toString());
            default:
                return null;
        }
    }



    private void handleException(final Bundle requestBundle, String errorResponse) {
        Bundle errorBundle = new Bundle();
        errorBundle.putString(HttpConstants.RESPONSE_ERROR, errorResponse);
        errorBundle.putBoolean(HttpConstants.RESPONSE_SUCCESS, false);
        sendReplyMessage(requestBundle, errorBundle);
    }

    private String getAuthString(final Bundle bundleRequest) {
        switch (bundleRequest.getInt(HttpConstants.KEY_REQUEST_ID)) {
            case HttpConstants.ACCOUNT_INFO_REQUEST:
            case HttpConstants.CONVERT_FROM_REQUEST:
            case HttpConstants.CURRENCIES_JSON_REQUEST:
            case HttpConstants.CONVERT_FROM_JSON_REQUEST:
            case HttpConstants.CONVERT_TO_JSON_REQUEST:
            case HttpConstants.HISTORIC_RATE_JSON_REQUEST:
            case HttpConstants.HISTORIC_RATE_PERIOD_REQUEST:
                String account_id = HttpConstants.REQUEST_ACCOUNT_ID;
                String app_key = HttpConstants.REQUEST_APP_KEY;
                return getAuthToken(account_id, app_key);
        }
        return null;
    }

    /**
     * Get Auth token to as request header
     **/
    private String getAuthToken(String accountID, String appKey) {
        String token = null;
        if (!TextUtils.isEmpty(accountID) && !TextUtils.isEmpty(appKey)) {
            String outString = accountID.trim() + ":" + appKey.trim();
            try {
                /* MODIFIED-BEGIN by qiong.liu1, 2017-03-20,BUG-3621966*/
                token = Base64.encodeToString(outString.getBytes(ENCODE_CHARSET), Base64.NO_WRAP);
            } catch (UnsupportedEncodingException e) {
                token = Base64.encodeToString(outString.getBytes(), Base64.NO_WRAP);
                Log.d(TAG, "getAuthToken e  " + e);
                /* MODIFIED-END by qiong.liu1,BUG-3621966*/
            }
        }
        if (EXTRA_DEBUGGING) Log.d(TAG, token == null ? "Empty token" : "userToken=" + token);
        return token;
    }

    private void handleJSONResponse(JSONObject jsonObject, final Bundle bundleRequest)
        throws JSONException {
        Bundle responseBundle = null;

        int requestID = bundleRequest.getInt(HttpConstants.KEY_REQUEST_ID);
        Log.d(TAG," handleJSONResponse  requestID " + requestID + " : "); // MODIFIED by qiong.liu1, 2017-03-20,BUG-3621966
        switch (requestID) {
            case HttpConstants.ACCOUNT_INFO_REQUEST:
                //TODO There will add logic of parse data of response.
                break;
            case HttpConstants.CONVERT_FROM_REQUEST:
                //TODO There will add logic of parse data of response.
                break;
            case HttpConstants.CURRENCIES_JSON_REQUEST:
                //TODO There will add logic of parse data of response.
                break;
            case HttpConstants.CONVERT_FROM_JSON_REQUEST:
                //Save all converter from USD amount = 1
                String from = jsonObject.optString("from");
                String timestamp = jsonObject.optString("timestamp");
                int amount = jsonObject.optInt("amount");
                JSONArray jsonArray = jsonObject.optJSONArray("to");
                SharedPreferences sp = getSharedPreferences(SHARED_PREFERENCES_CONVERT_RECORDER, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                String updateTime = sp.getString(WHEN_UPDATE, "");
                if (updateTime != timestamp) {
                    editor.putString(WHEN_UPDATE, timestamp);
                    editor.clear();
                }else {
                    Log.d(TAG," the same update, return" );
                    return;
                }
                if (from.equals("USD") && amount == 1){
                    saveJSArrayFromUSD(jsonArray, timestamp);
                }else {
                    Log.d(TAG," not from USD or amount != 1 " + from + " : " + amount);
                }
                break;
            /* MODIFIED-BEGIN by qiong.liu1, 2017-03-23,BUG-3621966*/
            case HttpConstants.CONVERT_TO_JSON_REQUEST:
                //TODO There will add logic of parse data of response.
                break;
            case HttpConstants.HISTORIC_RATE_JSON_REQUEST:
                //TODO There will add logic of parse data of response.
                break;
            case HttpConstants.HISTORIC_RATE_PERIOD_REQUEST:
                JSONObject jsonObjectAll = jsonObject.optJSONObject("to");
                JSONArray jsonArrayAll = jsonObjectAll.getJSONArray(bundleRequest.getString(HttpConstants.KEY_TO));

                List sevenDaysRates = new ArrayList();
                List oneMonthRates = new ArrayList();
                List sixMonthRates = new ArrayList();
                List oneYearRates = new ArrayList();
                Log.d(TAG, " jsonObjectTo.length(): " + jsonArrayAll.length());
                for (int i = 0; i < jsonArrayAll.length(); i++) {
                    JSONObject jsObject = jsonArrayAll.optJSONObject(i);
                    String value = jsObject.optString("mid");
                    //get 7days rates
                    /* MODIFIED-BEGIN by qiong.liu1, 2017-03-24,BUG-3621966*/
                    if (i < 12) {
                        sevenDaysRates.add(Float.valueOf(value));
                    }
                    //get 1M rates
                    if (i < 36 &&i % 3 == 0) {
                    /* MODIFIED-END by qiong.liu1,BUG-3621966*/
                        oneMonthRates.add(Float.valueOf(value));
                    }
                    //get 6M rates
                    if (i < 180 && i % 15 == 0) {
                        sixMonthRates.add(Float.valueOf(value));
                    }
                    //get 1Y rates
                    if (i <= 366 && i % 30 == 0) {
                        oneYearRates.add(Float.valueOf(value));
                    }

                }
                Log.d(TAG, " sevenDaysRates.size(): " + sevenDaysRates.size());
                Log.d(TAG, " oneMonthRates.size(): " + oneMonthRates.size());
                Log.d(TAG, " sixMonthRates.size(): " + sixMonthRates.size());
                Log.d(TAG, " oneYearRates.size(): " + oneYearRates.size());
                List<List> allRates = new ArrayList<>();
                allRates.add(sevenDaysRates);
                allRates.add(oneMonthRates);
                allRates.add(sixMonthRates);
                allRates.add(oneYearRates);
                if (mChangeGrapViewListener != null) {
                    mChangeGrapViewListener.onViewRatesChanged(allRates);
                }
                /* MODIFIED-END by qiong.liu1,BUG-3621966*/
                break;
        }
    }

    /* MODIFIED-BEGIN by qiong.liu1, 2017-03-20,BUG-3621966*/
    private void saveJSArrayFromUSD(JSONArray jsonArray, String timestamp) {
        ContentValues contentValues = new ContentValues();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsObject = jsonArray.optJSONObject(i);
            String quotecurrency = jsObject.optString("quotecurrency");
            String value = jsObject.optString("mid");
            contentValues.put(HistoryContentUri.CURRENCY_TIME, timestamp);
            contentValues.put(HistoryContentUri.CURRENCY_TO_USD, value);
            getContentResolver().update(HistoryContentUri.CONTENT_RECORD_URI,
                    contentValues,
                    HistoryContentUri.CURRENCY_SHORTER_FORM + " = ? ",
                    new String[]{quotecurrency});
        }
        /* MODIFIED-BEGIN by qiong.liu1, 2017-03-24,BUG-3621966*/
        if (mChangeGrapViewListener != null) {
            mChangeGrapViewListener.onDbHadInit();
        }
        /* MODIFIED-END by qiong.liu1,BUG-3621966*/
    }
    /* MODIFIED-END by qiong.liu1,BUG-3621966*/

    private void sendReplyMessage(Bundle requestBundle, Bundle responseBundle) {
        responseBundle.putInt(HttpConstants.KEY_REQUEST_ID, requestBundle.getInt(HttpConstants.KEY_REQUEST_ID));
        Messenger messenger = (Messenger) requestBundle.get(HttpConstants.MESSENGER);
        if (messenger != null) {
            Message msg = Message.obtain();
            msg.setData(responseBundle);
            try {
                messenger.send(msg);
            } catch (RemoteException e) {
                Log.e(TAG, Log.getStackTraceString(e));
            }
        }
    }

    /* MODIFIED-BEGIN by qiong.liu1, 2017-03-23,BUG-3621966*/
    public void setChangeGraphViewListener(ChangeGraphViewListener listener){
        mChangeGrapViewListener = listener;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent service) {
        return new MyBinder();
    }

    public class MyBinder extends Binder {
        public HttpRequestService getService(){
            return HttpRequestService.this;
        }
    }
    /* MODIFIED-END by qiong.liu1,BUG-3621966*/
}
