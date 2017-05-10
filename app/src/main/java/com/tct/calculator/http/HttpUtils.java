/* Copyright (C) 2016 Tcl Corporation Limited */
package com.tct.calculator.http;

import android.content.Context;
import android.os.Binder;

/**
 * Created by ts on 10/19/16.
 */
public class HttpUtils {


    /**
     * Retrieves the name of the package that called the current service operation. Note that,
     * while this works for such things as Content Providers, it doesn't always work for straight
     * services.
     *
     * @param context The context used to obtain the ActivityManager system service.
     * @return The name of the calling package, or {@code null} if not available.
     */
    public static String getCallingPackageName(Context context) {
        return context.getPackageManager().getNameForUid(Binder.getCallingUid());
    }
}
