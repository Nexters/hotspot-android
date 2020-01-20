package com.example.hotspot

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_main.view.*

class RecyclerAdapter (private val list:MutableList<VOClass>) :

    RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val v: View = LayoutInflater.from(parent.context).inflate(R.layout.item_main, parent,false)

            return ViewHolder(v)
        }

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val name_text = list.get(position).firstname
            val age_text = list.get(position).age

            holder.name_TxtV.text = name_text
            holder.age_TxtV.text = age_text.toString()
        }


    inner class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView){
        val name_TxtV = itemView.name_txtView
        val age_TxtV = itemView.age_txtView
    }
}
