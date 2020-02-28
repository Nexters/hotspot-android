package com.example.hotspot

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.PointF
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.ScaleAnimation
import android.widget.*
import androidx.annotation.UiThread
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible

import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.hotspot.views.BestMenuFinView
import com.example.hotspot.views.ConsSentView
import com.example.hotspot.views.PhotoFinView
import com.example.hotspot.views.WorkTimeFinView
import com.google.android.gms.location.FusedLocationProviderClient
import com.kakao.kakaolink.v2.KakaoLinkResponse
import com.kakao.kakaolink.v2.KakaoLinkService
import com.kakao.message.template.ButtonObject
import com.kakao.message.template.ContentObject
import com.kakao.message.template.LinkObject
import com.kakao.message.template.LocationTemplate
import com.kakao.network.ErrorResult
import com.kakao.network.callback.ResponseCallback
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.Overlay
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.FusedLocationSource
import com.naver.maps.map.widget.LocationButtonView
import com.squareup.otto.Subscribe
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.map_view.*
import kotlinx.android.synthetic.main.myplace_item.*
import kotlinx.android.synthetic.main.register_view.*
import org.w3c.dom.Text
import java.io.Serializable
import java.net.URLEncoder


class FragmentMap: Fragment()
    , OnMapReadyCallback
    , NaverMap.OnMapClickListener {
    private lateinit var mapView: MapView
    private lateinit var placeList : List<MyPlace>
    private lateinit var markerList : ArrayList<Marker>
    private lateinit var spotinfoLayout : ConstraintLayout
    private lateinit var btn_insta : ImageView
    private lateinit var instaTag : String
    private lateinit var layout_transparency : ConstraintLayout
    private var stateCategory = "전체"
    private var stateImgVisted = 2 // 0이면 방문함 1이면 방문예정 2이면 모두
    private val MARKER_WIDTH = 200
    private val MARKER_HEIGHT = 200
    //사용자 위치 추적 기능
    private lateinit var locationSource : FusedLocationSource
    private var spot_count = 0
    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }
    private var curr_longitude : Double = 126.977944
    private var curr_latitude : Double = 37.566293

    private lateinit var nMap: NaverMap

    private var searched_longitude : Double = 0.0
    private var searched_latitude : Double = 0.0
    private var searched_roadAddress = ""
    private var searched_placeName = ""
    private var isSpotAdd = false
    private var isNewUser = false

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (locationSource.onRequestPermissionsResult(requestCode, permissions,
                grantResults)) {
            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        isNewUser = arguments!!.getSerializable("IsNewUser") as Boolean
        locationSource = FusedLocationSource(activity!!, LOCATION_PERMISSION_REQUEST_CODE)

        spotinfoLayout = activity!!.findViewById(R.id.spotinfolayout)
        layout_transparency = activity!!.findViewById(R.id.layout_trans_main)

        val bundle = arguments
        placeList = bundle!!.getSerializable("PlaceList") as List<MyPlace>
        isSpotAdd = bundle!!.getSerializable("IsSpotAdd") as Boolean
        spot_count = placeList.size
        activity!!.mapBt.visibility = View.INVISIBLE
        activity!!.listBt.visibility = View.VISIBLE

        val view = inflater.inflate(R.layout.map_view,container,false)


        return view
    }


    override fun onMapReady(p0: NaverMap) {
        markerList = arrayListOf()
        nMap = p0
        p0.minZoom = 6.0
        p0.maxZoom = 19.0
        p0.extent = LatLngBounds(LatLng(31.43, 122.37), LatLng(44.35, 132.0))
        p0.uiSettings.isCompassEnabled = false
        p0.uiSettings.isZoomControlEnabled = false
        //지도 나이트 모드
        p0.mapType = NaverMap.MapType.Navi
        p0.isNightModeEnabled = true
        if(isSpotAdd) {
            val cameraUpdate = CameraUpdate.scrollAndZoomTo(
                LatLng(placeList.get(0).place.y.toDouble(), placeList.get(0).place.x.toDouble()),
                10.0
            ) //해당 위치로 카메라 시점 이동(위치 넘겨받기)
            p0.moveCamera(cameraUpdate)

            //마지막에 트래킹 모드 켜야함.
            p0.locationSource = locationSource
            p0.locationTrackingMode = LocationTrackingMode.NoFollow
            p0.addOnLocationChangeListener { location ->
                curr_latitude = location.latitude
                curr_longitude = location.longitude
            }
        }
        else{
            //현위치 업데이트
            p0.locationSource = locationSource
            p0.locationTrackingMode = LocationTrackingMode.Follow
            p0.addOnLocationChangeListener { location ->
                curr_latitude = location.latitude
                curr_longitude = location.longitude
            }

        }
        p0.setOnMapClickListener(this)



        //마커추가
        for(i in 0 .. placeList.size-1) {
            val marker = Marker()
            val myPlace :MyPlace = placeList[i]
            marker.position =
                LatLng(myPlace.place.y.toDouble(), myPlace.place.x.toDouble())
            marker.map = p0
            if(myPlace.place.categoryName.isNullOrEmpty()){
                setMarkerImg("기타",marker,p0)
            }else{
                setMarkerImg(myPlace.place.categoryName,marker,p0)
            }
            marker.captionColor = resources.getColor(R.color.colorWhite)
            marker.captionHaloColor = resources.getColor(R.color.colorNocolor)
            marker.setOnClickListener(Overlay.OnClickListener {
                if(!spotinfoLayout.isVisible) {
                    setStickerPosition()
                    setSticker(marker, myPlace)
                    var placeName = myPlace.place.placeName
                    var roadAdress = myPlace.place.roadAddressName
                    var category = myPlace.place.categoryName
                    var categoryImgView =
                        activity!!.findViewById<ImageView>(R.id.img__spotinfo_category)
                    if (category != null) {
                        when (category) {
                            "카페" -> {
                                categoryImgView.setImageResource(R.drawable.ic_cafe_black)
                                spotinfoLayout.background =
                                    resources.getDrawable(R.drawable.myplace_list_btn2)
                                layout_transparency.findViewById<ImageView>(R.id.main_category_sticker_view)
                                    .setImageResource(R.drawable.img_icon_cafe_png)
                            }
                            "맛집" -> {
                                categoryImgView.setImageResource(R.drawable.ic_food_black)
                                spotinfoLayout.background =
                                    resources.getDrawable(R.drawable.myplace_list_btn1)
                                layout_transparency.findViewById<ImageView>(R.id.main_category_sticker_view)
                                    .setImageResource(R.drawable.img_icon_food_png)
                            }
                            "문화" -> {
                                categoryImgView.setImageResource(R.drawable.ic_culture_black)
                                spotinfoLayout.background =
                                    resources.getDrawable(R.drawable.myplace_list_btn4)
                                layout_transparency.findViewById<ImageView>(R.id.main_category_sticker_view)
                                    .setImageResource(R.drawable.img_icon_culture_png)
                            }
                            "술집" -> {
                                categoryImgView.setImageResource(R.drawable.ic_drink_black)
                                spotinfoLayout.background =
                                    resources.getDrawable(R.drawable.myplace_list_btn3)
                                layout_transparency.findViewById<ImageView>(R.id.main_category_sticker_view)
                                    .setImageResource(R.drawable.img_icon_drink_png)
                            }
                            "기타" -> {
                                categoryImgView.setImageResource(R.drawable.ic_etc_black)
                                spotinfoLayout.background =
                                    resources.getDrawable(R.drawable.myplace_list_btn5)
                                layout_transparency.findViewById<ImageView>(R.id.main_category_sticker_view)
                                    .setImageResource(R.drawable.img_icon_etc_png)
                            }
                        }
                    } else {
                        categoryImgView.setImageResource(R.drawable.ic_etc_black)
                        spotinfoLayout.background =
                            resources.getDrawable(R.drawable.myplace_list_btn5)
                        layout_transparency.findViewById<ImageView>(R.id.main_category_sticker_view)
                            .setImageResource(R.drawable.img_icon_etc_png)
                    }
                    activity!!.findViewById<TextView>(R.id.txt_spotinfo_placename).text = placeName
                    activity!!.findViewById<TextView>(R.id.txt_spotinfo_address).text = roadAdress

                    var ratingView1 =
                        activity!!.findViewById<ImageView>(R.id.img_spotinfo_rating1)
                    var ratingView2 =
                        activity!!.findViewById<ImageView>(R.id.img_spotinfo_rating2)
                    var ratingView3 =
                        activity!!.findViewById<ImageView>(R.id.img_spotinfo_rating3)
                    if (myPlace.visited) {
                        var rating = myPlace.rating

                        when (rating) {
                            1 -> {
                                ratingView1.visibility = View.VISIBLE
                                ratingView2.visibility = View.INVISIBLE
                                ratingView3.visibility = View.INVISIBLE
                            }
                            2 -> {
                                ratingView1.visibility = View.VISIBLE
                                ratingView2.visibility = View.VISIBLE
                                ratingView3.visibility = View.INVISIBLE
                            }
                            3 -> {
                                ratingView1.visibility = View.VISIBLE
                                ratingView2.visibility = View.VISIBLE
                                ratingView3.visibility = View.VISIBLE
                            }
                        }
                    }
                    else{
                        ratingView1.visibility = View.INVISIBLE
                        ratingView2.visibility = View.INVISIBLE
                        ratingView3.visibility = View.INVISIBLE
                    }

                    instaTag = placeName
                    searched_latitude = myPlace.place.y.toDouble()
                    searched_longitude = myPlace.place.x.toDouble()
                    searched_placeName = myPlace.place.placeName
                    searched_roadAddress = myPlace.place.roadAddressName
                    /*
                if(spotinfoLayout.isVisible){
                    spotinfoLayout.visibility = View.GONE
                    layout_transparency.visibility = View.INVISIBLE
                    img_curr_pos.visibility = View.VISIBLE
                    img_main_isvisited.visibility = View.VISIBLE
                    activity!!.findViewById<ImageView>(R.id.map_btn_add).visibility = View.VISIBLE

                }*/


                    val cameraUpdate2 =
                        CameraUpdate.scrollAndZoomTo(marker.position, p0.cameraPosition.zoom)

                    cameraUpdate2.animate(CameraAnimation.Easing, 700)
                    cameraUpdate2.finishCallback {
                        //mapView.visibility = View.VISIBLE


                    }
                    //mapView.visibility = View.INVISIBLE
                    spotinfoLayout.visibility = View.VISIBLE
                    layout_transparency.visibility = View.VISIBLE
                    img_curr_pos.visibility = View.INVISIBLE
                    img_main_isvisited.visibility = View.INVISIBLE
                    activity!!.findViewById<ImageView>(R.id.map_btn_add).visibility = View.GONE
                    p0.moveCamera(cameraUpdate2)


                    spotinfoLayout.setOnClickListener {
                        val intent = Intent(activity, DetailActivity::class.java)
                        intent.putExtra("myPlace", myPlace as Serializable)
                        intent.putExtra("Position", i)
                        intent.putExtra("RequestCode", 21)
                        startActivityForResult(intent, 21)


                    }
                }
                true
            })
            marker.captionText = placeList[i].place.placeName
            markerList.add(marker)
        }
        setStickerPosition()
        //Map 위의 버튼들 세팅
        setButton(p0)
        if(isSpotAdd) {
            markerList.get(0).performClick()
            //gif 실행
            startSpotGif(placeList.get(0).place.categoryName)
        }



    }
    private fun startSpotGif(category: String) {
        val listener = object : RequestListener<GifDrawable> {
            override fun onLoadFailed(
                e: GlideException?, model: Any,
                target: Target<GifDrawable>,
                isFirstResource: Boolean
            ): Boolean {
                return false
            }

            override fun onResourceReady(
                resource: GifDrawable,
                model: Any,
                target: Target<GifDrawable>,
                dataSource: DataSource,
                isFirstResource: Boolean
            ): Boolean {
                resource.setLoopCount(1)
                return false
            }
        }
        when (category) {
            "맛집" -> {
                Glide.with(this)
                    .asGif()
                    .load(R.raw.food_gif)
                    .listener(listener)
                    .into(activity!!.findViewById(R.id.main_category_sticker_view))
            }
            "카페" -> {
                Glide.with(this)
                    .asGif()
                    .load(R.raw.cafe_gif)
                    .listener(listener)
                    .into(activity!!.findViewById(R.id.main_category_sticker_view))
            }
            "술집" -> {
                Glide.with(this)
                    .asGif()
                    .load(R.raw.drink_gif)
                    .listener(listener)
                    .into(activity!!.findViewById(R.id.main_category_sticker_view))
            }
            "문화" -> {
                Glide.with(this)
                    .asGif()
                    .load(R.raw.culture_gif)
                    .listener(listener)
                    .into(activity!!.findViewById(R.id.main_category_sticker_view))
            }
            "기타" -> {
                Glide.with(this)
                    .asGif()
                    .load(R.raw.etc_gif)
                    .listener(listener)
                    .into(activity!!.findViewById(R.id.main_category_sticker_view))
            }

        }
    }


    private fun setSticker(marker :Marker , myPlace : MyPlace){
        var alldayView = layout_transparency.findViewById<ConsSentView>(R.id.main_24h_view)
        var consentView = layout_transparency.findViewById<ConsSentView>(R.id.main_consent_view)
        var parkView = layout_transparency.findViewById<ConsSentView>(R.id.main_park_view)
        var bestMenuView = layout_transparency.findViewById<BestMenuFinView>(R.id.main_best_view)
        var photoView = layout_transparency.findViewById<PhotoFinView>(R.id.main_photo_view)
        var worktimeView = layout_transparency.findViewById<WorkTimeFinView>(R.id.main_worktime_view)
        if(myPlace.allDayAvailable == null){
            alldayView.visibility = View.INVISIBLE
        }
        else{
            alldayView.visibility = View.VISIBLE
        }
        if(myPlace.powerPlugAvailable == null){
            consentView.visibility = View.INVISIBLE
        }
        else{
            consentView.visibility = View.VISIBLE
        }
        if(myPlace.parkingAvailable == null){
            parkView.visibility = View.INVISIBLE
        }
        else{
            parkView.visibility = View.VISIBLE
        }
        if(myPlace.bestMenu.isNullOrEmpty()){
            bestMenuView.visibility = View.INVISIBLE
        }
        else{
            bestMenuView.findViewById<TextView>(R.id.up1).text = myPlace.bestMenu!![0]
            if(myPlace.bestMenu!!.size == 2) {
                bestMenuView.findViewById<TextView>(R.id.up2).text = myPlace.bestMenu!![1]
            }
            bestMenuView.visibility = View.VISIBLE
        }
        if(myPlace.images.isNullOrEmpty()){
            photoView.visibility = View.INVISIBLE
        }
        else{
            photoView.findViewById<TextView>(R.id.photo_count_txt).text = myPlace.images!!.size.toString()
            photoView.visibility = View.VISIBLE
        }
        if(myPlace.businessHours!!.open.isNullOrEmpty()){
            worktimeView.visibility = View.INVISIBLE
        }
        else{
            if(myPlace.businessHours!!.open!!.toInt() <= 12){
                worktimeView.findViewById<TextView>(R.id.work_fin_open_txt).text = myPlace.businessHours!!.open + "AM"
            }
            else{
                worktimeView.findViewById<TextView>(R.id.work_fin_open_txt).text = (myPlace.businessHours!!.open!!.toInt()-12).toString() + "PM"
            }

            if(myPlace.businessHours!!.close!!.toInt() <= 12){
                worktimeView.findViewById<TextView>(R.id.work_fin_closed_txt).text = myPlace.businessHours!!.close + "AM"
            }
            else{
                worktimeView.findViewById<TextView>(R.id.work_fin_closed_txt).text = (myPlace.businessHours!!.close!!.toInt()-12).toString() + "PM"
            }
            worktimeView.visibility = View.VISIBLE
        }
    }
    private fun setMarkerImg(categoryName : String,marker : Marker, nMap: NaverMap){
        when(categoryName){
            "카페" -> {
                var size = BitmapFactory.decodeResource(resources,R.drawable.img_icon_cafe_png)
                size = Bitmap.createScaledBitmap(size,MARKER_WIDTH,MARKER_HEIGHT,true)
                marker.icon = OverlayImage.fromBitmap(size)
                marker.map = nMap
            }
            "맛집" -> {
                var size = BitmapFactory.decodeResource(resources,R.drawable.img_icon_food_png)
                size = Bitmap.createScaledBitmap(size,MARKER_WIDTH,MARKER_HEIGHT,true)
                marker.icon = OverlayImage.fromBitmap(size)
                marker.map = nMap
            }
            "문화" -> {
                var size = BitmapFactory.decodeResource(resources,R.drawable.img_icon_culture_png)
                size = Bitmap.createScaledBitmap(size,MARKER_WIDTH,MARKER_HEIGHT,true)
                marker.icon = OverlayImage.fromBitmap(size)
                marker.map = nMap
            }
            "술집" -> {
                var size = BitmapFactory.decodeResource(resources,R.drawable.img_icon_drink_png)
                size = Bitmap.createScaledBitmap(size,MARKER_WIDTH,MARKER_HEIGHT,true)
                marker.icon = OverlayImage.fromBitmap(size)
                marker.map = nMap
            }
            "기타" -> {
                var size = BitmapFactory.decodeResource(resources,R.drawable.img_icon_etc_png)
                size = Bitmap.createScaledBitmap(size,MARKER_WIDTH,MARKER_HEIGHT,true)
                marker.icon = OverlayImage.fromBitmap(size)
                marker.map = nMap
            }
        }
    }
    private fun changeCategory(nMap : NaverMap,stateCategory : String , stateImgVisited : Int){// 0이면 방문함 1이면 방문예정 2이면 모두
        spot_count = 0
        when(stateImgVisited){
            0 ->{
                for( i in 0..(markerList.size-1)){
                    var marker = markerList.get(i)
                    var place = placeList.get(i)
                    if(place.place.categoryName.isNullOrEmpty()){
                        if(stateCategory.equals("기타")||stateCategory.equals("전체")){
                            if(place.visited){
                                marker.map = nMap
                                spot_count++
                            }
                            else marker.map = null
                        }
                        else{
                            marker.map = null
                        }
                    }
                    else if(stateCategory.equals("전체")){
                        if(place.visited){
                            marker.map = nMap
                            spot_count++
                        }
                        else marker.map = null
                    }
                    else if((place.visited)&&(place.place.categoryName.equals(stateCategory))){
                        marker.map = nMap
                        spot_count++
                    }
                    else{
                        marker.map = null
                    }
                }
                activity!!.findViewById<TextView>(R.id.hpCount).text = spot_count.toString()
            }
            1 ->{
                for( i in 0..(markerList.size-1)){
                    var marker = markerList.get(i)
                    var place = placeList.get(i)
                    if(place.place.categoryName.isNullOrEmpty()){
                        if(stateCategory.equals("기타")||stateCategory.equals("전체")){
                            if(!place.visited){
                                marker.map = nMap
                                spot_count++

                            }
                            else marker.map = null
                        }
                        else{
                            marker.map = null
                        }
                    }
                    else if(stateCategory.equals("전체")){
                        if(!place.visited){
                            marker.map = nMap
                            spot_count++

                        }
                        else marker.map = null
                    }
                    else if((!place.visited)&&(place.place.categoryName.equals(stateCategory))){
                        marker.map = nMap
                        spot_count++

                    }
                    else{
                        marker.map = null
                    }
                }
                activity!!.findViewById<TextView>(R.id.hpCount).text = spot_count.toString()


            }
            2 ->{
                for( i in 0..(markerList.size-1)){
                    var marker = markerList.get(i)
                    var place = placeList.get(i)
                    if(place.place.categoryName.isNullOrEmpty()){
                        if(stateCategory.equals("기타")||stateCategory.equals("전체")){
                            marker.map = nMap
                            spot_count++
                        }
                        else{
                            marker.map = null
                        }
                    }
                    else if(stateCategory.equals("전체")){
                        marker.map = nMap
                        spot_count++
                    }
                    else if(place.place.categoryName.equals(stateCategory)){
                        marker.map = nMap
                        spot_count++
                    }
                    else{
                        marker.map = null
                    }
                }
                activity!!.findViewById<TextView>(R.id.hpCount).text = spot_count.toString()

            }
        }
    }
    private fun setButton(nMap : NaverMap){
        layout_transparency.setOnClickListener{
            spotinfoLayout.visibility = View.GONE
            layout_transparency.visibility = View.INVISIBLE
            img_curr_pos.visibility = View.VISIBLE
            img_main_isvisited.visibility = View.VISIBLE
            activity!!.findViewById<ImageView>(R.id.map_btn_add).visibility = View.VISIBLE

        }
        btn_insta = activity!!.findViewById(R.id.btn_insta)

        btn_insta.setOnClickListener {
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
        activity!!.findViewById<ImageView>(R.id.btn_search_road).setOnClickListener{
            try {
                var strEncodedUrl = URLEncoder.encode(instaTag)
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

        activity!!.findViewById<ImageView>(R.id.btn_share).setOnClickListener{

            try {
                var params: LocationTemplate
                params = LocationTemplate.newBuilder(
                    searched_roadAddress, ContentObject.newBuilder(
                        searched_placeName,
                        "https://",
                        LinkObject.newBuilder().setWebUrl("https://developers.kakao.com")
                            .setMobileWebUrl("https://developers.kakao.com").build()
                    )
                        .setDescrption(searched_roadAddress).build()
                ).addButton(
                    ButtonObject(
                        "Spotter", LinkObject.newBuilder()
                            .setWebUrl("https://developers.kakao.com")
                            .setMobileWebUrl("https://developers.kakao.com")
                            .setAndroidExecutionParams("hi").build()
                    )
                )
                    .setAddressTitle(searched_placeName).build()

                var serverCallbackArgs = HashMap<String, String>()

                serverCallbackArgs.put("user_id", "${placeList.get(0).userId}")
                serverCallbackArgs.put("product_id", "${2}")
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
            }catch(e : ActivityNotFoundException){

                var intent_market = Intent(Intent.ACTION_VIEW,Uri.parse("market://details?id=com.kakao.talk"))
                startActivity(intent_market)
            }
        }
        img_curr_pos.setOnClickListener{
            val cameraUpdate = CameraUpdate.scrollAndZoomTo(LatLng(curr_latitude, curr_longitude),10.0) //해당 위치로 카메라 시점 이동(위치 넘겨받기)
            nMap.moveCamera(cameraUpdate)
        }
        img_main_isvisited.setOnClickListener{
            spot_count= 0
            when(stateImgVisted){// 0이면 방문함 1이면 방문예정 2이면 모두
                2 -> {//모두이면 0 방문함으로 바꾸고 마커들도 바꿔야함
                    img_main_isvisited.setImageResource(R.drawable.img_main_ismarked)
                    stateImgVisted = 0
                    for( i in (0..markerList.size-1)){
                        var marker = markerList.get(i)
                        var place = placeList.get(i)
                        if(place.place.categoryName.isNullOrEmpty()){
                            if(stateCategory.equals("기타")||stateCategory.equals("전체")){
                                if(place.visited){
                                    marker.map = nMap
                                    spot_count++
                                }
                                else marker.map = null
                            }
                            else{
                                marker.map = null
                            }
                        }
                        else if(stateCategory.equals("전체")){
                            if(place.visited){
                                marker.map = nMap
                                spot_count++
                            }
                            else marker.map = null
                        }
                        else if((place.visited)&&(place.place.categoryName.equals(stateCategory))){
                            marker.map = nMap
                            spot_count++
                        }
                        else{
                            marker.map = null
                        }
                    }
                    activity!!.findViewById<TextView>(R.id.hpCount).text = spot_count.toString()

                }
                1 -> {//방문예정이면 2 모두로 바꾸기
                    img_main_isvisited.setImageResource(R.drawable.img_main_all_xxxhdpi)
                    stateImgVisted = 2
                    for( i in (0..markerList.size-1)){
                        var marker = markerList.get(i)
                        var place = placeList.get(i)
                        if(place.place.categoryName.isNullOrEmpty()){
                            if(stateCategory.equals("기타")||stateCategory.equals("전체")){
                                marker.map = nMap
                                spot_count++
                            }
                            else{
                                marker.map = null
                            }
                        }
                        else if(stateCategory.equals("전체")){
                            marker.map = nMap
                            spot_count++
                        }
                        else if(place.place.categoryName.equals(stateCategory)) {
                            marker.map = nMap
                            spot_count++
                        }
                        else marker.map = null
                    }
                    activity!!.findViewById<TextView>(R.id.hpCount).text = spot_count.toString()

                }
                0 -> {//방문함이면 1 방문예정으로
                    img_main_isvisited.setImageResource(R.drawable.img_main_will)
                    stateImgVisted = 1
                    for( i in (0..markerList.size-1)){
                        var marker = markerList.get(i)
                        var place = placeList.get(i)
                        if(place.place.categoryName.isNullOrEmpty()){//카테고리 널이면 기타로 인식
                            if(stateCategory.equals("기타")||stateCategory.equals("전체")){
                                if(!place.visited) {
                                    marker.map = nMap
                                    spot_count++
                                }
                                else{
                                    marker.map = null
                                }
                            }
                            else{
                                marker.map = null
                            }
                        }
                        else if(stateCategory.equals("전체")){
                            if(!place.visited) {
                                marker.map = nMap
                                spot_count++
                            }
                            else{
                                marker.map = null
                            }
                        }
                        else if((!placeList.get(i).visited)&&(placeList.get(i).place.categoryName.equals(stateCategory))){
                            marker.map = nMap
                            spot_count++
                        }
                        else{
                            marker.map = null
                        }
                    }
                    activity!!.findViewById<TextView>(R.id.hpCount).text = spot_count.toString()

                }
            }
        }

        activity!!.findViewById<TextView>(R.id.category_item1_txt).setOnClickListener{
            if(!spotinfoLayout.isVisible) {
                stateCategory = "전체"
                changeCategory(nMap, stateCategory, this.stateImgVisted)
                activity!!.findViewById<TextView>(R.id.category_item1_txt)
                    .setTextColor(Color.parseColor("#FFFFFF"))
                activity!!.findViewById<TextView>(R.id.category_item2_txt)
                    .setTextColor(Color.parseColor("#393D46"))
                activity!!.findViewById<TextView>(R.id.category_item3_txt)
                    .setTextColor(Color.parseColor("#393D46"))
                activity!!.findViewById<TextView>(R.id.category_item4_txt)
                    .setTextColor(Color.parseColor("#393D46"))
                activity!!.findViewById<TextView>(R.id.category_item5_txt)
                    .setTextColor(Color.parseColor("#393D46"))
                activity!!.findViewById<TextView>(R.id.category_item6_txt)
                    .setTextColor(Color.parseColor("#393D46"))
                activity!!.findViewById<ImageView>(R.id.title_category_imgview)
                    .setImageResource(R.drawable.img_category_title_all)
            }
        }
        activity!!.findViewById<TextView>(R.id.category_item2_txt).setOnClickListener{
            if(!spotinfoLayout.isVisible) {
                stateCategory = "맛집"
                changeCategory(nMap, stateCategory, this.stateImgVisted)
                activity!!.findViewById<TextView>(R.id.category_item1_txt)
                    .setTextColor(Color.parseColor("#393D46"))
                activity!!.findViewById<TextView>(R.id.category_item2_txt)
                    .setTextColor(Color.parseColor("#FFFFFF"))
                activity!!.findViewById<TextView>(R.id.category_item3_txt)
                    .setTextColor(Color.parseColor("#393D46"))
                activity!!.findViewById<TextView>(R.id.category_item4_txt)
                    .setTextColor(Color.parseColor("#393D46"))
                activity!!.findViewById<TextView>(R.id.category_item5_txt)
                    .setTextColor(Color.parseColor("#393D46"))
                activity!!.findViewById<TextView>(R.id.category_item6_txt)
                    .setTextColor(Color.parseColor("#393D46"))
                activity!!.findViewById<ImageView>(R.id.title_category_imgview)
                    .setImageResource(R.drawable.img_category_title_food)
            }
        }
        activity!!.findViewById<TextView>(R.id.category_item3_txt).setOnClickListener{
            if(!spotinfoLayout.isVisible) {
                stateCategory = "카페"
                changeCategory(nMap, stateCategory, this.stateImgVisted)
                activity!!.findViewById<TextView>(R.id.category_item1_txt)
                    .setTextColor(Color.parseColor("#393D46"))
                activity!!.findViewById<TextView>(R.id.category_item2_txt)
                    .setTextColor(Color.parseColor("#393D46"))
                activity!!.findViewById<TextView>(R.id.category_item3_txt)
                    .setTextColor(Color.parseColor("#FFFFFF"))
                activity!!.findViewById<TextView>(R.id.category_item4_txt)
                    .setTextColor(Color.parseColor("#393D46"))
                activity!!.findViewById<TextView>(R.id.category_item5_txt)
                    .setTextColor(Color.parseColor("#393D46"))
                activity!!.findViewById<TextView>(R.id.category_item6_txt)
                    .setTextColor(Color.parseColor("#393D46"))
                activity!!.findViewById<ImageView>(R.id.title_category_imgview)
                    .setImageResource(R.drawable.img_category_title_cafe)
            }
        }
        activity!!.findViewById<TextView>(R.id.category_item4_txt).setOnClickListener{
            if(!spotinfoLayout.isVisible) {
                stateCategory = "술집"
                changeCategory(nMap, stateCategory, this.stateImgVisted)
                activity!!.findViewById<TextView>(R.id.category_item1_txt)
                    .setTextColor(Color.parseColor("#393D46"))
                activity!!.findViewById<TextView>(R.id.category_item2_txt)
                    .setTextColor(Color.parseColor("#393D46"))
                activity!!.findViewById<TextView>(R.id.category_item3_txt)
                    .setTextColor(Color.parseColor("#393D46"))
                activity!!.findViewById<TextView>(R.id.category_item4_txt)
                    .setTextColor(Color.parseColor("#FFFFFF"))
                activity!!.findViewById<TextView>(R.id.category_item5_txt)
                    .setTextColor(Color.parseColor("#393D46"))
                activity!!.findViewById<TextView>(R.id.category_item6_txt)
                    .setTextColor(Color.parseColor("#393D46"))
                activity!!.findViewById<ImageView>(R.id.title_category_imgview)
                    .setImageResource(R.drawable.img_category_title_drink)
            }
        }
        activity!!.findViewById<TextView>(R.id.category_item5_txt).setOnClickListener{
            if(!spotinfoLayout.isVisible) {
                stateCategory = "문화"
                changeCategory(nMap, stateCategory, this.stateImgVisted)
                activity!!.findViewById<TextView>(R.id.category_item1_txt)
                    .setTextColor(Color.parseColor("#393D46"))
                activity!!.findViewById<TextView>(R.id.category_item2_txt)
                    .setTextColor(Color.parseColor("#393D46"))
                activity!!.findViewById<TextView>(R.id.category_item3_txt)
                    .setTextColor(Color.parseColor("#393D46"))
                activity!!.findViewById<TextView>(R.id.category_item4_txt)
                    .setTextColor(Color.parseColor("#393D46"))
                activity!!.findViewById<TextView>(R.id.category_item5_txt)
                    .setTextColor(Color.parseColor("#FFFFFF"))
                activity!!.findViewById<TextView>(R.id.category_item6_txt)
                    .setTextColor(Color.parseColor("#393D46"))
                activity!!.findViewById<ImageView>(R.id.title_category_imgview)
                    .setImageResource(R.drawable.img_category_title_culture)
            }
        }
        activity!!.findViewById<TextView>(R.id.category_item6_txt).setOnClickListener{
            if(!spotinfoLayout.isVisible) {
                stateCategory = "기타"
                changeCategory(nMap, stateCategory, stateImgVisted)
                activity!!.findViewById<TextView>(R.id.category_item1_txt)
                    .setTextColor(Color.parseColor("#393D46"))
                activity!!.findViewById<TextView>(R.id.category_item2_txt)
                    .setTextColor(Color.parseColor("#393D46"))
                activity!!.findViewById<TextView>(R.id.category_item3_txt)
                    .setTextColor(Color.parseColor("#393D46"))
                activity!!.findViewById<TextView>(R.id.category_item4_txt)
                    .setTextColor(Color.parseColor("#393D46"))
                activity!!.findViewById<TextView>(R.id.category_item5_txt)
                    .setTextColor(Color.parseColor("#393D46"))
                activity!!.findViewById<TextView>(R.id.category_item6_txt)
                    .setTextColor(Color.parseColor("#FFFFFF"))
                activity!!.findViewById<ImageView>(R.id.title_category_imgview)
                    .setImageResource(R.drawable.img_category_title_etc)
            }
        }
    }
    override fun onMapClick(p0: PointF, p1: LatLng) {


    }
    private fun setStickerPosition(){
        activity!!.findViewById<ConsSentView>(R.id.main_24h_view).x = layout_transparency.width*0.67f
        activity!!.findViewById<ConsSentView>(R.id.main_24h_view).y = layout_transparency.height*0.21f

        activity!!.findViewById<ConsSentView>(R.id.main_consent_view).x = layout_transparency.width*0.1f
        activity!!.findViewById<ConsSentView>(R.id.main_consent_view).y = layout_transparency.height*0.42f

        activity!!.findViewById<ConsSentView>(R.id.main_park_view).x = layout_transparency.width*0.3f
        activity!!.findViewById<ConsSentView>(R.id.main_park_view).y = layout_transparency.height*0.57f

        activity!!.findViewById<PhotoFinView>(R.id.main_photo_view).x = layout_transparency.width*0.1f
        activity!!.findViewById<PhotoFinView>(R.id.main_photo_view).y = layout_transparency.height*0.62f

        activity!!.findViewById<BestMenuFinView>(R.id.main_best_view).x = layout_transparency.width*0.5f
        activity!!.findViewById<BestMenuFinView>(R.id.main_best_view).y = layout_transparency.height*0.45f

        activity!!.findViewById<WorkTimeFinView>(R.id.main_worktime_view).x = layout_transparency.width*0.03f
        activity!!.findViewById<WorkTimeFinView>(R.id.main_worktime_view).y = layout_transparency.height*0.21f


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView = view.findViewById(R.id.map_view)
        mapView.getMapAsync(this)
        mapView.onCreate(savedInstanceState)
        map_btn_add.setOnClickListener {
            val intent = Intent(activity, RegisterActivity::class.java)
            val isAdd = true

            intent.putExtra("IsNewUser",isNewUser as Serializable)
            intent.putExtra("IsAdd",isAdd)
            //10번은 맵 > 장소등록
            startActivityForResult(intent,10)

        }
    }
    @Subscribe
    fun onActivityResultEvent(activityResultEvent: ActivityResultEvent){
        onActivityResult(activityResultEvent.get_RequestCode(),activityResultEvent.get_ResultCode(),activityResultEvent.get_Data())

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == 9){//장소등록에서 취소 햇을 때
            isNewUser = false
        }
        if(resultCode == 10){//장소 등록했으니 맵에서 띄우고 애니메이션
            //myplacelist에 추가
            //markerlist에 추가
            //위치로 카메라 업데이트 후
            //애니메이션
            //스티커 보여주기
            isNewUser = false

        }

    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

}