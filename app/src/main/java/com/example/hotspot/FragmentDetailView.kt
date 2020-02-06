package com.example.hotspot

import android.os.Bundle
import android.util.Log.d
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.detail_view.*
import java.io.Serializable

class FragmentDetailView : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.detail_view, container, false)
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val myPlace = arguments!!.getSerializable("myPlace") as MyPlace

        d("TAG", "FragmentDetailView : ${myPlace}")

        detail_placeName_txt.text = myPlace.place.placeName
        detail_roadAddressName_txt.text = myPlace.place.roadAddressName
        detail_memo_txt.text = myPlace.memo

        detail_esc_btn.setOnClickListener {
            fragmentManager!!.beginTransaction()
                .remove(this)
                .commit()
            activity!!.finish()
        }

        detail_edit_btn.setOnClickListener {

            val bundle = Bundle()
            bundle.putSerializable("myPlace", myPlace as Serializable)
            bundle.putBoolean("isAdd", false)

            val fr_reg = FragmentRegister()
            fr_reg.arguments = bundle

            fragmentManager!!.beginTransaction()
                .addToBackStack(null)
                .replace(R.id.detail_activity, fr_reg)
                .commit()
        }
    }
}