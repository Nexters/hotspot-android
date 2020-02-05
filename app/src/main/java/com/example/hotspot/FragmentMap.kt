package com.example.hotspot

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.PointF
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.map_view.*


class FragmentMap: Fragment()/*, MapView.MapViewEventListener,MapView.POIItemEventListener*/
    , OnMapReadyCallback
    , NaverMap.OnMapClickListener {
    private lateinit var mapView: MapView
    private lateinit var placeList : List<MyPlace>
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        val bundle = arguments
        placeList = bundle!!.getSerializable("PlaceList") as List<MyPlace>


        val view = inflater.inflate(R.layout.map_view,container,false)
        /*
        val mapView = MapView(activity)
        var customMarker = MapPOIItem()
        customMarker.itemName = placeList[0].place.placeName
        customMarker.tag = 1
        customMarker.mapPoint = MapPoint.mapPointWithGeoCoord(placeList[0].place.y.toDouble(), placeList[0].place.x.toDouble())
        customMarker.markerType = MapPOIItem.MarkerType.CustomImage
        customMarker.customImageResourceId = R.drawable.star_marker
        customMarker.isCustomImageAutoscale = true // hdpi, xhdpi 등 안드로이드 플랫폼의 스케일을 사용할 경우 지도 라이브러리의 스케일 기능을 꺼줌.
        customMarker.setCustomImageAnchor(0.5f, 1.0f); // 마커 이미지중 기준이 되는 위치(앵커포인트) 지정 - 마커 이미지 좌측 상단 기준 x(0.0f ~ 1.0f), y(0.0f ~ 1.0f) 값.
        mapView.addPOIItem(customMarker)
        mapView.setPOIItemEventListener(this)
        val mapViewContainer = view.findViewById<RelativeLayout>(R.id.map_view) as ViewGroup
        mapView.setMapViewEventListener(this)
        mapViewContainer.addView(mapView)
        */

        return view
    }


    override fun onMapReady(p0: NaverMap) {
        p0.mapType = NaverMap.MapType.Navi
        p0.isNightModeEnabled = true
        p0.setOnMapClickListener(this)
        val marker = Marker()
        marker.position = LatLng(placeList[0].place.y.toDouble(), placeList[0].place.x.toDouble())
        marker.map = p0
        var size = BitmapFactory.decodeResource(resources, R.drawable.star_marker)
        size = Bitmap.createScaledBitmap(size, 250, 250, true)

        marker.icon = OverlayImage.fromBitmap(size)
        marker.captionText = placeList[0].place.placeName

    }
    override fun onMapClick(p0: PointF, p1: LatLng) {

//        var spotinfoLayout = activity!!.findViewById<ConstraintLayout>(R.id.spotinfolayout)
//        if(spotinfoLayout.isVisible){
//            spotinfoLayout.visibility = View.GONE
//        }
//        else{
//            spotinfoLayout.visibility = View.VISIBLE
//        }
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
    /*
    override fun onMapViewCenterPointMoved(p0: MapView?, mapCenterPoint: MapPoint?) {
        //val mapPointGeo = mapCenterPoint!!.getMapPointGeoCoord()
    }

    override fun onMapViewDoubleTapped(p0: MapView?, p1: MapPoint?) {
    }

    override fun onMapViewDragEnded(p0: MapView?, p1: MapPoint?) {
    }

    override fun onMapViewDragStarted(p0: MapView?, p1: MapPoint?) {
    }

    override fun onMapViewInitialized(mapView: MapView?) {
//        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading)
//        mapView.setMapCenterPointAndZoomLevel(
//            MapPoint.mapPointWithGeoCoord(37.537229, 127.005515),
//            2,
//            true
//        );
        var constraintLayout = activity!!.findViewById<ConstraintLayout>(R.id.constraintLayout)
        constraintLayout.visibility = View.VISIBLE
    }
    override fun onMapViewLongPressed(p0: MapView?, p1: MapPoint?) {
    }

    override fun onMapViewMoveFinished(p0: MapView?, p1: MapPoint?) {
    }

    override fun onMapViewSingleTapped(p0: MapView?, p1: MapPoint?) {
        var spotinfoLayout = activity!!.findViewById<ConstraintLayout>(R.id.spotinfolayout)
        if(spotinfoLayout.isVisible){
            spotinfoLayout.visibility = View.GONE
        }
        else{
            spotinfoLayout.visibility = View.VISIBLE
        }
    }

    override fun onMapViewZoomLevelChanged(p0: MapView?, p1: Int) {
    }
    override fun onCalloutBalloonOfPOIItemTouched(
        p0: MapView?,
        p1: MapPOIItem?,
        p2: MapPOIItem.CalloutBalloonButtonType?
    ) {
    }

    override fun onDraggablePOIItemMoved(p0: MapView?, p1: MapPOIItem?, p2: MapPoint?) {
    }

    override fun onPOIItemSelected(p0: MapView?, p1: MapPOIItem?) {
        var spotinfoLayout = activity!!.findViewById<ConstraintLayout>(R.id.spotinfolayout)
        if(spotinfoLayout.isVisible){
            spotinfoLayout.visibility = View.GONE
        }
        else{
            spotinfoLayout.visibility = View.VISIBLE
        }

    }

    override fun onCalloutBalloonOfPOIItemTouched(p0: MapView?, p1: MapPOIItem?) {
    }*/
}