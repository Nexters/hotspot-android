package com.example.hotspot

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.FragmentTransaction
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.NaverMapOptions
import com.naver.maps.map.OnMapReadyCallback
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.activity_sticker_regist.*

class StickerRegistActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sticker_regist)

        //네이버 맵 옵션 설정
        var naverMapOptions = NaverMapOptions()
        naverMapOptions.allGesturesEnabled(false)
        naverMapOptions.zoomControlEnabled(false)

        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.mapframe) as MapFragment?
            ?: MapFragment.newInstance(naverMapOptions).also  {
                it.getMapAsync(OnMapReadyCallback {
                    it.mapType = NaverMap.MapType.Navi
                    it.isNightModeEnabled = true
                    val cameraUpdate = CameraUpdate.scrollTo(LatLng(37.527180, 126.932794)) //해당 위치로 카메라 시점 이동(위치 넘겨받기)
                    it.moveCamera(cameraUpdate)
                    var nmo = NaverMapOptions()
                    nmo.scrollGesturesEnabled(false)
                })
                fm.beginTransaction().add(R.id.mapframe, it).commit()
            }



    }
}
