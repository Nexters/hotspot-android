package com.example.hotspot

import android.app.Activity
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
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible

import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.Overlay
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.FusedLocationSource
import com.naver.maps.map.widget.LocationButtonView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.map_view.*
import kotlinx.android.synthetic.main.register_view.*
import org.w3c.dom.Text
import java.io.Serializable


class FragmentMap: Fragment()/*, MapView.MapViewEventListener,MapView.POIItemEventListener*/
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
    private var curr_longitude : Double = 0.0
    private var curr_latitude : Double = 0.0

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

        locationSource = FusedLocationSource(activity!!, LOCATION_PERMISSION_REQUEST_CODE)

        spotinfoLayout = activity!!.findViewById(R.id.spotinfolayout)
        layout_transparency = activity!!.findViewById(R.id.layout_trans_main)

        val bundle = arguments
        placeList = bundle!!.getSerializable("PlaceList") as List<MyPlace>
        spot_count = placeList.size


        val view = inflater.inflate(R.layout.map_view,container,false)


        return view
    }

    override fun onMapReady(p0: NaverMap) {
        markerList = arrayListOf()
        p0.uiSettings.isCompassEnabled = false
        p0.uiSettings.isZoomControlEnabled = false
        //지도 나이트 모드
        p0.mapType = NaverMap.MapType.Navi
        p0.isNightModeEnabled = true
        p0.isIndoorEnabled = true
        p0.setOnMapClickListener(this)
        //현위치 업데이트
        p0.locationSource = locationSource
        p0.locationTrackingMode = LocationTrackingMode.Follow
        p0.addOnLocationChangeListener { location ->
            curr_latitude = location.latitude
            curr_longitude = location.longitude
        }

        val cameraUpdate = CameraUpdate.scrollTo(LatLng(curr_latitude, curr_longitude)) //해당 위치로 카메라 시점 이동(위치 넘겨받기)
        p0.moveCamera(cameraUpdate)
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
            marker.setOnClickListener(Overlay.OnClickListener {

                var placeName = myPlace.place.placeName
                var roadAdress = myPlace.place.roadAddressName

                var category = myPlace.place.categoryName
                var categoryImgView = activity!!.findViewById<ImageView>(R.id.img__spotinfo_category)
                if(category!=null) {
                    when (category) {
                        "카페" -> {
                            categoryImgView.setImageResource(R.drawable.ic_cafe_black)
                            spotinfoLayout.background =
                                resources.getDrawable(R.drawable.myplace_list_btn2)
                        }
                        "맛집" -> {
                            categoryImgView.setImageResource(R.drawable.ic_food_black)
                            spotinfoLayout.background =
                                resources.getDrawable(R.drawable.myplace_list_btn1)
                        }
                        "문화" -> {
                            categoryImgView.setImageResource(R.drawable.ic_culture_black)
                            spotinfoLayout.background =
                                resources.getDrawable(R.drawable.myplace_list_btn4)
                        }
                        "술집" -> {
                            categoryImgView.setImageResource(R.drawable.ic_drink_black)
                            spotinfoLayout.background =
                                resources.getDrawable(R.drawable.myplace_list_btn3)
                        }
                        "기타" -> {
                            categoryImgView.setImageResource(R.drawable.ic_etc_black)
                            spotinfoLayout.background =
                                resources.getDrawable(R.drawable.myplace_list_btn5)
                        }
                    }
                }
                else{
                    categoryImgView.setImageResource(R.drawable.ic_etc_black)
                    spotinfoLayout.background =
                        resources.getDrawable(R.drawable.myplace_list_btn5)

                }
                activity!!.findViewById<TextView>(R.id.txt_spotinfo_placename).text = placeName
                activity!!.findViewById<TextView>(R.id.txt_spotinfo_address).text = roadAdress

                if(myPlace.visited) {
                    var rating = myPlace.rating
                    var ratingView1 = activity!!.findViewById<ImageView>(R.id.img_spotinfo_rating1)
                    var ratingView2 = activity!!.findViewById<ImageView>(R.id.img_spotinfo_rating2)
                    var ratingView3 = activity!!.findViewById<ImageView>(R.id.img_spotinfo_rating3)
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

                instaTag = placeName

                if(spotinfoLayout.isVisible){
                    spotinfoLayout.visibility = View.GONE
                    layout_transparency.visibility = View.GONE
                    img_curr_pos.visibility = View.VISIBLE
                    img_main_isvisited.visibility = View.VISIBLE
                    activity!!.findViewById<ImageView>(R.id.map_btn_add).visibility = View.VISIBLE

                }
                else{
                    spotinfoLayout.visibility = View.VISIBLE
                    layout_transparency.visibility = View.VISIBLE
                    img_curr_pos.visibility = View.INVISIBLE
                    img_main_isvisited.visibility = View.INVISIBLE
                    activity!!.findViewById<ImageView>(R.id.map_btn_add).visibility = View.GONE

                    val cameraUpdate2 = CameraUpdate.scrollTo(marker.position)
                    cameraUpdate2.animate(CameraAnimation.Easing,1000)
                    p0.moveCamera(cameraUpdate2)

                    spotinfoLayout.setOnClickListener{
                        val intent = Intent(activity, DetailActivity::class.java)
                        intent.putExtra("myPlace",myPlace as Serializable)
                        startActivity(intent)
                    }

                }
                true
            })
            marker.captionText = placeList[i].place.placeName
            markerList.add(marker)
        }
        //Map 위의 버튼들 세팅
        setButton(p0)

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
        btn_insta = activity!!.findViewById(R.id.btn_insta)
        btn_insta.setOnClickListener {
            var intent_insta = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/explore/tags/"+instaTag+"/"))
            startActivity(intent_insta)
        }
        img_curr_pos.setOnClickListener{
            val cameraUpdate = CameraUpdate.scrollTo(LatLng(curr_latitude, curr_longitude)) //해당 위치로 카메라 시점 이동(위치 넘겨받기)
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
            stateCategory="전체"
            changeCategory(nMap,stateCategory,this.stateImgVisted)
            activity!!.findViewById<TextView>(R.id.category_item1_txt).setTextColor(Color.parseColor("#FFFFFF"))
            activity!!.findViewById<TextView>(R.id.category_item2_txt).setTextColor(Color.parseColor("#393D46"))
            activity!!.findViewById<TextView>(R.id.category_item3_txt).setTextColor(Color.parseColor("#393D46"))
            activity!!.findViewById<TextView>(R.id.category_item4_txt).setTextColor(Color.parseColor("#393D46"))
            activity!!.findViewById<TextView>(R.id.category_item5_txt).setTextColor(Color.parseColor("#393D46"))
            activity!!.findViewById<TextView>(R.id.category_item6_txt).setTextColor(Color.parseColor("#393D46"))
            activity!!.findViewById<ImageView>(R.id.title_category_imgview).setImageResource(R.drawable.img_category_title_all)
        }
        activity!!.findViewById<TextView>(R.id.category_item2_txt).setOnClickListener{
            stateCategory="맛집"
            changeCategory(nMap,stateCategory,this.stateImgVisted)
            activity!!.findViewById<TextView>(R.id.category_item1_txt).setTextColor(Color.parseColor("#393D46"))
            activity!!.findViewById<TextView>(R.id.category_item2_txt).setTextColor(Color.parseColor("#FFFFFF"))
            activity!!.findViewById<TextView>(R.id.category_item3_txt).setTextColor(Color.parseColor("#393D46"))
            activity!!.findViewById<TextView>(R.id.category_item4_txt).setTextColor(Color.parseColor("#393D46"))
            activity!!.findViewById<TextView>(R.id.category_item5_txt).setTextColor(Color.parseColor("#393D46"))
            activity!!.findViewById<TextView>(R.id.category_item6_txt).setTextColor(Color.parseColor("#393D46"))
            activity!!.findViewById<ImageView>(R.id.title_category_imgview).setImageResource(R.drawable.img_category_title_food)
        }
        activity!!.findViewById<TextView>(R.id.category_item3_txt).setOnClickListener{
            stateCategory="카페"
            changeCategory(nMap,stateCategory,this.stateImgVisted)
            activity!!.findViewById<TextView>(R.id.category_item1_txt).setTextColor(Color.parseColor("#393D46"))
            activity!!.findViewById<TextView>(R.id.category_item2_txt).setTextColor(Color.parseColor("#393D46"))
            activity!!.findViewById<TextView>(R.id.category_item3_txt).setTextColor(Color.parseColor("#FFFFFF"))
            activity!!.findViewById<TextView>(R.id.category_item4_txt).setTextColor(Color.parseColor("#393D46"))
            activity!!.findViewById<TextView>(R.id.category_item5_txt).setTextColor(Color.parseColor("#393D46"))
            activity!!.findViewById<TextView>(R.id.category_item6_txt).setTextColor(Color.parseColor("#393D46"))
            activity!!.findViewById<ImageView>(R.id.title_category_imgview).setImageResource(R.drawable.img_category_title_cafe)
        }
        activity!!.findViewById<TextView>(R.id.category_item4_txt).setOnClickListener{
            stateCategory="술집"
            changeCategory(nMap,stateCategory,this.stateImgVisted)
            activity!!.findViewById<TextView>(R.id.category_item1_txt).setTextColor(Color.parseColor("#393D46"))
            activity!!.findViewById<TextView>(R.id.category_item2_txt).setTextColor(Color.parseColor("#393D46"))
            activity!!.findViewById<TextView>(R.id.category_item3_txt).setTextColor(Color.parseColor("#393D46"))
            activity!!.findViewById<TextView>(R.id.category_item4_txt).setTextColor(Color.parseColor("#FFFFFF"))
            activity!!.findViewById<TextView>(R.id.category_item5_txt).setTextColor(Color.parseColor("#393D46"))
            activity!!.findViewById<TextView>(R.id.category_item6_txt).setTextColor(Color.parseColor("#393D46"))
            activity!!.findViewById<ImageView>(R.id.title_category_imgview).setImageResource(R.drawable.img_category_title_drink)
        }
        activity!!.findViewById<TextView>(R.id.category_item5_txt).setOnClickListener{
            stateCategory="문화"
            changeCategory(nMap,stateCategory,this.stateImgVisted)
            activity!!.findViewById<TextView>(R.id.category_item1_txt).setTextColor(Color.parseColor("#393D46"))
            activity!!.findViewById<TextView>(R.id.category_item2_txt).setTextColor(Color.parseColor("#393D46"))
            activity!!.findViewById<TextView>(R.id.category_item3_txt).setTextColor(Color.parseColor("#393D46"))
            activity!!.findViewById<TextView>(R.id.category_item4_txt).setTextColor(Color.parseColor("#393D46"))
            activity!!.findViewById<TextView>(R.id.category_item5_txt).setTextColor(Color.parseColor("#FFFFFF"))
            activity!!.findViewById<TextView>(R.id.category_item6_txt).setTextColor(Color.parseColor("#393D46"))
            activity!!.findViewById<ImageView>(R.id.title_category_imgview).setImageResource(R.drawable.img_category_title_culture)
        }
        activity!!.findViewById<TextView>(R.id.category_item6_txt).setOnClickListener{
            stateCategory="기타"
            changeCategory(nMap,stateCategory,stateImgVisted)
            activity!!.findViewById<TextView>(R.id.category_item1_txt).setTextColor(Color.parseColor("#393D46"))
            activity!!.findViewById<TextView>(R.id.category_item2_txt).setTextColor(Color.parseColor("#393D46"))
            activity!!.findViewById<TextView>(R.id.category_item3_txt).setTextColor(Color.parseColor("#393D46"))
            activity!!.findViewById<TextView>(R.id.category_item4_txt).setTextColor(Color.parseColor("#393D46"))
            activity!!.findViewById<TextView>(R.id.category_item5_txt).setTextColor(Color.parseColor("#393D46"))
            activity!!.findViewById<TextView>(R.id.category_item6_txt).setTextColor(Color.parseColor("#FFFFFF"))
            activity!!.findViewById<ImageView>(R.id.title_category_imgview).setImageResource(R.drawable.img_category_title_etc)
        }
    }
    override fun onMapClick(p0: PointF, p1: LatLng) {

        if(spotinfoLayout.isVisible){
            spotinfoLayout.visibility = View.GONE
            layout_transparency.visibility = View.GONE
            img_curr_pos.visibility = View.VISIBLE
            img_main_isvisited.visibility = View.VISIBLE
            activity!!.findViewById<ImageView>(R.id.map_btn_add).visibility = View.VISIBLE
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView = view.findViewById(R.id.map_view)
        mapView.getMapAsync(this)
        mapView.onCreate(savedInstanceState)
        //FragmentSearch activity 호출
        map_btn_add.setOnClickListener {
            val intent = Intent(activity, RegisterActivity::class.java)
            val isAdd = true
            intent.putExtra("IsAdd",isAdd)
            startActivity(intent)
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