package com.example.hotspot

// 임시 USER VOClass
data class VOClass(
    val firstname: String,
    val age: Int
)

data class Address(
    val loca: String,
    val address: String
)


// kakao account token Data
data class Token(
//    @SerializedName("kakao_access_token")
    val kakao_access_token: String
)

// Access Token Data
data class AccessToken(
    val access_token: String,
    val new_sign_up: Boolean
)