package com.example.hotspot

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.myplace_item.view.*
import kotlinx.android.synthetic.main.mysearch_list.view.*
import kotlinx.android.synthetic.main.search_list.view.*
import kotlinx.android.synthetic.main.search_list.view.placeName_txtView
import kotlinx.android.synthetic.main.search_list.view.roadAddressName_txtView

class MySearchRecyclerAdapter(var context: Context,
                            var mData: ArrayList<MyPlace>,
                              var myPlace: ArrayList<MyPlace>) :
    RecyclerView.Adapter<MySearchRecyclerAdapter.myViewHolder>(),
    Filterable {
    internal var mfilter: NewFilter

    override fun getFilter(): Filter {
        return mfilter
    }

    init {
        mfilter = NewFilter(this@MySearchRecyclerAdapter)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.mysearch_list, parent, false)
        return myViewHolder(view)
    }

    override fun onBindViewHolder(holder: myViewHolder, position: Int) {
        val placeName_text = mData.get(position).place.placeName
        val roadAddressName_text = mData.get(position).place.placeName
//            val category_name = mData.get(position).place.categoryName

        holder.placeName_TxtV.text = placeName_text
        holder.roadAddressName_TxtV.text = roadAddressName_text
        var categoryName: String = mData.get(position).place.categoryName
//            var categoryName: String? = ""



        if(categoryName != null) {
            Log.d("TAG", "categoryName : ${categoryName}")

            when (categoryName) {
                "맛집" -> {
                    holder.category_Img.setImageResource(R.drawable.img_icon_food_png)
                }
                "카페" -> {
                    holder.category_Img.setImageResource(R.drawable.img_icon_cafe_png)
                }
                "술집" -> {
                    holder.category_Img.setImageResource(R.drawable.img_icon_drink_png)
                }
                "문화" -> {
                    holder.category_Img.setImageResource(R.drawable.img_icon_culture_png)
                }
                "기타" -> {
                    holder.category_Img.setImageResource(R.drawable.img_icon_etc_png)
                }
                "null" -> {
                    holder.category_Img.setImageResource(R.drawable.img_icon_etc_png)
                }
                null -> {
                    holder.category_Img.setImageResource(R.drawable.img_icon_etc_png)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    inner class myViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val placeName_TxtV = itemView.mysearch_placeName_txtView
        val roadAddressName_TxtV = itemView.mysearch_roadAddressName_txtView
        val category_Img = itemView.mysearch_category_img
    }

    inner class NewFilter(var mAdapter: MySearchRecyclerAdapter) : Filter() {
        override fun performFiltering(charSequence: CharSequence): FilterResults {
            mData!!.clear()
            val results = FilterResults()
            if (charSequence.length == 0) {
                mData.addAll(myPlace)
//                    for(i in myPlace) {
//                        place!!.add(i.place.placeName)
//                    }
            } else {
                val filterPattern = charSequence.toString().toLowerCase().trim { it <= ' ' }
                for (myPlace in myPlace) {
                    if (myPlace.place.placeName.toLowerCase().startsWith(filterPattern)) {
                        Log.d("TAG", "myPlaceName : ${myPlace.place.placeName}")
//                            place!!.add(myPlace.place.placeName)
                        mData.add(myPlace)
                    }
                }
            }
            results.values = mData
            results.count = mData!!.size
            Log.d("TAG", "results.values = ${results.values}, results.count = ${results.count}")

            return results
        }

        override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
            this.mAdapter.notifyDataSetChanged()
        }

    }
}