package com.example.hotspot

import retrofit2.Call
import retrofit2.http.*

const val baseUrl = "https://api.dev.hotspot-team.com"
interface APIService {
    @POST("/auth/login/kakao")
    fun postToken(@Body token: Token) : Call<AccessToken>

    @GET("/place/search")
    fun getPlace(@Query("search_keyword") search_name : String) : Call<List<Place>>

    @POST("/place/my_places")
    fun postPlace(@Header("Authorization") auth : String,
                  @Body spotObject : SpotListVO) : Call<SpotListVO>

    @GET("/place/my_places")
    fun getMyPlaces(@Header("Authorization") auth : String) : Call<GetSpotList>

    @PUT("/place/my_places/{myPlaceId}")
    fun putPlace(@Header("Authorization") auth : String,
                 @Path("myPlaceId") myPlaceId : String,
                 @Body spotObject: SpotListVO) : Call<SpotListVO>
}
