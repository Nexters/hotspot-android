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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible

import androidx.fragment.app.Fragment
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.Overlay
import com.naver.maps.map.overlay.OverlayImage
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.map_view.*


class FragmentMap: Fragment()/*, MapView.MapViewEventListener,MapView.POIItemEventListener*/
    , OnMapReadyCallback
    , NaverMap.OnMapClickListener {
    private lateinit var mapView: MapView
    private lateinit var placeList : List<MyPlace>
    private lateinit var spotinfoLayout : ConstraintLayout
    private lateinit var btn_insta : Button
    private lateinit var instaTag : String
    private lateinit var layout_transparency : ConstraintLayout
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        spotinfoLayout = activity!!.findViewById(R.id.spotinfolayout)
        layout_transparency = activity!!.findViewById(R.id.layout_trans_main)
        val bundle = arguments
        placeList = bundle!!.getSerializable("PlaceList") as List<MyPlace>

        btn_insta = spotinfoLayout.findViewById(R.id.btn_insta)
        btn_insta.setOnClickListener {
            var intent_insta = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/explore/tags/"+instaTag+"/"))
            startActivity(intent_insta)
        }

        val view = inflater.inflate(R.layout.map_view,container,false)


        return view
    }


    override fun onMapReady(p0: NaverMap) {
        p0.mapType = NaverMap.MapType.Navi
        p0.isNightModeEnabled = true
        p0.setOnMapClickListener(this)
        val cameraUpdate = CameraUpdate.scrollTo(LatLng(37.527180, 126.932794)) //해당 위치로 카메라 시점 이동(위치 넘겨받기)
        p0.moveCamera(cameraUpdate)
        for(i in 0 .. placeList.size-1) {
            val marker = Marker()
            marker.position =
                LatLng(placeList[i].place.y.toDouble(), placeList[i].place.x.toDouble())
            marker.map = p0
            marker.setOnClickListener(Overlay.OnClickListener {


                btn_insta.text = placeList[i].place.placeName
                instaTag = btn_insta.text.toString()
                if(spotinfoLayout.isVisible){
                    spotinfoLayout.visibility = View.GONE
                    layout_transparency.visibility = View.GONE
                    activity!!.findViewById<Button>(R.id.map_btn_add).visibility = View.VISIBLE

                }
                else{
                    spotinfoLayout.visibility = View.VISIBLE
                    layout_transparency.visibility = View.VISIBLE
                    activity!!.findViewById<Button>(R.id.map_btn_add).visibility = View.GONE

                    val cameraUpdate2 = CameraUpdate.scrollTo(marker.position)
                    cameraUpdate2.animate(CameraAnimation.Easing,1000)
                    p0.moveCamera(cameraUpdate2)

                }
                true
            })

            var size = BitmapFactory.decodeResource(resources, R.drawable.star_marker)
            size = Bitmap.createScaledBitmap(size, 250, 250, true)
            marker.icon = OverlayImage.fromBitmap(size)
            marker.captionText = placeList[i].place.placeName
        }

    }
    override fun onMapClick(p0: PointF, p1: LatLng) {

        if(spotinfoLayout.isVisible){
            spotinfoLayout.visibility = View.GONE
            layout_transparency.visibility = View.GONE
            activity!!.findViewById<Button>(R.id.map_btn_add).visibility = View.VISIBLE
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