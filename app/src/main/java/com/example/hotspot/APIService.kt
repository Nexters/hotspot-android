package com.example.hotspot



import retrofit2.Call
import retrofit2.http.*
import java.util.*

interface APIService {
    @POST("/auth/login/kakao")
    fun postToken(@Body token: Token) : Call<AccessToken>

    @POST("/place/my_places")
    fun postPlace(@Header("Authorization") auth : String,
                  @Body spotObject : SpotListVO) : Call<Objects>
}
