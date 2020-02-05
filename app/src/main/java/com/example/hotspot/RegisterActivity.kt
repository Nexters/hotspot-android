package com.example.hotspot

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log.d
import android.view.View
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
    private lateinit var accessToken: String
    private lateinit var spotList : SpotListVO


    private var isAdd = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        isAdd = intent.getBooleanExtra("IsAdd",true) // true면 등록 , false면 수정

        setRetrofitInit()
        setApiServiceInit()
        accessToken = GlobalApplication.prefs.getPreferences()
        setLayout()

        btn_esc3.setOnClickListener {
            finish()
        }

        txt_place_name.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .add(R.id.register_activity, SearchActivity())
                .commit()
        }

        txt_visited.setOnClickListener{
            if(ratingBar.visibility == View.GONE){
                ratingBar.visibility = View.VISIBLE
                txt_rating_info.visibility = View.VISIBLE
                txt_visited.setTextColor(Color.WHITE)
            }
            else{
                ratingBar.visibility = View.GONE
                txt_rating_info.visibility = View.GONE
                txt_visited.setTextColor(resources.getColor(R.color.colorMyGrayDark))

            }
        }

        btn_regist.setOnClickListener{

            registSpot()
        }

        stickerBt.setOnClickListener{
            var intent = Intent(this,StickerRegistActivity::class.java)
            startActivity(intent)
        }





    }
    private fun setLayout(){
        if(isAdd == false) {//수정 이기때문에 장소에 대한 정보 뿌리기
            val intent = getIntent()
            place = intent.getSerializableExtra("place") as Place
            txt_place_name.text = place.placeName
            txt_address.text = place.roadAddressName
        }


    }
    private fun registSpot(){
        var isVisited = true
        if(txt_visited.currentTextColor == Color.WHITE){
            isVisited = false
        }
        else isVisited = true
        spotList = SpotListVO(place,isVisited,edtTxt_memo.text.toString(),ratingBar.rating.toInt())

        apiService.postPlace("Bearer " + "${accessToken}",
            spotList).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    d("TAG", "RegisterActivity onResponse() ")

                } else {
                    try {
                        /*val jObjError = JSONObject(response.errorBody()!!.string())

                        Toast.makeText(this@RegisterActivity,
                            jObjError.getJSONObject("error").getString("message"),
                            Toast.LENGTH_LONG
                        ).show()*/


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
