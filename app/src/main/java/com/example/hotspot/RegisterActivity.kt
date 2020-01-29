package com.example.hotspot

import android.content.Intent
import android.os.Bundle
import android.util.Log.d
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_register.*
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


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

        val intent = getIntent()
        val place = intent.getSerializableExtra("place") as Place

//        var place = Place("247906074","http://place.map.kakao.com/247906074",
//            "아모레성수","서울 성동구 성수동2가 277-52",
//            "서울 성동구 아차산로11길 7","127.059040730967",
//            "37.5445477220243"
//        )

        val accesstoken = GlobalApplication.prefs.getPreferences()


        var spotList = SpotListVO(place,true,"분위기가 멋진 곳!",3)

        apiService.postPlace("Bearer " + "${accesstoken}",
            spotList).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    d("TAG", "RegisterActivity onResponse() ")

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

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                d("TAG", "RegisterActivity onFailure() ")
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
