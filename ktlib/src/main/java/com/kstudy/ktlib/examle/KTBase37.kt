package com.kstudy.ktlib.examle

fun main(){
    test1_37()
}

fun test1_37() {
    val method01 : (Int) -> String
    method01 = fun(value: Int) = value.toString()
    println(method01(5))


    val method02 : (Int, Int) -> Unit = {_,num -> {}}
    println(method02.invoke(199,10))
}

