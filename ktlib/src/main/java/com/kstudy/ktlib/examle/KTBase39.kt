package com.kstudy.ktlib.examle

fun main(){
    val fun0 = fun(n1:Int, n2:Int) : () -> String = {"fun开始:$n1,$n2"}
    println(fun0(1,2))
    println(fun0(1,2)())
    val fun1 = fun(n1:Int, n2:Int) :  String = "11111111"
    println(fun1(1,3))
    val fun2 = fun(n1:Int, n2:Int) : (Int, Int) -> String = {n1 , n2 -> "hhhhhh:$n1,$n2"}
    println(fun2(10,11)(100,100))

    val fun999 : (String) -> (String) -> (Boolean) -> (Int) -> (String) -> Int =
        {it : String ->
            {it : String ->
                {it : Boolean ->
                    {it : Int ->
                        {it : String ->
                            9999
                        }
                    }
                }
            }
        }
    println("******************")
    println(fun999("11")("88")(true)(1)("1"))
    println("******************")

    val fun3 = fun(n1:Int,n2:(String)->(Int)->String) : () -> Int = {66666}
    println(fun3(5){ it:String->
        {it:Int->
            "end"
        }
    })

}