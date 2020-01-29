package com.example.hotspot

import com.google.gson.annotations.SerializedName
import java.io.Serializable

// 임시 USER VOClass
data class VOClass(
    val firstname: String,
    val age: Int
)

data class SpotListVO(
    val place : Place,
    val visited : Boolean,
    val memo : String,
    val rating : Int
)

// For GET Place data class
data class Place(
    @SerializedName("kakaoId")
    val kakaoId: String,        // kakaoId
    @SerializedName("kakaoUrl")
    val kakaoUrl: String,       // place's url
    @SerializedName("placeName")
    val placeName: String,      // place's name
    @SerializedName("addressName")
    val addressName: String,    // address
    @SerializedName("roadAddressName")
    val roadAddressName: String,// road address
    @SerializedName("x")
    val x: String,              // longitude
    @SerializedName("y")
    val y: String               // latitude
) : Serializable

data class GetSpotList(
    @SerializedName("myPlaces")
    val myPlaces : List<MyPlace>,
    @SerializedName("count")
    val count : Int
) : Serializable

data class MyPlace(
    @SerializedName("id")
    val id: String,
    @SerializedName("userId")
    val userId: String,
    @SerializedName("place")
    val place : Place,
    @SerializedName("visited")
    val visited: Boolean,
    @SerializedName("memo")
    val memo: String,
    @SerializedName("rating")
    val rating: Int,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("updateAt")
    val updateAt: String
) : Serializable

// kakao account token Data
data class Token(
    @SerializedName("kakao_access_token")
    val kakao_access_token: String
)

// Access Token Data
data class AccessToken(
    @SerializedName("access_token")
    val access_token: String,
    @SerializedName("new_sign_up")
    val new_sign_up: Boolean
)