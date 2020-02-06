package com.example.hotspot

import android.content.Intent
import android.os.Bundle
import android.util.Log.d
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
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

        val placeList = arguments!!.getSerializable("PlaceList") as List<MyPlace>

        myplace_recyclerview.setHasFixedSize(true)
        myplace_recyclerview.layoutManager = LinearLayoutManager(context)
        myplace_recyclerview.adapter = MyPlaceRecyclerAdapter(placeList)

        myplace_recyclerview.addOnItemTouchListener(
            FragmentSearch.RecyclerTouchListener(
                activity,
                myplace_recyclerview,
                object : FragmentSearch.ClickListener {
                    override fun onClick(view: View?, position: Int) {
                        d("TAG", "No.${position} DetailView : ")

                        val myPlace = placeList.get(position)

//                        val bundle = Bundle()
//                        bundle.putSerializable("myPlace", myPlace as Serializable )
//
//                        val fr_detaliview = FragmentDetailView()
//                        fr_detaliview.arguments = bundle
//
//                        val fr_myPlace = fragmentManager!!.findFragmentById(R.id.fragment_map)

//                        if(fr_myPlace != null) {
//                            fragmentManager!!.beginTransaction()
//                                .remove(fr_myPlace)
//                                .commit()
//                        }

                        val intent = Intent(activity, DetailActivity::class.java)
                        intent.putExtra("myPlace", myPlace as Serializable )
                        startActivity(intent)

//                        fragmentManager!!.beginTransaction()
//                            .addToBackStack(null)
//                            .replace(R.id.main, fr_detaliview)
//                            .commit()
                    }

                    override fun onLongClick(view: View?, position: Int) {

                    }
                })
        )
    }

}