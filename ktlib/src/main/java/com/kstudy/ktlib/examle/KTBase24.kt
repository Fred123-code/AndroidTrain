package com.kstudy.ktlib.examle

fun main(){
    val info = "Derry YOU"

    println("${info.toLowerCase()}")

    val infoNew :String =info.apply {
        //不持有it，但是有this == info
        println("$this")
        println("${toLowerCase()}")
    }.apply {
        //........
        //可以链式调用
    }





}