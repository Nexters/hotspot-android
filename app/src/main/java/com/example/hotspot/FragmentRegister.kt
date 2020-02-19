package com.example.hotspot

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.Log.d
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.net.toUri
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.squareup.otto.Subscribe
import kotlinx.android.synthetic.main.mylist_view.*
import kotlinx.android.synthetic.main.myplace_item.*
import kotlinx.android.synthetic.main.register_view.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory



class FragmentRegister : BaseFragment() {
    companion object{
        lateinit var place: Place
        lateinit var myPlace: MyPlace
    }
    private lateinit var mRetrofit: Retrofit
    lateinit var apiService : APIService
    private lateinit var spotList : SpotListVO
    private var isAdd = true

    private var accessToken = GlobalApplication.prefs.getPreferences()
    var rating : Int? = 0
    var choicedCategory = ""
    var isRcylrdecoAdd = false
    var search_OK = false
    private var requestCode = 0
    private var stickerData: StickerData? = null


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
            showFinishDailog()

        }

        edtTxt_memo.doOnTextChanged { text, start, count, after ->
            if(count > 50){
                //50자 넘어갔다고 알림창 ?ㅊ
            }
            if(text.isNullOrEmpty()){
                img_uncheck2.setImageResource(R.drawable.ic_img_uncheck)
            }
            else{
                img_uncheck2.setImageResource(R.drawable.ic_img_check)
            }
        }

        reg_category_txt.setOnClickListener {

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
                            reg_category_txt.setImageResource(R.drawable.img_category_cafe)
                        }
                        "맛집" -> {
                            reg_category_txt.setImageResource(R.drawable.img_category_food)
                        }
                        "문화" -> {
                            reg_category_txt.setImageResource(R.drawable.img_category_culture)
                        }
                        "술집" -> {
                            reg_category_txt.setImageResource(R.drawable.img_category_drink)
                        }
                        "기타" -> {
                            reg_category_txt.setImageResource(R.drawable.img_category_etc)
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
            if(search_OK) {
                if (layout_visited.visibility == View.GONE) {
                    layout_visited.visibility = View.VISIBLE
                    txt_visited.setTextColor(Color.WHITE)


                    img_uncheck3.setImageResource(R.drawable.ic_img_check)

                    setRatingBar()
                } else {
                    layout_visited.visibility = View.GONE
                    txt_visited.setTextColor(resources.getColor(R.color.colorMyGrayDark))
                    img_uncheck3.setImageResource(R.drawable.ic_img_uncheck)

                }
            }
        }

        stickerBt.setOnClickListener{
            if(search_OK) {

                var intent = Intent(activity, StickerRegistActivity::class.java)
                intent.putExtra("Category", choicedCategory)
                intent.putExtra("PlaceName", place.placeName)
                intent.putExtra("Longitude", place.x.toDouble())
                intent.putExtra("Latitude", place.y.toDouble())
                activity!!.startActivityForResult(intent, 1)
            }

        }

        btn_regist.setOnClickListener{
            if(txt_place_name.text == "" || txt_place_name.text == "장소 추가"){
                Toast.makeText(activity,
                    "장소를 추가해 주세요!",
                    Toast.LENGTH_SHORT)
                    .show()
            }
            else registSpot()
        }
    }

    @Subscribe
    fun onActivityResultEvent(activityResultEvent: ActivityResultEvent){
        onActivityResult(activityResultEvent.get_RequestCode(),activityResultEvent.get_ResultCode(),activityResultEvent.get_Data())

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(resultCode){
            1 -> {
                stickerData = data!!.getSerializableExtra("StickerData") as StickerData
                //리사이클러 뷰 만들고 visible

                //리스트 만들기 (ㅅ트링 리스트)
                val list : ArrayList<String> = arrayListOf()
                var count = 0
                if(stickerData!!.powerPlugAvailable){
                    list.add("콘센트 있음")
                    count++
                }
                if(stickerData!!.allDayAvailable){
                    list.add("24시간 영업")
                    count++
                }
                if(stickerData!!.parkingAvailable){
                    list.add("주차 가능")
                    count++
                }
                if(!stickerData!!.open.isNullOrEmpty()){
                    var str : String
                    if(stickerData!!.open.toInt()<=12){
                        str = stickerData!!.open + "AM - "
                    }
                    else{
                        str = (stickerData!!.open.toInt()-12).toString() + "PM - "
                    }
                    if(stickerData!!.close.toInt()<=12){
                        str = str+ stickerData!!.close + "AM"
                    }
                    else{
                        str = str+ (stickerData!!.close.toInt()-12).toString() + "PM"
                    }
                    list.add(str)
                    count++
                }
                if(!stickerData!!.bestMenu.isNullOrEmpty()){
                    for(i in (0..stickerData!!.bestMenu.size-1)){
                        list.add(stickerData!!.bestMenu.get(i))
                    }
                    count++
                }
                if(!stickerData!!.cloudinaryUrlList.isNullOrEmpty()){
                    list.add("사진 있음")
                    count++
                }


                if(!list.isNullOrEmpty()){
                    rcycl_sticker_view.layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
                    rcycl_sticker_view.adapter = StickerRcylrAdapter(list)
                    val deco = Stk_Rcylr_Item_Deco()
                    if(isRcylrdecoAdd) {
                        rcycl_sticker_view.removeItemDecorationAt(0)
                        isRcylrdecoAdd = false
                    }
                    rcycl_sticker_view.addItemDecoration(deco)
                    isRcylrdecoAdd = true
                    if(list.size >= 3) {
                        hide_eff_left.visibility = View.VISIBLE
                        hide_eff_right.visibility = View.VISIBLE
                    }
                    else{
                        hide_eff_left.visibility = View.INVISIBLE
                        hide_eff_right.visibility = View.INVISIBLE
                    }
                    rcycl_sticker_view.visibility = View.VISIBLE
                    stickerBt.text = "스티커 " +count.toString()+"개"
                    img_uncheck4.setImageResource(R.drawable.ic_img_check)
                    stickerBt.setTextColor(resources.getColor(R.color.colorWhite))
                }
                else{
                    hide_eff_left.visibility = View.INVISIBLE
                    hide_eff_right.visibility = View.INVISIBLE
                    rcycl_sticker_view.visibility = View.INVISIBLE
                    img_uncheck4.setImageResource(R.drawable.ic_img_uncheck)
                    stickerBt.setTextColor(resources.getColor(R.color.colorEditTextGray))
                    stickerBt.text = "스티커 추가"
                }


            }

        }

    }


    private fun setLayout(){
        //isAdd : true 등록, false 수정
        isAdd = arguments!!.getBoolean("isAdd", true)
        search_OK = arguments!!.getBoolean("search_OK", false)

        d("TAG", "setLayout() isAdd : ${isAdd}, search_OK : $search_OK")

        if(!isAdd) {//수정 이기때문에 장소에 대한 정보 뿌리기
            myPlace = arguments!!.getSerializable("myPlace") as MyPlace
            requestCode = arguments!!.getSerializable("RequestCode") as Int
            place = myPlace.place
            search_OK = true
            txt_place_name.isClickable = false
            txt_place_name.text = place.placeName
            txt_place_name.setTextColor(resources.getColor(R.color.colorWhite))
            txt_address.text = place.roadAddressName
            choicedCategory = place.categoryName
            if(!choicedCategory.isNullOrEmpty()) {
                when (choicedCategory) {
                    "카페" -> {
                        reg_category_txt.setImageResource(R.drawable.img_category_cafe)
                    }
                    "맛집" -> {
                        reg_category_txt.setImageResource(R.drawable.img_category_food)
                    }
                    "문화" -> {
                        reg_category_txt.setImageResource(R.drawable.img_category_culture)
                    }
                    "술집" -> {
                        reg_category_txt.setImageResource(R.drawable.img_category_drink)
                    }
                    "기타" -> {
                        reg_category_txt.setImageResource(R.drawable.img_category_etc)
                    }

                }
            }
            else{
                reg_category_txt.setImageResource(R.drawable.img_category_etc)
                choicedCategory = "기타"
            }
            img_uncheck.setImageResource(R.drawable.ic_img_check)
            txt_address.visibility = View.VISIBLE
            reg_category_txt.visibility = View.VISIBLE
            edtTxt_memo.isFocusableInTouchMode = true
            edtTxt_memo.isFocusable = true

            if(!myPlace.memo.isNullOrEmpty()){
                edtTxt_memo.setText(myPlace.memo)
                img_uncheck2.setImageResource(R.drawable.ic_img_check)
            }
            if(myPlace.visited){
                layout_visited.visibility = View.VISIBLE
                txt_visited.setTextColor(Color.WHITE)
                img_uncheck3.setImageResource(R.drawable.ic_img_check)
                rating = myPlace.rating
                setRatingBar()
                setRatingTxt(rating)
                when(rating){
                    1 ->{
                        ratingbar1.setImageResource(R.drawable.ic_img_star_yellow)
                    }
                    2 ->{
                        ratingbar1.setImageResource(R.drawable.ic_img_star_yellow)
                        ratingbar2.setImageResource(R.drawable.ic_img_star_yellow)
                    }
                    3 ->{
                        ratingbar1.setImageResource(R.drawable.ic_img_star_yellow)
                        ratingbar2.setImageResource(R.drawable.ic_img_star_yellow)
                        ratingbar3.setImageResource(R.drawable.ic_img_star_yellow)
                    }
                }
            }
            //리사이클러 뷰 만들고 visible
            stickerData = StickerData()
            //리스트 만들기 (ㅅ트링 리스트)
            val list : ArrayList<String> = arrayListOf()
            var count = 0
            if(myPlace.powerPlugAvailable != null){
                list.add("콘센트 있음")
                count++
                stickerData!!.powerPlugAvailable = true
            }
            if(myPlace.allDayAvailable != null){
                list.add("24시간 영업")
                count++
                stickerData!!.allDayAvailable = true
            }
            if(myPlace.parkingAvailable != null){
                list.add("주차 가능")
                count++
                stickerData!!.parkingAvailable = true
            }
            if(!myPlace.businessHours!!.open.isNullOrEmpty()){
                var str : String
                stickerData!!.open = myPlace.businessHours!!.open!!
                stickerData!!.close = myPlace.businessHours!!.close!!
                if(myPlace.businessHours!!.open!!.toInt()<=12){
                    str = myPlace.businessHours!!.open + "AM - "
                }
                else{
                    str = (myPlace.businessHours!!.open!!.toInt()-12).toString() + "PM - "
                }
                if(myPlace.businessHours!!.close!!.toInt()<=12){
                    str = str+ myPlace.businessHours!!.close + "AM"
                }
                else{
                    str = str+ (myPlace.businessHours!!.close!!.toInt()-12).toString() + "PM"
                }
                list.add(str)
                count++
            }
            if(!myPlace.bestMenu.isNullOrEmpty()){
                for(i in (0..myPlace.bestMenu!!.size-1)){
                    list.add(myPlace.bestMenu!!.get(i))
                }
                count++
                stickerData!!.bestMenu = myPlace.bestMenu!!
            }
            if(!myPlace.images.isNullOrEmpty()){
                list.add("사진 있음")
                count++
                for(i in (0..myPlace.images!!.size-1)){
                    stickerData!!.cloudinaryIdList!!.add(myPlace.images!!.get(i).cloudinaryId)
                    stickerData!!.cloudinaryUrlList!!.add(myPlace.images!!.get(i).url)
                }
            }
            if(!list.isNullOrEmpty()){
                rcycl_sticker_view.layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
                rcycl_sticker_view.adapter = StickerRcylrAdapter(list)
                val deco = Stk_Rcylr_Item_Deco()
                if(isRcylrdecoAdd) {
                    rcycl_sticker_view.removeItemDecorationAt(0)
                    isRcylrdecoAdd = false
                }
                rcycl_sticker_view.addItemDecoration(deco)
                isRcylrdecoAdd = true
                if(list.size >= 3) {
                    hide_eff_left.visibility = View.VISIBLE
                    hide_eff_right.visibility = View.VISIBLE
                }
                else{
                    hide_eff_left.visibility = View.INVISIBLE
                    hide_eff_right.visibility = View.INVISIBLE
                }
                rcycl_sticker_view.visibility = View.VISIBLE
                stickerBt.text = "스티커 " +count.toString()+"개"
                img_uncheck4.setImageResource(R.drawable.ic_img_check)
                stickerBt.setTextColor(resources.getColor(R.color.colorWhite))
            }
            else{
                hide_eff_left.visibility = View.INVISIBLE
                hide_eff_right.visibility = View.INVISIBLE
                rcycl_sticker_view.visibility = View.INVISIBLE
                img_uncheck4.setImageResource(R.drawable.ic_img_uncheck)
                stickerBt.setTextColor(resources.getColor(R.color.colorEditTextGray))
            }
        }
        else if(search_OK) {
            edtTxt_memo.isFocusableInTouchMode = true
            edtTxt_memo.isFocusable = true

            txt_place_name.setTextColor(resources.getColor(R.color.colorWhite))
            place = arguments!!.getSerializable("searchPlace") as Place
            if(place.roadAddressName.isNullOrEmpty()){
                place.roadAddressName = ""
            }
            choicedCategory = place.categoryName
            when(choicedCategory){
                "카페" -> {
                    reg_category_txt.setImageResource(R.drawable.img_category_cafe)
                }
                "맛집" -> {
                    reg_category_txt.setImageResource(R.drawable.img_category_food)
                }
                "문화" -> {
                    reg_category_txt.setImageResource(R.drawable.img_category_culture)
                }
                "술집" -> {
                    reg_category_txt.setImageResource(R.drawable.img_category_drink)
                }
                "기타" -> {
                    reg_category_txt.setImageResource(R.drawable.img_category_etc)
                }

            }
            img_uncheck.setImageResource(R.drawable.ic_img_check)

            txt_place_name.text = place.placeName
            txt_address.text = place.roadAddressName

            txt_address.visibility = View.VISIBLE
            reg_category_txt.visibility = View.VISIBLE
        }
    }

    private fun registSpot(){
        btn_regist.isClickable = false
        var isVisited = true
        if(txt_visited.currentTextColor == Color.WHITE){
            isVisited = true
            if(!isAdd) {
                myPlace.visited = true
            }
        }
        else {
            isVisited = false
            rating = null
            if(!isAdd) {
                myPlace.visited = false
                myPlace.rating = 0
            }
        }
        val registPlace = Place(
            place.kakaoId,place.kakaoUrl,
            place.placeName,
            place.addressName,
            place.roadAddressName,
            place.x,
            place.y,
            this.choicedCategory
        )
        if(!isAdd) {
            myPlace.place.categoryName = this.choicedCategory
        }
        //spotList는 서버에 보낼 데이터
        if(stickerData == null) {
            spotList = SpotListVO(
                registPlace,
                isVisited,
                edtTxt_memo.text.toString(),
                rating,
                null, null, null, null, null, null
            )
            if(!isAdd) {
                myPlace.memo = edtTxt_memo.text.toString()
                myPlace.images!!.clear()
                myPlace.bestMenu!!.clear()
                myPlace.businessHours!!.open = null
                myPlace.businessHours!!.close = null
                myPlace.parkingAvailable = null
                myPlace.allDayAvailable = null
                myPlace.powerPlugAvailable = null
            }
        }
        else{
            if(stickerData != null) {
                var images: ArrayList<Images>?
                var bestMenu: ArrayList<String>?
                var businessHours: BusinessHours?
                var parkingAvailable: Boolean?
                var allDayAvailable: Boolean?
                var powerPlugAvailalbe: Boolean?
                if (stickerData!!.cloudinaryUrlList!!.size == 0) {
                    images = null
                } else {
                    images = arrayListOf()
                    for (i in 0..(stickerData!!.cloudinaryUrlList!!.size - 1)) {
                        var image = Images(
                            stickerData!!.cloudinaryIdList!!.get(i),
                            stickerData!!.cloudinaryUrlList!!.get(i)
                        )
                        images.add(image)
                    }
                }
                if (stickerData!!.bestMenu.size == 0) {
                    bestMenu = null
                } else {
                    bestMenu = stickerData!!.bestMenu
                }
                if (stickerData!!.open.isNullOrEmpty()) {
                    businessHours = null
                } else {
                    businessHours = BusinessHours(stickerData!!.open, stickerData!!.close)
                }
                if (!stickerData!!.parkingAvailable) {
                    parkingAvailable = null
                } else {
                    parkingAvailable = true
                }
                if (!stickerData!!.allDayAvailable) {
                    allDayAvailable = null
                } else {
                    allDayAvailable = true
                }
                if (!stickerData!!.powerPlugAvailable) {
                    powerPlugAvailalbe = null
                } else {
                    powerPlugAvailalbe = true
                }
                spotList = SpotListVO(
                    registPlace,
                    isVisited,
                    edtTxt_memo.text.toString(),
                    rating,
                    images,
                    bestMenu,
                    businessHours,
                    parkingAvailable,
                    allDayAvailable,
                    powerPlugAvailalbe
                )
                if(!isAdd) {
                    myPlace.memo = edtTxt_memo.text.toString()
                    if(images != null) {
                        myPlace.images = images
                    }
                    else myPlace.images!!.clear()
                    if(!bestMenu.isNullOrEmpty()) {
                        myPlace.bestMenu = bestMenu
                    }else myPlace.bestMenu!!.clear()
                    if(businessHours != null) {
                        myPlace.businessHours = businessHours
                    }else {
                        myPlace.businessHours!!.open = null
                        myPlace.businessHours!!.close = null
                    }
                    myPlace.parkingAvailable = parkingAvailable
                    myPlace.allDayAvailable = allDayAvailable
                    myPlace.powerPlugAvailable = powerPlugAvailalbe
                }
            }
        }
        if(isAdd) {

            apiService.postPlace(
                "Bearer " + "${accessToken}",
                spotList
            ).enqueue(object : Callback<SpotListVO> {
                override fun onResponse(call: Call<SpotListVO>, response: Response<SpotListVO>) {
                    if (response.isSuccessful) {
                        d("TAG", "RegisterActivity onResponse() ")
                        var intent = Intent()
                        intent.putExtra("NewSpotInfo", spotList)
                        activity!!.setResult(10, intent)
                        activity!!.finish()
                    } else {
                        if(response.code() == 403 ){
                            //이미 등록된 장소
                            Toast.makeText(
                                activity!!,
                                "이미 등록된 장소입니다!",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        else {
                            d("TAG", "Regist onResPonse : " + response.toString())
                            d("TAG", "Regis onPresPonse : " + response.message())
                            Toast.makeText(
                                activity!!,
                                "장소 등록 실패! 네트워크를 체크해 주세요.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                    btn_regist.isClickable = true
                }

                override fun onFailure(call: Call<SpotListVO>, t: Throwable) {
                    d("TAG", "RegisterActivity onFailure() ")
                    Toast.makeText(activity, "post 실패 !!", Toast.LENGTH_LONG).show()
                    btn_regist.isClickable = true
                }
            })
        }
        else{
            apiService.putPlace("Bearer " + "${accessToken}",
                "${myPlace.id}",
                spotList
            ).enqueue(object : Callback<SpotListVO>{
                override fun onResponse(call: Call<SpotListVO>, response: Response<SpotListVO>) {
                    if (response.isSuccessful) {
                        d("TAG", "장소 업데이트 성공")
                        if(requestCode == 20){
                            var intent = Intent()
                            intent.putExtra("NewSpotInfo", myPlace)
                            activity!!.setResult(99, intent)
                            activity!!.finish()
                        }
                        else if(requestCode == 21){
                            var intent = Intent()
                            intent.putExtra("NewSpotInfo", myPlace)
                            activity!!.setResult(100, intent)
                            activity!!.finish()
                        }
                    } else {
                        d("TAG", "Regist onResPonse : " + response.toString())
                        Toast.makeText(activity!!, "장소 업데이트 실패! 네트워크를 체크해 주세요.", Toast.LENGTH_LONG)
                    }
                }

                override fun onFailure(call: Call<SpotListVO>, t: Throwable) {
                    d("TAG", "RegisterActivity onFailure() ")
                    Toast.makeText(activity!!, "장소 업데이트 실패! 네트워크를 체크해 주세요.", Toast.LENGTH_LONG)
                }
            })
        }
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
    private fun setRatingTxt(rating: Int?){
        if(rating != null) {
            when (rating) {
                0 -> txt_rating_info.text = "평점을 남겨주세요!"
                1 -> txt_rating_info.text = "나쁘지 않은 곳이에요!"
                2 -> txt_rating_info.text = "멋진 곳이에요!"
                3 -> txt_rating_info.text = "내 맘에 쏙 드는 곳이에요!"
            }
        }
    }
    private fun showFinishDailog(){
        regist_popup_layout.visibility = View.VISIBLE
        btn_regist.visibility = View.INVISIBLE
        regist_popup_layout.findViewById<TextView>(R.id.regist_quit_ok_txt).setOnClickListener{
            fragmentManager!!.beginTransaction()
                .remove(this)
                .commit()
            var intent = Intent()
            activity!!.setResult(9,intent)
            activity!!.finish()
        }
        regist_popup_layout.findViewById<TextView>(R.id.regist_quit_no_txt).setOnClickListener{
            regist_popup_layout.visibility = View.GONE
            btn_regist.visibility = View.VISIBLE
        }
    }

}