package com.example.hotspot

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.util.Log.d
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.util.FusedLocationSource
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.categoty_items.*
import kotlinx.android.synthetic.main.myplace_item.*
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.Serializable


class MainActivity : AppCompatActivity() {

    private lateinit var mMyPlaceList : List<MyPlace>
    private lateinit var tmpMyPlaceList : List<MyPlace>
    private var myPlaceSize: Int = 0
    private lateinit var mRetrofit: Retrofit
    lateinit var apiService : APIService
    private lateinit var locationSource: FusedLocationSource
    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }
//    val mainScope = MainScope()

    val categoryList = listOf("ALL", "카페", "맛집", "술집", "문화", "기타") // Category List
    var fragmentState : Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        locationSource =
            FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)

        setRetrofitInit()
        setApiServiceInit()
//        getMyPlaceApi()

        category_item1_txt.setOnClickListener {
            category_item1_txt.setTextColor(Color.parseColor("#FFFFFF"))
            category_item2_txt.setTextColor(Color.parseColor("#393D46"))
            category_item3_txt.setTextColor(Color.parseColor("#393D46"))
            category_item4_txt.setTextColor(Color.parseColor("#393D46"))
            category_item5_txt.setTextColor(Color.parseColor("#393D46"))
            category_item6_txt.setTextColor(Color.parseColor("#393D46"))

            d("TAG 전체", "$mMyPlaceList")
            myPlaceSize = mMyPlaceList.size
            hpCount.text = myPlaceSize.toString()
            if(fragmentState)
                getMap(mMyPlaceList)
            else
                getMyPlace(mMyPlaceList)
        }

        category_item2_txt.setOnClickListener {
            var myPlace = mMyPlaceList.toMutableList() //myPlaceList temperary list

            category_item1_txt.setTextColor(Color.parseColor("#393D46"))
            category_item2_txt.setTextColor(Color.parseColor("#FFFFFF"))
            category_item3_txt.setTextColor(Color.parseColor("#393D46"))
            category_item4_txt.setTextColor(Color.parseColor("#393D46"))
            category_item5_txt.setTextColor(Color.parseColor("#393D46"))
            category_item6_txt.setTextColor(Color.parseColor("#393D46"))

            val iter = myPlace.iterator()
            while(iter.hasNext()){
                if(!categoryList[1].equals(iter.next())){
                    iter.remove()
                }
            }
            d("TAG 맛집", "$myPlace")
            myPlaceSize = myPlace.size
            hpCount.text = myPlaceSize.toString()
            if(fragmentState)
                getMap(myPlace)
            else
                getMyPlace(myPlace)
        }

        category_item3_txt.setOnClickListener {
            var myPlace = mMyPlaceList.toMutableList() //myPlaceList temperary list

            category_item1_txt.setTextColor(Color.parseColor("#393D46"))
            category_item2_txt.setTextColor(Color.parseColor("#393D46"))
            category_item3_txt.setTextColor(Color.parseColor("#FFFFFF"))
            category_item4_txt.setTextColor(Color.parseColor("#393D46"))
            category_item5_txt.setTextColor(Color.parseColor("#393D46"))
            category_item6_txt.setTextColor(Color.parseColor("#393D46"))

            val iter = myPlace.iterator()
            while(iter.hasNext()){
                if(!categoryList[2].equals(iter.next())){
                    iter.remove()
                }
            }
            d("TAG 카페", "$myPlace")
            myPlaceSize = myPlace.size
            hpCount.text = myPlaceSize.toString()
            if(fragmentState)
                getMap(myPlace)
            else
                getMyPlace(myPlace)
        }

        category_item4_txt.setOnClickListener {
            var myPlace = mMyPlaceList.toMutableList() //myPlaceList temperary list

            category_item1_txt.setTextColor(Color.parseColor("#393D46"))
            category_item2_txt.setTextColor(Color.parseColor("#393D46"))
            category_item3_txt.setTextColor(Color.parseColor("#393D46"))
            category_item4_txt.setTextColor(Color.parseColor("#FFFFFF"))
            category_item5_txt.setTextColor(Color.parseColor("#393D46"))
            category_item6_txt.setTextColor(Color.parseColor("#393D46"))

            val iter = myPlace.iterator()
            while(iter.hasNext()){
                if(!categoryList[3].equals(iter.next())){
                    iter.remove()
                }
            }
            d("TAG 술집", "$myPlace")
            myPlaceSize = myPlace.size
            hpCount.text = myPlaceSize.toString()
            if(fragmentState)
                getMap(myPlace)
            else
                getMyPlace(myPlace)
        }

        category_item5_txt.setOnClickListener {
            var myPlace = mMyPlaceList.toMutableList() //myPlaceList temperary list

            category_item1_txt.setTextColor(Color.parseColor("#393D46"))
            category_item2_txt.setTextColor(Color.parseColor("#393D46"))
            category_item3_txt.setTextColor(Color.parseColor("#393D46"))
            category_item4_txt.setTextColor(Color.parseColor("#393D46"))
            category_item5_txt.setTextColor(Color.parseColor("#FFFFFF"))
            category_item6_txt.setTextColor(Color.parseColor("#393D46"))

            val iter = myPlace.iterator()
            while(iter.hasNext()){
                if(!categoryList[4].equals(iter.next())){
                    iter.remove()
                }
            }
            d("TAG 문화", "$myPlace")
            myPlaceSize = myPlace.size
            hpCount.text = myPlaceSize.toString()
            if(fragmentState)
                getMap(myPlace)
            else
                getMyPlace(myPlace)
        }

        category_item6_txt.setOnClickListener {
            var myPlace = mMyPlaceList.toMutableList() //myPlaceList temperary list

            category_item1_txt.setTextColor(Color.parseColor("#393D46"))
            category_item2_txt.setTextColor(Color.parseColor("#393D46"))
            category_item3_txt.setTextColor(Color.parseColor("#393D46"))
            category_item4_txt.setTextColor(Color.parseColor("#393D46"))
            category_item5_txt.setTextColor(Color.parseColor("#393D46"))
            category_item6_txt.setTextColor(Color.parseColor("#FFFFFF"))

            val iter = myPlace.iterator()
            while(iter.hasNext()){
                if(!categoryList[5].equals(iter.next())){
                    iter.remove()
                }
            }
            d("TAG 기타", "$myPlace")
            myPlaceSize = myPlace.size
            hpCount.text = myPlaceSize.toString()
            if(fragmentState)
                getMap(myPlace)
            else
                getMyPlace(myPlace)
        }

        //MyList Btn
        listBt.setOnClickListener {
            //fragment operation
            listBt.visibility = View.INVISIBLE
            mapBt.visibility = View.VISIBLE

            category_item1_txt.setTextColor(Color.parseColor("#FFFFFF"))
            category_item2_txt.setTextColor(Color.parseColor("#393D46"))
            category_item3_txt.setTextColor(Color.parseColor("#393D46"))
            category_item4_txt.setTextColor(Color.parseColor("#393D46"))
            category_item5_txt.setTextColor(Color.parseColor("#393D46"))
            category_item6_txt.setTextColor(Color.parseColor("#393D46"))

            myPlaceSize = mMyPlaceList.size
            hpCount.text = myPlaceSize.toString()
            getMyPlace(mMyPlaceList)
        }

        //remove MyPlace Fragment
        mapBt.setOnClickListener {
            mapBt.visibility = View.INVISIBLE
            listBt.visibility = View.VISIBLE

            category_item1_txt.setTextColor(Color.parseColor("#FFFFFF"))
            category_item2_txt.setTextColor(Color.parseColor("#393D46"))
            category_item3_txt.setTextColor(Color.parseColor("#393D46"))
            category_item4_txt.setTextColor(Color.parseColor("#393D46"))
            category_item5_txt.setTextColor(Color.parseColor("#393D46"))
            category_item6_txt.setTextColor(Color.parseColor("#393D46"))

            myPlaceSize = mMyPlaceList.size
            hpCount.text = myPlaceSize.toString()
            getMap(mMyPlaceList)
        }

//            //category Recyclerview init
//            category_recyclerview.setHasFixedSize(true)
//            category_recyclerview.layoutManager = LinearLayoutManager(
//                applicationContext,
//                LinearLayoutManager.HORIZONTAL,
//                false
//            )
//            category_recyclerview.adapter = CategoryRecyclerAdapter(categoryList, mMyPlaceList)

        getMyPlaceApi()

    }

    override fun onResume() {
        super.onResume()
        d("Resune","Resume start!")
    }

    override fun onDestroy() {
        super.onDestroy()

//        mainScope.cancel()
    }

    fun getMyPlaceApi() {
        val accesstoken = GlobalApplication.prefs.getPreferences() // accesstoken
        apiService.getMyPlaces("Bearer " + "${accesstoken}").enqueue(object :
            Callback<GetSpotList> {
            override fun onResponse(call: Call<GetSpotList>, response : Response<GetSpotList>){
                if(response.isSuccessful){
                    d("TAG", "responsebody : ${response.body()!!}")
                    if(response.body() == null){
                        //등록된 장소가 없는 경우 (마커띄울 필요없음)
                        mMyPlaceList = mutableListOf()
                    }
                    else{
                        mMyPlaceList = response.body()!!.myPlaces

                        tmpMyPlaceList = mMyPlaceList
                        myPlaceSize = mMyPlaceList.size
                        hpCount.text = myPlaceSize.toString()
                        getMap(mMyPlaceList)
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


    fun getMyPlace(myPlaceList : List<MyPlace>) {
        d("TAG getMyPlace","myPlaceList : $myPlaceList")
        fragmentState = false

        var bundle = Bundle()

        if(myPlaceList.isNullOrEmpty()) {
            d("TAG getMap", "myPlaceList is null")

            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_map, FragmentMyPlaceEmpty())
                .commit()
        } else {
            d("TAG getMap", "myPlaceList is not null")

            val fr_myPlace = FragmentMyPlace()
            bundle.putSerializable("PlaceList", myPlaceList as Serializable)
            fr_myPlace.arguments = bundle
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_map, fr_myPlace)
                .commitAllowingStateLoss()
        }
    }

    fun getMap(myPlaceList : List<MyPlace>) {
        d("TAG getMap","myPlaceList : $myPlaceList")
        fragmentState = true

        val mapFragment = FragmentMap()
        var bundle = Bundle()

        MapFragment.newInstance()

        d("TAG getMap", "myPlaceList is not null")

        bundle.putSerializable("PlaceList",myPlaceList as Serializable)
        mapFragment.arguments = bundle

        //fragment_naver map 시작
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_map, mapFragment)//mapFragment로 교체
            .commitAllowingStateLoss()//  onSaveInstanceState 이후에 이런 액션(본인의 경우 다른 플래그먼트 호출)을 할수 없어 추가




    }

    fun setRetrofitInit(){
        //interceptor 선언
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient.Builder().addInterceptor(interceptor).build()

        mRetrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }
    fun setApiServiceInit(){
        apiService = mRetrofit.create(APIService::class.java)
    }



}