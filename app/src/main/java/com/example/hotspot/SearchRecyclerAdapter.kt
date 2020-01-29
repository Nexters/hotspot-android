package com.example.hotspot

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.search_list.view.*

class SearchRecyclerAdapter (private val list:List<Place>) :
    RecyclerView.Adapter<SearchRecyclerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v: View = LayoutInflater.from(parent.context).inflate(R.layout.search_list, parent,false)

        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val placeName_text = list.get(position).placeName
        val roadAddressName_text = list.get(position).roadAddressName
        holder.placeName_TxtV.text = placeName_text
        holder.roadAddressName_TxtV.text = roadAddressName_text

    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val placeName_TxtV = itemView.placeName_txtView
        val roadAddressName_TxtV = itemView.roadAddressName_txtView
    }
}