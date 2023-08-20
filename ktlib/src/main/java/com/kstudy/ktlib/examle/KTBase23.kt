package com.kstudy.ktlib.examle

import kotlin.math.roundToInt

fun main(){
    val number :Int = "6666".toInt()
    println(number)

    val number2 :Int? = "666.6".toIntOrNull()
    println(number2 ?: "原来你是NULL")

    println(66.35.toInt())
    println(66.85.toInt())
    println(66.35.roundToInt())
    println(66.85.roundToInt())
}