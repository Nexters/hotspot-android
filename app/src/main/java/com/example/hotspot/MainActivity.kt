package com.example.hotspot

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import kotlinx.android.synthetic.main.activity_main.*




class MainActivity : AppCompatActivity(){
    private lateinit var btn1 : Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        val mapView = MapView(this)
//        val mapViewContainer = map_view as ViewGroup
//        mapView.setMapViewEventListener(this)
//        mapViewContainer.addView(mapView)


        btn1 = findViewById(R.id.btn_1)



        listBt.setOnClickListener {
            //fragment_recyclerview 시작
            val intent = Intent(this, RVActivity::class.java)
            startActivity(intent)
        }

        btn1.setOnClickListener{
            var intent = Intent(this,SearchActivity::class.java)
            startActivity(intent)
        }




    }

    override fun onResume() {
        super.onResume()
        //fragment_kakaomap 시작
        var transaction : FragmentTransaction = supportFragmentManager.beginTransaction()
        var mapFragment = FragmentMap()
        transaction.replace(R.id.frame,mapFragment)//fragment1로 교체해라
        transaction.commit()//transaction 새로고침

        var menutransaction = supportFragmentManager.beginTransaction()
        var menuFragment = MenuMapFragment()
        menutransaction.replace(R.id.categoryframe,menuFragment)
        menutransaction.commit()
    }
}