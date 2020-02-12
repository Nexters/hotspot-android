package com.example.hotspot

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log.d
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.PagerAdapter
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

        if(!myPlace.visited) {

        }

        detail_esc_btn.setOnClickListener {
            fragmentManager!!.beginTransaction()
                .remove(this)
                .commit()
            activity!!.finish()
        }

        //viewPager
        val adapter = ImageAdapter(activity!!)
        viewPager.adapter = adapter

        detail_edit_btn.setOnClickListener {

            val intent = Intent(activity, RegisterActivity::class.java)
            intent.putExtra("isAdd", false)
            intent.putExtra("myPlace", myPlace as Serializable)
            startActivity(intent)

//            val bundle = Bundle()
//            bundle.putSerializable("myPlace", myPlace as Serializable)
//            bundle.putBoolean("isAdd", false)

//            val fr_reg = FragmentRegister()
//            fr_reg.arguments = bundle
//
//            fragmentManager!!.beginTransaction()
//                .addToBackStack(null)
//                .replace(R.id.detail_activity, fr_reg)
//                .commit()
        }
    }

    inner class ImageAdapter : PagerAdapter {
        private var mContext : Context

        private var mImage = mutableListOf(
            R.drawable.best_menu,
            R.drawable.gallery
        )

        constructor(context: Context){
            mContext = context
        }

        override fun isViewFromObject(view: View, obj: Any): Boolean {
            return view == obj
        }

        override fun getCount(): Int {
            return mImage.size
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val imageView = ImageView(mContext)
            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
            imageView.setImageResource(mImage[position])
            container.addView(imageView, 0)

            return imageView
        }

        override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
            container.removeView(obj as ImageView)
        }
    }
}