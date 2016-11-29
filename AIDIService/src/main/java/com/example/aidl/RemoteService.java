package com.example.aidl;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * 描述：AIDL服务端代码
 * 作者：傅健
 * 创建时间：2016/7/16 17:19
 */
public class RemoteService extends Service {

    /**
     * 当客户端绑定到该服务时，会执行
     *
     * @param intent
     * @return
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private IBinder mBinder = new IMyAidlInterface.Stub() {
        @Override
        public int add(int num1, int num2) throws RemoteException {

            Log.d("RemoteService", "num1+num2=" + num1 + "+" + num2);

            return num1 + num2;
        }
    };
}
