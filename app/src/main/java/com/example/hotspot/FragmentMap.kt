package com.example.hotspot

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.map_view.*
import net.daum.mf.map.api.MapCircle
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView

class FragmentMap: Fragment(), MapView.MapViewEventListener,MapView.POIItemEventListener{

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.mapfragment,container,false)
        val mapView = MapView(activity)
        var customMarker = MapPOIItem()
        customMarker.itemName = ""
        customMarker.tag = 1
        customMarker.mapPoint = MapPoint.mapPointWithGeoCoord(37.532169, 126.928073)
        customMarker.markerType = MapPOIItem.MarkerType.CustomImage
        customMarker.customImageResourceId = R.drawable.star_marker
        customMarker.isCustomImageAutoscale = true // hdpi, xhdpi 등 안드로이드 플랫폼의 스케일을 사용할 경우 지도 라이브러리의 스케일 기능을 꺼줌.
        customMarker.setCustomImageAnchor(0.5f, 1.0f); // 마커 이미지중 기준이 되는 위치(앵커포인트) 지정 - 마커 이미지 좌측 상단 기준 x(0.0f ~ 1.0f), y(0.0f ~ 1.0f) 값.
        mapView.addPOIItem(customMarker)
        mapView.setPOIItemEventListener(this)
        val mapViewContainer = view.findViewById<RelativeLayout>(R.id.map_view) as ViewGroup
        mapView.setMapViewEventListener(this)
        mapViewContainer.addView(mapView)

        return view
    }

    override fun onMapViewCenterPointMoved(p0: MapView?, mapCenterPoint: MapPoint?) {
        val mapPointGeo = mapCenterPoint!!.getMapPointGeoCoord()
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
    }
}