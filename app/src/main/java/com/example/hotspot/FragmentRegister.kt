package com.example.hotspot

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.util.Log.d
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.register_view.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class FragmentRegister : Fragment() {
    private lateinit var mRetrofit: Retrofit
    lateinit var apiService : APIService
    lateinit var place: Place
    private lateinit var spotList : SpotListVO

    private var accessToken = GlobalApplication.prefs.getPreferences()
    var rating = 0
    var choicedCategory = ""
    var isEdtChecked = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.register_view, container, false)
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        setRetrofitInit()
        setApiServiceInit()


        setLayout()


        btn_esc3.setOnClickListener {
            fragmentManager!!.beginTransaction()
                .remove(this)
                .commit()
            activity!!.finish()
        }
        edtTxt_memo.setOnFocusChangeListener { v, hasFocus ->
            img_uncheck2.setImageResource(R.drawable.ic_img_check)
            isEdtChecked = true
        }
        edtTxt_memo.doOnTextChanged { text, start, count, after ->
            if(count > 50){
                //50자 넘어갔다고 알림창 ?
            }
        }

        reg_category_txt.setOnClickListener {
            /*
            fragmentManager!!.beginTransaction()
                .addToBackStack(null)
                .replace(R.id.register_activity, FragmentCategory())
                .commit()*/
            var stateStickerBt : Boolean // stickerBt이 활성화된 상태라면 true
            var statevisited_layout : Boolean
            txt_place_name.isClickable = false
            txt_address.visibility = View.INVISIBLE

            edtTxt_memo.isClickable = false
            edtTxt_memo.isFocusable = false
            edtTxt_memo.visibility = View.INVISIBLE
            if(layout_visited.visibility == View.VISIBLE) {
                statevisited_layout = true
                layout_visited.visibility = View.INVISIBLE
            }
            else statevisited_layout = false
            if(stickerBt.visibility == View.VISIBLE){
                stateStickerBt = true
                stickerBt.visibility = View.INVISIBLE
            }
            else{
                stateStickerBt = false
            }
            txt_visited.visibility = View.INVISIBLE
            btn_regist.visibility = View.INVISIBLE
            regist_category_layout.visibility = View.VISIBLE

            setCategoryClick()

            btn_category_add.setOnClickListener{
                edtTxt_memo.visibility = View.VISIBLE
                edtTxt_memo.isFocusableInTouchMode = true
                edtTxt_memo.isFocusable = true
                txt_address.visibility = View.VISIBLE
                txt_visited.visibility = View.VISIBLE
                txt_place_name.isClickable = true
                layout_visited.visibility = View.VISIBLE
                btn_regist.visibility = View.VISIBLE
                if(stateStickerBt){
                    stickerBt.visibility = View.VISIBLE
                }
                else stickerBt.visibility = View.INVISIBLE
                if(statevisited_layout) layout_visited.visibility = View.VISIBLE
                else layout_visited.visibility = View.GONE
                regist_category_layout.visibility = View.GONE
                if(choicedCategory != ""){
                    when(choicedCategory){
                        "카페" -> {
                            reg_category_txt.setImageResource(R.drawable.img_cafe)
                        }
                        "맛집" -> {
                            reg_category_txt.setImageResource(R.drawable.ic_img_category_food)
                        }
                        "문화" -> {
                            reg_category_txt.setImageResource(R.drawable.ic_img_category_culture)
                        }
                        "술집" -> {
                            reg_category_txt.setImageResource(R.drawable.ic_img_category_drink)
                        }
                        "기타" -> {
                            reg_category_txt.setImageResource(R.drawable.ic_img_category_etc)
                        }

                    }
                }

            }
        }

        txt_place_name.setOnClickListener {
            fragmentManager!!.beginTransaction()
                .addToBackStack(null)
                .replace(R.id.register_activity, FragmentSearch())
                .commit()
        }

        txt_visited.setOnClickListener{
            if(isEdtChecked) {
                if (layout_visited.visibility == View.GONE) {
                    layout_visited.visibility = View.VISIBLE
                    txt_visited.setTextColor(Color.WHITE)

                    stickerBt.visibility = View.VISIBLE
                    img_uncheck4.visibility = View.VISIBLE

                    img_uncheck3.setImageResource(R.drawable.ic_img_check)

                    setRatingBar()
                } else {
                    layout_visited.visibility = View.GONE
                    txt_visited.setTextColor(resources.getColor(R.color.colorMyGrayDark))
                    stickerBt.visibility = View.INVISIBLE
                    img_uncheck4.visibility = View.INVISIBLE

                }
            }
        }

        stickerBt.setOnClickListener{
            img_uncheck4.setImageResource(R.drawable.ic_img_check)
            stickerBt.setTextColor(resources.getColor(R.color.colorWhite))
            var intent = Intent(activity, StickerRegistActivity::class.java)
            startActivity(intent)

        }

        btn_regist.setOnClickListener{
            if(txt_place_name.text == "" || txt_place_name.text == "장소 추가"){
                Toast.makeText(activity,
                    "장소 이름을 작성해주세요.",
                    Toast.LENGTH_SHORT)
                    .show()
            }
            else registSpot()
        }
    }

    private fun setLayout(){
        //isAdd : true 등록, false 수정
        val isAdd = arguments!!.getBoolean("isAdd", true)
        val search_OK = arguments!!.getBoolean("search_OK", false)
        val category_OK = arguments!!.getBoolean("categoty_OK", false)

        d("TAG", "setLayout() isAdd : ${isAdd}, search_OK : $search_OK, category_OK : $category_OK")

        if(isAdd == false) {//수정 이기때문에 장소에 대한 정보 뿌리기
            val myplace = arguments!!.getSerializable("myPlace") as MyPlace

            txt_place_name.text = myplace.place.placeName
            txt_address.text = myplace.place.roadAddressName
            reg_category_txt.visibility = View.VISIBLE
            //reg_category_txt 에 svg 이미지 띄우기  myplace에서 category가 카페면 카페이미지  이런식으로
        }
        if(search_OK) {
            edtTxt_memo.isFocusableInTouchMode = true
            edtTxt_memo.isFocusable = true

            txt_place_name.setTextColor(resources.getColor(R.color.colorWhite))
            place = arguments!!.getSerializable("searchPlace") as Place

            when(place.categoryName){
                "카페" -> {
                    reg_category_txt.setImageResource(R.drawable.img_cafe)
                }
                "맛집" -> {
                    reg_category_txt.setImageResource(R.drawable.ic_img_category_food)
                }
                "문화" -> {
                    reg_category_txt.setImageResource(R.drawable.ic_img_category_culture)
                }
                "술집" -> {
                    reg_category_txt.setImageResource(R.drawable.ic_img_category_drink)
                }
                "기타" -> {
                    reg_category_txt.setImageResource(R.drawable.ic_img_category_etc)
                }

            }
            img_uncheck.setImageResource(R.drawable.ic_img_check)

            txt_place_name.text = place.placeName
            txt_address.text = place.roadAddressName

            txt_address.visibility = View.VISIBLE
            reg_category_txt.visibility = View.VISIBLE
        }
        if(category_OK) {
            val category_txt = arguments!!.getString("categoryName")
            reg_category_txt.visibility = View.VISIBLE
            //reg_category_txt 에 svg 이미지 띄우기  myplace에서 category가 카페면 카페이미지  이런식으로
        }
        else {

        }
    }

    private fun registSpot(){
        var isVisited = true
        if(txt_visited.currentTextColor == Color.WHITE){
            isVisited = true
        }
        else isVisited = false
        val registPlace = Place(place.kakaoId,place.kakaoUrl,place.placeName,place.addressName,place.roadAddressName,place.x,place.y,this.choicedCategory)
        spotList = SpotListVO(registPlace,isVisited,edtTxt_memo.text.toString(),rating)

        apiService.postPlace("Bearer " + "${accessToken}",
            spotList).enqueue(object : Callback<SpotListVO> {
            override fun onResponse(call: Call<SpotListVO>, response: Response<SpotListVO>) {
                if (response.isSuccessful) {
                    d("TAG", "RegisterActivity onResponse() ")

                } else {
                    try {
                        /*val jObjError = JSONObject(response.errorBody()!!.string())

                        Toast.makeText(this@RegisterActivity,
                            jObjError.getJSONObject("error").getString("message"),
                            Toast.LENGTH_LONG
                        ).show()*/


                    } catch (e: Exception) {
                        Toast.makeText(activity, e.message, Toast.LENGTH_LONG).show()
                    }
                }
            }

            override fun onFailure(call: Call<SpotListVO>, t: Throwable) {
                d("TAG", "RegisterActivity onFailure() ")
                Toast.makeText(activity,"post 실패 !!", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun setRetrofitInit(){
        mRetrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    private fun setApiServiceInit(){
        apiService = mRetrofit.create(APIService::class.java)
    }
    private fun setCategoryClick(){
        img_icon_cafe.setOnClickListener{

            img_cafe_click.visibility = View.VISIBLE
            img_culture_click.visibility = View.GONE
            img_drink_click.visibility = View.GONE
            img_food_click.visibility = View.GONE
            img_etc_click.visibility = View.GONE
            this.choicedCategory = "카페"

        }
        img_icon_culture.setOnClickListener{
            img_cafe_click.visibility = View.GONE
            img_culture_click.visibility = View.VISIBLE
            img_drink_click.visibility = View.GONE
            img_food_click.visibility = View.GONE
            img_etc_click.visibility = View.GONE
            this.choicedCategory = "문화"
        }
        img_icon_drink.setOnClickListener{
            img_cafe_click.visibility = View.GONE
            img_culture_click.visibility = View.GONE
            img_drink_click.visibility = View.VISIBLE
            img_food_click.visibility = View.GONE
            img_etc_click.visibility = View.GONE
            this.choicedCategory = "술집"
        }
        img_icon_food.setOnClickListener{
            img_cafe_click.visibility = View.GONE
            img_culture_click.visibility = View.GONE
            img_drink_click.visibility = View.GONE
            img_food_click.visibility = View.VISIBLE
            img_etc_click.visibility = View.GONE
            this.choicedCategory = "맛집"
        }
        img_icon_etc.setOnClickListener{
            img_cafe_click.visibility = View.GONE
            img_culture_click.visibility = View.GONE
            img_drink_click.visibility = View.GONE
            img_food_click.visibility = View.GONE
            img_etc_click.visibility = View.VISIBLE
            this.choicedCategory = "기타"
        }
    }
    private fun setRatingBar(){
        ratingbar1.setOnClickListener{
            when(rating){
                0 ->{
                    ratingbar1.setImageResource(R.drawable.ic_img_star_yellow)
                    rating = 1
                }
                1 ->{
                    ratingbar1.setImageResource(R.drawable.ic_img_start_gray)
                    rating = 0
                }
                2 ->{
                    ratingbar2.setImageResource(R.drawable.ic_img_start_gray)
                    rating = 1
                }
                3 ->{
                    ratingbar3.setImageResource(R.drawable.ic_img_start_gray)
                    ratingbar2.setImageResource(R.drawable.ic_img_start_gray)
                    rating = 1
                }
            }
            setRatingTxt(rating)
        }
        ratingbar2.setOnClickListener{
            when(rating){
                0 ->{
                    ratingbar1.setImageResource(R.drawable.ic_img_star_yellow)
                    ratingbar2.setImageResource(R.drawable.ic_img_star_yellow)
                    rating = 2
                }
                1 ->{
                    ratingbar2.setImageResource(R.drawable.ic_img_star_yellow)
                    rating = 2
                }
                2 ->{
                    ratingbar2.setImageResource(R.drawable.ic_img_start_gray)
                    rating = 1
                }
                3 ->{
                    ratingbar3.setImageResource(R.drawable.ic_img_start_gray)
                    rating = 2
                }
            }
            setRatingTxt(rating)
        }
        ratingbar3.setOnClickListener{
            when(rating){
                0 ->{
                    ratingbar1.setImageResource(R.drawable.ic_img_star_yellow)
                    ratingbar2.setImageResource(R.drawable.ic_img_star_yellow)
                    ratingbar3.setImageResource(R.drawable.ic_img_star_yellow)
                    rating = 3
                }
                1 ->{
                    ratingbar2.setImageResource(R.drawable.ic_img_star_yellow)
                    ratingbar3.setImageResource(R.drawable.ic_img_star_yellow)
                    rating = 3
                }
                2 ->{
                    ratingbar3.setImageResource(R.drawable.ic_img_star_yellow)
                    rating = 3
                }
                3 ->{
                    ratingbar3.setImageResource(R.drawable.ic_img_start_gray)
                    rating = 2
                }
            }
            setRatingTxt(rating)
        }
    }
    private fun setRatingTxt(rating: Int){
        when(rating){
            0 -> txt_rating_info.text = "평점을 남겨주세요!"
            1 -> txt_rating_info.text = "나쁘지 않은 곳이에요!"
            2 -> txt_rating_info.text = "멋진 곳이에요!"
            3 -> txt_rating_info.text = "내 맘에 쏙 드는 곳이에요!"
        }
    }

}