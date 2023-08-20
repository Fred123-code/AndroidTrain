package com.kstudy.ktlib.examle

fun main(){
    //可变
    var list = mutableListOf("Derry","Boy","Sw")
    list.add("赵四")
    list.remove("Sw")
    println(list)

    //不可变
    val list2 = listOf(123,456,789)

    val list3 : MutableList<Int> = list2.toMutableList()
    list3.add(111)
    println(list3)
}
