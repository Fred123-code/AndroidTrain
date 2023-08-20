package com.kstudy.ktlib.examle

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class  WeiTuo {
    private var url = "http:"

    operator fun getValue(owner : Owner, property: KProperty<*>) : String {
        println("@@@@")
        println("url=$url")
        return url
    }

    operator fun setValue(owner : Owner, property: KProperty<*>, string: String) {
        println("****")
        println("url=$url")
        url = string
    }
}

class  WeiTuo2 : ReadWriteProperty<Owner, String> {
    private var url = "http:"

    override fun getValue(thisRef: Owner, property: KProperty<*>): String {
        if (property.name.contains("name3")){
            println("url=$url")
            return "33333"
        }else{
            println("@@@@")
            println("url=$url")
            return url
        }
    }


    override fun setValue(thisRef: Owner, property: KProperty<*>, value: String) {
        println("****")
        println("url=$url")
        url = value
    }

}

class Owner{
    var name : String by WeiTuo()
    var name2 : String by WeiTuo2()
    var name3 : String by WeiTuo2()
}

fun main(){
    val o = Owner()
        o.name = "11111111"
    println(o.name)
    println(o.name2)
    println(o.name3)
}