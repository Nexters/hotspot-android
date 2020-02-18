package com.example.hotspot

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.myplace_item.view.*
import kotlinx.android.synthetic.main.rgist_rcyl_item.view.*

class StickerRcylrAdapter  (private val list:List<String>) :

    RecyclerView.Adapter<StickerRcylrAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v: View = LayoutInflater.from(parent.context).inflate(R.layout.rgist_rcyl_item, parent,false)


        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val sticker_info_txt = list.get(position)
        holder.stickerInfo_txtView.text = sticker_info_txt

    }


    inner class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView){
        val stickerInfo_txtView = itemView.item_textview
    }
}