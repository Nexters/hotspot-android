package com.example.hotspot

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.util.Log.d
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.util.FusedLocationSource
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.activity_sticker_regist.*
import kotlinx.android.synthetic.main.categoty_items.*
import kotlinx.android.synthetic.main.map_view.*
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
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity()  {

    private lateinit var mMyPlaceList : ArrayList<MyPlace>
    private var backPressedTime : Long = 0.toLong()
    private lateinit var toastBackBt : Toast
    private lateinit var tmpMyPlaceList : ArrayList<MyPlace>
    private var myPlaceSize: Int = 0
    private lateinit var mRetrofit: Retrofit
    lateinit var apiService : APIService
    private lateinit var locationSource: FusedLocationSource
    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }
    private lateinit var updatedSpot : MyPlace
    private var update_position = 0
    private var isSpotAdd = false
//    val mainScope = MainScope()

    val categoryList = listOf("ALL", "맛집", "카페", "술집", "문화", "기타") // Category List
    var fragmentState : Boolean = true
    private var stateCategory = "전체"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        locationSource =
            FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)

        setRetrofitInit()
        setApiServiceInit()
//        getMyPlaceApi()


        category_item1_txt2.setOnClickListener {
            stateCategory = "전체"
            category_item1_txt2.setTextColor(Color.parseColor("#FFFFFF"))
            category_item2_txt2.setTextColor(Color.parseColor("#393D46"))
            category_item3_txt2.setTextColor(Color.parseColor("#393D46"))
            category_item4_txt2.setTextColor(Color.parseColor("#393D46"))
            category_item5_txt2.setTextColor(Color.parseColor("#393D46"))
            category_item6_txt2.setTextColor(Color.parseColor("#393D46"))

            d("TAG 전체", "$mMyPlaceList")
            title_category_imgview.setImageResource(R.drawable.img_category_title_all)
            myPlaceSize = mMyPlaceList.size
            hpCount.text = myPlaceSize.toString()
            if(fragmentState)
            else
                getMyPlace(mMyPlaceList,stateCategory)
        }

        category_item2_txt2.setOnClickListener {
            stateCategory = "맛집"
            var myPlace = mMyPlaceList.toMutableList() //myPlaceList temperary list

            category_item1_txt2.setTextColor(Color.parseColor("#393D46"))
            category_item2_txt2.setTextColor(Color.parseColor("#FFFFFF"))
            category_item3_txt2.setTextColor(Color.parseColor("#393D46"))
            category_item4_txt2.setTextColor(Color.parseColor("#393D46"))
            category_item5_txt2.setTextColor(Color.parseColor("#393D46"))
            category_item6_txt2.setTextColor(Color.parseColor("#393D46"))

            val iter = myPlace.listIterator()
            while(iter.hasNext()){
                if(iter.next().place.categoryName.isNullOrEmpty()){
                    iter.remove()
                }
                else if(!categoryList[1].equals(iter.previous().place.categoryName)){
                    iter.remove()
                }
                else{
                    iter.next()
                }
            }
            d("TAG 맛집", "$myPlace")
            title_category_imgview.setImageResource(R.drawable.img_category_title_food)
            myPlaceSize = myPlace.size
            hpCount.text = myPlaceSize.toString()
            if(fragmentState)
            else
                getMyPlace(myPlace,stateCategory)
        }

        category_item3_txt2.setOnClickListener {
            stateCategory = "카페"
            var myPlace = mMyPlaceList.toMutableList() //myPlaceList temperary list

            category_item1_txt2.setTextColor(Color.parseColor("#393D46"))
            category_item2_txt2.setTextColor(Color.parseColor("#393D46"))
            category_item3_txt2.setTextColor(Color.parseColor("#FFFFFF"))
            category_item4_txt2.setTextColor(Color.parseColor("#393D46"))
            category_item5_txt2.setTextColor(Color.parseColor("#393D46"))
            category_item6_txt2.setTextColor(Color.parseColor("#393D46"))

            val iter = myPlace.listIterator()
            while(iter.hasNext()){
                if(iter.next().place.categoryName.isNullOrEmpty()){
                    iter.remove()
                }
                else if(!categoryList[2].equals(iter.previous().place.categoryName)){
                    iter.remove()
                }
                else{
                    iter.next()
                }
            }
            d("TAG 카페", "$myPlace")
            title_category_imgview.setImageResource(R.drawable.img_category_title_cafe)
            myPlaceSize = myPlace.size
            hpCount.text = myPlaceSize.toString()
            if(fragmentState)
            else
                getMyPlace(myPlace,stateCategory)
        }

        category_item4_txt2.setOnClickListener {
            stateCategory = "술집"
            var myPlace = mMyPlaceList.toMutableList() //myPlaceList temperary list

            category_item1_txt2.setTextColor(Color.parseColor("#393D46"))
            category_item2_txt2.setTextColor(Color.parseColor("#393D46"))
            category_item3_txt2.setTextColor(Color.parseColor("#393D46"))
            category_item4_txt2.setTextColor(Color.parseColor("#FFFFFF"))
            category_item5_txt2.setTextColor(Color.parseColor("#393D46"))
            category_item6_txt2.setTextColor(Color.parseColor("#393D46"))

            val iter = myPlace.listIterator()
            while(iter.hasNext()){
                if(iter.next().place.categoryName.isNullOrEmpty()){
                    iter.remove()
                }
                else if(!categoryList[3].equals(iter.previous().place.categoryName)){
                    iter.remove()
                }
                else{
                    iter.next()
                }
            }
            d("TAG 술집", "$myPlace")
            title_category_imgview.setImageResource(R.drawable.img_category_title_drink)
            myPlaceSize = myPlace.size
            hpCount.text = myPlaceSize.toString()
            if(fragmentState)
            else
                getMyPlace(myPlace,stateCategory)
        }

        category_item5_txt2.setOnClickListener {
            stateCategory = "문화"
            var myPlace = mMyPlaceList.toMutableList() //myPlaceList temperary list

            category_item1_txt2.setTextColor(Color.parseColor("#393D46"))
            category_item2_txt2.setTextColor(Color.parseColor("#393D46"))
            category_item3_txt2.setTextColor(Color.parseColor("#393D46"))
            category_item4_txt2.setTextColor(Color.parseColor("#393D46"))
            category_item5_txt2.setTextColor(Color.parseColor("#FFFFFF"))
            category_item6_txt2.setTextColor(Color.parseColor("#393D46"))

            val iter = myPlace.listIterator()
            while(iter.hasNext()){
                if(iter.next().place.categoryName.isNullOrEmpty()){
                    iter.remove()
                }
                else if(!categoryList[4].equals(iter.previous().place.categoryName)){
                    iter.remove()
                }
                else{
                    iter.next()
                }
            }
            d("TAG 문화", "$myPlace")
            title_category_imgview.setImageResource(R.drawable.img_category_title_culture)
            myPlaceSize = myPlace.size
            hpCount.text = myPlaceSize.toString()
            if(fragmentState)
            else
                getMyPlace(myPlace,stateCategory)
        }

        category_item6_txt2.setOnClickListener {
            stateCategory = "기타"
            var myPlace = mMyPlaceList.toMutableList() //myPlaceList temperary list

            category_item1_txt2.setTextColor(Color.parseColor("#393D46"))
            category_item2_txt2.setTextColor(Color.parseColor("#393D46"))
            category_item3_txt2.setTextColor(Color.parseColor("#393D46"))
            category_item4_txt2.setTextColor(Color.parseColor("#393D46"))
            category_item5_txt2.setTextColor(Color.parseColor("#393D46"))
            category_item6_txt2.setTextColor(Color.parseColor("#FFFFFF"))

            val iter = myPlace.listIterator()
            while(iter.hasNext()){
                if(iter.next().place.categoryName.isNullOrEmpty()){

                }
                else if(!categoryList[5].equals(iter.previous().place.categoryName)){
                    iter.remove()
                }
                else{
                    iter.next()
                }
            }
            d("TAG 기타", "$myPlace")
            title_category_imgview.setImageResource(R.drawable.img_category_title_etc)
            myPlaceSize = myPlace.size
            hpCount.text = myPlaceSize.toString()
            if(fragmentState)
            else
                getMyPlace(myPlace,stateCategory)
        }

        //MyList Btn
        listBt.setOnClickListener {
            stateCategory = "전체"
            //fragment operation

            categoryframe.visibility = View.INVISIBLE
            categoryframe2.visibility = View.VISIBLE
            spotinfolayout.visibility = View.INVISIBLE
            layout_trans_main.visibility = View.INVISIBLE

            category_item1_txt2.setTextColor(Color.parseColor("#FFFFFF"))
            category_item2_txt2.setTextColor(Color.parseColor("#393D46"))
            category_item3_txt2.setTextColor(Color.parseColor("#393D46"))
            category_item4_txt2.setTextColor(Color.parseColor("#393D46"))
            category_item5_txt2.setTextColor(Color.parseColor("#393D46"))
            category_item6_txt2.setTextColor(Color.parseColor("#393D46"))

            myPlaceSize = mMyPlaceList.size
            hpCount.text = myPlaceSize.toString()
            getMyPlace(mMyPlaceList,stateCategory)
        }

        //remove MyPlace Fragment
        mapBt.setOnClickListener {

            categoryframe.visibility = View.VISIBLE
            categoryframe2.visibility = View.INVISIBLE


            category_item1_txt.setTextColor(Color.parseColor("#FFFFFF"))
            category_item2_txt.setTextColor(Color.parseColor("#393D46"))
            category_item3_txt.setTextColor(Color.parseColor("#393D46"))
            category_item4_txt.setTextColor(Color.parseColor("#393D46"))
            category_item5_txt.setTextColor(Color.parseColor("#393D46"))
            category_item6_txt.setTextColor(Color.parseColor("#393D46"))

            myPlaceSize = mMyPlaceList.size
            hpCount.text = myPlaceSize.toString()
            getMap(mMyPlaceList,false)
        }

        findBt.setOnClickListener {
            if(myPlaceSize == 0){
                val intent = Intent(this, MyPlaceIsEmpty::class.java)
                startActivityForResult(intent, 6)
            }
            else {
                val intent = Intent(this, MySearchActivity::class.java)
                intent.putExtra("myPlace", mMyPlaceList as Serializable)
                startActivityForResult(intent, 5)
            }
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

    fun getMyPlaceApi() {
        val accesstoken = GlobalApplication.prefs.getPreferences() // accesstoken
        apiService.getMyPlaces("Bearer " + "${accesstoken}").enqueue(object :
            Callback<GetSpotList> {
            override fun onResponse(call: Call<GetSpotList>, response : Response<GetSpotList>){
                if(response.isSuccessful){
                    d("TAG", "responsebody : ${response.body()!!}")
                    if(response.body() == null){
                        //등록된 장소가 없는 경우 (마커띄울 필요없음)
                        mMyPlaceList = arrayListOf()
                    }
                    else{
                        mMyPlaceList = response.body()!!.myPlaces as ArrayList<MyPlace>
                        findBt.visibility = View.VISIBLE
                        tmpMyPlaceList = mMyPlaceList
                        myPlaceSize = mMyPlaceList.size
                        hpCount.text = myPlaceSize.toString()
                        getMap(mMyPlaceList,isSpotAdd)
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
                Toast.makeText(this@MainActivity,"장소를 불러오지 못했습니다, 네트워크를 확인바랍니다.", Toast.LENGTH_LONG).show()
            }
        })
    }


    fun getMyPlace(myPlaceList : List<MyPlace>,stateCategory: String) {
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
            bundle.putSerializable("CateGory",stateCategory as Serializable)
            fr_myPlace.arguments = bundle
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_map, fr_myPlace)
                .commitAllowingStateLoss()
        }
    }

    fun getMap(myPlaceList : List<MyPlace>, isSpotAdd : Boolean) {
        d("TAG getMap","myPlaceList : $myPlaceList")
        fragmentState = true

        val mapFragment = FragmentMap()
        var bundle = Bundle()

        MapFragment.newInstance()

        d("TAG getMap", "myPlaceList is not null")

        bundle.putSerializable("PlaceList",myPlaceList as Serializable)
        bundle.putSerializable("IsSpotAdd", isSpotAdd as Serializable)
        this.isSpotAdd = false
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == 9) { // 장소 등록 안함 맵 > 등록
            BusProvider.getInstance().post(ActivityResultEvent(requestCode, resultCode, data))
        }
        if(resultCode == 10){// 장소등록 성공 맵 > 등록
            //애니메이션 띄우고
            //장소 새로 받기?
            isSpotAdd = true
            getMyPlaceApi()
        }
        if(resultCode == 98){ // 마이플레이스 > 디테일 업데이트 성공
            //getMyplace 요청
            if(data != null) {
                updatedSpot = data.getSerializableExtra("NewSpotInfo")as MyPlace
                update_position = data.getIntExtra("Position",0)
                //BusProvider.getInstance().post(ActivityResultEvent(requestCode, resultCode, data))
                mMyPlaceList.set(update_position,updatedSpot)
                getMyPlace(mMyPlaceList,stateCategory)
            }
        }
        if(resultCode == 97){ // 맵 > 디테일 업데이트 성공
            if(data != null) {
                updatedSpot = data.getSerializableExtra("NewSpotInfo")as MyPlace
                update_position = data.getIntExtra("Position",0)
                //BusProvider.getInstance().post(ActivityResultEvent(requestCode, resultCode, data))
                mMyPlaceList.set(update_position,updatedSpot)
                spotinfolayout.visibility = View.INVISIBLE
                layout_trans_main.visibility = View.INVISIBLE
                getMap(mMyPlaceList,false)
            }
        }
        //새장소 편집시 > 리스트에 반영 후 myplace fragment로 onActivityresult 전달  ( 편집시에는 myPlace가 있지만 새장 소 등록할때는 myPlace가 아니다)
        //myplace framgent에서 리스트에 추가후  어뎁터 에게 알리기

    }

    override fun onBackPressed() {
        var currentFragment = getVisibleFragment()
        if(currentFragment is FragmentMap){
            if(layout_trans_main.isVisible){
                spotinfolayout.visibility = View.GONE
                layout_trans_main.visibility = View.INVISIBLE
                img_curr_pos.visibility = View.VISIBLE
                img_main_isvisited.visibility = View.VISIBLE
                map_btn_add.visibility = View.VISIBLE
                return
            }
        }

        if(System.currentTimeMillis() > backPressedTime + 2000.toLong()){
            backPressedTime = System.currentTimeMillis()
            toastBackBt = Toast.makeText(this," 뒤로 버튼을 한번 더 누르면 종료됩니다.",Toast.LENGTH_LONG)
            toastBackBt.show()
            return
        }
        if(System.currentTimeMillis() <= backPressedTime + 2000){
            finish()
            toastBackBt.cancel()
        }


    }
    private fun getVisibleFragment() : Fragment? {
        for(fragment in supportFragmentManager.fragments){
            if(fragment.isVisible){
                return fragment
            }
        }
        return null
    }




}