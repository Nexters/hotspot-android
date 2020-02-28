package com.example.hotspot

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import kotlinx.coroutines.delay
import java.io.Serializable

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        val handler = Handler()

        val str = GlobalApplication.prefs.getPreferences()
        if(str == ""){
            handler.postDelayed({
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            },3000)

        }
        else{
            handler.postDelayed({
                val intent = Intent(this, MainActivity::class.java)
                val isnewUser = false
                intent.putExtra("IsNewUser",isnewUser as Serializable)
                startActivity(intent)
                finish()
            },3000)

        }
    }
}
