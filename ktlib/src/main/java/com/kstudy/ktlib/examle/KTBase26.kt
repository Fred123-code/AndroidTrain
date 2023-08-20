package com.kstudy.ktlib.examle

fun main(){
    val str = "Deery is OK"
    val r = str.run {
        true
        543.5f
    }
    println(r)
    println(str)

    //结果变化
    val r2 = str.run(::islong)
        .run(::showText)
        .run(::mapText)
        .run(::println)

    str.let(::islong)
       .let(::showText)
       .let(::mapText)
       .let(::println)

    str
        .run{
            if(str.length > 5 ) true else false
        }.run {
            if (this) "666" else "888"
        }.run {
            "$this"
        }.run(::println)



    val len = with(str,::islong)
    val text = with(len,::showText)
    val textinfo = with(text,::mapText)
    with(textinfo,::println)




}
fun islong(str:String) = if(str.length > 5 ) true else false

fun showText(islong:Boolean) = if (islong) "666" else "888"

fun mapText(getshow:String) = "$getshow"