package com.example.hotspot

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.detail_image_item.view.*


class ImageRecyclerAdapter(var mContext: Context,
                           var urlList: ArrayList<String>) :
    RecyclerView.Adapter<ImageRecyclerAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v: View = LayoutInflater.from(parent.context).inflate(R.layout.detail_image_item, parent,false)

        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return urlList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val urlBuffer = StringBuffer(urlList[position])

        Log.d("ImageRecyclerAdapter", "urlBuffer[4] : ${urlBuffer[4]}")

        if(urlBuffer[4] != 's'){
            urlBuffer.insert( 4,'s')
        }

        val url = urlBuffer.toString()

        Log.d("ImageRecyclerAdapter", "url : $url")

        val option = RequestOptions()
        option.centerCrop()

        Glide.with(holder.itemView.context)
            .load(url)
            .apply(option)
            .into(holder.detailImg)

    }

    inner class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var detailImg = itemView.detail_img

    }

}