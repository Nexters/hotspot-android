package com.example.hotspot

import android.app.Application
import com.kakao.auth.KakaoSDK

class GlobalApplication : Application() {

    companion object{
        lateinit var prefs : SharedPreferencesActivity
        var instance : GlobalApplication? = null
    }


    override fun onCreate() {
        super.onCreate()
        prefs = SharedPreferencesActivity(applicationContext)
        instance = this
        KakaoSDK.init(KakaoSDKAdapter())

    }

    override fun onTerminate() {
        super.onTerminate()
        instance = null
    }

    fun getGlobalApplicationContext(): GlobalApplication {
        checkNotNull(instance) { "this application does not inherit com.kakao.GlobalApplication" }
        return instance!!
    }
}