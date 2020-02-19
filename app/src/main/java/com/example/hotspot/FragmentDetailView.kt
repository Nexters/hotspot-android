package com.example.hotspot

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log.d
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.Constraints
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.squareup.otto.Subscribe
import com.squareup.picasso.Picasso
import com.squareup.picasso.Transformation
import kotlinx.android.synthetic.main.detail_view.*
import kotlinx.android.synthetic.main.register_view.*
import java.io.Serializable


class FragmentDetailView : Fragment() {

    private var position = 0
    private var isEditSpot = false
    private lateinit var newPlace: MyPlace
    private var dotscount = 0
    private lateinit var instaTag : String
    private var requestCode = 0
    private var resCode = 0
    private lateinit var urlList: ArrayList<String>
    private lateinit var imgList: ArrayList<ImageView>

    private var imageSize: Int = 0

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
        val categoryName = myPlace.place.categoryName
        val isVisited = myPlace.visited
        val bestmenu = myPlace.bestMenu
        val hours = myPlace.businessHours
        val parking = myPlace.parkingAvailable
        val allDay = myPlace.allDayAvailable
        val powerPlug = myPlace.powerPlugAvailable

        if(!isVisited){
            detail_top.setBackgroundResource(R.drawable.detail_top_layout6)

            notVisitImg.isVisible = true
            notVisitImg.setBackgroundResource(R.drawable.ic_img_novisit)
        }
        else if(categoryName != null && isVisited) {
            when(categoryName) {
                "맛집"->{
                    activity!!.findViewById<Constraints>(R.id.detail_top).setBackgroundResource(R.drawable.detail_top_layout1)
                }
                "카페"->{
                    activity!!.findViewById<Constraints>(R.id.detail_top).setBackgroundResource(R.drawable.detail_top_layout2)
                }
                "술집" ->{
                    activity!!.findViewById<Constraints>(R.id.detail_top).setBackgroundResource(R.drawable.detail_top_layout3)
                }
                "문화" ->{
                    activity!!.findViewById<Constraints>(R.id.detail_top).setBackgroundResource(R.drawable.detail_top_layout4)
                }
                "기타" ->{
                    activity!!.findViewById<Constraints>(R.id.detail_top).setBackgroundResource(R.drawable.detail_top_layout5)
                }
            }
        }

        if(bestmenu != null) {
            detail_menu_txt1.isVisible = true
            detail_menu_txt2.isVisible = true


            for(i in 0..myPlace.bestMenu!!.size-1){
                if(i == 0){
                    detail_menu_txt2.text = myPlace.bestMenu!![0]
                }
                else {
                    detail_menu_txt3.isVisible = true
                    detail_menu_txt3.text = myPlace.bestMenu!![1]
                }
            }
        }

        if(hours!!.open != null && hours!!.close != null) {
            detail_time_txt1.isVisible = true
            detail_time_txt2.isVisible = true
            val st = StringBuffer()
            var open = myPlace.businessHours!!.open.toString()
            var close = myPlace.businessHours!!.close.toString()
            if(open.length == 1){
                st.append("0")
            }
            st.append(open)
            st.append(":00 - ")

            if(close.length == 1){
                st.append("0")
            }
            st.append(close)
            st.append(":00")
            detail_time_txt2.text = st.toString()
        }

        if(parking != null ||
            allDay != null ||
            powerPlug != null) {

            var st = StringBuffer()
            if (parking != null) {
                detail_additional_txt1.isVisible = true
                detail_additional_txt2.isVisible = true
                st.append("주차가능 / ")
            }
            if (allDay != null) {
                detail_additional_txt1.isVisible = true
                detail_additional_txt2.isVisible = true
                st.append("콘센트 O / ")
            }
            if (powerPlug != null) {
                detail_additional_txt1.isVisible = true
                detail_additional_txt2.isVisible = true
                st.append("24시 / ")
            }
            val len = st.length
            st.delete(len - 2, len - 1)
        }

        urlList = arrayListOf()
        imgList = arrayListOf()
        for(i in 0..myPlace.images!!.size-1){
            if(myPlace.images!!.get(i).url != null) {
                urlList.add(myPlace.images!!.get(i).url)
                imageSize++
                d("TAG", "urlList[i] : ${urlList[i]}")
            }
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
//        val adapter = ImageAdapter(activity!!, urlList)
//        viewPager.adapter = adapter

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

        ////////////////////////////////////////////////////////////////////////////////////
        // 삭제 API 추가!
        detail_delete_btn.setOnClickListener {
            detail_popup_layout.visibility = View.VISIBLE
            regist_quit_ok_txt.setOnClickListener{
                fragmentManager!!.beginTransaction()
                    .remove(this)
                    .commit()
                var intent = Intent()
                activity!!.setResult(10,intent)
                activity!!.finish()
            }
            regist_quit_no_txt.setOnClickListener{
                regist_popup_layout.visibility = View.GONE
            }
        }

        ////////////////////////////////////////////////////////////////////////////////////
        // image가 존재하면 Gone 해제
        if(imageSize != 0) {
            viewPager.isVisible = true

            val mAdapter = ImageAdapter(activity!!, urlList)
            viewPager.adapter = mAdapter
            dotscount = mAdapter.count

            when (dotscount) {
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
        }

        // ViewPager 동작
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

    ////////////////////////////////////////////////////////////////////////
    // ViewPager 미완
    inner class ImageAdapter : PagerAdapter {
        private var mContext : Context
        private var mUrlList : ArrayList<String>

        constructor(context: Context, urllist: ArrayList<String>){
            mContext = context
            mUrlList = urllist
        }

        override fun isViewFromObject(view: View, obj: Any): Boolean {
            return view == obj
        }

        override fun getCount(): Int {
            return mUrlList.size
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {

            val imageView = ImageView(mContext)
//            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
//            imageView.setImageResource(mImage[position])
            Picasso.get()
                .load(mUrlList[position])
                .fit()
                .centerCrop()
                .into(imageView)
            container.addView(imageView)

            return imageView
        }

        override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
            container.removeView(obj as View)
        }
    }

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