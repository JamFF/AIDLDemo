// IMyAidlInterfaceTest.aidl
package com.example.aidl;

// Declare any non-default types here with import statements

interface IMyAidlInterfaceTest {
    /**
     * 测试数据类型的aidl
     */
    List<String> basicTypes(byte aByte, int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, char aChar, String aString, in List<String> aList);
}
