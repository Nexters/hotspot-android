package com.example.hotspot

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_main.*
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView



class MainActivity : AppCompatActivity(),MapView.MapViewEventListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mapView = MapView(this)

        val mapViewContainer = map_view as ViewGroup
        mapView.setMapViewEventListener(this)
        mapViewContainer.addView(mapView)


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
        mapView!!.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading)
        mapView.setMapCenterPointAndZoomLevel(
            MapPoint.mapPointWithGeoCoord(37.537229, 127.005515),
            2,
            true
        );
    }
    override fun onMapViewLongPressed(p0: MapView?, p1: MapPoint?) {
    }

    override fun onMapViewMoveFinished(p0: MapView?, p1: MapPoint?) {
    }

    override fun onMapViewSingleTapped(p0: MapView?, p1: MapPoint?) {
        val intent = Intent(this,LoginActivity::class.java)
        startActivity(intent)
    }

    override fun onMapViewZoomLevelChanged(p0: MapView?, p1: Int) {
    }

}