package com.example.hotspot

// 임시 USER VOClass
data class VOClass(
    val firstname: String,
    val age: Int
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

data class SpotListVO(
    val place : Place,
    val visited : Boolean,
    val memo : String,
    val rating : Int
)
data class Place(
    val kakaoId : String,
    val kakaoUrl : String,
    val placeName : String,
    val addressName : String,
    val roadAddressName : String,
    val x : String,
    val y : String

)