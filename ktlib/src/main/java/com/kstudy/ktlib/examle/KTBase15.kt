package com.kstudy.ktlib.examle

fun main(){
    //默认是不可为空
    var name : String = "Derry"

    //声明是指定为可空类型
    var name2 :String ?
    name2 = null


    var name3 : String? = "233"
    name3 = null
    //name.capitalize()  name是可空使用必须给出补救
    name3?.capitalize() //如果为空不执行

    //!!--都执行
    var name4 : String? = "233"
    name4 = null
    name4!!.capitalize()

}