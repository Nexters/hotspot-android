package com.example.hotspot

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.drawable.Drawable
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
import androidx.core.content.getSystemService
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.mylist_view.*
import kotlinx.android.synthetic.main.myplace_item.view.*
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.Serializable

class MyPlaceRecyclerAdapter (private var list:ArrayList<MyPlace>,
                              private val cardstyleList : ArrayList<Drawable>,
                              private val isVisit : Int,
                              private val context: Activity) :

    RecyclerView.Adapter<MyPlaceRecyclerAdapter.ViewHolder>() {
    private lateinit var mRetrofit: Retrofit
    lateinit var apiService : APIService

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val v: View = LayoutInflater.from(parent.context).inflate(R.layout.myplace_item, parent,false)

            setRetrofitInit()
            setApiServiceInit()
            return ViewHolder(v)
        }

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val placeName_text = list.get(position).place.placeName
            val roadAddressName_text = list.get(position).place.roadAddressName
            val rating = list.get(position).rating
            val visited = list.get(position).visited
            var categoryTemp: String? = list.get(position).place.categoryName
            var categoryName: String? = ""
            var nameBuffer = StringBuffer(placeName_text)
            var addrBuffer = StringBuffer(roadAddressName_text)

            d("TAG", "categoryName : ${categoryName}")


            (holder.cardLayout as View).setOnClickListener{
                d("TAG","Card OnClick!")
                val myPlace = list.get(position)

                val intent = Intent(context, DetailActivity::class.java)
                intent.putExtra("myPlace", myPlace as Serializable )
                intent.putExtra("Position",position)
                intent.putExtra("RequestCode",20)
                context.startActivityForResult(intent,20)
            }
            /*
            (holder.cardLayout as View).setOnLongClickListener {
                d("TAG","Card OnLongClick!")
                val startTime = System.currentTimeMillis()
                var isSwipeOk : Boolean = false
                while (!isSwipeOk) {
                    if (System.currentTimeMillis() > (startTime + 500)) {
                        //do something
                        if (vibrateOK) {

                            var vibrator: Vibrator
                            vibrator =
                                context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                            vibrator.vibrate(VibrationEffect.EFFECT_TICK.times(15.toLong()))
                            vibrateOK = false
                            Toast.makeText(context,"왼쪽으로 밀어서 삭제하세요!!",Toast.LENGTH_LONG).show()
                        }
                        isSwipeOk = true

                    }

                }

                true

            }

            (holder.cardLayout as View).setOnTouchListener(object : View.OnTouchListener{

                override fun onTouch(v: View?, event: MotionEvent?): Boolean {

                    if((event!=null) && (v!=null)){
                        when(event.action){
                            MotionEvent.ACTION_DOWN -> {

                            }
                            MotionEvent.ACTION_UP -> {
                                itemTouchHelper = null
                                d("TAG", "Card Action UP!")


                            }
                        }
                    }
                    return false
                }
            })*/

            d("TAG", "categoryName : ${categoryName}")

            if(isVisit == 2) {
                categoryName = categoryTemp
                holder.cardLayout.background = cardstyleList.get(5)
                when(categoryName){
                    "맛집" -> {
                        holder.cardLayout.findViewById<ImageView>(R.id.mypl_category_img).setImageResource(R.drawable.ic_mypl_icon_food)
                        holder.cardLayout.findViewById<ImageView>(R.id.mypl_category_img).visibility = View.VISIBLE
                    }
                    "카페" -> {
                        holder.cardLayout.findViewById<ImageView>(R.id.mypl_category_img).setImageResource(R.drawable.ic_mypl_icon_cafe)
                        holder.cardLayout.findViewById<ImageView>(R.id.mypl_category_img).visibility = View.VISIBLE

                    }
                    "술집" -> {
                        holder.cardLayout.findViewById<ImageView>(R.id.mypl_category_img).setImageResource(R.drawable.ic_mypl_icon_drink)
                        holder.cardLayout.findViewById<ImageView>(R.id.mypl_category_img).visibility = View.VISIBLE
                    }
                    "문화" -> {
                        holder.cardLayout.findViewById<ImageView>(R.id.mypl_category_img).setImageResource(R.drawable.ic_mypl_icon_culture)
                        holder.cardLayout.findViewById<ImageView>(R.id.mypl_category_img).visibility = View.VISIBLE
                    }
                    else ->{
                        holder.cardLayout.findViewById<ImageView>(R.id.mypl_category_img).setImageResource(R.drawable.ic_mypl_icon_etc)
                        holder.cardLayout.findViewById<ImageView>(R.id.mypl_category_img).visibility = View.VISIBLE
                    }

                }
            }
            else {
                if (categoryTemp != null) {
                    categoryName = categoryTemp

                    when (categoryName) {
                        "맛집" -> {
                            holder.cardLayout.background = cardstyleList.get(0)
                            holder.cardLayout.findViewById<ImageView>(R.id.mypl_category_img).visibility = View.INVISIBLE
                        }
                        "카페" -> {
                            holder.cardLayout.background = cardstyleList.get(1)
                            holder.cardLayout.findViewById<ImageView>(R.id.mypl_category_img).visibility = View.INVISIBLE
                        }
                        "술집" -> {
                            holder.cardLayout.background = cardstyleList.get(2)
                            holder.cardLayout.findViewById<ImageView>(R.id.mypl_category_img).visibility = View.INVISIBLE
                        }
                        "문화" -> {
                            holder.cardLayout.background = cardstyleList.get(3)
                            holder.cardLayout.findViewById<ImageView>(R.id.mypl_category_img).visibility = View.INVISIBLE
                        }
                        "기타" -> {
                            holder.cardLayout.background = cardstyleList.get(4)
                            holder.cardLayout.findViewById<ImageView>(R.id.mypl_category_img).visibility = View.INVISIBLE
                        }
                        else ->{
                            holder.cardLayout.background = cardstyleList.get(4)
                            holder.cardLayout.findViewById<ImageView>(R.id.mypl_category_img).visibility = View.INVISIBLE
                        }
                    }
                }
                else{
                    holder.cardLayout.background = cardstyleList.get(4)
                    holder.cardLayout.findViewById<ImageView>(R.id.mypl_category_img).visibility = View.INVISIBLE
                }
            }



            if(visited) {
                when (rating) {
                    1-> {
                        holder.rating_img1.isVisible = true
                    }
                    2 -> {
                        holder.rating_img1.isVisible = true
                        holder.rating_img2.isVisible = true
                    }
                    3 -> {
                        holder.rating_img1.isVisible = true
                        holder.rating_img2.isVisible = true
                        holder.rating_img3.isVisible = true
                    }
                }
            }
            //길이가 10 이상이면 뒤에 ... 추가
            if(nameBuffer.length > 9) {
                nameBuffer.delete(9, placeName_text.length-1)
                nameBuffer.append("...")
            }
            if(addrBuffer.length > 20){
                addrBuffer.delete(20, roadAddressName_text.length-1)
                addrBuffer.append("...")
            }
            holder.placeName_txtV.text = nameBuffer.toString()
            holder.roadAddressName_txtV.text =addrBuffer.toString()
        }


    inner class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val placeName_txtV = itemView.placeName_txt
        val roadAddressName_txtV = itemView.roadAddressName_txt
        val cardLayout = itemView.rcyl_card_layout

        val rating_img1 = itemView.myplace_rating_img1
        val rating_img2 = itemView.myplace_rating_img2
        val rating_img3 = itemView.myplace_rating_img3



    }
    fun setRetrofitInit() {
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

    fun setApiServiceInit() {
        apiService = mRetrofit.create(APIService::class.java)
    }


}

