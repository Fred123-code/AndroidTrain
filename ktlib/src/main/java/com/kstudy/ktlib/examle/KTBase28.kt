package com.kstudy.ktlib.examle

fun main(){
    val result = checkPermissionAction("Derry","123")
    println("$result")
}

public fun checkPermissionAction(name:String,pwd:String):String?{
    return name.takeIf { permissionSystem(name,pwd) }
}

public fun checkPermissionAction2(name:String,pwd:String):String{
    return name.takeIf { permissionSystem(name,pwd) } ?:"NULL!!!"
}

fun permissionSystem(username:String,userpwd:String):Boolean{
    return if(username=="root"&&username=="123") true else false
}