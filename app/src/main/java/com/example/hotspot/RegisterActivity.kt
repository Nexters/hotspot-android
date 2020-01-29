package com.example.hotspot

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_register.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

import org.json.JSONObject





class RegisterActivity : AppCompatActivity() {
    private lateinit var mRetrofit: Retrofit
    lateinit var apiService : APIService
    private val URL : String = "http://hotspot-dev-654767138.ap-northeast-2.elb.amazonaws.com"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        btn_esc3.setOnClickListener {
            finish()
        }

        setRetrofitInit()
        setApiServiceInit()
        var place = Place("247906074","http://place.map.kakao.com/247906074",
            "아모레성수","서울 성동구 성수동2가 277-52",
            "서울 성동구 아차산로11길 7","127.059040730967",
            "37.5445477220243"
        )
        var spotList = SpotListVO(place,true,"분위기가 멋진 곳!",3)
        apiService.postPlace("Bearer " + "fiduvljyhquku09nyoux58px448rmox6hysn9d4btowg8keisl38ot7",
            spotList).enqueue(object : Callback<Objects> {
            override fun onResponse(call: Call<Objects>, response: Response<Objects>) {
                if (response.isSuccessful) {
                    // Do your success stuff...
                } else {
                    try {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        Toast.makeText(this@RegisterActivity,
                            jObjError.getJSONObject("error").getString("message"),
                            Toast.LENGTH_LONG
                        ).show()
                    } catch (e: Exception) {
                        Toast.makeText(this@RegisterActivity, e.message, Toast.LENGTH_LONG).show()
                    }

                }



            }

            override fun onFailure(call: Call<Objects>, t: Throwable) {
                Toast.makeText(this@RegisterActivity,"post 실패 !!",Toast.LENGTH_LONG).show()
            }
        })
        registBt.setOnClickListener{
            var intent = Intent(this,StickerRegistActivity::class.java)
            startActivity(intent)
        }
    }
    private fun setRetrofitInit(){
        mRetrofit = Retrofit.Builder()
            .baseUrl(URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    private fun setApiServiceInit(){
        apiService = mRetrofit.create(APIService::class.java)
    }
}
