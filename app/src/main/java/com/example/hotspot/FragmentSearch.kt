package com.example.hotspot

import android.os.Bundle
import android.util.Log.d
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_search.*

class FragmentSearch : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Search Fragment 리사이클러뷰 테스트 리스트
        var list = mutableListOf(
            Address("HELLO", "Address"),
            Address("WORLD", "Address")
        )

        search_recyclerview.setHasFixedSize(true)
        search_recyclerview.layoutManager = LinearLayoutManager(MainActivity())
        search_recyclerview.adapter = SearchRecyclerAdapter(list)


        btn_esc2.setOnClickListener {
            activity!!
                .supportFragmentManager
                .beginTransaction()
                .remove(this)
                .commit()
        }
    }

}

