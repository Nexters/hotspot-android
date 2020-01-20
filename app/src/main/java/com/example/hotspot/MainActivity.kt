package com.example.hotspot

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.map_view.*
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView


class MainActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        val mapView = MapView(this)
//
//
//        val mapViewContainer = map_view as ViewGroup
//        mapView.setMapViewEventListener(this)
//        mapViewContainer.addView(mapView)

        //fragment_kakaomap 시작
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_map, FragmentMap())
            .commit()


        btn_recycler.setOnClickListener {
            //fragment_recyclerview 시작
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_recycler, FragmentRV())
                .commit()
        }






    }
}