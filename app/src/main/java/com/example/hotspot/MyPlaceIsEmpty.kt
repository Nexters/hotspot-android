package com.example.hotspot

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.myplace_empty.*

class MyPlaceIsEmpty: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.myplace_empty)

        myplace_empty_imgbtn.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            val isAdd = true
            intent.putExtra("IsAdd",isAdd)
            startActivity(intent)
            finish()
        }
    }
}