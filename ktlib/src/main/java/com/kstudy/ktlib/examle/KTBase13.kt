package com.kstudy.ktlib.examle

fun main(){
    val nimg_show = show2("11")
    println(nimg_show("33",1))
}

fun show(info: String){
    println("我是show函数 info：$info")
    return
}

fun show2(info: String) : (String,Int)->String{
    println("我是show函数 info：$info")
    return {name:String ,age:Int ->
        """
            111:$name
            222:$age
        """.trimIndent()
    }
fun show2(info: String) : ()->String{
    println("我是show函数 info：$info")
    return { ->
        """

        """.trimIndent()
        }
    }
}