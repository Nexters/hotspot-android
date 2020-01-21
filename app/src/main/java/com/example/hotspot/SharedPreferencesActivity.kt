package com.example.hotspot

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences

class SharedPreferencesActivity(context: Context) {

    val prefs : SharedPreferences = context.getSharedPreferences("access_token",
        MODE_PRIVATE
    )

    //값 불러오기
    fun getPreferences(): String {
        return prefs.getString("access_token", "").toString()
    }

    //값 저장하기
    fun savePreferences(value: String) {
        val editor : SharedPreferences.Editor = prefs.edit()
        editor.putString("access_token", value)
        editor.commit()
    }
}