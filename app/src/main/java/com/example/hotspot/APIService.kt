package com.example.hotspot



import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*
import java.util.*

interface APIService {
    @POST("/auth/login/kakao")
    fun postToken(@Body token: Token) : Call<AccessToken>

    @GET("/place/search")
    fun getPlace(@Query("search_keyword") search_name : String) : Call<List<Place>>

    @POST("/place/my_places")
    fun postPlace(@Header("Authorization") auth : String,
                  @Body spotObject : SpotListVO) : Call<ResponseBody>

    @GET("/place/my_places")
    fun getMyPlaces(@Header("Authorization") auth : String) : Call<GetSpotList>
}
