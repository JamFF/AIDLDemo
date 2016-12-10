package com.example.aidiclient;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.example.aidl.IMyAidlInterface;

/**
 * 描述：Client，调用Service中的AIDL
 * 作者：sam.fu
 * 创建时间：2016/12/10 11:33
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText et_num1, et_num2, et_res;

    private IMyAidlInterface mIMyAidlInterface;

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {

            // iBinder就是Service中onBind返回的mBinder，mBinder就是IMyAidlInterface
            // 可以强转为IMyAidlInterface，但是提供了一个asInterface方法得到
            mIMyAidlInterface = IMyAidlInterface.Stub.asInterface(iBinder);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

            // 释放资源
            mIMyAidlInterface = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        bindService();
    }

    private void initView() {
        et_num1 = (EditText) findViewById(R.id.et_num1);
        et_num2 = (EditText) findViewById(R.id.et_num2);
        et_res = (EditText) findViewById(R.id.et_res);
        findViewById(R.id.bt_add).setOnClickListener(this);
    }

    /**
     * 绑定服务
     */
    private void bindService() {

        // 5.0以后不能使用隐式绑定服务
        Intent intent = new Intent();
        // 传入包名，完整类名
        intent.setComponent(new ComponentName("com.example.aidl", "com.example.aidl.RemoteService"));

        bindService(intent, conn, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.bt_add:
                int num1 = Integer.parseInt(et_num1.getText().toString());
                int num2 = Integer.parseInt(et_num2.getText().toString());

                try {
                    int res = mIMyAidlInterface.add(num1, num2);
                    // 第二个例子，这里需要用ArrayList接收List的返回值
                    // ArrayList<String> arrayList = mIMyAidlInterfaceTest.basicTypes(...);
                    et_res.setText(res + "");
                } catch (RemoteException e) {
                    et_res.setText("错误了");
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(conn);
    }
}
