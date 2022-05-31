package com.example.aidlsecond;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;

import java.util.ArrayList;
import java.util.List;

public class MyService extends Service {

    private final ArrayList<Person> mPersons = new ArrayList<>();

    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /**
     * 创建存根，实现add方法
     */
    private final Binder mBinder = new IMyAidlInterface.Stub() {
        @Override
        public List<Person> add(Person person) throws RemoteException {
            mPersons.add(person);
            return mPersons;
        }
    };
}
