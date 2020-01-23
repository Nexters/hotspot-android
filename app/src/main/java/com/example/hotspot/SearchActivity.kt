package com.example.hotspot

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_search.*

class SearchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        btn_esc2.setOnClickListener {
            finish()
        }
        btn_registtestBt.setOnClickListener{
            var intent = Intent(this,RegisterActivity::class.java)
            startActivity(intent)
        }


    }
}