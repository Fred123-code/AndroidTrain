package com.kstudy.ktlib.examle

fun main(){
    val len = "Derry".count()
    println(len)

    val len_r = "Deery".count{
        it == 'r'
    }
    println(len_r)
}