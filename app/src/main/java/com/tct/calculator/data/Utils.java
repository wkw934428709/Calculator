/* Copyright (C) 2017 Tcl Corporation Limited */

package com.tct.calculator.data;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import android.content.SharedPreferences;

public class Utils {
    private static final String TAG = "Utils";
    private static final String PATH = "/custpack/plf/JrdCalculator/";
    private static final String FILE = "isdm_calculator_default.xml";
    private static final String TRANSLATE_STATE_FILE = "translateStateFile";
    private static final String KEY_TRANSLATE_STATE = "key_currentState";

    /**
     * get isdm value which is bool
     *
     * @param mContext
     * @param def_name : the name of isdmID
     * @return
     */
    public static boolean getBoolean(Context mContext, String def_name) {
        Resources res = mContext.getResources();
        int id = res.getIdentifier(def_name, "bool", mContext.getPackageName());
        // get the native isdmID value
        boolean result = mContext.getResources().getBoolean(id);
        try {
            String bool_frameworks = getISDMString(new File(PATH + FILE), def_name, "bool");
            if (null != bool_frameworks) {
                result = Boolean.parseBoolean(bool_frameworks);
            }
        } catch (XmlPullParserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }

    /**
     * get isdm value which is bool
     *
     * @param mContext
     * @param def_name : the name of isdmID
     * @return
     */
    public static String getString(Context mContext, String def_name) {
        Resources res = mContext.getResources();
        int id = res.getIdentifier(def_name, "string", mContext.getPackageName());
        // get the native isdmID value
        String result = mContext.getResources().getString(id);
        try {
            String string_frameworks = getISDMString(new File(PATH + FILE), def_name, "string");
            if (null != string_frameworks) {
                result = string_frameworks;
            }
        } catch (XmlPullParserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }

    /**
     * parser the XML file to get the isdmID value
     *
     * @param file : xml file
     * @param name : isdmID
     * @param type : isdmID type like bool and string
     * @return
     * @throws XmlPullParserException
     * @throws IOException
     */
    private static String getISDMString(File file, String name, String type)
            throws XmlPullParserException,
            IOException {
        if (!file.exists() || null == file) {
            Log.e(TAG, "file not exist : " + file);
            return null;
        }
        String result = null;
        InputStream inputStream = new FileInputStream(file);
        XmlPullParser xmlParser = Xml.newPullParser();
        xmlParser.setInput(inputStream, "utf-8");

        int evtType = xmlParser.getEventType();
        boolean query_end = false;
        while (evtType != XmlPullParser.END_DOCUMENT && !query_end) {

            switch (evtType) {
                case XmlPullParser.START_TAG:

                    String start_tag = xmlParser.getAttributeValue(null, "name");
                    String start_type = xmlParser.getName();
                    if (null != start_tag && type.equals(start_type) && start_tag.equals(name)) {
                        result = xmlParser.nextText();
                        query_end = true;
                    }
                    break;

                case XmlPullParser.END_TAG:

                    break;

                default:
                    break;
            }
            // move to next node if not tail
            evtType = xmlParser.next();
        }
        inputStream.close();
        return result;
    }

    /*MODIFIED-BEGIN by xing.zhao, 2016-04-19,BUG-1926540*/
    public static String getLanguageType() {
        String langcode = null;
        try {
            langcode = Locale.getDefault().getLanguage() + "-" + Locale.getDefault().getCountry().toLowerCase();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        if (langcode != null) {
            Log.i(TAG, langcode);
            return langcode;
        } else {
            return "en";
        }
    }
    /*MODIFIED-END by xing.zhao,BUG-1926540*/
    /**
     *  Get the SIM card parameters
     */

    private static String getSimOperator(Context c){
        TelephonyManager tm = (TelephonyManager)c.getSystemService(Context.TELEPHONY_SERVICE);
        try{
            return  tm.getSimOperator();
        }catch (Exception e){
            Log.i(TAG, "getSimOperator failse");
        }
        return null;
    }

    /**
     *   By judging whether the area code is null
     */
    private static boolean isOperatorEmpty(String operator){

        if (TextUtils.isEmpty(operator)||operator.toLowerCase(Locale.US).contains("null")) {
            return true;
        }

        return  false;
    }

    /**
     *   By judging whether the area code for China region
     */

    public static boolean isChinaSimCard(Context c) {
        String mcc = getSimOperator(c);
        if(TextUtils.isEmpty(mcc)){
            return  false;
        }

        if (isOperatorEmpty(mcc)) {
            Log.i(TAG,"getSimOperator is empty");
            return false;
        } else {
            return mcc.startsWith("460");
        }
    }

    public static boolean isPackageExist(Context context, String packageName) {
        try {
            context.getPackageManager().getApplicationInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            Log.d(TAG, packageName + " is not installed");
            return false;
        }

    }

    public static void saveTranslateState(Context context, int isNeedExpend) {
        SharedPreferences preferences = context.getSharedPreferences(TRANSLATE_STATE_FILE, context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(KEY_TRANSLATE_STATE, isNeedExpend);
        editor.commit();
    }

    public static int getTranslateState(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(TRANSLATE_STATE_FILE, context.MODE_PRIVATE);
        return preferences.getInt(KEY_TRANSLATE_STATE, -1);
    }
}
