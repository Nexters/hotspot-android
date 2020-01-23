package com.example.hotspot

import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.http.*

interface APIService {
    @POST("/auth/login/kakao")
    fun postToken(@Body token: Token) : Call<AccessToken>
}
