package com.kstudy.ktlib.examle

fun main(){
    var name :String ?=null

    name = ""

    val r = name?.let {
        //it == name
        if(it.isBlank()){   //  ""
            "Defalut"
        }else{
            it  //"$it"
        }
    }//把name拿进去用

    println(r)
}