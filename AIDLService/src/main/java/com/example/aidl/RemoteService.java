package com.example.aidl;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import androidx.annotation.Nullable;

import android.util.Log;

import java.util.List;

/**
 * 描述：Service代码，供Client调用，注意清单文件一定要添加android:exported="true"
 * 作者：JamFF
 * 创建时间：2016/7/16 17:19
 */
public class RemoteService extends Service {

    private static final String TAG = "RemoteService";

    /**
     * 当客户端绑定到该服务时，会执行
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private final IBinder mBinder = new IMyAidlInterface.Stub() {
        @Override
        public int add(int num1, int num2) throws RemoteException {

            Log.d(TAG, "num1 + num2 = " + num1 + "+" + num2);

            return num1 + num2;
        }
    };

    /**
     * 测试返回值类型
     */
    private IBinder mTestBinder = new IMyAidlInterfaceTest.Stub() {
        @Override
        public List<String> basicTypes(byte aByte, int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, char aChar, String aString, List<String> aList) throws RemoteException {
            return null;
        }
    };
}
