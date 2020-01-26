package com.example.hotspot

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_main.view.*
import kotlinx.android.synthetic.main.search_list.view.*

class SearchRecyclerAdapter (private val list:MutableList<Address>) :

    RecyclerView.Adapter<SearchRecyclerAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val v: View = LayoutInflater.from(parent.context).inflate(R.layout.search_list, parent,false)

            return ViewHolder(v)
        }

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val loca_text = list.get(position).loca
            val addr_text = list.get(position).address

            holder.loca_TxtV.text = loca_text
            holder.addr_TxtV.text = addr_text
        }


    inner class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView){
        val loca_TxtV = itemView.loca_txtView
        val addr_TxtV = itemView.addr_txtView
    }
}
