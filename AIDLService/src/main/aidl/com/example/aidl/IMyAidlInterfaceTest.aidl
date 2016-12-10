// IMyAidlInterfaceTest.aidl
package com.example.aidl;

// Declare any non-default types here with import statements

interface IMyAidlInterfaceTest {
    /**
     * 测试数据类型的aidl，在Client中接收时要用ArrayList，不能用List，它是个接口
     */
    List<String> basicTypes(byte aByte, int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, char aChar, String aString, in List<String> aList);
}
