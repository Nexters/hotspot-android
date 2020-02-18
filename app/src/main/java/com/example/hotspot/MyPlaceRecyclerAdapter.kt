package com.example.hotspot

import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.util.Log.d
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.myplace_item.view.*

class MyPlaceRecyclerAdapter (private val list:List<MyPlace>,private val cardstyleList : ArrayList<Drawable>, private val isVisit : Int) :

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
            val rating = list.get(position).rating
            val visited = list.get(position).visited
            var categoryTemp: String? = list.get(position).place.categoryName
            var categoryName: String? = ""

            d("TAG", "categoryName : ${categoryName}")

            if(isVisit == 2) {
                holder.cardLayout.background = cardstyleList.get(5)
            }
            else {
                if (categoryTemp != null) {
                    categoryName = categoryTemp

                    when (categoryName) {
                        "맛집" -> {
                            holder.cardLayout.background = cardstyleList.get(0)
                        }
                        "카페" -> {
                            holder.cardLayout.background = cardstyleList.get(1)
                        }
                        "술집" -> {
                            holder.cardLayout.background = cardstyleList.get(2)
                        }
                        "문화" -> {
                            holder.cardLayout.background = cardstyleList.get(3)
                        }
                        "기타" -> {
                            holder.cardLayout.background = cardstyleList.get(4)
                        }
                    }
                }
            }



            if(visited) {
                when (rating) {
                    1-> {
                        holder.rating_img1.isVisible = true
                    }
                    2 -> {
                        holder.rating_img1.isVisible = true
                        holder.rating_img2.isVisible = true
                    }
                    3 -> {
                        holder.rating_img1.isVisible = true
                        holder.rating_img2.isVisible = true
                        holder.rating_img3.isVisible = true
                    }
                }
            }
            
            holder.placeName_txtV.text = placeName_text
            holder.roadAddressName_txtV.text =roadAddressName_text
//            holder.rating_rbV.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
//                ratingBar.rating = rating_num.toFloat()
//            }
        }


    inner class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView){
        val placeName_txtV = itemView.placeName_txt
        val roadAddressName_txtV = itemView.roadAddressName_txt
        val cardLayout = itemView.rcyl_card_layout

        val rating_img1 = itemView.myplace_rating_img1
        val rating_img2 = itemView.myplace_rating_img2
        val rating_img3 = itemView.myplace_rating_img3
    }
}
