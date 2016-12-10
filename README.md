# AIDLDemo

Android Service中AIDL的简单应用

##简单的调用步骤

1.建立Service

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

2.新建aidl文件夹，写aidl中Service端代码

	package com.example.aidl;
	
	// Declare any non-default types here with import statements
	
	interface IMyAidlInterface {
	
	    // 计算两个数的和
	    int add(int num1, int num2);
	}

3.编译后会在`build/generated/source/aidl/debug/`下生成对应报名下的java文件

4.新建Client端，将Service中的aidl文件夹粘贴过来

5.bindService，得到aidl远程接口，调用其中add方法

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

##自定义类型



##注意事项

1.aidl的包名无需和项目包名一致，但必须aidl的Service端和Client端中的包名及文件一模一样。

2.Service允许替他程序start或者bind时，需要清单文件中添加`android:exported="true"`

3.aidl可传输的基本数据类型中，不包括`short`；
List、Map中的类型也必须是可支持的基本数据类型，同样不包括`short`；
如果传输List、Map需要在前面书写`in`、`out`、`inout`其中的一种。

4.如果返回值类型是List，在Client中接收时，由于List是一个接口，所以必须要用ArrayList接受，例如：

aidl的代码

	interface IMyAidlInterfaceTest {
	    /**
	     * 测试数据类型的aidl，在Client中接收时要用ArrayList，不能用List，它是个接口
	     */
	    List<String> basicTypes(byte aByte, int anInt, long aLong, boolean aBoolean, float aFloat,
	            double aDouble, char aChar, String aString, in List<String> aList);
	}

Client端代码

	ArrayList<String> arrayList = mIMyAidlInterfaceTest.basicTypes(...);

##参考

[AIDL-小白成长记](http://www.imooc.com/learn/606)

[Android 接口定义语言 (AIDL)](https://developer.android.google.cn/guide/components/aidl.html)