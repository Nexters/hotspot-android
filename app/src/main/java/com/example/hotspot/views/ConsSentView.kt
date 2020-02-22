package com.example.hotspot.views

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.Nullable


import android.content.ClipData
import android.content.ClipDescription
import android.view.animation.ScaleAnimation


class ConsSentView : ImageView , View.OnTouchListener{
    var fromX: Float = 0.toFloat()
    var fromY: Float = 0.toFloat()





    constructor(context: Context) : super(context){
        initView()
    }
    constructor(context: Context, @Nullable attr: AttributeSet) : super(context,  attr) {
        initView()
    }
    constructor(context: Context, @Nullable attr: AttributeSet, defstyleAttr : Int) : super(context, attr,defstyleAttr) {
        initView()
    }
    private fun initView(){
        setOnTouchListener(this)
    }



    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        bringToFront()
        if(v != null && event != null) {
            val parentWidth = (v.parent as ViewGroup).width    // 부모 View 의 Width
            val parentHeight = (v.parent as ViewGroup).height    // 부모 View 의 Height




            val action = event.action
            when(action){
                MotionEvent.ACTION_DOWN ->{
                    fromX = event.x
                    fromY = event.y

                }
                MotionEvent.ACTION_MOVE ->{

                    v.x = v.x + event.x - v.width/2
                    v.y = v.y + event.y - v.height/2




                }
                MotionEvent.ACTION_UP -> {

                    if(v.x<0){
                        v.x = 0.toFloat()
                    }
                    else if((v.x + v.width)> parentWidth){
                        v.x = parentWidth.toFloat() - v.width
                    }
                    if(v.y <0){
                        v.y = 0.toFloat()
                    }
                    else if((v.y+v.height)>parentHeight){
                        v.y = parentHeight.toFloat() -v.height
                    }

                    /*
                    performClick()*/
                }
            }
        }

        return true

    }



}