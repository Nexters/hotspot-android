package com.example.hotspot

import android.graphics.Color
import android.util.Log.d
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.categoty_items.view.*


class CategoryRecyclerAdapter (private val categoryList:List<String>,
                               private val myPlaceList:List<MyPlace>) :
    RecyclerView.Adapter<CategoryRecyclerAdapter.ViewHolder>() {

    var selectedItems = mutableMapOf(
        0 to true,
        1 to false,
        2 to false,
        3 to false,
        4 to false,
        5 to false
    )

    //myPlaceList temperary list
    var myPlace = myPlaceList.toMutableList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v: View = LayoutInflater.from(parent.context).inflate(R.layout.categoty_items, parent,false)

        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val categoryName_txt = categoryList.get(position)
        val category_name = categoryList.get(position) // 카테고리 이름, 일치 여부 판단

        d("TAG Category", "categoryName_txt : $categoryName_txt")

        holder.category_txt.text = categoryName_txt

        if(position == 0) {
            holder.category_txt.setTextColor(Color.parseColor("#c3f04e"))
            holder.category_circle.setCardBackgroundColor(Color.parseColor("#c3f04e"))
        }

        if(selectedItems[position] == true) {
            holder.category_txt.setTextColor(Color.parseColor("#c3f04e"))
            holder.category_circle.setCardBackgroundColor(Color.parseColor("#c3f04e"))

            myPlace.removeAll{
                it.place.categoryName != category_name
            }

            d("TAG onBindViewHolder", "myPlace : ${myPlace}")

            val mainActivity = MainActivity()
            mainActivity.getMap(myPlace,false)

        }
        else {
            holder.category_txt.setTextColor(Color.parseColor("#65FFFFFF"))
            holder.category_circle.setCardBackgroundColor(Color.parseColor("#101010"))
        }
    }

    inner class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView){
        val category_txt = itemView.category_item_txt
        val category_circle = itemView.category_circle
        private lateinit var categoryMyList : List<MyPlace>

        init {
            itemView.setOnClickListener {
                val position= adapterPosition

                d("TAG itemView", "position : $position")

                selectedItems.put(position, true)


                for(i in selectedItems.keys) {
                    if (i == position) {
                        notifyItemChanged(i)
                        continue
                    }
                    selectedItems[i] = false
                    notifyItemChanged(i)
                    d("TAG itemView", "mSelectedItems : {${i} : ${selectedItems[i]}}")
                }
            }
        }
    }
}
