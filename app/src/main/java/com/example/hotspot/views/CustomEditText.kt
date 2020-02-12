package com.example.hotspot.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.widget.EditText
import com.example.hotspot.R


class CustomEditText// we need this constructor for LayoutInflater
    (context: Context, attrs: AttributeSet) : EditText(context, attrs) {
    private val mRect: Rect
    private val mPaint: Paint

    init {

        mRect = Rect()
        mPaint = Paint()
        mPaint.setStyle(Paint.Style.STROKE)
        mPaint.setColor(resources.getColor(R.color.colorMyGrayDark))
        mPaint.strokeWidth = 4f
    }

    override fun onDraw(canvas: Canvas) {
        val count = lineCount
        val r = mRect
        val paint = mPaint

        for (i in 0 until count) {
            val baseline = getLineBounds(i, r)

            canvas.drawLine(r.left.toFloat(), (baseline + 10).toFloat(), r.right.toFloat(), (baseline + 10).toFloat(), paint)
        }

        super.onDraw(canvas)
    }
}