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

        //fragment_kakaomap 시작
//        supportFragmentManager.beginTransaction()
//            .add(R.id.fragment_map, FragmentMap())
//            .addToBackStack(null)
//            .commit()


        //List는 Intent로 구현
        listBt.setOnClickListener {
            //fragment_recyclerview 시작
            val intent1 = Intent(this, RVActivity::class.java)
            startActivity(intent1)
        }

        //FragmentSearch 프래그먼트 호출
        btn_add.setOnClickListener {
            val intent2 = Intent(this, SearchActivity::class.java)
            startActivity(intent2)
//            supportFragmentManager.beginTransaction()
//                .replace(R.id.fragment_search, SearchActivity())
//                .addToBackStack(null)
//                .commit()
        }




    }

    override fun onResume() {
        super.onResume()
        //fragment_kakaomap 시작
        var transaction : FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_map, FragmentMap())//fragment1로 교체해라
        transaction.commit()//transaction 새로고침
    }
}