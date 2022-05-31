# AIDLDemo

Android Service中AIDL的简单应用

## Service中AIDL的简单的调用

### 步骤

1. 创建AIDL中Service端项目，新建Service

    ```java
    public class RemoteService extends Service {
    
        /**
         * 当客户端绑定到该服务时，会执行
         */
        @Nullable
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }
    }
    ```

2. 新建aidl文件夹，**包名无所谓**，写AIDL中的代码

    ```aidl
    package com.example.aidl;
    
    // Declare any non-default types here with import statements
    
    interface IMyAidlInterface {
    
        // 计算两个数的和
        int add(int num1, int num2);
    }
    ```

3. 编译后会在 `build/generated/source/aidl/debug/` 下生成对应包名下的java文件

4. 在onBind时返回IBinder对象，也就是IMyAidlInterface

    ```java
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
    }
    ```

5. 创建AIDL中Client端项目，将Service端的aidl文件夹粘贴过来，**这两者的包名必须一致**

6. 在Client端的Activity中bindService，得到AIDL远程接口，调用其中add方法

    ```java
    public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    
        private EditText et_num1, et_num2, et_res;
    
        private IMyAidlInterface mIMyAidlInterface;
    
        private final ServiceConnection conn = new ServiceConnection() {
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
            et_num1 = findViewById(R.id.et_num1);
            et_num2 = findViewById(R.id.et_num2);
            et_res = findViewById(R.id.et_res);
            findViewById(R.id.bt_add).setOnClickListener(this);
        }
    
        /**
         * 绑定服务
         */
        private void bindService() {
    
            // 5.0以后不能使用隐式绑定服务
            // 方式一：没有action的情况，包名+完整类名
            Intent intent = new Intent().setComponent(
                    new ComponentName("com.example.aidl", "com.example.aidl.RemoteService")
            );
            // 方式二：没有action的情况，action+包名
//          Intent intent = new Intent("com.example.aidl.ADD").setPackage("com.example.aidl");
            bindService(intent, conn, Context.BIND_AUTO_CREATE);
        }
    
        @Override
        public void onClick(View v) {
    
            if (v.getId() == R.id.bt_add) {
                 int num1 = Integer.parseInt(et_num1.getText().toString());
                 int num2 = Integer.parseInt(et_num2.getText().toString());
 
                 try {
                     int res = mIMyAidlInterface.add(num1, num2);
                     et_res.setText(String.valueOf(res));
                 } catch (RemoteException e) {
                     et_res.setText("错误了");
                     e.printStackTrace();
                 }
            }
        }
    
        @Override
        protected void onDestroy() {
            super.onDestroy();
            unbindService(conn);
        }
    }
    ```

### 注意事项

1. AIDL编译，通过SDK中提供的aidl.exe进行编译，文件目录：`${SDK_ROOT}/build-tools/${BUILD_TOOL_VERSION}/aidl.exe`；

2. AIDL的**包名无需和项目包名一致**，但AIDL的Service端和Client端的包名及文件必须一模一样（在自定义类型中有序列化对象时，是需要包名一致的！下面自定义类型有说明）；

3. Service想允许**其他程序**start或者bind时，需要在清单文件中添加 `android:exported="true"`，如果在清单文件添加了action，则说明希望被替他应用程序调用，该属性默认为true，否则默认为false；

4. AIDL可传输的基本数据类型中，不包括 `short`，由于在序列化时没有 `dest.writeShort()` 方法，所以不支持 `short`；

5. List、Map中的类型也必须是可支持的基本数据类型，同样不包括 `short`；

6. 如果传输List、Map需要在前面书写 `in`、`out`、`inout` 其中的一种。

7. Android11以上，在客户端的AndroidManifest中，需要添加[管理软件包可见性](https://developer.android.google.cn/training/basics/intents/package-visibility?hl=zh-cn#package-name)，服务端不需要添加

## 自定义类型SecondDemo

### 步骤

1. AIDL文件，这里参数为自定义对象Person，返回为Person的List集合

    ```aidl
    package com.example.aidlsecond;
    
    // Declare any non-default types here with import statements
    import com.example.aidlsecond.Person;
    
    interface IMyAidlInterface {
    
        List<Person> add(in Person person);
    }
    ```

2. 新建Person.aidl

    ```aidl
    package com.example.aidlsecond;
    
    // Declare any non-default types here with import statements
    
    parcelable Person;
    ```

3. 新建Person.java并进行序列化，这里的包名要和AIDL中的包名一致

    ```java
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
    ```

4. Service代码

    ```java
    public class MyService extends Service {
    
        private ArrayList<Person> mPersons = new ArrayList<>();
    
        public MyService() {
        }
    
        @Override
        public IBinder onBind(Intent intent) {
            return mBinder;
        }
    
        private final Binder mBinder = new IMyAidlInterface.Stub() {
            @Override
            public List<Person> add(Person person) throws RemoteException {
                mPersons.add(person);
                return mPersons;
            }
        };
    }
    ```

5. 创建Client端，将Service中的aidl文件夹粘贴过来，将Person.java粘贴过来，这里一定要保证包名一致

6. bindService，得到AIDL远程接口，调用其中add方法

    ```java
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
    
        private final ServiceConnection conn = new ServiceConnection() {
    
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                // 拿到的不是远程服务，只是一个远程服务的代理Proxy
                mIMyAidlInterface = IMyAidlInterface.Stub.asInterface(service);
            }
    
            @Override
            public void onServiceDisconnected(ComponentName name) {
                mIMyAidlInterface = null;
            }
        };
    
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.bt) {
                try {
                    List<Person> persons = mIMyAidlInterface.add(new Person("sam", 27));
                    Log.d(TAG, "onClick: " + persons);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    
        @Override
        protected void onDestroy() {
            super.onDestroy();
            unbindService(conn);
        }
    }
    ```


## 原理剖析

直接上图
![AIDL](https://github.com/JamFF/AIDLDemo/blob/master/art/aidl.png)

### 注意事项

1. 针对自定义类型时，AIDL的Service端、Client端必须保证AIDL中自定义类型与java中的自定义类型包名一致；

2. AIDL只支持方法，不能定义静态成员；

3. 除默认的类型外，均需要导包，例如Person类；

4. AIDL、Binder、Messager的选择
    * AIDL：IPC，有多线程，有多个应用程序
    * Binder：只有IPC，没有多线程，有多个应用程序
    * Messager：只有IPC，没有多线程，没有多个应用程序

5. AIDL方法在服务端的Binder线程池中执行，因此多个客户端同时连接时，会存在多个线程同时访问的情形，所以我们要在AIDL方法（Stub的实现）中处理线程同步。

6. AIDL中不能使用普通java接口，而是aidl接口，且回调时在Binder线程池中执行，如果要进行UI操作，则要切换线程。

7. 因为跨进程，所以传到服务端的接口是一个新的对象，所以需要使用RemoteCallbackList来解注册。

8. 客户端的onServiceConnected和onServiceDisconnected方法都运行在UI线程中，所以不可以在其中直接调用服务端的耗时方法；服务端的方法本身就运行在服务端的Binder线程池中，所以本身就可以执行大量耗时操作，而不用开线程。

9. Binder的linkToDeath和unlinkToDeath方法，设置DeathRecipient 接口，会在客户端的Binder线程池中被回调，而onServiceDisconnected在客户端的UI线程被回调，也就是说DeathRecipient 的回调不能访问UI。

10. AIDL权限验证，可以使用Service的onBind中验证，不通过就返回null；也可以在服务端的onTransact中返回false。

## 参考

[AIDL-小白成长记](http://www.imooc.com/learn/606)

[Android 接口定义语言 (AIDL)](https://developer.android.google.cn/guide/components/aidl.html)

[AIDL注意点](https://www.jianshu.com/p/7efbe4237d26)

[管理软件包可见性](https://developer.android.google.cn/training/basics/intents/package-visibility?hl=zh-cn#package-name)