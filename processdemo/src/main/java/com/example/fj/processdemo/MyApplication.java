package com.example.fj.processdemo;

import android.app.ActivityManager;
import android.app.Application;
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
        Log.d(TAG, "[MyApplication onCreate] application start, process name:" + getProcessName());
    }

    private String getProcessName() {
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
