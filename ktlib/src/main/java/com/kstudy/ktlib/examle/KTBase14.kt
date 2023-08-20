package com.kstudy.ktlib.examle

fun main(){
    showPensonInfo("list",99,'男',"学习KT语音"){
        println("显示结果：$it")
    }

}

inline fun showPensonInfo(name:String, age:Int ,sex:Char ,study:String, showResult: (String) -> Unit){
    val str = "name:$name,age:$age,sex:$sex,study:$study"
    showResult(str)
}