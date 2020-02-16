package com.example.hotspot

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log.d
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.mylist_view.*
import java.io.Serializable
import androidx.recyclerview.widget.RecyclerView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.ItemTouchHelper
import kotlinx.android.synthetic.main.category_view.*


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

        var cardStyleList : ArrayList<Drawable>
        cardStyleList = arrayListOf()
        cardStyleList.add(resources.getDrawable(R.drawable.myplace_list_btn1))
        cardStyleList.add(resources.getDrawable(R.drawable.myplace_list_btn2))
        cardStyleList.add(resources.getDrawable(R.drawable.myplace_list_btn3))
        cardStyleList.add(resources.getDrawable(R.drawable.myplace_list_btn4))
        cardStyleList.add(resources.getDrawable(R.drawable.myplace_list_btn5))
        cardStyleList.add(resources.getDrawable(R.drawable.myplace_list_btn6))

        val placeList = arguments!!.getSerializable("PlaceList") as MutableList<MyPlace>
        val stateCategory = arguments!!.getSerializable("CateGory") as String
        myplace_recyclerview.setHasFixedSize(true)
        myplace_recyclerview.layoutManager = LinearLayoutManager(context)
        myplace_recyclerview.adapter = MyPlaceRecyclerAdapter(placeList,cardStyleList,stateCategory)
        val simpleItemTouchCallback = object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                placeList.removeAt(position)
                myplace_recyclerview.adapter!!.notifyItemRemoved(position)
            }
        }


        var itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(myplace_recyclerview)

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