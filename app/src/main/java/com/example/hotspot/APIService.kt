package com.example.hotspot

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET

interface APIService {
    @GET("/users")
    fun fetchAllUsers(): Call<ResponseBody>
}
