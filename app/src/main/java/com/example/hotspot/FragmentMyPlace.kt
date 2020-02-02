package com.example.hotspot

import android.os.Bundle
import android.util.Log.d
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.mylist_view.*
import java.io.Serializable

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


        myplace_recyclerview.setHasFixedSize(true)
        myplace_recyclerview.layoutManager = LinearLayoutManager(MainActivity())
        myplace_recyclerview.adapter = MyPlaceRecyclerAdapter(placeList)



        myplace_recyclerview.addOnItemTouchListener(
            SearchActivity.RecyclerTouchListener(
                activity,
                myplace_recyclerview,
                object : SearchActivity.ClickListener {
                    override fun onClick(view: View?, position: Int) {
                        d("TAG", "No.${position} DetailView : ")

                        val bundle = Bundle()
                        val myPlace = placeList.get(position)

                        bundle.putSerializable("myPlace", myPlace as Serializable )

                        val fr_detaliview = FragmentDetailView()
                        fr_detaliview.arguments = bundle

                        fragmentManager!!.beginTransaction()
                            .addToBackStack(null)
                            .replace(R.id.main, fr_detaliview)
                            .commit()
                    }

                    override fun onLongClick(view: View?, position: Int) {

                    }
                })
        )
    }

}