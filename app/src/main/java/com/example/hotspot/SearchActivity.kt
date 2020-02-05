package com.example.hotspot

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Log.d
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
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
import java.io.Serializable

class SearchActivity : Fragment() {
    lateinit var searchList: List<Place>

    private val URL : String = "http://hotspot-dev-654767138.ap-northeast-2.elb.amazonaws.com"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.activity_search, container, false)
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

                    searchList = response.body()!!.toList()
                    search_recyclerview.setHasFixedSize(true)
                    search_recyclerview.layoutManager = LinearLayoutManager(MainActivity())
                    search_recyclerview.adapter = SearchRecyclerAdapter(searchList)
                }

                override fun onFailure(call: Call<List<Place>>, t: Throwable) {
                    Log.d("TAG", "FragmentSearch onFailure : ")

                }
            })
        }

        //뒤로가기 버튼
        search_esc_btn.setOnClickListener {
            fragmentManager!!.beginTransaction()
                .remove(this)
                .commit()
        }




        search_recyclerview.addOnItemTouchListener(
            RecyclerTouchListener(
                activity,
                search_recyclerview,
                object : ClickListener {
                    override fun onClick(view: View?, position: Int) {
                        d("TAG", "startRegister() : ")
//                        val fr_reg = RegisterActivity()
//                        val bundle = Bundle()
//                        bundle.putSerializable("place", searchList.get(position) as Serializable)

                        val intent = Intent(activity, RegisterActivity::class.java)
                        intent.putExtra("place", searchList.get(position))
                        startActivity(intent)
                    }

                    override fun onLongClick(view: View?, position: Int) {}
                })
        )


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