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
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.ItemTouchHelper
import com.squareup.otto.Subscribe
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_sticker_regist.*
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
    private lateinit var itemSwipeHelper: ItemSwipeHelper
    private var vibrateOK = true
    private lateinit var mRetrofit: Retrofit
    lateinit var apiService : APIService
    private var myPlaceSize = 0
    private var IsNewUser = false
    private var categoryState = 0
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
        IsNewUser = arguments!!.getSerializable("IsNewUser") as Boolean
        myPlaceSize = placeList.size


        currentList = arrayListOf()
        for(i in (0..(placeList.size-1))){
            currentList.add(placeList.get(i))
        }

        if(placeList.size == 0) {
            mylistEmptyimg.visibility = View.VISIBLE
        }

        else {
            mylistEmptyimg.visibility = View.GONE
            myplace_recyclerview.setHasFixedSize(true)
            myplace_recyclerview.layoutManager = LinearLayoutManager(context)
            recyclerviewInit(currentList, 0)
            itemSwipeHelper = ItemSwipeHelper()
            itemTouchHelper = ItemTouchHelper(itemSwipeHelper)
            itemTouchHelper.attachToRecyclerView(myplace_recyclerview)

        }


        myplace_isvisited.setOnClickListener {
            if(isvisitedState>=2)
                isvisitedState = 0
            else
                isvisitedState++

            if(isvisitedState == 0)
                changeCategory(isvisitedState, placeList as ArrayList<MyPlace>)
            else if(isvisitedState == 1)
                changeCategory(isvisitedState, placeList as ArrayList<MyPlace>)
            else if(isvisitedState == 2)
                changeCategory(isvisitedState, placeList as ArrayList<MyPlace>)
        }

        myplace_add_btn.setOnClickListener {
            val intent = Intent(activity, RegisterActivity::class.java)
            val isAdd = true
            intent.putExtra("IsNewUser",IsNewUser)
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
            for(i in (0..(placeList.size-1))){
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

        IsNewUser = false
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
            setButtonClickable(false)



            showDeletePopup(targetId,position)

        }
    }
    private fun showDeletePopup(targetId : String , position : Int){
        activity!!.findViewById<ConstraintLayout>(R.id.main_delete_popup_layout).visibility = View.VISIBLE
        activity!!.findViewById<TextView>(R.id.main_delete_quit_ok_txt).setOnClickListener{
            activity!!.findViewById<TextView>(R.id.main_delete_quit_ok_txt).isClickable = false
            activity!!.findViewById<TextView>(R.id.main_delete_quit_no_txt).isClickable = false
            activity!!.findViewById<TextView>(R.id.main_delete_quit_ok_txt).text = "삭제중.."
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

                        val maxIndex = placeList.size - 1
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
                        setButtonClickable(true)
                        activity!!.findViewById<TextView>(R.id.main_delete_quit_ok_txt).isClickable = true
                        activity!!.findViewById<TextView>(R.id.main_delete_quit_no_txt).isClickable = true
                        activity!!.findViewById<ConstraintLayout>(R.id.main_delete_popup_layout).visibility = View.GONE
                        activity!!.findViewById<TextView>(R.id.main_delete_quit_ok_txt).text = "삭 제"
                        d("Delete", response.message())
                        d("Delete", response.body().toString())
                    } else {
                        d("Delete Error", response.errorBody().toString())
                        d("Delete Error", response.message())
                        setButtonClickable(true)
                        activity!!.findViewById<TextView>(R.id.main_delete_quit_ok_txt).isClickable = true
                        activity!!.findViewById<TextView>(R.id.main_delete_quit_no_txt).isClickable = true
                        activity!!.findViewById<ConstraintLayout>(R.id.main_delete_popup_layout).visibility = View.GONE
                        activity!!.findViewById<TextView>(R.id.main_delete_quit_ok_txt).text = "삭 제"
                    }

                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(context!!, "삭제 실패 ! 네트워크 확인 바랍니다 !!", Toast.LENGTH_LONG)
                        .show()
                    setButtonClickable(true)
                    Log.d("Delete Error", t.message.toString())
                    Log.d("Delete Error", t.cause.toString())
                    activity!!.findViewById<TextView>(R.id.main_delete_quit_ok_txt).isClickable = true
                    activity!!.findViewById<TextView>(R.id.main_delete_quit_no_txt).isClickable = true
                    activity!!.findViewById<ConstraintLayout>(R.id.main_delete_popup_layout).visibility = View.GONE
                    activity!!.findViewById<TextView>(R.id.main_delete_quit_ok_txt).text = "삭 제"
                }
            })
        }
        activity!!.findViewById<TextView>(R.id.main_delete_quit_no_txt).setOnClickListener{

            setButtonClickable(true)
            activity!!.findViewById<ConstraintLayout>(R.id.main_delete_popup_layout).visibility = View.GONE
        }
    }

    private fun setButtonClickable(bool : Boolean){
        activity!!.findViewById<ImageView>(R.id.findBt).isClickable = bool
        activity!!.findViewById<ImageView>(R.id.mapBt).isClickable = bool
        activity!!.findViewById<TextView>(R.id.category_item1_txt2).isClickable = bool
        activity!!.findViewById<TextView>(R.id.category_item2_txt2).isClickable = bool
        activity!!.findViewById<TextView>(R.id.category_item3_txt2).isClickable = bool
        activity!!.findViewById<TextView>(R.id.category_item4_txt2).isClickable = bool
        activity!!.findViewById<TextView>(R.id.category_item5_txt2).isClickable = bool
        activity!!.findViewById<TextView>(R.id.category_item6_txt2).isClickable = bool
        myplace_isvisited.isClickable = bool
        myplace_add_btn.isClickable = bool

        itemSwipeHelper.isSwipeOk = bool
        itemTouchHelper = ItemTouchHelper(itemSwipeHelper)
        itemTouchHelper.attachToRecyclerView(myplace_recyclerview)

        (myplace_recyclerview.adapter as MyPlaceRecyclerAdapter).setClickable(bool)
        myplace_recyclerview.adapter!!.notifyDataSetChanged()
    }

}