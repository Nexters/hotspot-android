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
    lateinit var place: Place
    private val URL : String = "http://hotspot-dev-654767138.ap-northeast-2.elb.amazonaws.com"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        setRetrofitInit()
        setApiServiceInit()


        reg_esc_btn.setOnClickListener {
            finish()
        }

        reg_spec_btn.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .add(R.id.register_activity, SearchActivity())
                .commit()
        }

        registBt.setOnClickListener{
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
        }





    }

    override fun onPause() {
        super.onPause()

        val intent = getIntent()
        place = intent.getSerializableExtra("place") as Place


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
