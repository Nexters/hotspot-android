package com.example.hotspot

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.myplace_item.view.*

class MyPlaceRecyclerAdapter (private val list:List<MyPlace>) :

    RecyclerView.Adapter<MyPlaceRecyclerAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val v: View = LayoutInflater.from(parent.context).inflate(R.layout.myplace_item, parent,false)

            return ViewHolder(v)
        }

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val placeName_text = list.get(position).place.placeName
            val roadAddressName_text = list.get(position).place.roadAddressName
            val memo_text = list.get(position).memo
//            val rating_num = list.get(position).rating


            holder.placeName_txtV.text = placeName_text
            holder.roadAddressName_txtV.text =roadAddressName_text
            holder.memo_txtV.text = memo_text
//            holder.rating_rbV.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
//                ratingBar.rating = rating_num.toFloat()
//            }
        }


    inner class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView){
        val placeName_txtV = itemView.placeName_txt
        val roadAddressName_txtV = itemView.roadAddressName_txt
        val memo_txtV = itemView.memo_txt
//        val rating_rbV = itemView.rating_rb
    }
}
