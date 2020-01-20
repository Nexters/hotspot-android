package com.example.hotspot

import com.google.gson.annotations.SerializedName

// 임시 USER VOClass
data class VOClass(
    val firstname: String,
    val age: Int
)

data class Token(
//    @SerializedName("kakao_access_token")
    val kakao_access_token: String
)