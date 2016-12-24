# AIDLDemo

Android Service中AIDL的简单应用

##简单的调用

###步骤

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

3.编译后会在`build/generated/source/aidl/debug/`下生成对应包名下的java文件

4.创建Client端，将Service中的aidl文件夹粘贴过来

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


###注意事项

1.AIDL编译，通过SDK中提供的程序编译，目录：${SDK_ROOT}/build-tools/${BUILD_TOOL_VERSION}/aidl.exe

2.aidl的包名无需和项目包名一致，但必须aidl的Service端和Client端中的包名及文件一模一样（在自定义类型中有序列化对象时，是需要包名一致的！下面自定义类型有说明）；

3.Service允许其他程序start或者bind时，需要清单文件中添加`android:exported="true"`，如果在清单文件添加了action，则说明希望被替他应用程序调用该属性为true，否则为false；

4.aidl可传输的基本数据类型中，不包括`short`，由于在序列化时没有`dest.writeShort()`方法，所以不支持`short`；
List、Map中的类型也必须是可支持的基本数据类型，同样不包括`short`；
如果传输List、Map需要在前面书写`in`、`out`、`inout`其中的一种。

##自定义类型

###步骤

1.aidl文件，这里参数为自定义对象Person，返回为Person的List集合

	package com.example.aidlsecond;
	
	// Declare any non-default types here with import statements
	import com.example.aidlsecond.Person;
	
	interface IMyAidlInterface {
	
	    List<Person> add(in Person person);
	}

2.新建Person.aidl

	package com.example.aidlsecond;
	
	// Declare any non-default types here with import statements
	
	parcelable Person;

3.新建Person.java并进行序列化，这里的包名要和aidl中的包名一致

	package com.example.aidlsecond;
	
	import android.os.Parcel;
	import android.os.Parcelable;
	
	public class Person implements Parcelable {
	
	    private String name;
	
	    private int age;
	
	    public Person(String name, int age) {
	        this.name = name;
	        this.age = age;
	    }
	
	    public String getName() {
	        return name;
	    }
	
	    public void setName(String name) {
	        this.name = name;
	    }
	
	    public int getAge() {
	        return age;
	    }
	
	    public void setAge(int age) {
	        this.age = age;
	    }
	
	    @Override
	    public int describeContents() {
	        return 0;
	    }
	
	    /**
	     * 写数据
	     */
	    @Override
	    public void writeToParcel(Parcel dest, int flags) {
	
	        dest.writeString(name);
	        dest.writeInt(age);
	    }
	
	    /**
	     * 读数据
	     */
	    protected Person(Parcel in) {
	
	        // name与age的顺序必须与写入的顺序一致
	        name = in.readString();
	        age = in.readInt();
	    }
	
	    public static final Creator<Person> CREATOR = new Creator<Person>() {
	        @Override
	        public Person createFromParcel(Parcel in) {
	            return new Person(in);
	        }
	
	        @Override
	        public Person[] newArray(int size) {
	            return new Person[size];
	        }
	    };
	}

4.Service代码

	public class MyService extends Service {
	
	    private ArrayList<Person> mPersons = new ArrayList<>();
	
	    public MyService() {
	    }
	
	    @Override
	    public IBinder onBind(Intent intent) {
	        return mBinder;
	    }
	
	    private Binder mBinder = new IMyAidlInterface.Stub() {
	        @Override
	        public List<Person> add(Person person) throws RemoteException {
	            mPersons.add(person);
	            return mPersons;
	        }
	    };
	}

5.创建Client端，将Service中的aidl文件夹粘贴过来，将Person.java粘贴过来，这里一定要保证包名一致

6.bindService，得到aidl远程接口，调用其中add方法

	public class MainActivity extends AppCompatActivity implements View.OnClickListener {
	
	    private static final String TAG = "MainActivity";
	
	    private IMyAidlInterface mIMyAidlInterface;
	
	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_main);
	        findViewById(R.id.bt).setOnClickListener(this);
	        bindService();
	    }
	
	    private void bindService() {
	
	        Intent intent = new Intent();
	        intent.setComponent(new ComponentName("com.example.aidlsecond", "com.example.aidlsecond.MyService"));
	
	        bindService(intent, conn, BIND_AUTO_CREATE);
	    }
	
	    private ServiceConnection conn = new ServiceConnection() {
	
	        @Override
	        public void onServiceConnected(ComponentName name, IBinder service) {
	            mIMyAidlInterface = IMyAidlInterface.Stub.asInterface(service);
	        }
	
	        @Override
	        public void onServiceDisconnected(ComponentName name) {
	            mIMyAidlInterface = null;
	        }
	    };
	
	    @Override
	    public void onClick(View v) {
	        switch (v.getId()) {
	
	            case R.id.bt:
	                try {
	                    List<Person> persons = mIMyAidlInterface.add(new Person("sam", 27));
	                    Log.d(TAG, "onClick: " + persons);
	                } catch (RemoteException e) {
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


###注意事项

1.针对自定义类型时，AIDL的Service、Client端必须保证aidl中自定义类型与java中的自定义类型包名一致；

2.AIDL只支持方法，不能定义静态成员；

3.除默认的类型外，均需要导包，例如Person类；

4.AIDL、Binder、Messager的选择

* AIDL：IPC，有多线程，有多个应用程序；
* Binder：只有IPC，没有多线程，有多个应用程序；
* Messager：只有IPC，没有多线程，没有多个应用程序。

##参考

[AIDL-小白成长记](http://www.imooc.com/learn/606)

[Android 接口定义语言 (AIDL)](https://developer.android.google.cn/guide/components/aidl.html)