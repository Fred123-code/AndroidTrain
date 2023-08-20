package com.kstudy.ktlib.examle

fun main(){
    val name1 :String = "Derry"
    val name2 :String = "Derry"

    println(name1.equals(name2)) //java
    println(name1 == name2)   //kt

    println(name1 === name2)  //true

    val name4 ="derry".capitalize() //"修改成Derry"
    println(name1 === name4)
}