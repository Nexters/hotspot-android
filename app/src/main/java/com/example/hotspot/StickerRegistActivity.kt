package com.example.hotspot

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentTransaction

class StickerRegistActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sticker_regist)

        var transaction : FragmentTransaction = supportFragmentManager.beginTransaction()
        var mapFragment = RegistMapFragment()
        transaction.replace(R.id.mapframe,mapFragment)//fragment1로 교체해라
        transaction.commit()//transaction 새로고침

    }
}
