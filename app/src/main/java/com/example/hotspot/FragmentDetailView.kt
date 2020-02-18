package com.example.hotspot

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log.d
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.PagerAdapter
import com.squareup.otto.Subscribe
import kotlinx.android.synthetic.main.detail_view.*
import kotlinx.android.synthetic.main.mylist_view.*
import java.io.Serializable

class FragmentDetailView : Fragment() {

    private var position = 0
    private var isEditSpot = false
    private lateinit var newPlace: MyPlace
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
        position = arguments!!.getSerializable("Position") as Int
        d("TAG", "FragmentDetailView : ${myPlace}")

        detail_placeName_txt.text = myPlace.place.placeName
        detail_roadAddressName_txt.text = myPlace.place.roadAddressName
        detail_memo_txt.text = myPlace.memo

        if(!myPlace.visited) {

        }

        detail_esc_btn.setOnClickListener {
            if(isEditSpot){//장소가 업데이트 되었다 .
                fragmentManager!!.beginTransaction()
                    .remove(this)
                    .commit()
                var intent2 = Intent()
                intent2.putExtra("NewSpotInfo",newPlace)
                intent2.putExtra("Position",position)
                activity!!.setResult(98,intent2)
                activity!!.finish()
            }
            else {
                fragmentManager!!.beginTransaction()
                    .remove(this)
                    .commit()
                activity!!.finish()
            }
        }

        //viewPager
        val adapter = ImageAdapter(activity!!)
        viewPager.adapter = adapter

        detail_edit_btn.setOnClickListener {

            val intent = Intent(activity, RegisterActivity::class.java)
            intent.putExtra("isAdd", false)
            intent.putExtra("myPlace", myPlace as Serializable)
            startActivityForResult(intent,5)

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
            R.drawable.img_sticker_list_best,
            R.drawable.img_sticker_list_gallery
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
    @Subscribe
    fun onActivityResultEvent(activityResultEvent: ActivityResultEvent){
        onActivityResult(activityResultEvent.get_RequestCode(),activityResultEvent.get_ResultCode(),activityResultEvent.get_Data())

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == 99){

            if(data != null){
                newPlace = data.getSerializableExtra("NewSpotInfo") as MyPlace
                //UI 업데이트!!!!!!!!!!!


                isEditSpot = true
            }



        }

    }
}