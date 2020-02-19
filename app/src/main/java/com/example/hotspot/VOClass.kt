package com.example.hotspot

import com.google.gson.annotations.SerializedName
import java.io.Serializable

// 임시 USER VOClass
data class VOClass(
    val firstname: String,
    val age: Int
)
/*images?: {
  cloudinaryId: string // cloudinary 업로드했을 때 나오는 public_id
  url: string // cloudinary 업로드했을 때 나오는 url
}[]
bestMenu?: string[]
businessHours?: {
  open: string // 0~23 string으로
  close: string
}
priceRange?: string // 화면에 나오는 string 그대로
parkingAvailable?: boolean
allDayAvailable?: boolean
powerPlugAvailable?: boolean*/
data class Images(
    var cloudinaryId : String,
    var url : String
): Serializable
data class BusinessHours(
    var open : String?, // 0~23
    var close : String?
): Serializable


data class SpotListVO(
    val place : Place,
    val visited : Boolean,
    val memo : String?,
    val rating : Int?,
    val images : ArrayList<Images>?,
    val bestMenu : ArrayList<String>?,
    val businessHours: BusinessHours?,
    val parkingAvailable : Boolean?,
    val allDayAvailable : Boolean?,
    val powerPlugAvailable : Boolean?

): Serializable

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
    val y: String,               // latitude
    @SerializedName("categoryName")
    var categoryName: String
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
    var place : Place,
    @SerializedName("visited")
    var visited: Boolean,
    @SerializedName("memo")
    var memo: String,
    @SerializedName("rating")
    var rating: Int,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("updateAt")
    val updateAt: String,
    @SerializedName("images")
    var images : ArrayList<Images>?,
    @SerializedName("bestMenu")
    var bestMenu : ArrayList<String>?,
    @SerializedName("businessHours")
    var businessHours: BusinessHours?,
    @SerializedName("parkingAvailable")
    var parkingAvailable : Boolean?,
    @SerializedName("allDayAvailable")
    var allDayAvailable : Boolean?,
    @SerializedName("powerPlugAvailable")
    var powerPlugAvailable : Boolean?
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