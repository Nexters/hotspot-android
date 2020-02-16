package com.example.hotspot.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.Nullable
import com.example.hotspot.R

class BestMenuInputView: LinearLayout, View.OnTouchListener{


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
        var infService = Context.LAYOUT_INFLATER_SERVICE
        var li : LayoutInflater = context.getSystemService(infService) as LayoutInflater
        var v : View = li.inflate(R.layout.bestmenu_input,this,false)
        //setOnTouchListener(this)
        addView(v)


    }
    fun setX( x : Float,  y : Float){

        this.x = x
        this.y = y
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
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
                }
            }
        }

        return true

    }



}