package com.kstudy.ktlib.examle

fun main() {

    //lambda函数类型对象 ,需要那其普通函数类型变成函数类型队形（函数需要引用）
    login("Derry","12345",::responseResult)

    val obj1 = ::responseResult
    val obj2 = obj1

    login("Derry","12345",obj2)

}

fun responseResult(msg:String, code:Int){
    println("最终：msg$msg，code$code")
}

const val USER_NAME_SAVE_DB1 = "Derry"
const val USER_PWD_SAVE_DB1 = "12345"

inline fun login(userName: String, userpwd: String ,responseResult: (String,Int)->Unit) {
    if(userName == USER_NAME_SAVE_DB1 && userpwd == USER_PWD_SAVE_DB1){
        responseResult("login success",200)
    }else {
        //  登录失败
        responseResult("login error",200)
    }
}