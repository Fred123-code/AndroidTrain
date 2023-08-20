package com.kstudy.ktlib.examle
fun main(){

    var value1:String ?= null
    //checkNotNUull(value1)/requireNotNull(value1)
    //Boolean rquire(value1)

    try{
        var info:String? = null
        checkException(info)
    }catch (e :Exception){
        e.printStackTrace()
    }
}

fun checkException(info: String?){
    info ?: throw  CustomException()
}
class CustomException : IllegalAccessException("你的代码不太严谨")