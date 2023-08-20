package com.kstudy.ktlib.examle

import kotlin.properties.Delegates

class User {
    var name: String by Delegates.observable("初始值") {
            prop, old, new ->
        println("旧值：$old -> 新值：$new")
    }
}

fun main(args: Array<String>) {
    val user = User()
    println("11111")
    user.name = "第一次赋值"
    println("22222")
    user.name = "第二次赋值"
    println("33333")
}