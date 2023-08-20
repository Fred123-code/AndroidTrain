package com.kstudy.ktlib.examle

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

//TODO(提供委托) provideDelegate

class Owner2{
    var aaa :String by TestDelegate()
    var bbb :String by TestDelegate()
}

class TestDelegate {
    operator fun provideDelegate(
        thisRef : Owner2,
        property: KProperty<*>) : ReadWriteProperty<Owner2, String>{
        return if (property.name.contains("aaa")){
            TestUpdate("33333")
        }else{
            TestUpdate("44444")
        }

    }
}

class  TestUpdate(var url : String = "http:") : ReadWriteProperty<Owner2, String> {

    override fun getValue(thisRef: Owner2, property: KProperty<*>): String {
        println("@@@@")
        return url
    }


    override fun setValue(thisRef: Owner2, property: KProperty<*>, value: String) {
        println("****")
        url = value
    }

}


fun main(){
    var o = Owner2()
    println(o.aaa)
    println(o.bbb)
}