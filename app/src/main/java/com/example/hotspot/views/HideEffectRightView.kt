package com.example.hotspot.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.annotation.Nullable
import com.example.hotspot.R

class HideEffectRightView  : LinearLayout {


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
        var v : View = li.inflate(R.layout.hide_effect_right_view,this,false)
        addView(v)


    }




}