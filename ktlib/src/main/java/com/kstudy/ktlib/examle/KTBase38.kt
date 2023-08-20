package com.kstudy.ktlib.examle

fun main(){
    val method01 : String.() -> Unit = {
        // this == String 本身
        println("我是$this")
    }

    "DDD".method01()


    val method02 : Int.(Int) -> String = {
        "三数相加的结果是${this + it}"
    }

    println(100.method02(20))
    println(method02(100,20))


    fun aa() {
        "555"
    }

    println(aa())
}

fun String.show() {  //.()
// this == String本身
}