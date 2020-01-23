package com.example.hotspot

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
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
            .replace(R.id.fragment_map, FragmentMap())
            .commit()


        btn_recycler.setOnClickListener {
            //fragment_recyclerview 시작
            val intent = Intent(this, RVActivity::class.java)
            startActivity(intent)
        }

        btn_add.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
        }






    }
}