package com.kstudy.ktlib.examle

class Manager{
    private var infoValue:String?= null
    fun getInfoValue() = infoValue
    fun setInfoValue() {
        this.infoValue = infoValue
    }

}

fun main(){
    val manager = Manager()
    val result = manager.getInfoValue().takeUnless { it.isNullOrBlank() } ?: "未经过初始化"
    println(result)
}