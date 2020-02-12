package com.example.hotspot

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.mylist_empty_view.*

class FragmentMyPlaceEmpty :Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.mylist_empty_view, container, false)
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        add_Btn.setOnClickListener {
            val intent = Intent(activity, RegisterActivity::class.java)
            intent.putExtra("isAdd", true)
            startActivity(intent)
        }
    }
}