package com.kstudy.ktlib.examle

fun main(){
   test1_40()
}


fun test1_40() {
    //  是Unit不是String
    val fun1 = fun(n1:Int, n2:Int) : Unit {
        "hhhhhh:$n1,$n2"
    }
    println(fun1(10,20))
    val fun11 = fun(n1:Int, n2:Int) : () -> Any = {
            println("hhhhhh:$n1,$n2")
    }

    val fun2 = fun(n1:Int, n2: (String, Int) -> Int) {
        "hhhhhh:$n1,$n2"
    }
    println(fun2(1){
            name: String, num: Int ->
        5
    })

    val fun3 = fun(n1:Int, n2:(String, Int) -> Int , n3:Boolean) {
        "hhhhhh:$n1,$n2"
    }
    fun3(1,{
            name: String, num: Int ->
        5
    },true)


    val fun0 = fun(n1:Int, n2:Int) : (Int, Int) -> String = {n1 , n2 -> "hhhhhh:$n1,$n2"}
    println(fun0(10,11)(100,100))
}