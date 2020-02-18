package com.example.hotspot

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log.d
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.mylist_view.*
import java.io.Serializable
import androidx.recyclerview.widget.RecyclerView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.ItemTouchHelper
import com.squareup.otto.Subscribe
import kotlinx.android.synthetic.main.category_view.*


class FragmentMyPlace : Fragment() {

    var isvisitedState = 0
    private lateinit var newPlace : MyPlace
    private lateinit var placeList:MutableList<MyPlace>
    private lateinit var stateCategory : String

    lateinit var cardStyleList : ArrayList<Drawable>

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


        cardStyleList = arrayListOf()
        cardStyleList.add(resources.getDrawable(R.drawable.ic_myplace_list_img1))
        cardStyleList.add(resources.getDrawable(R.drawable.ic_myplace_list_img2))
        cardStyleList.add(resources.getDrawable(R.drawable.ic_myplace_list_img3))
        cardStyleList.add(resources.getDrawable(R.drawable.ic_myplace_list_img4))
        cardStyleList.add(resources.getDrawable(R.drawable.ic_myplace_list_img5))
        cardStyleList.add(resources.getDrawable(R.drawable.ic_rectangle))

        placeList = arguments!!.getSerializable("PlaceList") as MutableList<MyPlace>
        stateCategory = arguments!!.getSerializable("CateGory") as String

        myplace_recyclerview.setHasFixedSize(true)
        myplace_recyclerview.layoutManager = LinearLayoutManager(context)
        recyclerviewInit(placeList, 0)

        val simpleItemTouchCallback = object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT ) {
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

                        val intent = Intent(activity, DetailActivity::class.java)
                        intent.putExtra("myPlace", myPlace as Serializable )
                        intent.putExtra("Position",position)
                        intent.putExtra("RequestCode",20)
                        startActivityForResult(intent,20)

                    }

                    override fun onLongClick(view: View?, position: Int) {

                    }
                })
        )

        myplace_isvisited.setOnClickListener {
            if(isvisitedState>=2)
                isvisitedState = 0
            else
                isvisitedState++

            if(isvisitedState == 0)
                changeCategory(0, placeList as ArrayList<MyPlace>)
            else if(isvisitedState == 1)
                changeCategory(1, placeList as ArrayList<MyPlace>)
            else if(isvisitedState == 2)
                changeCategory(2, placeList as ArrayList<MyPlace>)
        }

        myplace_add_btn.setOnClickListener {
            val intent = Intent(activity, RegisterActivity::class.java)
            val isAdd = true
            intent.putExtra("IsAdd",isAdd)
            startActivity(intent)
        }
    }

    fun recyclerviewInit(list: MutableList<MyPlace>, state: Int) {
        myplace_recyclerview.adapter = MyPlaceRecyclerAdapter(list, cardStyleList, state)
    }

    fun changeCategory(state : Int, tempList : ArrayList<MyPlace>) {

        var resultList = tempList
        d("TAG", "state : $state")
        d("TAG", "placeList : ${tempList}")

        if(state == 0) {
            activity!!.findViewById<ImageView>(R.id.myplace_isvisited).setImageResource(R.drawable.img_main_all_xxxhdpi)
            recyclerviewInit(tempList, state)
        }
        else if(state == 1) {
            var resultList = ArrayList<MyPlace>()

            activity!!.findViewById<ImageView>(R.id.myplace_isvisited).setImageResource(R.drawable.img_main_ismarked)

            for(i in 0..resultList.size) {
                if(tempList[i].visited) {
                    resultList.add(tempList[i])
                }
            }
//            resultList.removeAll { !it.visited }
            d("TAG", "resultList : ${resultList}")
            recyclerviewInit(resultList, state)
        }
        else if(state == 2) {
            var resultList = ArrayList<MyPlace>()
            activity!!.findViewById<ImageView>(R.id.myplace_isvisited).setImageResource(R.drawable.img_main_will)

            for(i in 0..resultList.size) {
                if(!tempList[i].visited) {
                    resultList.add(tempList[i])
                }
            }

//            resultList.removeAll { it.visited }
            d("TAG", "resultList : ${resultList}")
            recyclerviewInit(resultList, state)
        }



        d("TAG", "placeList : ${tempList}")
        d("TAG", "resultList.size : ${resultList.size}")

        activity!!.findViewById<TextView>(R.id.hpCount).text = resultList.size.toString()
//        hpCount.text = resultList.size.toString()


    }

    @Subscribe
    fun onActivityResultEvent(activityResultEvent: ActivityResultEvent){
        onActivityResult(activityResultEvent.get_RequestCode(),activityResultEvent.get_ResultCode(),activityResultEvent.get_Data())

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


    }

}