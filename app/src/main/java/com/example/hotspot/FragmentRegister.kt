package com.example.hotspot

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.util.Log.d
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.register_view.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class FragmentRegister : Fragment() {
    private lateinit var mRetrofit: Retrofit
    lateinit var apiService : APIService
    lateinit var place: Place
    private val URL : String = "http://hotspot-dev-654767138.ap-northeast-2.elb.amazonaws.com"
    private lateinit var spotList : SpotListVO

    private var accessToken = GlobalApplication.prefs.getPreferences()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.register_view, container, false)
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        setRetrofitInit()
        setApiServiceInit()

        setLayout()


        btn_esc3.setOnClickListener {
            fragmentManager!!.beginTransaction()
                .remove(this)
                .commit()
            activity!!.finish()
        }

        txt_place_name.setOnClickListener {
            fragmentManager!!.beginTransaction()
                .replace(R.id.register_activity, FragmentSearch())
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

        stickerBt.setOnClickListener{

            var intent = Intent(activity, StickerRegistActivity::class.java)
            startActivity(intent)

        }

        btn_regist.setOnClickListener{
            if(txt_place_name.text == "" || txt_place_name.text == "장소 이름"){
                Toast.makeText(activity,
                    "장소 이름을 작성해주세요.",
                    Toast.LENGTH_SHORT)
                    .show()
            }
            else registSpot()
        }
    }

    private fun setLayout(){
        val isAdd = arguments!!.getBoolean("isAdd")
        val search_OK = arguments!!.getBoolean("search_OK")
        d("TAG", "setLayout() isAdd : ${isAdd}")

        if(isAdd == false) {//수정 이기때문에 장소에 대한 정보 뿌리기
            val place = arguments!!.getSerializable("place") as Place

            txt_place_name.text = place.placeName
            txt_address.text = place.roadAddressName
        }
        if(search_OK) {
            val place = arguments!!.getSerializable("searchPlace") as Place

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
                    Log.d("TAG", "RegisterActivity onResponse() ")

                } else {
                    try {
                        /*val jObjError = JSONObject(response.errorBody()!!.string())

                        Toast.makeText(this@RegisterActivity,
                            jObjError.getJSONObject("error").getString("message"),
                            Toast.LENGTH_LONG
                        ).show()*/


                    } catch (e: Exception) {
                        Toast.makeText(activity, e.message, Toast.LENGTH_LONG).show()
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("TAG", "RegisterActivity onFailure() ")
                Toast.makeText(activity,"post 실패 !!", Toast.LENGTH_LONG).show()
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