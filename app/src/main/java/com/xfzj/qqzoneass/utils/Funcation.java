package com.xfzj.qqzoneass.utils;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;

import com.xfzj.qqzoneass.activity.MyApp;
import com.xfzj.qqzoneass.operation.BmobUtils;

/**
 * Created by zj on 2015/8/23.
 */
public class Funcation {
    private static final String TAG = "Funcation";
    private static final boolean DEBUG = false;
    private static final int DEFAULT_FREE_ADCOUNT = 30;

    /**
     * 初始化BmobUtil类，初始化myapp的一些值
     *
     * @param application
     * @param number
     */
    public static void init(Application application, String number) {
        if (TextUtils.isEmpty(number)) {
            return;
        }
        BmobUtils.getInstance(application).getMemberInfo(application.getApplicationContext(), number);
        if (DEBUG) {
            Log.i(TAG, "init :lineNumber:(29) ");
        }
    }

    /**
     * 是否能够启用这个个功能
     *
     * @param application
     * @param time
     * @return
     */
    public static boolean isOK(Application application, long time) {
        MyApp myApp = (MyApp) application;
        boolean b = (myApp.adCount >= DEFAULT_FREE_ADCOUNT || isTimeOk(time,myApp.availablrTime));
        if (DEBUG) {
            Log.i(TAG, "isOK :lineNumber:(42) b=" + b+"  myApp.adCount="+myApp.adCount);
        }
        return b;
    }

    private static boolean isTimeOk(long time,long availablrTime) {
        return time < availablrTime ? true : false;
    }
}
