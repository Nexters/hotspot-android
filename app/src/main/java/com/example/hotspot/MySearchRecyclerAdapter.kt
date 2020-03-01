package com.example.hotspot

import android.content.Context
import android.content.Intent
import android.util.Log
import android.util.Log.d
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.mysearch_list.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.Serializable

class MySearchRecyclerAdapter(var mActivity: MySearchActivity,
                            var mDataList: ArrayList<MyPlace>,
                              var myPlaceList: ArrayList<MyPlace>) :
    RecyclerView.Adapter<MySearchRecyclerAdapter.myViewHolder>(),
    Filterable {
    internal var mfilter: NewFilter
    private var searchActivity: MySearchActivity

    override fun getFilter(): Filter {
        return mfilter
    }

    init {
        mfilter = NewFilter(this@MySearchRecyclerAdapter)
        searchActivity = mActivity
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder {
        val view =
            LayoutInflater.from(mActivity).inflate(R.layout.mysearch_list, parent, false)
        return myViewHolder(view)
    }

    override fun onBindViewHolder(holder: myViewHolder, position: Int) {
        val placeName_text = mDataList.get(position).place.placeName
        val roadAddressName_text = mDataList.get(position).place.placeName
//            val category_name = mData.get(position).place.categoryName

        holder.placeName_TxtV.text = placeName_text
        holder.roadAddressName_TxtV.text = roadAddressName_text
        var categoryName: String = mDataList.get(position).place.categoryName
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
        return mDataList.size
    }

    inner class myViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val placeName_TxtV = itemView.mysearch_placeName_txtView
        val roadAddressName_TxtV = itemView.mysearch_roadAddressName_txtView
        val category_Img = itemView.mysearch_category_img

        init {
            itemView.setOnClickListener {
                Log.d("TAG", "No.${position} DetailView : ")

                val myPlace1 = myPlaceList.get(position)

                val intent = Intent(mActivity, DetailActivity::class.java)

                val resCode = 22
                intent.putExtra("mySearch", true)
                intent.putExtra("myPlace", myPlace1 as Serializable)
                intent.putExtra("Position", position)
                intent.putExtra("RequestCode", resCode)
                mActivity.startActivityForResult(intent, resCode)
            }
        }
    }

    inner class NewFilter(var mAdapter: MySearchRecyclerAdapter) : Filter() {
        override fun performFiltering(charSequence: CharSequence): FilterResults {

            mDataList!!.clear()
            val results = FilterResults()

            if (charSequence.length == 0) {
                mDataList.addAll(myPlaceList)
            } else {
                val filterPattern = charSequence.toString().toLowerCase().trim { it <= ' ' }
                for (myPlace in myPlaceList) {
                    if (myPlace.place.placeName.toLowerCase().startsWith(filterPattern)) {
                        d("TAG", "myPlaceName : ${myPlace.place.placeName}")
                        mDataList.add(myPlace)
                    }
                }
            }
            results.values = mDataList
            results.count = mDataList!!.size
            d("TAG", "results.values = ${results.values}")
            d("TAG", "results.count = ${results.count}")




            return results
        }

        override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
            this.mAdapter.notifyDataSetChanged()
        }

    }
}