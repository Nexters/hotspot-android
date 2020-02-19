package com.example.hotspot

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.util.Log.d
import android.view.LayoutInflater
import android.view.MotionEvent
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
    private lateinit var placeList:MutableList<MyPlace> // 메인액티비티에서 넘어온(카테고리별로 넘어온) 모든 장소들을 담고있는 리스트
    private lateinit var stateCategory : String
    private lateinit var currentList : ArrayList<MyPlace>   //현재 화면에 보여지고 있는 리스트
    lateinit var cardStyleList : ArrayList<Drawable>
    private var itemTouchHelper : ItemTouchHelper? = null
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

        currentList = arrayListOf()
        for(i in (0..placeList.size-1)){
            currentList.add(placeList.get(i))
        }

        myplace_recyclerview.setHasFixedSize(true)
        myplace_recyclerview.layoutManager = LinearLayoutManager(context)
        recyclerviewInit(currentList, 0)



        myplace_recyclerview.addOnItemTouchListener(
            FragmentSearch.RecyclerTouchListener(
                activity,
                myplace_recyclerview,
                object : FragmentSearch.ClickListener {
                    override fun onClick(view: View?, position: Int) {
                        d("TAG", "No.${position} DetailView : ")

                        val myPlace = currentList.get(position)

                        val intent = Intent(activity, DetailActivity::class.java)
                        intent.putExtra("myPlace", myPlace as Serializable )
                        intent.putExtra("Position",position)
                        intent.putExtra("RequestCode",20)
                        startActivityForResult(intent,20)


                    }

                    override fun onLongClick(view: View?, position: Int) {
                        var vibrator : Vibrator
                        vibrator = activity!!.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                        vibrator.vibrate(VibrationEffect.EFFECT_TICK.times(20.toLong()))


                        val simpleItemTouchCallback = object :
                            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT ) {
                            override fun onMove(
                                recyclerView: RecyclerView,
                                viewHolder: RecyclerView.ViewHolder,
                                target: RecyclerView.ViewHolder
                            ): Boolean {
                                return true
                            }


                            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                                val position = viewHolder.adapterPosition
                                val targetId = currentList.get(position).id

                                //placeLIst에서도 삭제  id값으로 매핑 ?  >> 메인의 mplaceList에서도 삭제 됨!!
                                val maxIndex = placeList.size-1
                                for( i in 0 .. maxIndex){
                                    if(targetId == placeList.get(i).id){
                                        placeList.removeAt(i)
                                        break
                                    }
                                }
                                currentList.removeAt(position)
                                //어뎁터에 알리기
                                //myplace_recyclerview.adapter!!.notifyItemRemoved(position)
                                //서버에 알리기
                                myplace_recyclerview.adapter = MyPlaceRecyclerAdapter(currentList, cardStyleList, isvisitedState)
                                //hpCount reset
                                activity!!.findViewById<TextView>(R.id.hpCount).text = currentList.size.toString()
                                itemTouchHelper!!.attachToRecyclerView(null)

                            }
                        }

                        itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
                        itemTouchHelper!!.attachToRecyclerView(myplace_recyclerview)

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
            startActivityForResult(intent,10)
        }
    }

    fun recyclerviewInit(list: MutableList<MyPlace>, state: Int) {
        myplace_recyclerview.adapter = MyPlaceRecyclerAdapter(list, cardStyleList, state)
    }

    fun changeCategory(state : Int, tempList : ArrayList<MyPlace>) {

        d("TAG", "state : $state")
        d("TAG", "placeList : ${tempList}")

        if(state == 0) {
            currentList.clear()
            for(i in (0..placeList.size-1)){
                currentList.add(placeList.get(i))
            }
            activity!!.findViewById<ImageView>(R.id.myplace_isvisited).setImageResource(R.drawable.img_main_all_xxxhdpi)
            recyclerviewInit(currentList, state)
            activity!!.findViewById<TextView>(R.id.hpCount).text = tempList.size.toString()
        }
        else if(state == 1) {
            currentList.clear()
            activity!!.findViewById<ImageView>(R.id.myplace_isvisited).setImageResource(R.drawable.img_main_ismarked)

            for(i in 0..(tempList.size-1)) {
                if(tempList[i].visited) {
                    currentList.add(tempList[i])
                }
            }
            activity!!.findViewById<TextView>(R.id.hpCount).text = currentList.size.toString()
//            resultList.removeAll { !it.visited }
            d("TAG", "resultList : ${currentList}")
            recyclerviewInit(currentList, state)
        }
        else if(state == 2) {
            currentList.clear()
            activity!!.findViewById<ImageView>(R.id.myplace_isvisited).setImageResource(R.drawable.img_main_will)

            for(i in (0..tempList.size-1)) {
                if(!tempList[i].visited) {
                    currentList.add(tempList[i])
                }
            }
            activity!!.findViewById<TextView>(R.id.hpCount).text = currentList.size.toString()

//            resultList.removeAll { it.visited }
            d("TAG", "resultList : ${currentList}")
            recyclerviewInit(currentList, state)

        }



        d("TAG", "placeList : ${tempList}")
        d("TAG", "resultList.size : ${currentList.size}")


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