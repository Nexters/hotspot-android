package com.example.hotspot

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.util.Log.d
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.mylist_view.*
import java.io.Serializable
import androidx.recyclerview.widget.RecyclerView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.ItemTouchHelper
import com.squareup.otto.Subscribe
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class FragmentMyPlace : Fragment() {

    var isvisitedState = 0
    private lateinit var placeList:MutableList<MyPlace> // 메인액티비티에서 넘어온(카테고리별로 넘어온) 모든 장소들을 담고있는 리스트
    private lateinit var stateCategory : String
    private lateinit var currentList : ArrayList<MyPlace>   //현재 화면에 보여지고 있는 리스트
    lateinit var cardStyleList : ArrayList<Drawable>
    private lateinit var itemTouchHelper : ItemTouchHelper
    private var vibrateOK = true
    private lateinit var mRetrofit: Retrofit
    lateinit var apiService : APIService
    private var myPlaceSize = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.mylist_view,container,false)
        activity!!.mapBt.visibility = View.VISIBLE
        activity!!.listBt.visibility = View.INVISIBLE
        return v
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setRetrofitInit()
        setApiServiceInit()


        cardStyleList = arrayListOf()
        cardStyleList.add(resources.getDrawable(R.drawable.ic_myplace_list_img1))
        cardStyleList.add(resources.getDrawable(R.drawable.ic_myplace_list_img2))
        cardStyleList.add(resources.getDrawable(R.drawable.ic_myplace_list_img3))
        cardStyleList.add(resources.getDrawable(R.drawable.ic_myplace_list_img4))
        cardStyleList.add(resources.getDrawable(R.drawable.ic_myplace_list_img5))
        cardStyleList.add(resources.getDrawable(R.drawable.ic_rectangle))

        placeList = arguments!!.getSerializable("PlaceList") as MutableList<MyPlace>
        stateCategory = arguments!!.getSerializable("CateGory") as String
        myPlaceSize = placeList.size


        currentList = arrayListOf()
        for(i in (0..myPlaceSize-1)){
            currentList.add(placeList.get(i))
        }

        if(myPlaceSize == 0) {
            mylistEmptyimg.visibility = View.VISIBLE
        }

        else {
            mylistEmptyimg.visibility = View.GONE
            myplace_recyclerview.setHasFixedSize(true)
            myplace_recyclerview.layoutManager = LinearLayoutManager(context)
            recyclerviewInit(currentList, 0)
            itemTouchHelper = ItemTouchHelper(ItemSwipeHelper())
            itemTouchHelper.attachToRecyclerView(myplace_recyclerview)

        }


        myplace_isvisited.setOnClickListener {
            if(isvisitedState>=2)
                isvisitedState = 0
            else
                isvisitedState++

            if(isvisitedState == 0)
                changeCategory(0, placeList as ArrayList<MyPlace>)
            else if(isvisitedState == 1)
                changeCategory(1, placeList as ArrayList<MyPlace>)
            else if(isvisitedState == 2)
                changeCategory(2, placeList as ArrayList<MyPlace>)
        }

        myplace_add_btn.setOnClickListener {
            val intent = Intent(activity, RegisterActivity::class.java)
            val isAdd = true
            intent.putExtra("IsAdd",isAdd)
            startActivityForResult(intent,10)
        }

    }

    fun recyclerviewInit(list: MutableList<MyPlace>, state: Int) {
        myplace_recyclerview.adapter = MyPlaceRecyclerAdapter(list as ArrayList<MyPlace>, cardStyleList, state,activity!!)
    }

    fun changeCategory(state : Int, tempList : ArrayList<MyPlace>) {

        d("TAG", "state : $state")
        d("TAG", "placeList : ${tempList}")

        if(state == 0) {
            currentList.clear()
            for(i in (0..myPlaceSize-1)){
                currentList.add(placeList.get(i))
            }
            activity!!.findViewById<ImageView>(R.id.myplace_isvisited).setImageResource(R.drawable.img_main_all_xxxhdpi)
            recyclerviewInit(currentList, state)
            activity!!.findViewById<TextView>(R.id.hpCount).text = tempList.size.toString()
        }
        else if(state == 1) {
            currentList.clear()
            activity!!.findViewById<ImageView>(R.id.myplace_isvisited).setImageResource(R.drawable.img_main_ismarked)

            for(i in 0..(tempList.size-1)) {
                if(tempList[i].visited) {
                    currentList.add(tempList[i])
                }
            }
            activity!!.findViewById<TextView>(R.id.hpCount).text = currentList.size.toString()
//            resultList.removeAll { !it.visited }
            d("TAG", "resultList : ${currentList}")
            recyclerviewInit(currentList, state)
        }
        else if(state == 2) {
            currentList.clear()
            activity!!.findViewById<ImageView>(R.id.myplace_isvisited).setImageResource(R.drawable.img_main_will)

            for(i in (0..tempList.size-1)) {
                if(!tempList[i].visited) {
                    currentList.add(tempList[i])
                }
            }
            activity!!.findViewById<TextView>(R.id.hpCount).text = currentList.size.toString()

//            resultList.removeAll { it.visited }
            d("TAG", "resultList : ${currentList}")
            recyclerviewInit(currentList, state)

        }



        d("TAG", "placeList : ${tempList}")
        d("TAG", "resultList.size : ${currentList.size}")


//        hpCount.text = resultList.size.toString()


    }

    @Subscribe
    fun onActivityResultEvent(activityResultEvent: ActivityResultEvent){
        onActivityResult(activityResultEvent.get_RequestCode(),activityResultEvent.get_ResultCode(),activityResultEvent.get_Data())

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


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
    inner class ItemSwipeHelper : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        var isSwipeOk = true
        fun setIsSwipeOk(bool : Boolean){
            isSwipeOk = bool
        }


        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            d("TAG","onMove!!")
            return true
        }

        override fun isItemViewSwipeEnabled(): Boolean {
            return isSwipeOk
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.adapterPosition
            val targetId = currentList.get(position).id

            //서버에 알리기
            val accesstoken = GlobalApplication.prefs.getPreferences() // accesstoken
            apiService.deletPlace(
                "Bearer " + "${accesstoken}",
                "${targetId}"
            ).enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.isSuccessful) {
                        //placeLIst에서도 삭제  id값으로 매핑 ?  >> 메인의 mplaceList에서도 삭제 됨!!

                        val maxIndex = myPlaceSize - 1
                        for (i in 0..maxIndex) {
                            if (targetId == placeList.get(i).id) {
                                placeList.removeAt(i)
                                break
                            }
                        }

                        currentList.removeAt(position)
                        //어뎁터에 알리기
                        myplace_recyclerview.adapter!!.notifyItemRemoved(position)
                        myplace_recyclerview.adapter!!.notifyDataSetChanged()
                        //hpCount reset
                        activity!!.findViewById<TextView>(R.id.hpCount).text =
                            currentList.size.toString()
                        d("Delete", response.message())
                        d("Delete", response.body().toString())
                    } else {
                        d("Delete Error", response.errorBody().toString())
                        d("Delete Error", response.message())
                    }

                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(context!!, "삭제 실패 ! 네트워크 확인 바랍니다 !!", Toast.LENGTH_LONG)
                        .show()
                    Log.d("Delete Error", t.message.toString())
                    Log.d("Delete Error", t.cause.toString())
                }
            })

        }
    }

}