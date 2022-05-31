package com.example.fj.processdemo;

import android.app.ActivityManager;
import android.app.Application;
import android.os.Build;
import android.util.Log;

/**
 * 描述：
 * 作者：傅健
 * 创建时间：2017/2/28 20:26
 */

public class MyApplication extends Application {

    private static final String TAG = "MyApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        final String processName;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            processName = getProcessName();
        } else {
            processName = getMyProcessName();
        }
        Log.d(TAG, "[MyApplication onCreate] application start, process name:" + processName);
    }

    private String getMyProcessName() {
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo :
                activityManager.getRunningAppProcesses()) {
            if (runningAppProcessInfo.pid == android.os.Process.myPid()) {
                return runningAppProcessInfo.processName;
            }
        }
        return null;
    }
}
