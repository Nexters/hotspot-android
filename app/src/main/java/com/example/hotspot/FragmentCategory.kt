package com.example.hotspot

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.category_view.*

class FragmentCategory: Fragment() {
    //이미지로 변경 예정
    lateinit var category_text: String


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.category_view, container, false)
        return v
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        category_text = category_txt1.text.toString()

        val fr_reg = FragmentRegister()
        val bundle = Bundle()


        category_view1.setOnClickListener{
            category_text = category_txt1.text.toString()
            category_view1.setBackgroundResource(R.drawable.category_layout2)
            category_view2.setBackgroundResource(R.drawable.category_layout3)
            category_view3.setBackgroundResource(R.drawable.category_layout3)
            category_view4.setBackgroundResource(R.drawable.category_layout3)
        }

        category_view2.setOnClickListener{
            category_text = category_txt2.text.toString()
            category_view1.setBackgroundResource(R.drawable.category_layout3)
            category_view2.setBackgroundResource(R.drawable.category_layout2)
            category_view3.setBackgroundResource(R.drawable.category_layout3)
            category_view4.setBackgroundResource(R.drawable.category_layout3)
        }

        category_view3.setOnClickListener{
            category_text = category_txt3.text.toString()
            category_view1.setBackgroundResource(R.drawable.category_layout3)
            category_view2.setBackgroundResource(R.drawable.category_layout3)
            category_view3.setBackgroundResource(R.drawable.category_layout2)
            category_view4.setBackgroundResource(R.drawable.category_layout3)
        }

        category_view4.setOnClickListener{
            category_text = category_txt4.text.toString()
            category_view1.setBackgroundResource(R.drawable.category_layout3)
            category_view2.setBackgroundResource(R.drawable.category_layout3)
            category_view3.setBackgroundResource(R.drawable.category_layout3)
            category_view4.setBackgroundResource(R.drawable.category_layout2)
        }



        category_add_btn.setOnClickListener {
            bundle.putString("categoryName", category_text)
            bundle.putBoolean("categoty_OK", true)
            fr_reg.arguments = bundle

            fragmentManager!!.popBackStack()

            fragmentManager!!.beginTransaction()
                .replace(R.id.register_activity, fr_reg)
                .commit()
        }
    }
}