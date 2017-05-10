package com.tct.calculator.data;


import android.content.Context;
import android.util.Log;

import com.tct.calculator.utils.SharedPreferencesHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtils {
    private static final String TAG = "FileUtils";

    private static final String FILE_NAME = "history.db";

    private static final String FILE_PATH = "/data/data/com.tct.calculator/databases/";

    private static SharedPreferencesHelper mSharedPreferencesHelper;

    private static FileUtils mFileUtils;

    private Context mContext;

    private boolean mIsFirstRun;

    private FileUtils(Context context) {
        this.mContext = context;
        mSharedPreferencesHelper = SharedPreferencesHelper.getInstance(mContext);
        mIsFirstRun = mSharedPreferencesHelper.getApplicationFirstRun();
    }

    public static FileUtils getInstance(Context context) {
        if (null == mFileUtils) {
            mFileUtils = new FileUtils(context);
        }
        return mFileUtils;
    }

    /**
     * Copy db to data/data/com.tct.calculator/databases/
     */
    public void copyDBFromAssets() {
        if (!mIsFirstRun) {
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                InputStream in = null;
                OutputStream out = null;
                try {
                    in = mContext.getAssets().open(FILE_NAME);
                    String newFileName = FILE_PATH + "/" + FILE_NAME;
                    File file = new File(FILE_PATH);
                    if (!file.exists()) {
                        file.mkdir();
                    }
                    out = new FileOutputStream(newFileName);

                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = in.read(buffer)) != -1) {
                        out.write(buffer, 0, length);
                    }
                    out.flush();
                    Log.e(TAG, "copy file success... ");
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "copy file has an error : ", e);
                } finally {
                    try {
                        in.close();
                        out.close();
                        in = null;
                        out = null;
                        if (mSharedPreferencesHelper != null) {
                            mSharedPreferencesHelper.saveApplicationFirstRun(false);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e(TAG, "close inputStream or outputStream has an error : ", e);
                    }
                }
            }
        }).start();
    }

}
