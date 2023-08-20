package com.kstudy.ktlib.examle

import kotlin.reflect.KProperty

fun main() {
    test1()
    creatDB(AA_DB()).save()     //委托



}
class A {
    var name = 5555.511f
        set (v) {
            field = v
            println("set的v:$v")
        }
        get() {
            return field
        }

    var student : Float by ::name
//    var student : Float by name
}

//private operator fun Float.setValue(a: Any, property: KProperty<*>, fl: Float) {
//
//}
//
//private operator fun Float.getValue(a: Any, property: KProperty<*>): Float = this

class creatDB(initalize : DB) : DB by initalize {
//    override fun save() {
//        println("!!!!!!!!!!!!!!!!!!")
//    }
}

interface DB {
    fun save()

    fun save1()
}

class AA_DB :DB {
    override fun save() {
        println("AA_DB start save")
    }

    override fun save1() {
        TODO("Not yet implemented")
    }

}

fun test1() {
    println("**************************")
    println(55)
    println(aa("22"))
    println(aa(null))
    println("**************************")
}

fun aa(num : String?) : Int{
    return num?.length ?: -1
}