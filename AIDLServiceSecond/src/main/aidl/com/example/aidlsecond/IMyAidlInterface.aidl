// IMyAidlInterface.aidl
package com.example.aidlsecond;

// Declare any non-default types here with import statements
import com.example.aidlsecond.Person;

interface IMyAidlInterface {

    List<Person> add(in Person person);
}
