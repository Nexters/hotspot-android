package com.example.hotspot

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_search.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {
    lateinit var searchList: MutableList<Place>

    private val URL : String = "http://hotspot-dev-654767138.ap-northeast-2.elb.amazonaws.com"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        //interceptor 선언
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient.Builder().addInterceptor(interceptor).build()


        val retrofit = Retrofit.Builder()
            .baseUrl(URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        val api = retrofit.create(APIService::class.java)

        btn_search.setOnClickListener {
            api.getPlace(edtTxt_search.text.toString()).enqueue(object : Callback<List<Place>> {
                override fun onResponse(call: Call<List<Place>>, response: Response<List<Place>>) {
                    Log.d("TAG", "FragmentSearch onResponse : ")
                    Log.d("TAG", "responsebody : ${response.body()!![0].addressName}")
                    Log.d("TAG", "responsebody : ${response.body()!![0].kakaoId}")
                    Log.d("TAG", "responsebody : ${response.body()!![0].kakaoUrl}")
                    Log.d("TAG", "responsebody : ${response.body()!![0].placeName}")
                    Log.d("TAG", "responsebody : ${response.body()!![0].roadAddressName}")
                    Log.d("TAG", "responsebody : ${response.body()!![0].x}")
                    Log.d("TAG", "responsebody : ${response.body()!![0].y}")

                    searchList = response.body()!!.toMutableList()
                    search_recyclerview.setHasFixedSize(true)
                    search_recyclerview.layoutManager = LinearLayoutManager(MainActivity())
                    search_recyclerview.adapter = SearchRecyclerAdapter(searchList)
                }

                override fun onFailure(call: Call<List<Place>>, t: Throwable) {
                    Log.d("TAG", "FragmentSearch onFailure : ")

                }
            })
        }


        search_recyclerview.addOnItemTouchListener(
            RecyclerTouchListener(
                applicationContext,
                search_recyclerview,
                object : ClickListener {
                    override fun onClick(view: View?, position: Int) {
                        Log.d("TAG", "startRegister() : ")

                        val intent = Intent(this@SearchActivity, RegisterActivity::class.java)
                        startActivity(intent)
                    }

                    override fun onLongClick(view: View?, position: Int) {}
                })
        )

        //뒤로가기 버튼
        btn_esc2.setOnClickListener {
            finish()
        }
    }
    interface ClickListener {
        fun onClick(view: View?, position: Int)
        fun onLongClick(view: View?, position: Int)
    }

    class RecyclerTouchListener(
        context: Context?,
        recyclerView: RecyclerView,
        private val clickListener: ClickListener?
    ) :
        RecyclerView.OnItemTouchListener {
        private val gestureDetector: GestureDetector
        override fun onInterceptTouchEvent(
            rv: RecyclerView,
            e: MotionEvent
        ): Boolean {
            val child = rv.findChildViewUnder(e.x, e.y)
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildAdapterPosition(child))
            }
            return false
        }

        override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}
        override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}

        init {
            gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
                override fun onSingleTapUp(e: MotionEvent): Boolean {
                    return true
                }

                override fun onLongPress(e: MotionEvent) {
                    val child =
                        recyclerView.findChildViewUnder(e.x, e.y)
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(
                            child,
                            recyclerView.getChildAdapterPosition(child)
                        )
                    }
                }
            })
        }
    }
}