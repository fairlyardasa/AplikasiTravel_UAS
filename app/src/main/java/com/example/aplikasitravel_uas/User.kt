package com.example.aplikasitravel_uas

data class User(
    var username:String = "",
    var password:String = "",
    var role_id:String = ""
//    role_id = 0 untuk admin dan 1 untuk user biasa
)
