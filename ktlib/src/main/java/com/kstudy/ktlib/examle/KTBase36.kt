package com.kstudy.ktlib.examle

import kotlin.reflect.KProperty
import kotlin.reflect.jvm.javaField

var aa :String? = "http"

operator fun String.setValue(thisRef: Any?, property: KProperty<*>, value: String?) {
    println("thisRef=$thisRef,this=$this")
    println(property)
//    aa = value

    property.javaField?.isAccessible = true
    property.javaField?.set(thisRef,value)
}

operator fun String.getValue(thisRef: Any?, property: KProperty<*>) = this + "开始修改" + aa

class Item {
    var name1 : String = "111"
    var name2 : String by name1
    var name3 : String by ::name1


    fun updateName1() {
        name1 = "UUUUUUUUU"
    }
}

fun main() {

    val item = Item()

    println("**********************************")
    println("name2=" + item.name2)
    println("name3=" + item.name3)
    item.name2 = "2222222"
    println("name2=" + item.name2)
    println("name3=" + item.name3)
    item.name1 = "3333333"
    println("name2=" + item.name2)
    println("name3=" + item.name3)
    item.updateName1()
    println("name2=" + item.name2)
    println("name3=" + item.name3)
    println("**********************************")

}