package com.example.hotspot

import android.net.Uri
import java.io.Serializable

class StickerData : Serializable {
    var photoUriList : ArrayList<String>? = arrayListOf()
    var cloudinaryIdList :  ArrayList<String>? = arrayListOf()
    var bestMenu : ArrayList<String> = arrayListOf()
    var open = ""
    var close = ""
    var parkingAvailable = false
    var allDayAvailable = false
    var powerPlugAvailable = false
    var cloudinaryUrlList : ArrayList<String>? = arrayListOf()
}