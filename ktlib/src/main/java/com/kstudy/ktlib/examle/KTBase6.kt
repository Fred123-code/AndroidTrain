package com.kstudy.ktlib.examle

fun main(){
    method01(name = "list",age = 99)
}

private fun method01(age: Int, name: String):Int{
    println("你的名字是：$name,你的年龄是：$age")
    return 200
}