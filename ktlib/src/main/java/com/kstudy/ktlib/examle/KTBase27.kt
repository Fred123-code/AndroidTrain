package com.kstudy.ktlib.examle

fun main(){
    val str = "ABCDEFGHIJKLMN"

    val r: String = str.also{
        true
    }

    println(r)


    str.also{
        println("$it")
    }.also {
        println("${it.toLowerCase()}")
    }
}