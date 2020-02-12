package com.example.hotspot

import android.content.Intent

class ActivityResultEvent(private var requestCode : Int,private var resultCode : Int,private var data : Intent?) {
    fun get_RequestCode(): Int{
        return requestCode
    }
    fun set_RequestCode(requestCode :Int){
        this.requestCode = requestCode
    }
    fun get_ResultCode(): Int{
        return resultCode
    }
    fun set_ResultCode(resultCode: Int){
        this.resultCode = resultCode
    }
    fun get_Data(): Intent?{
        return data
    }
    fun set_Data(data: Intent){
        this.data = data
    }

}