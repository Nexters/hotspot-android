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
import kotlinx.android.synthetic.main.detail_view.*
import kotlinx.android.synthetic.main.mylist_view.*
import kotlinx.android.synthetic.main.register_view.*
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
    private var mySearch = false
    private lateinit var newPlace: MyPlace
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
    private var isRcylrdecoAdd = false

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

        myPlace = arguments!!.getSerializable("myPlace") as MyPlace
        imageSize = myPlace.images!!.size
        position = arguments!!.getSerializable("Position") as Int
        requestCode = arguments!!.getSerializable("RequestCode") as Int
        mySearch = arguments!!.getBoolean("mySearch")
        instaTag = StringBuffer(myPlace.place.placeName)
        latitude = myPlace.place.y.toDouble()
        longitude = myPlace.place.x.toDouble()
        rating = myPlace.rating
        d("TAG", "FragmentDetailView : ${myPlace}")
        d("TAG FragmentDetailView", "mySearch : ${mySearch}")

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
        if (categoryName != null) {
            when (categoryName) {
                "맛집" -> {
                    detail_category_img.setImageResource(R.drawable.ic_img_icon_food)
                }
                "카페" -> {
                    detail_category_img.setImageResource(R.drawable.ic_img_icon_cafe)
                }
                "술집" -> {
                    detail_category_img.setImageResource(R.drawable.ic_img_icon_drink)
                }
                "문화" -> {
                    detail_category_img.setImageResource(R.drawable.ic_img_icon_culture)
                }
                "기타" -> {
                    detail_category_img.setImageResource(R.drawable.ic_img_icon_etc)
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
        if(allDay!= null && allDay ) {
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
            var open_tmp = hours.open!!.toInt()
            var close_tmp = hours.close!!.toInt()

            //open time 설정
            if(12 < open_tmp && open_tmp < 24){
                open_tmp -= 12
                hoursBuffer.append(open_tmp.toString() +" PM")
            }else {
                hoursBuffer.append(open_tmp.toString()+" AM")
            }

            hoursBuffer.append(" - ")

            //close time 설정
            if(12 < close_tmp && close_tmp < 24){
                close_tmp -= 12
                hoursBuffer.append(close_tmp.toString()+" PM")
            }else {
                hoursBuffer.append(close_tmp.toString() +" AM")
            }

            list.add(hoursBuffer.toString())
        }
        d("TAG", "hours : ${hours}")
        d("TAG", "hours_open : ${hours!!.open}, hours_close: ${hours!!.close}")

        d("TAG", "list : ${list}")

        val deco = Stk_Rcylr_Item_Deco()
        detail_recycler1.layoutManager = LinearLayoutManager(
            activity!!,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        if(isRcylrdecoAdd) {
            detail_recycler1.removeItemDecorationAt(0)
            isRcylrdecoAdd = false
        }
        detail_recycler1.addItemDecoration(deco)
        isRcylrdecoAdd = true
        detail_recycler1.adapter = StickerRcylrAdapter(list)
        urlList = arrayListOf()
        imgList = mutableListOf()
        for(i in 0..imageSize-1){
            if(myPlace.images!!.get(i).url != null) {
                urlList.add(myPlace.images!!.get(i).url)

                d("TAG", "urlList[i] : ${urlList[i]}")
            }
        }

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
                if(mySearch)
                    resCode = 85

                var intent = Intent()
                intent.putExtra("NewSpotInfo", myPlace)
                intent.putExtra("Position", position)
                intent.putExtra("mySearch", true)
                d("TAG", "resCode : ${resCode}")
                activity!!.setResult(resCode,intent)
                activity!!.finish()
                d("TAG", "@@@@@@@@@@@@@@@@@@@@@@@@")

            }
            else {
                fragmentManager!!.beginTransaction()
                    .remove(this)
                    .commit()

                d("TAG", "#########################")
                activity!!.finish()
            }
        }

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
            try {
                var strEncodedUrl = URLEncoder.encode(instaTag.toString())
                var searched_latitude = myPlace.place.y
                var searched_longitude = myPlace.place.x
                var intent_search_road = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("nmap://route/public?dlat=" + searched_latitude + "&dlng=" + searched_longitude + "&dname=" + strEncodedUrl + "&appname=com.example.hotspot")
                )
                startActivity(intent_search_road)
            }catch(e : ActivityNotFoundException){

                var intent_market = Intent(Intent.ACTION_VIEW,Uri.parse("market://details?id=com.nhn.android.nmap"))
                startActivity(intent_market)
            }
        }

        ////////////////////////////////////////////////////////////////////////////////////
        // 삭제 API 추가!
        detail_delete_btn.setOnClickListener {
            detail_popup_layout.visibility = View.VISIBLE
            detail_findroad_img.isClickable = false
            detail_share_img.isClickable = false
            detail_insta_img.isClickable = false
            detail_edit_btn.isClickable = false
            detail_delete_btn.isClickable = false
        }
        detail_quit_ok_txt.setOnClickListener {
            detail_findroad_img.isClickable = true
            detail_share_img.isClickable = true
            detail_insta_img.isClickable = true
            detail_edit_btn.isClickable = true
            detail_delete_btn.isClickable = true

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

                        val intent = Intent()

                        if(requestCode == 21) {
                            requestCode = 94
                        }else {
                            requestCode = 95
                        }

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
                        detail_quit_ok_txt.isClickable = true
                    }

                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(activity!!,"삭제 실패 ! 네트워크 확인 바랍니다 !!", Toast.LENGTH_LONG).show()
                    d("Delete Error", t.message.toString())
                    d("Delete Error",t.cause.toString())
                    detail_quit_ok_txt.isClickable = true
                }
            })

        }
        detail_quit_no_txt.setOnClickListener {
            detail_popup_layout.visibility = View.GONE
            detail_findroad_img.isClickable = true
            detail_share_img.isClickable = true
            detail_insta_img.isClickable = true
            detail_edit_btn.isClickable = true
            detail_delete_btn.isClickable = true
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
        else if(resultCode == 89){
            resCode = 88
            if(data != null){
                newPlace = data.getSerializableExtra("NewSpotInfo") as MyPlace
                mySearch = data.getBooleanExtra("mySearch", false)
                d("TAG DetailView", "mySearch : $mySearch")
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
                    detail_category_img.setImageResource(R.drawable.ic_img_icon_food)
                }
                "카페" -> {
                    detail_category_img.setImageResource(R.drawable.ic_img_icon_cafe)
                }
                "술집" -> {
                    detail_category_img.setImageResource(R.drawable.ic_img_icon_drink)
                }
                "문화" -> {
                    detail_category_img.setImageResource(R.drawable.ic_img_icon_culture)
                }
                "기타" -> {
                    detail_category_img.setImageResource(R.drawable.ic_img_icon_etc)
                }
            }
        }

        if (!isVisited) {
            notVisitTxt.visibility = View.VISIBLE
        } else {
            notVisitTxt.visibility = View.GONE
        }

        d("FragmentdetailView", "rating: ${rating}")
        if(rating == 1) {
            detail_rating_img1.setImageResource(R.drawable.ic_img_star_gray2)
            detail_rating_img2.setImageResource(R.drawable.ic_img_start_gray)
            detail_rating_img3.setImageResource(R.drawable.ic_img_start_gray)
        }else if(rating == 2) {
            detail_rating_img1.setImageResource(R.drawable.ic_img_star_gray2)
            detail_rating_img2.setImageResource(R.drawable.ic_img_star_gray2)
            detail_rating_img3.setImageResource(R.drawable.ic_img_start_gray)
        }else if((rating == 0) || (rating == null)){
            detail_rating_img1.setImageResource(R.drawable.ic_img_start_gray)
            detail_rating_img2.setImageResource(R.drawable.ic_img_start_gray)
            detail_rating_img3.setImageResource(R.drawable.ic_img_start_gray)
        }else {
            detail_rating_img1.setImageResource(R.drawable.ic_img_star_gray2)
            detail_rating_img2.setImageResource(R.drawable.ic_img_star_gray2)
            detail_rating_img3.setImageResource(R.drawable.ic_img_star_gray2)
        }

        detail_menu_txt.text = "메뉴를 등록해주세요."

        if (bestmenu != null ) {
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

        // 추가 정보
        list.clear()
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
            var open_tmp = hours.open!!.toInt()
            var close_tmp = hours.close!!.toInt()

            //open time 설정
            if(12 < open_tmp && open_tmp < 24){
                open_tmp -= 12
                hoursBuffer.append(open_tmp.toString() +" PM")
            }else {
                hoursBuffer.append(open_tmp.toString()+" AM")
            }

            hoursBuffer.append(" - ")

            //close time 설정
            if(12 < close_tmp && close_tmp < 24 ){
                close_tmp -= 12
                hoursBuffer.append(close_tmp.toString()+" PM")
            }else {
                hoursBuffer.append(close_tmp.toString() +" AM")
            }

            list.add(hoursBuffer.toString())
        }
        d("TAG update", "hours : ${hours}")
        d("TAG update", "hours_open : ${hours!!.open}, hours_close: ${hours!!.close}")

        d("TAG Update", "list : ${list}")

        val deco = Stk_Rcylr_Item_Deco()
        detail_recycler1.layoutManager = LinearLayoutManager(
            activity!!,
            LinearLayoutManager.HORIZONTAL,
            false
        )

        if(isRcylrdecoAdd) {
            detail_recycler1.removeItemDecorationAt(0)
            isRcylrdecoAdd = false
        }
        detail_recycler1.addItemDecoration(deco)
        isRcylrdecoAdd = true
        detail_recycler1.adapter = StickerRcylrAdapter(list)

        urlList = arrayListOf()
        imgList = mutableListOf()
        d("TAG", "detailView ImgList Size : " + imgList.size.toString())
        for (i in 0..imageSize - 1) {
            if (myPlace.images!!.get(i).url != null) {
                urlList.add(myPlace.images!!.get(i).url)

                d("TAG", "urlList[i] : ${urlList[i]}")
            }
        }

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
            detail_recycler2.adapter = ImageRecyclerAdapter(activity!!, urlList)
            detail_recycler2.visibility = View.VISIBLE

        }
    }

}