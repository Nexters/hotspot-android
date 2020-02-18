package com.example.hotspot

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log.d
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.squareup.otto.Subscribe
import kotlinx.android.synthetic.main.detail_view.*
import kotlinx.android.synthetic.main.mylist_view.*
import java.io.Serializable

class FragmentDetailView : Fragment() {

    private var position = 0
    private var isEditSpot = false
    private lateinit var newPlace: MyPlace
    private var dotscount = 0
    private lateinit var instaTag : String
    private var requestCode = 0
    private var resCode = 0
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
        requestCode = arguments!!.getSerializable("RequestCode") as Int
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
                activity!!.setResult(resCode,intent2)
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
            intent.putExtra("RequestCode", requestCode as Serializable)
            startActivityForResult(intent,requestCode)
        }

        detail_insta_img.setOnClickListener {
            var intent_insta = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/explore/tags/"+instaTag+"/"))
            startActivity(intent_insta)
        }

        val mAdapter = ImageAdapter(activity!!)
        viewPager.adapter = mAdapter
        dotscount = mAdapter.count

        when(dotscount) {
            2 -> {
                dotImageView2.visibility = View.VISIBLE
            }
            3 -> {
                dotImageView2.visibility = View.VISIBLE
                dotImageView3.visibility = View.VISIBLE
            }
            4 -> {
                dotImageView2.visibility = View.VISIBLE
                dotImageView3.visibility = View.VISIBLE
                dotImageView4.visibility = View.VISIBLE
            }
            5 -> {
                dotImageView2.visibility = View.VISIBLE
                dotImageView3.visibility = View.VISIBLE
                dotImageView4.visibility = View.VISIBLE
                dotImageView5.visibility = View.VISIBLE
            }
        }

        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {

                when (position) {
                    0 -> {
                        dotImageView1.setImageResource(R.drawable.indicator_dot_on)
                        dotImageView2.setImageResource(R.drawable.indicator_dot_off)
                        dotImageView3.setImageResource(R.drawable.indicator_dot_off)
                        dotImageView4.setImageResource(R.drawable.indicator_dot_off)
                        dotImageView5.setImageResource(R.drawable.indicator_dot_off)

                    }
                    1 -> {
                        dotImageView1.setImageResource(R.drawable.indicator_dot_off)
                        dotImageView2.setImageResource(R.drawable.indicator_dot_on)
                        dotImageView3.setImageResource(R.drawable.indicator_dot_off)
                        dotImageView4.setImageResource(R.drawable.indicator_dot_off)
                        dotImageView5.setImageResource(R.drawable.indicator_dot_off)
                    }
                    2 -> {
                        dotImageView1.setImageResource(R.drawable.indicator_dot_off)
                        dotImageView2.setImageResource(R.drawable.indicator_dot_off)
                        dotImageView3.setImageResource(R.drawable.indicator_dot_on)
                        dotImageView4.setImageResource(R.drawable.indicator_dot_off)
                        dotImageView5.setImageResource(R.drawable.indicator_dot_off)
                    }
                    3 -> {
                        dotImageView1.setImageResource(R.drawable.indicator_dot_off)
                        dotImageView2.setImageResource(R.drawable.indicator_dot_off)
                        dotImageView3.setImageResource(R.drawable.indicator_dot_off)
                        dotImageView4.setImageResource(R.drawable.indicator_dot_on)
                        dotImageView5.setImageResource(R.drawable.indicator_dot_off)
                    }
                    4 -> {
                        dotImageView1.setImageResource(R.drawable.indicator_dot_off)
                        dotImageView2.setImageResource(R.drawable.indicator_dot_off)
                        dotImageView3.setImageResource(R.drawable.indicator_dot_off)
                        dotImageView4.setImageResource(R.drawable.indicator_dot_off)
                        dotImageView5.setImageResource(R.drawable.indicator_dot_on)
                    }
                }
            }
        })
    }

    inner class ImageAdapter : PagerAdapter {
        private var mContext : Context

        private var mImage = mutableListOf(
            R.drawable.best_menu
//            R.drawable.gallery
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

//    inner class ImageAdapter : PagerAdapter {
//        private var mContext : Context
//
//        private var mImage = mutableListOf(
//            R.drawable.img_sticker_list_best,
//            R.drawable.img_sticker_list_gallery
//        )
//
//        constructor(context: Context){
//            mContext = context
//        }
//
//        override fun isViewFromObject(view: View, obj: Any): Boolean {
//            return view == obj
//        }
//
//        override fun getCount(): Int {
//            return mImage.size
//        }
//
//        override fun instantiateItem(container: ViewGroup, position: Int): Any {
//            val imageView = ImageView(mContext)
//            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
//            imageView.setImageResource(mImage[position])
//            container.addView(imageView, 0)
//
//            return imageView
//        }
//
//        override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
//            container.removeView(obj as ImageView)
//        }
//    }
    @Subscribe
    fun onActivityResultEvent(activityResultEvent: ActivityResultEvent){
        onActivityResult(activityResultEvent.get_RequestCode(),activityResultEvent.get_ResultCode(),activityResultEvent.get_Data())

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == 99){
            resCode = 98
            if(data != null){
                newPlace = data.getSerializableExtra("NewSpotInfo") as MyPlace
                //UI 업데이트!!!!!!!!!!!


                isEditSpot = true
            }

        }
        else if(resultCode == 100){
            resCode = 97
            if(data != null){
                newPlace = data.getSerializableExtra("NewSpotInfo") as MyPlace
                //UI 업데이트!!!!!!!!!!!


                isEditSpot = true
            }
        }

    }
}