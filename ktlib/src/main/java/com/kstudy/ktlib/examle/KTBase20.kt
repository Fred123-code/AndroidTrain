package com.kstudy.ktlib.examle

fun main(){
    val sourcePwd : String = "ABCDEFGHKJIMN"
    println(sourcePwd)

    //加密
    val newPWD = sourcePwd.replace(Regex("[AKMNO]")){
        //it.value 原来
        when(it.value){
            "A"->"9"
            "B"->"4"
            "K"->"5"
            else -> it.value
        }
    }
    println(newPWD)

    //解密
    val oldPWD = newPWD.replace(Regex("[945]")){
        //it.value 原来
        when(it.value){
            "9"->"A"
            "4"->"B"
            "5"->"K"
            else -> it.value
        }
    }
    println(oldPWD)
}
