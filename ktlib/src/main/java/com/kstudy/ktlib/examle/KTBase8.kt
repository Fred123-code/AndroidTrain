package com.kstudy.ktlib.examle

fun main(){
    //第一步：函数输入输出的声明
    val methodAction : () -> String

    //第二步：对上面函数的实现
    methodAction = {
        val inputValue = 111111
        "$inputValue Derry"
    }

    //第三步：调用此函数
    println(methodAction())

    val methodAction1 : (Int ,Int ,Int)->String = {number1 ,number2 ,number3 ->
    val inputValue = 999999
    "$inputValue Derry 参数一：$number1，参数二：$number2，参数三：$number3"}
    println(methodAction1(1,2,3))

    val methodAction2 : (String)->String = {"$it Derry"}
    println(methodAction2("sss"))

    val methodAction3 : (Double)->String = {"$it Derry"}
    println(methodAction3(123.5677))
}