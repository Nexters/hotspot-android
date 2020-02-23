package com.example.hotspot

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log.d
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.myplace_item.view.*
import kotlinx.android.synthetic.main.search_list.view.*
import java.io.Serializable
import kotlin.collections.ArrayList


class MySearchActivity : AppCompatActivity() {

    private lateinit var myPlace: ArrayList<MyPlace>
    private var myPlaceSize = 0
    lateinit var place : ArrayList<MyPlace>
    private var recyclerAdapter: MySearchRecyclerAdapter? = null
    lateinit var category_List : ArrayList<Drawable>
    private var category = mutableListOf(
        "맛집",
        "카페",
        "술집",
        "문화",
        "기타"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

//        var category_img_List : ArrayList<Drawable>
        category_List = arrayListOf()
        category_List.add(resources.getDrawable(R.drawable.ic_myplace_list_img1))
        category_List.add(resources.getDrawable(R.drawable.ic_myplace_list_img2))
        category_List.add(resources.getDrawable(R.drawable.ic_myplace_list_img3))
        category_List.add(resources.getDrawable(R.drawable.ic_myplace_list_img4))
        category_List.add(resources.getDrawable(R.drawable.ic_myplace_list_img5))

        myPlace = intent.getSerializableExtra("myPlace") as ArrayList<MyPlace> // myPlace data
        myPlaceSize = myPlace.size // myPlace SIZE

        place = ArrayList()
        place.addAll(myPlace)

        //recyclerview init
        recyclerViewInit()

        //back btn
        search_esc_imgbtn.setOnClickListener {
            finish()
        }

        //editText event
        search_edtTxt!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence,
                i: Int,
                i1: Int,
                i2: Int
            ) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if(recyclerAdapter!!.itemCount != 0) {
                    mysearch_empty_layout.visibility = View.VISIBLE
                    search_recyclerview.visibility = View.GONE
                }
                else {
                    mysearch_empty_layout.visibility = View.GONE
                    search_recyclerview.visibility = View.VISIBLE
                }
                recyclerAdapter!!.getFilter().filter(charSequence)
            }
            override fun afterTextChanged(editable: Editable) {}
        })

        //delete Btn
        search_delete_imgbtn.setOnClickListener {
            search_edtTxt.setText("")
        }

        search_recyclerview.addOnItemTouchListener(
            FragmentSearch.RecyclerTouchListener(
                this,
                search_recyclerview,
                object : FragmentSearch.ClickListener {
                    override fun onClick(view: View?, position: Int) {
                        d("TAG", "No.${position} DetailView : ")

                        val myPlace1 = myPlace.get(position)

                        val intent = Intent(applicationContext, DetailActivity::class.java)
                        intent.putExtra("myPlace", myPlace1 as Serializable)
                        startActivity(intent)
                    }

                    override fun onLongClick(view: View?, position: Int) {

                    }
                })
        )
        myplace_empty_imgbtn.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            val isAdd = true
            intent.putExtra("IsAdd",isAdd)
            startActivity(intent)
        }
    }

    fun recyclerViewInit () {
        search_recyclerview.setHasFixedSize(true)
        search_recyclerview.layoutManager =
            LinearLayoutManager(this)
        recyclerAdapter = MySearchRecyclerAdapter(this, place, myPlace)
        search_recyclerview.adapter = recyclerAdapter
    }


}