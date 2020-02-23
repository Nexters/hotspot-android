package com.example.hotspot

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.Log.d
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.squareup.otto.Subscribe
import kotlinx.android.synthetic.main.detail_view.*
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.BufferedInputStream
import java.io.IOException
import java.io.Serializable
import java.net.HttpURLConnection
import java.net.URL


class FragmentDetailView : Fragment() {

    private var position = 0
    private var isEditSpot = false
    private lateinit var newPlace: MyPlace
    private var dotscount = 0
    private lateinit var instaTag : String
    private var requestCode = 0
    private var resCode = 0
    private lateinit var urlList: ArrayList<String>
    private lateinit var imgList: MutableList<ImageView>
    lateinit var apiService : APIService
    lateinit var mRetrofit: Retrofit
    private var imageSize: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.detail_view, container, false)
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val myPlace = arguments!!.getSerializable("myPlace") as MyPlace
        imageSize = myPlace.images!!.size
        position = arguments!!.getSerializable("Position") as Int
        requestCode = arguments!!.getSerializable("RequestCode") as Int
        instaTag = myPlace.place.placeName
        d("TAG", "FragmentDetailView : ${myPlace}")

        //Retrofit init
        setRetrofitInit()
        setApiServiceInit()

        detail_placeName_txt.text = myPlace.place.placeName
        detail_roadAddressName_txt.text = myPlace.place.roadAddressName
        detail_memo_txt.text = myPlace.memo
        val categoryName = myPlace.place.categoryName
        val isVisited = myPlace.visited
        val bestmenu = myPlace.bestMenu
        val hours = myPlace.businessHours
        val parking = myPlace.parkingAvailable
        val allDay = myPlace.allDayAvailable
        val powerPlug = myPlace.powerPlugAvailable

        detail_menu_txt1.text = "베스트 메뉴"
        detail_additional_txt1.text = "부가정보"
        detail_time_txt1.text = "영업시간"

        if(!isVisited){
            detail_top.setBackgroundResource(R.drawable.detail_top_layout6)

            notVisitImg.visibility = View.VISIBLE
            notVisitImg.setBackgroundResource(R.drawable.group_6)
        }
        else if(categoryName != null && isVisited) {
            when(categoryName) {
                "맛집"->{
                    detail_top.setBackgroundResource(R.drawable.detail_top_layout1)
                }
                "카페"->{
                    detail_top.setBackgroundResource(R.drawable.detail_top_layout2)
                }
                "술집" ->{
                    detail_top.setBackgroundResource(R.drawable.detail_top_layout3)
                }
                "문화" ->{
                    detail_top.setBackgroundResource(R.drawable.detail_top_layout4)
                }
                "기타" ->{
                    detail_top.setBackgroundResource(R.drawable.detail_top_layout5)
                }
            }
        }

        if(bestmenu != null) {
            detail_menu_txt2.visibility = View.VISIBLE


            for(i in 0..myPlace.bestMenu!!.size-1){
                if(i == 0){
                    detail_menu_txt2.text = myPlace.bestMenu!![0]
                }
                else {
                    detail_menu_txt3.visibility = View.VISIBLE
                    detail_menu_txt3.text = myPlace.bestMenu!![1]
                }
            }
        }

        if(hours!!.open != null && hours!!.close != null) {
            detail_time_txt2.visibility = View.VISIBLE
            val st = StringBuffer()
            var open = myPlace.businessHours!!.open.toString()
            var close = myPlace.businessHours!!.close.toString()
            if(open.length == 1){
                st.append("0")
            }
            st.append(open)
            st.append(":00 - ")

            if(close.length == 1){
                st.append("0")
            }
            st.append(close)
            st.append(":00")
            detail_time_txt2.text = st.toString()
        }

        if(parking != null ||
            allDay != null ||
            powerPlug != null) {

            var st = StringBuffer()
            if (parking != null) {
                st.append("주차가능 / ")
            }
            if (allDay != null) {
                st.append("콘센트 O / ")
            }
            if (powerPlug != null) {
                st.append("24시 / ")
            }
            val len = st.length
            st.delete(len - 2, len - 1)
            detail_additional_txt2.text = st.toString()
        }

        urlList = arrayListOf()
        imgList = mutableListOf()
        for(i in 0..imageSize-1){
            if(myPlace.images!!.get(i).url != null) {
                urlList.add(myPlace.images!!.get(i).url)

                d("TAG", "urlList[i] : ${urlList[i]}")
            }
        }

        detail_esc_btn.setOnClickListener {
            if(isEditSpot){//장소가 업데이트 되었다 .
                fragmentManager!!.beginTransaction()
                    .remove(this)
                    .commit()
                var intent2 = Intent()
                intent2.putExtra("NewSpotInfo",newPlace)
                intent2.putExtra("Position",position)
                activity!!.setResult(resCode,intent2)
                activity!!.finish()
            }
            else {
                fragmentManager!!.beginTransaction()
                    .remove(this)
                    .commit()
                activity!!.finish()
            }
        }

        //viewPager
//        val adapter = ImageAdapter(activity!!, urlList)
//        viewPager.adapter = adapter

        detail_edit_btn.setOnClickListener {

            val intent = Intent(activity, RegisterActivity::class.java)
            intent.putExtra("isAdd", false)
            intent.putExtra("myPlace", myPlace as Serializable)
            intent.putExtra("RequestCode", requestCode as Serializable)
            startActivityForResult(intent,requestCode)
        }

        detail_insta_img.setOnClickListener {
            var intent_insta = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/explore/tags/"+instaTag+"/"))
            startActivity(intent_insta)
        }

        ////////////////////////////////////////////////////////////////////////////////////
        // 삭제 API 추가!
        detail_delete_btn.setOnClickListener {
            detail_popup_layout.visibility = View.VISIBLE
        }
        detail_quit_ok_txt.setOnClickListener {

            //서버에 알리기
            val accesstoken = GlobalApplication.prefs.getPreferences() // accesstoken
            apiService.deletPlace("Bearer " + "${accesstoken}",
                "${myPlace.id}").enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if(response.isSuccessful) {
                        d("Delete", "onSuccess()")
                        d("Delete", response.message())
                        d("Delete", response.body().toString())
                        Toast.makeText(activity!!, "장소가 삭제되었습니다.", Toast.LENGTH_LONG).show()


                        requestCode = 95
                        val intent = Intent(activity, MainActivity::class.java)
                        intent.putExtra("myPlace", myPlace as Serializable)
                        intent.putExtra("position", position)
                        intent.putExtra("RequestCode", requestCode as Serializable)
                        activity!!.setResult(requestCode, intent)
                        activity!!.finish()
                    }
                    else{
                        d("Delete Error",response.errorBody().toString())
                        d("Delete Error",response.message())
                        Toast.makeText(activity!!, "실패!!", Toast.LENGTH_LONG).show()
                    }

                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(activity!!,"삭제 실패 ! 네트워크 확인 바랍니다 !!", Toast.LENGTH_LONG).show()
                    Log.d("Delete Error", t.message.toString())
                    Log.d("Delete Error",t.cause.toString())
                }
            })

        }
        detail_quit_no_txt.setOnClickListener {
            detail_popup_layout.visibility = View.GONE
        }

        ////////////////////////////////////////////////////////////////////////////////////
        // image가 존재하면 Gone 해제
        if(imageSize != 0) {
            detail_cardview.visibility = View.VISIBLE
            dotsContainer.visibility = View.VISIBLE

            val mAdapter = ImageAdapter(activity!!, urlList)
            viewPager.adapter = mAdapter
            dotscount = mAdapter.count

            dotImageView1.setImageResource(R.drawable.indicator_dot_on)
            when (dotscount) {
                1 -> {
                    dotImageView1.visibility = View.VISIBLE
                }
                2 -> {
                    dotImageView1.visibility = View.VISIBLE
                    dotImageView2.visibility = View.VISIBLE
                }
                3 -> {
                    dotImageView1.visibility = View.VISIBLE
                    dotImageView2.visibility = View.VISIBLE
                    dotImageView3.visibility = View.VISIBLE
                }
                4 -> {
                    dotImageView1.visibility = View.VISIBLE
                    dotImageView2.visibility = View.VISIBLE
                    dotImageView3.visibility = View.VISIBLE
                    dotImageView4.visibility = View.VISIBLE
                }
                5 -> {
                    dotImageView1.visibility = View.VISIBLE
                    dotImageView2.visibility = View.VISIBLE
                    dotImageView3.visibility = View.VISIBLE
                    dotImageView4.visibility = View.VISIBLE
                    dotImageView5.visibility = View.VISIBLE
                }
            }
        }

        // ViewPager 동작
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {

                d("TAG", "viewPager Position : ${position}")
                when (position) {
                    0 -> {
                        dotImageView1.setImageResource(R.drawable.indicator_dot_on)
                        dotImageView2.setImageResource(R.drawable.indicator_dot_off)
                        dotImageView3.setImageResource(R.drawable.indicator_dot_off)
                        dotImageView4.setImageResource(R.drawable.indicator_dot_off)
                        dotImageView5.setImageResource(R.drawable.indicator_dot_off)

                    }
                    1 -> {
                        dotImageView1.setImageResource(R.drawable.indicator_dot_off)
                        dotImageView2.setImageResource(R.drawable.indicator_dot_on)
                        dotImageView3.setImageResource(R.drawable.indicator_dot_off)
                        dotImageView4.setImageResource(R.drawable.indicator_dot_off)
                        dotImageView5.setImageResource(R.drawable.indicator_dot_off)
                    }
                    2 -> {
                        dotImageView1.setImageResource(R.drawable.indicator_dot_off)
                        dotImageView2.setImageResource(R.drawable.indicator_dot_off)
                        dotImageView3.setImageResource(R.drawable.indicator_dot_on)
                        dotImageView4.setImageResource(R.drawable.indicator_dot_off)
                        dotImageView5.setImageResource(R.drawable.indicator_dot_off)
                    }
                    3 -> {
                        dotImageView1.setImageResource(R.drawable.indicator_dot_off)
                        dotImageView2.setImageResource(R.drawable.indicator_dot_off)
                        dotImageView3.setImageResource(R.drawable.indicator_dot_off)
                        dotImageView4.setImageResource(R.drawable.indicator_dot_on)
                        dotImageView5.setImageResource(R.drawable.indicator_dot_off)
                    }
                    4 -> {
                        dotImageView1.setImageResource(R.drawable.indicator_dot_off)
                        dotImageView2.setImageResource(R.drawable.indicator_dot_off)
                        dotImageView3.setImageResource(R.drawable.indicator_dot_off)
                        dotImageView4.setImageResource(R.drawable.indicator_dot_off)
                        dotImageView5.setImageResource(R.drawable.indicator_dot_on)
                    }
                }
            }
        })
    }

    ////////////////////////////////////////////////////////////////////////
    // ViewPager 미완
    inner class ImageAdapter : PagerAdapter {
        private var mContext : Context
        private var mUrlList : ArrayList<String>

        constructor(context: Context, urllist: ArrayList<String>){
            mContext = context
            mUrlList = urllist
        }

        override fun isViewFromObject(view: View, obj: Any): Boolean {
            return view == obj
        }

        override fun getCount(): Int {
            return mUrlList.size
        }

        override fun instantiateItem(container: ViewGroup, position: Int): ImageView {

            val urlBuffer = StringBuffer(mUrlList[position])

            d("FragmentDetailView", "urlBuffer[4] : ${urlBuffer[4]}")

            if(urlBuffer[4] != 's'){
                urlBuffer.insert( 4,'s')
            }

            var bm: Bitmap? = null
            val imageView = ImageView(mContext)
            val url = urlBuffer.toString()

            d("FragmentDetailView", "url : $url")
            d("TAG", "setImage")

            Glide.with(activity!!)
                .load(url)
                .into(imageView)
            d("TAG", "bm : $bm")

            container.addView(imageView, 0)

            return imageView
        }

        override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
            container.removeView(obj as View)
        }
    }

    @Subscribe
    fun onActivityResultEvent(activityResultEvent: ActivityResultEvent){
        onActivityResult(activityResultEvent.get_RequestCode(),activityResultEvent.get_ResultCode(),activityResultEvent.get_Data())

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == 99){
            resCode = 98
            if(data != null){
                newPlace = data.getSerializableExtra("NewSpotInfo") as MyPlace
                //UI 업데이트!!!!!!!!!!!


                isEditSpot = true
            }

        }
        else if(resultCode == 100){
            resCode = 97
            if(data != null){
                newPlace = data.getSerializableExtra("NewSpotInfo") as MyPlace
                //UI 업데이트!!!!!!!!!!!


                isEditSpot = true
            }
        }
    }

    fun getBitmapFromURL(src: String): Bitmap? {
        try {
            d("TAG", "onSuccess")
            d("TAG", "url : $src")
            val url = URL(src)
            val connection = url.openConnection() as HttpURLConnection
            connection.setDoInput(true)
            connection.connect()
            val nSize = connection.contentLength
            val bis = BufferedInputStream(connection.getInputStream(), nSize)
            val bitmap = BitmapFactory.decodeStream(bis)

            d("TAG", "Bitmap : ${bitmap}")

            return bitmap
        } catch (e: IOException) {
            d("TAG", "onFailure")
            e.printStackTrace()
            return null
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
}