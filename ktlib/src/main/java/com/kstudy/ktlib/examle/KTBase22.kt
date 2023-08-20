package com.kstudy.ktlib.examle

fun main(){
    val str = "ABCDEFGHIJKLMNOPQ"
    str.forEach {
        //it == str
        //print("所有的字符是：$it")
        //c覆盖it
        c -> print("所有的字符是：$c")
    }
}