package com.kstudy.ktlib.examle

fun main(){
    val list = listOf(0,1,2,3,4,5)

    val resutl = listOf(8,1,2,3,4,5).let{
        it.first() + it.first()
    }
    println(resutl)
}