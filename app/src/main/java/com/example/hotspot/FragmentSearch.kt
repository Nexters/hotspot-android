package com.example.hotspot

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.util.Log.d
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.register_view.*
import kotlinx.coroutines.*
import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.Serializable
import kotlin.coroutines.coroutineContext

class FragmentSearch : Fragment() {
    lateinit var searchList: List<Place>
    private var mainScope = MainScope()

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

        var search_OK = false

        //interceptor 선언
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient.Builder().addInterceptor(interceptor).build()


        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        val api = retrofit.create(APIService::class.java)

        search_delete_btn.setOnClickListener {
            search_edtTxt.setText("")
        }


        search_edtTxt.doOnTextChanged {text, start, count, after ->
            d("TAG", "doOnTextChanged : ${text.toString()}, ${start}, $count, $after")


            // 코루틴 동작
            mainScope.launch {
                delay(500)
                d("TAG", "doOnTextChanged : text : ${text.toString()}, length : ${text.toString().length}")


                if(text.toString().length >= 2) {
                    api.getPlace(text.toString()).enqueue(object : Callback<List<Place>> {
                        override fun onResponse(
                            call: Call<List<Place>>,
                            response: Response<List<Place>>
                        ) {
                            d("TAG", "FragmentSearch onResponse : ")

                            if (response != null) {
                                searchList = response.body()!!.toList()
                                search_recyclerview.setHasFixedSize(true)
                                search_recyclerview.layoutManager =
                                    LinearLayoutManager(MainActivity())
                                search_recyclerview.adapter = SearchRecyclerAdapter(searchList)
                            }

                        }

                        override fun onFailure(call: Call<List<Place>>, t: Throwable) {
                            d("TAG", "FragmentSearch onFailure : ")

                        }
                    })
                }
            }
        }


        search_recyclerview.addOnItemTouchListener(
            RecyclerTouchListener(
                activity,
                search_recyclerview,
                object : ClickListener {
                    override fun onClick(view: View?, position: Int) {
                        d("TAG", "Recycler onClick() : ")
                        d("TAG", "searchList : ${searchList.get(position)}")

                        search_OK= true // 서치 완료됫다는 신호
                        val fr_reg = FragmentRegister()
                        val bundle = Bundle()
                        bundle.putSerializable("searchPlace", searchList.get(position) as Serializable)
                        bundle.putBoolean("search_OK", search_OK)
                        bundle.putBoolean("isAdd", true)
                        fr_reg.arguments = bundle

                        fragmentManager!!.popBackStack()

                        fragmentManager!!.beginTransaction()
                            .replace(R.id.register_activity, fr_reg)
                            .commit()
                    }

                    override fun onLongClick(view: View?, position: Int) {}
                })
        )

    }

    override fun onDestroy() {
        super.onDestroy()
        d("TAG", "Coroutine cancel() ")
        // 코루틴 종료
        mainScope.cancel()
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