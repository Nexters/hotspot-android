package com.example.hotspot

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log.d
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_search.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.collections.ArrayList

var mySearch:Boolean = false

class MySearchActivity : AppCompatActivity() {

    private lateinit var myPlace: ArrayList<MyPlace>
    private lateinit var newPlace : MyPlace
    private var myPlaceSize = 0
    lateinit var place : ArrayList<MyPlace>
    private var recyclerAdapter: MySearchRecyclerAdapter? = null
    private var position = 0
    private var isUpdate = false
    private var isDelete = false
    private lateinit var mRetrofit: Retrofit
    lateinit var apiService : APIService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        setRetrofitInit()
        setApiServiceInit()

        myPlace = intent.getSerializableExtra("myPlace") as ArrayList<MyPlace> // myPlace data
        myPlaceSize = myPlace.size // myPlace SIZE

        place = ArrayList()
        place.addAll(myPlace)

        //recyclerview init
        recyclerViewInit()

        //back btn
        search_esc_imgbtn.setOnClickListener {

            var intent = Intent()

//        if(isUpdate) {
            isUpdate = false
//            intent.putExtra("Position", position)
            intent.putExtra("NewSpotInfo", myPlace)
            this.setResult(1, intent)
//        }
//        if(isDelete) {
//            isDelete = false
//            intent.putExtra("myPlace", myPlace)
//            this.setResult(2, intent)
//        }
            this.finish()
        }

        //editText event
        search_edtTxt!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence,
                i: Int,
                i1: Int,
                i2: Int
            ) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                d("onTextChanged()", "char : ${charSequence}")
                if(charSequence.length > 1 && myPlaceSize > 0) {
                    recyclerAdapter!!.getFilter().filter(charSequence)


                    if (recyclerAdapter!!.itemCount != 0) {
                        mysearch_empty_layout.visibility = View.VISIBLE
                        search_recyclerview.visibility = View.GONE
                    } else {
                        mysearch_empty_layout.visibility = View.GONE
                        search_recyclerview.visibility = View.VISIBLE
                        if (charSequence == "") {
                            resetData()
                        }
                    }
                }
            }
            override fun afterTextChanged(editable: Editable) {}
        })

        //delete Btn
        search_delete_imgbtn.setOnClickListener {
            search_edtTxt.setText("")
            resetData()
        }

        myplace_empty_imgbtn.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            val isAdd = true
            mySearch = true
            intent.putExtra("IsAdd", isAdd)
            startActivityForResult(intent, 3)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        d("TAG onActivityResult", "&&&&&&&&&&&&&&&&&&&&&&&&&&&&")
        if(resultCode == 95) {// 디테일뷰 > 장소 삭제
            if(data != null ) {
//                position = data.getIntExtra("position", 0)
//                myPlace.removeAt(position)
//                myPlaceSize = myPlace.size
//                place = myPlace
//                isDelete = true
//                d("TAG onActivityResult", "position : $position")
//                recyclerViewInit()
                resetData()
            }
        }

        if(resultCode == 85) {
            if(data != null ) {
//                newPlace = data.getSerializableExtra("NewSpotInfo") as MyPlace
//                position = data.getIntExtra("Position", 0)
//                myPlaceSize = myPlace.size
//
//                myPlace[position] = newPlace
//
//                d("TAG onActivityResult", "myPlace : $newPlace / myPlaceSize : ${myPlaceSize}")
//                isUpdate = true
//                recyclerViewInit()
                resetData()
            }
        }

        else if(resultCode == 11){

            if(data != null){
                resetData()
            }
        }
    }

    fun recyclerViewInit () {
        d("TAG", "myPlaceSize : $myPlaceSize")
        if(myPlaceSize == 0) {
            mysearch_empty_layout.visibility = View.VISIBLE
            search_recyclerview.visibility = View.GONE
        }else {
            d("TAG", "aaaaaaaaaaaaaaaaaaaaa")
            mysearch_empty_layout.visibility = View.GONE
            search_recyclerview.visibility = View.VISIBLE

            search_recyclerview.setHasFixedSize(true)
            search_recyclerview.layoutManager =
                LinearLayoutManager(this)
            recyclerAdapter = MySearchRecyclerAdapter(this, place, myPlace)
            search_recyclerview.adapter = recyclerAdapter
        }
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

    fun resetData() {
        val accesstoken = GlobalApplication.prefs.getPreferences() // accesstoken
        apiService.getMyPlaces("Bearer " + "${accesstoken}").enqueue(object :
            Callback<GetSpotList> {
            override fun onResponse(
                call: Call<GetSpotList>,
                response: Response<GetSpotList>
            ) {
                d("TAG MySearch", "onResponse")
                if(response.isSuccessful) {
                    d("TAG MySearch","response Body : ${response.body()}")
                    myPlace = response.body()!!.myPlaces as ArrayList<MyPlace>
                    myPlaceSize = myPlace.size
                    place = myPlace


                    d("TAG onActivityResult", "myPlace : $myPlace")

                    recyclerViewInit()
                }
            }

            override fun onFailure(call: Call<GetSpotList>, t: Throwable) {
                d("TAG MySearch", "onFailure")

            }
        })
    }

    override fun onBackPressed() {
        var intent = Intent()

//        if(isUpdate) {
            isUpdate = false
//            intent.putExtra("Position", position)
            intent.putExtra("NewSpotInfo", myPlace)
            this.setResult(1, intent)
//        }
//        if(isDelete) {
//            isDelete = false
//            intent.putExtra("myPlace", myPlace)
//            this.setResult(2, intent)
//        }
        this.finish()
    }
}