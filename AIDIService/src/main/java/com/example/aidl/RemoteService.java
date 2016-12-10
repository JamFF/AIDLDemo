package com.example.aidl;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * 描述：Service代码，供Client调用，注意清单文件一定要添加android:exported="true"
 * 作者：sam.fu
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

    private IBinder mBinder = new IMyAidlInterface.Stub() {
        @Override
        public int add(int num1, int num2) throws RemoteException {

            Log.d(TAG, "num1 + num2 = " + num1 + "+" + num2);

            return num1 + num2;
        }
    };
}
