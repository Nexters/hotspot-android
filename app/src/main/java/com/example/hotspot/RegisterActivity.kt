package com.example.hotspot

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        registBt.setOnClickListener{
            var intent = Intent(this,StickerRegistActivity::class.java)
            startActivity(intent)
        }
    }
}
