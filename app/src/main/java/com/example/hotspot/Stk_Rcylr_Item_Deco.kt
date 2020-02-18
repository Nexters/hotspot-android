package com.example.hotspot

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class Stk_Rcylr_Item_Deco : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)

        if(parent.getChildAdapterPosition(view) != parent.adapter!!.itemCount -1){
            outRect.right = 24
        }
    }

}