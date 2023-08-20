package com.kstudy.ktlib.examle

import java.beans.JavaBean
import java.net.CacheResponse
import javax.print.attribute.standard.JobOriginatingUserName

fun main(){
    //  1[最佳]
    loginAPI("Derry","12345"){
        msg: String, code:Int ->
        println("最终：msg$msg，code$code")
    }
    //  2
    loginAPI("Derry","12345",{
            msg: String, code:Int ->
        println("最终：msg$msg，code$code")
    })

    //  3
    loginAPI("Derry","12345",responseResult = {
            msg: String, code:Int ->
        println("最终：msg$msg，code$code")
    })
}

//模拟：数据库SQLSERVER
const val USER_NAME_SAVE_DB = "Derry"
const val USER_PWD_SAVE_DB = "12345"
//如果此函数，不使用内联，在调用端，会生成多个对象来完成lambda的函数调用（会造成性能损耗）
//如果使用内联，相当于C++的#define 宏定义 宏替换
//小结：如果参数有lambda，尽量使用 inline关键帧，这样内部会做优化
//登录API 模仿前端

/*
public inline fun loginAPI(userName: String, userpwd: String ,responseResult: (String,Int)->Unit){
 */

fun loginAPI(userName: String, userpwd: String ,responseResult: (String,Int)->Unit){
    if(userName == null || userpwd == null){
        TODO("用户名或密码为null")//ERROR
    }
    if (userName.length > 3 && userpwd.length > 3){
        if (wbeServiceLoginAPI(userName,userpwd)){
            //  登录成功
            responseResult("login success",200)
        }else {
            //  登录失败
            responseResult("login error",200)
        }
    }else{
        TODO("用户名或密码不合格")
    }
}


private fun wbeServiceLoginAPI(name:String,pwd:String) : Boolean{
//    if中kt是表达式
//    if中java是语句
//    if(name == USER_NAME_SAVE_DB && pwd == USER_PWD_SAVE_DB){
//        return true
//    }else{
//        return false
//    }
    return if(name == USER_NAME_SAVE_DB && pwd == USER_PWD_SAVE_DB) true else false
}