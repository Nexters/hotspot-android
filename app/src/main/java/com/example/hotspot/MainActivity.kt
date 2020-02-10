package com.example.hotspot

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.util.Log.d
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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


class MainActivity : AppCompatActivity(){

    private lateinit var mMyPlaceList : List<MyPlace>
    private lateinit var mRetrofit: Retrofit
    lateinit var apiService : APIService
    lateinit var myPlace: List<MyPlace>
    private val URL : String = "https://api.dev.hotspot-team.com"
    val mainScope = MainScope()

    val categoryList = listOf("ALL", "카페", "맛집", "술집", "문화", "기타") // Category List


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainScope.launch {

            //delay 수정해야함
            delay(1500L)
            getMap(mMyPlaceList)

            //category Recyclerview init
            category_recyclerview.setHasFixedSize(true)
            category_recyclerview.layoutManager = LinearLayoutManager(
                applicationContext,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            category_recyclerview.adapter = CategoryRecyclerAdapter(categoryList, mMyPlaceList)

            //MyList Btn
            listBt.setOnClickListener {
                //fragment operation
                listBt.visibility = View.GONE
                mapBt.visibility = View.VISIBLE

                getMyPlace()
            }

            //remove MyPlace Fragment
            mapBt.setOnClickListener {
                mapBt.visibility = View.GONE
                listBt.visibility = View.VISIBLE

                getMap(mMyPlaceList)
            }
        }
        runBlocking {
            setRetrofitInit()
            setApiServiceInit()
            getMyPlaceApi()
        }


    }

    override fun onDestroy() {
        super.onDestroy()

        mainScope.cancel()
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


    fun getMyPlace() {

            if (::mMyPlaceList.isInitialized) {

                val fr_myPlace = FragmentMyPlace()

                var bundle = Bundle()
                bundle.putSerializable("PlaceList", mMyPlaceList as Serializable)

                fr_myPlace.arguments = bundle
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_map, fr_myPlace)
                .commitAllowingStateLoss()
        }
    }

    fun getMap(myPlaceList : List<MyPlace>) {
        val mapFragment = FragmentMap()

        var bundle = Bundle()

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
            .baseUrl(URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }
    fun setApiServiceInit(){
        apiService = mRetrofit.create(APIService::class.java)
    }
}