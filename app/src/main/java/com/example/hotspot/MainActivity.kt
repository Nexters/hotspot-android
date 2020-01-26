package com.example.hotspot

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.activity_main.*




class MainActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        val mapView = MapView(this)
//        val mapViewContainer = map_view as ViewGroup
//        mapView.setMapViewEventListener(this)
//        mapViewContainer.addView(mapView)

        //fragment_kakaomap 시작
        supportFragmentManager.beginTransaction()
            .add(R.id.fragment_map, FragmentMap())
            .commit()


        //List는 Intent로 구현
        btn_recycler.setOnClickListener {
            //fragment_recyclerview 시작
            val intent = Intent(this, RVActivity::class.java)
            startActivity(intent)
        }

        //FragmentSearch 프래그먼트 호출
        btn_add.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_search, FragmentSearch())
                .commit()
        }



    }
}