package com.example.hotspot

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.Serializable


class MainActivity : AppCompatActivity(){
    private lateinit var btn1 : Button
    private lateinit var mMyPlaceList : List<MyPlace>
    private lateinit var mRetrofit: Retrofit
    lateinit var apiService : APIService
    private val URL : String = "http://hotspot-dev-654767138.ap-northeast-2.elb.amazonaws.com"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setRetrofitInit()
        setApiServiceInit()

//        val mapView = MapView(this)
//        val mapViewContainer = map_view as ViewGroup
//        mapView.setMapViewEventListener(this)
//        mapViewContainer.addView(mapView)

        //fragment_kakaomap 시작
//        supportFragmentManager.beginTransaction()
//            .add(R.id.fragment_map, FragmentMap())
//            .addToBackStack(null)
//            .commit()



    }

    override fun onResume() {
        super.onResume()

        val fr_myPlace = FragmentMyPlace()

        //MyList Btn
        listBt.setOnClickListener {
            //fragment operation
            listBt.visibility = View.INVISIBLE
            mapBt.visibility = View.VISIBLE

            var bundle = Bundle()
            bundle.putSerializable("PlaceList", mMyPlaceList as Serializable)

            fr_myPlace.arguments = bundle

            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_map, fr_myPlace)
                .commit()
        }

        //remove MyPlace Fragment
        mapBt.setOnClickListener {
            mapBt.visibility = View.INVISIBLE
            listBt.visibility = View.VISIBLE

            supportFragmentManager.beginTransaction()
                .remove(fr_myPlace)
                .commit()
        }

        val accesstoken = GlobalApplication.prefs.getPreferences()
        apiService.getMyPlaces("Bearer " + "${accesstoken}").enqueue(object :
            Callback<GetSpotList> {
            override fun onResponse(call: Call<GetSpotList>, response : Response<GetSpotList>){
                if(response.isSuccessful){
                    Log.d("TAG", "responsebody : ${response.body()!!}")
                    if(response.body() == null){
                        //등록된 장소가 없는 경우 (마커띄울 필요없음)
                        mMyPlaceList = arrayListOf()
                    }
                    else{
                        mMyPlaceList = response.body()!!.myPlaces
                        var bundle = Bundle()
                        bundle.putSerializable("PlaceList",mMyPlaceList as Serializable)
                        //fragment_kakaomap 시작
                        var transaction : FragmentTransaction = supportFragmentManager.beginTransaction()
                        val mapFragment = FragmentMap()
                        mapFragment.arguments = bundle
                        transaction
                            .replace(R.id.fragment_map, mapFragment)//fragment1로 교체해라
                            .addToBackStack(null)
                            .commit()//transaction 새로고침
                    }
                }else {
                    try {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        Toast.makeText(
                            this@MainActivity,
                            jObjError.getJSONObject("error").getString("message"),
                            Toast.LENGTH_LONG
                        ).show()
                    } catch (e: Exception) {
                        Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_LONG).show()

                    }
                }
            }

            override fun onFailure(call: Call<GetSpotList>, t: Throwable) {
                Toast.makeText(this@MainActivity,"get 실패 !!!", Toast.LENGTH_LONG).show()
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