package com.example.hotspot

import android.os.Bundle
import android.util.Log
import android.util.Log.d
import androidx.appcompat.app.AppCompatActivity
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RestClass: AppCompatActivity() {

    //RestAPI URL
    private val URL : String = "http://hotspot-dev-654767138.ap-northeast-2.elb.amazonaws.com"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val retrofit = Retrofit.Builder()
            .baseUrl(URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(APIService::class.java)


//        api.postToken().enqueue(object : Callback<Token> {
//            override fun onResponse(call: Call<Token>, response: Response<Token>) {
//                if(response.isSuccessful)
//                    d("TAG", "onResponse() : ")
//            }
//
//            override fun onFailure(call: Call<Token>, t: Throwable) {
//                d("TAG", "onFailure() : ")
//            }
//        })
    }

}
