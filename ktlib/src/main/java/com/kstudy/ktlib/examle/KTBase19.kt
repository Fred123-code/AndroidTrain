package com.kstudy.ktlib.examle

const val INFO = "Derry is Success Result"

fun main(){
    val indexOf = INFO.indexOf('i')
    println(INFO.substring(0,indexOf))
    println(INFO.substring(0 until indexOf))
    println(INFO.substring(0..indexOf))

    val jsonText = "Derry,Leo,Lance"
    val list = jsonText.split(",")

    //直接输出分割
    println("直接输出分割$list")

    //C++ 结构
    val (v1,v2,v3) = list
    println("v1:$v1,$v2,$v3")
}