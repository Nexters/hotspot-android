package com.example.hotspot

import android.os.Bundle
import android.text.Editable
import android.util.Log.d
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_editview.*
import kotlinx.android.synthetic.main.mylist_view.*
import java.io.Serializable

class FragmentEditMyPlace : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.activity_editview,container,false)

        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val myPlace = arguments!!.getSerializable("myPlace") as MyPlace



        val placeName = myPlace.place.placeName
        val roadName = myPlace.place.roadAddressName
        val memo = myPlace.memo

        if(myPlace.place.placeName != null &&
            myPlace.place.roadAddressName != null &&
            myPlace.memo != null){

            edit_placeName_etxt.setText(placeName)
            edit_roadAddressName_etxt.setText(roadName)
            edit_memo_etxt.setText(memo)
        }



        //뒤로가기
        edit_esc_btn.setOnClickListener {

            val fr_myPlace = fragmentManager!!.findFragmentById(R.id.fragment_map)

            if(fr_myPlace != null) {
                fragmentManager!!.beginTransaction()
                    .replace(R.id.fragment_map, fr_myPlace)
                    .commit()
            }


            fragmentManager!!.beginTransaction()
                .remove(this)
                .commit()
        }
    }

}