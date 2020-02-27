package com.example.hotspot

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
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
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.kakao.kakaolink.v2.KakaoLinkResponse
import com.kakao.kakaolink.v2.KakaoLinkService
import com.kakao.message.template.ButtonObject
import com.kakao.message.template.ContentObject
import com.kakao.message.template.LinkObject
import com.kakao.message.template.LocationTemplate
import com.kakao.network.ErrorResult
import com.kakao.network.callback.ResponseCallback
import com.squareup.otto.Subscribe
import kotlinx.android.synthetic.main.detail_view1.*
import kotlinx.android.synthetic.main.mylist_view.*
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
import java.net.URLEncoder


class FragmentDetailView : Fragment() {

    private var position = 0
    private var isEditSpot = false
    private lateinit var newPlace: MyPlace
    private var dotscount = 0
    private lateinit var instaTag : StringBuffer
    private var requestCode = 0
    private var resCode = 0
    private lateinit var urlList: ArrayList<String>
    private lateinit var imgList: MutableList<ImageView>
    lateinit var apiService : APIService
    lateinit var mRetrofit: Retrofit
    private var imageSize: Int = 0
    private var roadAddressName = ""
    private var latitude = 0.0
    private var longitude = 0.0
    private var rating = 1
    private lateinit var myPlace : MyPlace
    private val list : ArrayList<String> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.detail_view1, container, false)
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        myPlace = arguments!!.getSerializable("myPlace") as MyPlace
        imageSize = myPlace.images!!.size
        position = arguments!!.getSerializable("Position") as Int
        requestCode = arguments!!.getSerializable("RequestCode") as Int
        instaTag = StringBuffer(myPlace.place.placeName)
        latitude = myPlace.place.y.toDouble()
        longitude = myPlace.place.x.toDouble()
        rating = myPlace.rating
        d("TAG", "FragmentDetailView : ${myPlace}")

        for(i in 0..instaTag.length-2) {
            if(instaTag[i] == ' '){
                instaTag.deleteCharAt(i)
            }
        }
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

        d("FragmentdetailView", "categoryName: ${categoryName}")
        if(categoryName != null) {
            when(categoryName) {
                "맛집"->{
                    detail_top.setBackgroundResource(R.drawable.detail_top_layout1)
                    detail_category_img.setImageResource(R.drawable.ic_mypl_icon_food)
                }
                "카페"->{
                    detail_top.setBackgroundResource(R.drawable.detail_top_layout2)
                    detail_category_img.setImageResource(R.drawable.ic_mypl_icon_cafe)
                }
                "술집" ->{
                    detail_top.setBackgroundResource(R.drawable.detail_top_layout3)
                    detail_category_img.setImageResource(R.drawable.ic_mypl_icon_drink)
                }
                "문화" ->{
                    detail_top.setBackgroundResource(R.drawable.detail_top_layout4)
                    detail_category_img.setImageResource(R.drawable.ic_mypl_icon_culture)
                }
                "기타" ->{
                    detail_top.setBackgroundResource(R.drawable.detail_top_layout5)
                    detail_category_img.setImageResource(R.drawable.ic_mypl_icon_etc)
                }
            }
        }

        if(!isVisited){
            notVisitTxt.visibility = View.VISIBLE
        } else {
            notVisitTxt.visibility = View.GONE
        }

        // rating 이미지
        d("FragmentdetailView", "rating: ${rating}")
        if(rating == 1) {
            detail_rating_img1.setImageResource(R.drawable.ic_img_star_gray2)
        }else if(rating == 2) {
            detail_rating_img1.setImageResource(R.drawable.ic_img_star_gray2)
            detail_rating_img2.setImageResource(R.drawable.ic_img_star_gray2)
        }else if((rating == 0) || (rating == null)){
            detail_rating_img1.setImageResource(R.drawable.ic_img_start_gray)
            detail_rating_img2.setImageResource(R.drawable.ic_img_start_gray)
            detail_rating_img3.setImageResource(R.drawable.ic_img_start_gray)
        }else {
            detail_rating_img1.setImageResource(R.drawable.ic_img_star_gray2)
            detail_rating_img2.setImageResource(R.drawable.ic_img_star_gray2)
            detail_rating_img3.setImageResource(R.drawable.ic_img_star_gray2)
        }

        if(bestmenu != null) {
            var buffer = StringBuffer("")

            for(i in 0..myPlace.bestMenu!!.size-1){
                if(i == 0){
                    buffer.append(myPlace.bestMenu!![0])
                    detail_menu_txt.text = buffer.toString()
                }
                else {
                    buffer.append(", ")
                    buffer.append(myPlace.bestMenu!![1])
                    detail_menu_txt.text = buffer.toString()
                }
            }
        }

        // 추가 정보
        if(allDay!= null && allDay) {
            list.add("24시 영업")
        }
        if(powerPlug != null && powerPlug) {
            list.add("콘센트 있음")
        }
        if(parking != null && parking) {
            list.add("주차장 있음")
        }
        if(hours!!.open != null && hours!!.close != null) {
            var hoursBuffer = StringBuffer("")

            var open = myPlace.businessHours!!.open.toString()
            var close = myPlace.businessHours!!.close.toString()

            hoursBuffer.append(open+"AM - ")
            hoursBuffer.append(close+"PM")
            list.add(hoursBuffer.toString())
        }

        d("TAG", "list : ${list}")

        val deco = Stk_Rcylr_Item_Deco()
        detail_recycler1.layoutManager = LinearLayoutManager(
            activity!!,
            LinearLayoutManager.HORIZONTAL,
            false
        )

        detail_recycler1.addItemDecoration(deco)
        detail_recycler1.adapter = StickerRcylrAdapter(list)

//        if(hours!!.open != null && hours!!.close != null) {
//            detail_time_txt.visibility = View.VISIBLE
//            val st = StringBuffer()
//            var open = myPlace.businessHours!!.open.toString()
//            var close = myPlace.businessHours!!.close.toString()
//            if(open.length == 1){
//                st.append("0")
//            }
//            st.append(open)
//            st.append(":00 - ")
//
//            if(close.length == 1){
//                st.append("0")
//            }
//            st.append(close)
//            st.append(":00")
//            detail_time_txt.text = st.toString()
//        }
//
//        if(parking != null ||
//            allDay != null ||
//            powerPlug != null) {
//
//            var st = StringBuffer()
//            if (parking != null) {
//                st.append("주차가능 / ")
//            }
//            if (allDay != null) {
//                st.append("콘센트 O / ")
//            }
//            if (powerPlug != null) {
//                st.append("24시 / ")
//            }
//            val len = st.length
//            st.delete(len - 2, len - 1)
//            detail_additional_txt2.text = st.toString()
//        }

        urlList = arrayListOf()
        imgList = mutableListOf()
        for(i in 0..imageSize-1){
            if(myPlace.images!!.get(i).url != null) {
                urlList.add(myPlace.images!!.get(i).url)

                d("TAG", "urlList[i] : ${urlList[i]}")
            }
        }

        ////////////////////////////////////////////////////////////////////////////////////
        // image가 존재하면 Gone 해제
        if(imageSize != 0) {
            detail_recycler2.visibility = View.VISIBLE
            detail_recycler2.setHasFixedSize(true)
            detail_recycler2.layoutManager = LinearLayoutManager(
                activity!!,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            detail_recycler2.adapter = ImageRecyclerAdapter(activity!!, urlList)

        }else {
            detail_recycler2.visibility = View.VISIBLE
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
            try {
                var instatagArr: List<String> = instaTag.split(" ")
                var intent_insta = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://www.instagram.com/explore/tags/" + instatagArr.get(0) + "/")
                )
                startActivity(intent_insta)
            }catch(e : ActivityNotFoundException){

                var intent_market = Intent(Intent.ACTION_VIEW,Uri.parse("market://details?id=com.instagram.android"))
                startActivity(intent_market)
            }
        }

        detail_share_img.setOnClickListener {
            var params : LocationTemplate

            var flag = false
            try {
                var packageManager: PackageManager = context!!.packageManager
                packageManager.getPackageInfo("com.kakao.talk", PackageManager.GET_ACTIVITIES)
                flag = true
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }
            if(flag) {
                params = LocationTemplate.newBuilder(
                    roadAddressName, ContentObject.newBuilder(
                        roadAddressName,
                        "https://",
                        LinkObject.newBuilder()
                            .setWebUrl("https://developers.kakao.com")
                            .setMobileWebUrl("https://developers.kakao.com")
                            .build()
                    ).setDescrption(roadAddressName).build()
                ).addButton(
                    ButtonObject(
                        "Spotter", LinkObject.newBuilder()
                            .setWebUrl("https://developers.kakao.com")
                            .setMobileWebUrl("https://developers.kakao.com")
                            .setAndroidExecutionParams("hi").build()
                    )
                ).setAddressTitle(roadAddressName).build()

                var serverCallbackArgs = HashMap<String, String>()

                KakaoLinkService.getInstance().sendDefault(
                    activity!!,
                    params,
                    serverCallbackArgs,
                    object : ResponseCallback<KakaoLinkResponse>() {
                        override fun onFailure(errorResult: ErrorResult?) {
                            Toast.makeText(activity!!, "카카오 링크 공유 실패!", Toast.LENGTH_LONG)
                        }

                        override fun onSuccess(result: KakaoLinkResponse?) {
                            // 템플릿 밸리데이션과 쿼터 체크가 성공적으로 끝남. 톡에서 정상적으로 보내졌는지 보장은 할 수 없다. 전송 성공 유무는 서버콜백 기능을 이용하여야 한다.
                        }
                    })
            } else { //
                val url = "market://details?id="+"com.kakao.talk"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(intent)
            }
        }

        detail_findroad_img.setOnClickListener {
            var flag = false
            try {
                var packageManager: PackageManager = activity!!.packageManager
                packageManager.getPackageInfo("com.naver.maps:map", PackageManager.GET_ACTIVITIES)
                flag = true
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }
            if(flag) {
                var strEncodedUrl = URLEncoder.encode(instaTag.toString())
                var intent_search_road = Intent(Intent.ACTION_VIEW, Uri.parse("nmap://route/public?dlat="+latitude+"&dlng="+longitude+"&dname="+strEncodedUrl+"&appname=com.example.hotspot"))
                startActivity(intent_search_road)
            } else { //
                val url = "market://details?id="+"com.nhn.android.nmap"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(intent)
            }
        }

        ////////////////////////////////////////////////////////////////////////////////////
        // 삭제 API 추가!
        detail_delete_btn.setOnClickListener {
            detail_popup_layout.visibility = View.VISIBLE
        }
        detail_quit_ok_txt.setOnClickListener {

            //서버에 알리기
            detail_quit_ok_txt.isClickable = false
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
                        val intent = Intent()
                        intent.putExtra("myPlace", myPlace as Serializable)
                        intent.putExtra("position", position)
                        intent.putExtra("RequestCode", requestCode as Serializable)
                        activity!!.setResult(requestCode)
                        activity!!.finish()
                    }
                    else{
                        d("Delete Error",response.errorBody().toString())
                        d("Delete Error",response.message())
                        Toast.makeText(activity!!, "실패!!", Toast.LENGTH_LONG).show()
                        detail_quit_ok_txt.isClickable = true
                    }

                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(activity!!,"삭제 실패 ! 네트워크 확인 바랍니다 !!", Toast.LENGTH_LONG).show()
                    Log.d("Delete Error", t.message.toString())
                    Log.d("Delete Error",t.cause.toString())
                    detail_quit_ok_txt.isClickable = true
                }
            })

        }
        detail_quit_no_txt.setOnClickListener {
            detail_popup_layout.visibility = View.GONE
        }

    }


    ////////////////////////////////////////////////////////////////////////
    // ViewPager 미완
//    inner class ImageAdapter : PagerAdapter {
//        private var mContext : Context
//        private var mUrlList : ArrayList<String>
//
//        constructor(context: Context, urllist: ArrayList<String>){
//            mContext = context
//            mUrlList = urllist
//        }
//
//        override fun isViewFromObject(view: View, obj: Any): Boolean {
//            return view == obj
//        }
//
//        override fun getCount(): Int {
//            return mUrlList.size
//        }
//
//        override fun instantiateItem(container: ViewGroup, position: Int): ImageView {
//
//            val urlBuffer = StringBuffer(mUrlList[position])
//
//            d("FragmentDetailView", "urlBuffer[4] : ${urlBuffer[4]}")
//
//            if(urlBuffer[4] != 's'){
//                urlBuffer.insert( 4,'s')
//            }
//
//            var bm: Bitmap? = null
//            val imageView = ImageView(mContext)
//            val url = urlBuffer.toString()
//
//            d("FragmentDetailView", "url : $url")
//            d("TAG", "setImage")
//
//            Glide.with(activity!!)
//                .load(url)
//                .into(imageView)
//            d("TAG", "bm : $bm")
//
//            container.addView(imageView, 0)
//
//            return imageView
//        }
//
//        override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
//            container.removeView(obj as View)
//        }
//    }

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
                myPlace = newPlace
                //UI 업데이트!!!!!!!!!!!
                updateUi(newPlace)

                isEditSpot = true
            }

        }
        else if(resultCode == 100){
            resCode = 97
            if(data != null){
                newPlace = data.getSerializableExtra("NewSpotInfo") as MyPlace
                //UI 업데이트!!!!!!!!!!!
                myPlace = newPlace
                updateUi(newPlace)

                isEditSpot = true
            }
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

    private fun updateUi(myPlace : MyPlace) {
        detail_placeName_txt.text = myPlace.place.placeName
        detail_roadAddressName_txt.text = myPlace.place.roadAddressName
        detail_memo_txt.text = myPlace.memo
        imageSize = myPlace.images!!.size
        val categoryName = myPlace.place.categoryName
        val isVisited = myPlace.visited
        val bestmenu = myPlace.bestMenu
        val hours = myPlace.businessHours
        val parking = myPlace.parkingAvailable
        val allDay = myPlace.allDayAvailable
        val powerPlug = myPlace.powerPlugAvailable
        rating = myPlace.rating


        if (categoryName != null) {
            when (categoryName) {
                "맛집" -> {
                    detail_top.setBackgroundResource(R.drawable.detail_top_layout1)
                    detail_category_img.setImageResource(R.drawable.ic_mypl_icon_food)
                }
                "카페" -> {
                    detail_top.setBackgroundResource(R.drawable.detail_top_layout2)
                    detail_category_img.setImageResource(R.drawable.ic_mypl_icon_cafe)
                }
                "술집" -> {
                    detail_top.setBackgroundResource(R.drawable.detail_top_layout3)
                    detail_category_img.setImageResource(R.drawable.ic_mypl_icon_drink)
                }
                "문화" -> {
                    detail_top.setBackgroundResource(R.drawable.detail_top_layout4)
                    detail_category_img.setImageResource(R.drawable.ic_mypl_icon_culture)
                }
                "기타" -> {
                    detail_top.setBackgroundResource(R.drawable.detail_top_layout5)
                    detail_category_img.setImageResource(R.drawable.ic_mypl_icon_etc)
                }
            }
        }

        if (!isVisited) {
            notVisitTxt.visibility = View.VISIBLE
        } else {
            notVisitTxt.visibility = View.GONE
        }

        d("FragmentdetailView", "rating: ${rating}")
        if (rating == 1) {
            detail_rating_img1.setImageResource(R.drawable.ic_img_star_gray2)
        } else if (rating == 2) {
            detail_rating_img1.setImageResource(R.drawable.ic_img_star_gray2)
            detail_rating_img2.setImageResource(R.drawable.ic_img_star_gray2)
        } else if ((rating == 0) || (rating == null)) {
            detail_rating_img1.setImageResource(R.drawable.ic_img_star_gray2)
            detail_rating_img2.setImageResource(R.drawable.ic_img_star_gray2)
            detail_rating_img3.setImageResource(R.drawable.ic_img_star_gray2)
        } else {
            detail_rating_img1.setImageResource(R.drawable.ic_img_star_gray2)
            detail_rating_img2.setImageResource(R.drawable.ic_img_star_gray2)
            detail_rating_img3.setImageResource(R.drawable.ic_img_star_gray2)
        }

        if (bestmenu != null) {
            var buffer = StringBuffer("")

            for (i in 0..myPlace.bestMenu!!.size - 1) {
                if (i == 0) {
                    buffer.append(myPlace.bestMenu!![0])
                    detail_menu_txt.text = buffer.toString()
                } else {
                    buffer.append(", ")
                    buffer.append(myPlace.bestMenu!![1])
                    detail_menu_txt.text = buffer.toString()
                }
            }
        }

//        if(hours!!.open != null && hours.close != null) {
//            detail_time_txt2.visibility = View.VISIBLE
//            val st = StringBuffer()
//            var open = myPlace.businessHours!!.open.toString()
//            var close = myPlace.businessHours!!.close.toString()
//            if(open.length == 1){
//                st.append("0")
//            }
//            st.append(open)
//            st.append(":00 - ")
//
//            if(close.length == 1){
//                st.append("0")
//            }
//            st.append(close)
//            st.append(":00")
//            detail_time_txt2.text = st.toString()
//        }
//        else{
//            detail_time_txt2.text = "등록해주세요"
//        }
//
//        if(parking != null ||
//            allDay != null ||
//            powerPlug != null) {
//
//            var st = StringBuffer()
//            if (parking != null) {
//                st.append("주차가능 / ")
//            }
//            if (allDay != null) {
//                st.append("콘센트 O / ")
//            }
//            if (powerPlug != null) {
//                st.append("24시 / ")
//            }
//            val len = st.length
//            st.delete(len - 2, len - 1)
//            detail_additional_txt2.text = st.toString()
//        }
//        else{
//            detail_additional_txt2.text = "등록해주세요"
//        }

        urlList = arrayListOf()
        imgList = mutableListOf()
        Log.d("TAG", "detailView ImgList Size : " + imgList.size.toString())
        for (i in 0..imageSize - 1) {
            if (myPlace.images!!.get(i).url != null) {
                urlList.add(myPlace.images!!.get(i).url)

                d("TAG", "urlList[i] : ${urlList[i]}")
            }
        }
        ////////////////////////////////////////////////////////////////////////////////////
        // image가 존재하면 Gone 해제
        if(imageSize != 0) {
            detail_recycler2.visibility = View.VISIBLE
            detail_recycler2.setHasFixedSize(true)
            detail_recycler2.layoutManager = LinearLayoutManager(
                activity!!,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            detail_recycler2.adapter = ImageRecyclerAdapter(activity!!, urlList)

        }else {
            detail_recycler2.visibility = View.VISIBLE
        }
    }

}