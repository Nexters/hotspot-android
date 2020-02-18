package com.example.hotspot.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.Nullable
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.view.animation.RotateAnimation


class ConsSentView : ImageView , View.OnTouchListener{
    var fromX: Float = 0.toFloat()
    var fromY: Float = 0.toFloat()


    private var mCurrAngle = 0.0
    private var mAddAngle = 0.0
    private var finalAngle = 0.0


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
    fun setPos( x : Float,  y : Float){

        this.x = x
        this.y = y
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        bringToFront()
        if(v != null && event != null) {
            val parentWidth = (v.parent as ViewGroup).width    // 부모 View 의 Width
            val parentHeight = (v.parent as ViewGroup).height    // 부모 View 의 Height

            val centerW = width/2.toFloat()
            val centerH = height/2.toFloat()
            val x = event.getX()
            val y = event.getY()



            val action = event.action
            when(action){
                MotionEvent.ACTION_DOWN ->{
                    fromX = event.x
                    fromY = event.y
                    /*
                    mCurrAngle = Math.toDegrees(Math.atan2((centerH - y).toDouble(), (x - centerW).toDouble()))*/

                }
                MotionEvent.ACTION_MOVE ->{

                    v.x = v.x + event.x - v.width/2
                    v.y = v.y + event.y - v.height/2
                    /*
                    val mPrevAngle = mCurrAngle
                    mCurrAngle = Math.toDegrees(Math.atan2((centerH - y).toDouble(), (x - centerW).toDouble()))
                    finalAngle = mAddAngle + mCurrAngle - mPrevAngle
                    animate(this, mAddAngle, finalAngle)
                    mAddAngle += mCurrAngle - */

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
    private fun animate(view : View, fromDegree : Double, toDegree : Double) {
        val animation = RotateAnimation( fromDegree.toFloat(), toDegree.toFloat(),
        RotateAnimation.RELATIVE_TO_SELF, 0.5f,
        RotateAnimation.RELATIVE_TO_SELF, 0.5f)
        animation.setDuration(0)
        animation.setFillAfter(true)
        view.startAnimation(animation)
    }

}