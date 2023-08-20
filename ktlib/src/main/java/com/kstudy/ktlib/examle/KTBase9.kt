package com.kstudy.ktlib.examle

fun main(){
    //方法名：必须指定 参数类型和返回类型
    //方法名 = 进行推断返回类型
    val method0 : ()-> Unit = { println("11")}
    println(method0())

    val method1 = {v1:Double, v2:Float ,v3:Int ->
        "V1:$v1, v2:$v2 ,v3:$v3"
    }
    println(method1(456.66,56.5f,8))

    val method2 = {
        345.5f
    }
    println(method2())

    val method3 = {number: Int ->
        number
    }
    println(method3(9))
}