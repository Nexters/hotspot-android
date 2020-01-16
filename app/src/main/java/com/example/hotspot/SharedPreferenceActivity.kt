package com.example.hotspot

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity

class SharedPreferenceActivity : AppCompatActivity() {

    //값 불러오기
    private fun getPreferences() {
        val pref : SharedPreferences = getSharedPreferences("token", MODE_PRIVATE)
        pref.getString("token", "")
    }

    //값 저장하기
    private fun savePreferences() {
        val pref : SharedPreferences = getSharedPreferences("token", MODE_PRIVATE)
        val editor : SharedPreferences.Editor = pref.edit()
        editor.putString("token", "token message")
        editor.commit()
    }

    //값(Key Data) 삭제하기
    private fun removePreferences() {
        val pref : SharedPreferences = getSharedPreferences("token", MODE_PRIVATE)
        val editor : SharedPreferences.Editor = pref.edit()
        editor.remove("token")
        editor.commit()
    }

    //모든 값 삭제하기
    private fun removeAllPreferences() {
        val pref : SharedPreferences = getSharedPreferences("token", MODE_PRIVATE)
        val editor : SharedPreferences.Editor = pref.edit()
        editor.clear()
        editor.commit()
    }
}
