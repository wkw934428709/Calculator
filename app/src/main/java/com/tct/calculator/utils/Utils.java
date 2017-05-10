package com.tct.calculator.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.DisplayMetrics;
import android.content.res.Resources;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;
import android.view.Display;
import android.view.SoundEffectConstants;
import android.provider.Settings;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List; // MODIFIED by qiong.liu1, 2017-03-23,BUG-3621966
import java.util.Locale;


public class Utils {

    private static final String TAG = "Utils";
    private static final String PATH = "/custpack/plf/JrdCalculator/";
    private static final String FILE = "isdm_calculator_default.xml";
    private static final int PORT_SCREEN_KEY_BOARD_AVERAGE_VALUE = 5;
    private static final int LAND_SCREEN_KEY_BOARD_AVERAGE_VALUE = 9;

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

    /**
     * Get current screen width.
     *
     * @param context
     * @return screen width
     */
    private static int getScreenWidth(Activity context) {
        try {
            DisplayMetrics dm = new DisplayMetrics();
            context.getWindowManager().getDefaultDisplay().getMetrics(dm);
            return dm.widthPixels;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
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

    /**
     * Set sound for wheel view.
     * <1.><We used the sound of press key to as the wheel view's sound./>
     * <2.><Replace the sound source if TCL provide the file of sound or system type. />
     *
     * @param activity context
     * @param soundView view
     */
    public static void setSoundForWheelView(Activity activity, View soundView) {
        /* MODIFIED-BEGIN by qiong.liu1, 2017-04-01,BUG-3621966*/
        try {
            int soundValue = Settings.System.getInt(activity.getContentResolver(), Settings.System.SOUND_EFFECTS_ENABLED, -1);
            Log.d(TAG, "Utils setSoundForWheelView soundValue : " + soundValue);
        }catch (Exception e){
            Log.d(TAG, "Utils Exception : " + e);
        }
        /* MODIFIED-END by qiong.liu1,BUG-3621966*/
        if (soundView != null) {
            soundView.playSoundEffect(SoundEffectConstants.CLICK);
        }
    }

    /**
     * the method will effect when the screen is in multi-screen
     *
     * @param activity
     * @return return -1: the app screen is 1/4 total screen height to 1/2 total screen height
     *         return 0: the app screen is 1/2 total screen height to 2/3 total screen height
     *         return 1: the app screen is 2/3 total screen height to all screen height
     */
    public static int getMultiScreenType(Activity activity) {
        WindowManager wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        Display display = wm.getDefaultDisplay();
        wm.getDefaultDisplay().getRealMetrics(displayMetrics);
        int screenHeight = displayMetrics.heightPixels;
        int height = display.getHeight();
        int navigationHeight = getNavigationBarHeight(activity);
        int screenShowHeight = screenHeight - navigationHeight;
        if (height > screenShowHeight * 2 / 3) {
            return 1;
        } else if (height > screenShowHeight / 2 && height <= screenShowHeight * 2 / 3) {
            return 0;
        } else if (height >= screenShowHeight / 4 && height < screenShowHeight / 2) {
            return -1;
        }
        return 1;
    }

    /**
     * get the navigation bar height
     *
     * @param activity
     * @return height
     */
    public static int getNavigationBarHeight(Activity activity) {
        Resources resources = activity.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        return resources.getDimensionPixelSize(resourceId);
    }


    /**
     *   get the max value of a Array int
     */

    public static float getArrayMax(float[] intargs){
        if(intargs == null || intargs.length<=1 ){
            return (intargs==null)?0:intargs[0];
        }
        int length = intargs.length;
        float max = intargs[0];
        for(int i = 0; i<length-1;i++){
            if(max<intargs[i+1]){
                max = intargs[i+1];
            }
        }
        return max;
    }


    /**
     *   get the min value of a Array Integer
     */
    public static float getArrayMin(float[] intargs){
        if(intargs == null || intargs.length<=1 ){
            return (intargs==null)?0:intargs[0];
        }
        int length = intargs.length;
        float min = intargs[0];
        for(int i = 0; i<length-1;i++){
            if(min>intargs[i]){
                min = intargs[i];
            }
        }
        return min;
    }

    /* MODIFIED-BEGIN by qiong.liu1, 2017-03-23,BUG-3621966*/
    /**
     *   get Array rates
     */
    public static float[] getFloatArray(List rates){
        int size = rates.size();
        float[] ratesArray = new float[size];
        for (int i = 0; i < size; i++) {
            ratesArray[i] = (float) rates.get(i);
        }
        return ratesArray;
    }
    /* MODIFIED-END by qiong.liu1,BUG-3621966*/

}
