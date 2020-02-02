package com.example.hotspot

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.mylist_view.*

class FragmentMyPlace : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.mylist_view,container,false)

        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bundle = arguments


        val placeList = bundle!!.getSerializable("PlaceList") as List<MyPlace>


        recycler_view.setHasFixedSize(true)
        recycler_view.layoutManager = LinearLayoutManager(MainActivity())
        recycler_view.adapter = RecyclerAdapter(placeList)

    }

}