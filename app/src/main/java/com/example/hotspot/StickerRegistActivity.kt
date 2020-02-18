package com.example.hotspot

import android.Manifest
import android.content.ClipData
import android.content.ClipDescription
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.net.toUri
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import gun0912.tedbottompicker.TedBottomPicker
import kotlinx.android.synthetic.main.activity_sticker_regist.*
import kotlinx.android.synthetic.main.closedtime_input.*
import kotlinx.android.synthetic.main.opentime_input.*

private var isMediaManagerInit = false
class StickerRegistActivity : AppCompatActivity() {
    private var stateBestInput = 0 // 스티커  입력 완료 눌렀을 때 사용할 것.
    // 0 : 입력 스티커  입력 안했음  1 : 베스트 메뉴 1개 입력창  2 : 베스트메뉴 2개 입력창   3 : 오픈 클롲즈 타임 입력 창
    private var stickerData = StickerData()
    private var uploadisSuccess = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sticker_regist)
        //네이버 맵 옵션 설정
        var naverMapOptions = NaverMapOptions()
        naverMapOptions.allGesturesEnabled(false)
        naverMapOptions.zoomControlEnabled(false)
        dragView.performClick()
        trash_view.setOnDragListener(dragListener(work_24_img,best_menu_img,consent_img,park_img,open_time_img,gallery_img,stickerData))
        h24_fin_view.setOnLongClickListener(LongClickListener(trash_view,"AlldayView"))
        consent_fin_view.setOnLongClickListener(LongClickListener(trash_view,"ConsentView"))
        park_fin_view.setOnLongClickListener(LongClickListener(trash_view,"ParkView"))
        photo_fin_view.setOnLongClickListener(LongClickListener(trash_view,"PhotoView"))
        work_time_fin_view.setOnLongClickListener(LongClickListener(trash_view,"WorktimeView"))
        best_fin_view.setOnLongClickListener(LongClickListener(trash_view,"BestView"))

        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.mapframe) as MapFragment?
            ?: MapFragment.newInstance(naverMapOptions).also  {
                it.getMapAsync(OnMapReadyCallback {
                    it.mapType = NaverMap.MapType.Navi
                    it.isNightModeEnabled = true
                    var nmo = NaverMapOptions()
                    nmo.scrollGesturesEnabled(false)
                    val longitude = intent.getDoubleExtra("Longitude",0.0)
                    val latitude = intent.getDoubleExtra("Latitude",0.0)
                    val cameraUpdate = CameraUpdate.scrollTo(LatLng(latitude,longitude)) //해당 위치로 카메라 시점 이동(위치 넘겨받기)
                    it.moveCamera(cameraUpdate)

                })
                fm.beginTransaction().add(R.id.mapframe, it).commit()
            }

        setViewTouchEvent()


        val placeName = intent.getStringExtra("PlaceName")
        val cateGory = intent.getStringExtra("Category")
        when(cateGory){
            "카페" -> {
                img_sticker_category_view.setImageResource(R.drawable.ic_img_icon_cafe)
            }
            "맛집" -> {
                img_sticker_category_view.setImageResource(R.drawable.ic_img_icon_food)            }
            "문화" -> {
                img_sticker_category_view.setImageResource(R.drawable.ic_img_icon_culture)            }
            "술집" -> {
                img_sticker_category_view.setImageResource(R.drawable.ic_img_icon_drink)            }
            "기타" -> {
                img_sticker_category_view.setImageResource(R.drawable.ic_img_icon_etc)            }
        }
        txt_sticker_place_name.text = placeName


    }
    inner class LongClickListener(var trashView: ImageView, var data : String) : View.OnLongClickListener {
        override fun onLongClick(v: View?): Boolean {
            trashView.visibility = View.VISIBLE
            if(v != null) {
                val item = ClipData.Item(data as CharSequence)
                val dragData = ClipData(
                    data as CharSequence,
                    arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN),
                    item
                )
                val myShadow = View.DragShadowBuilder(v)
                v.startDragAndDrop(
                    dragData, myShadow, v, 0
                )
                return true
            }
            else return false
        }
    }



    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        setStickerPosition()
        setSticKersListener()
    }
    private fun setSticKersListener(){
        input_open_time_view.findViewById<AppCompatEditText>(R.id.open_time_input_edt).doOnTextChanged { text, start, count, after ->
            if(text.isNullOrEmpty()){

            }
            else if((0>text.toString().toInt()) || (12 < text.toString().toInt())){
                Toast.makeText(this,"0 ~ 12 까지 입력하세요 !",Toast.LENGTH_LONG).show()
                input_open_time_view.findViewById<AppCompatEditText>(R.id.open_time_input_edt).setText(null)
            }
        }
        input_open_time_view.findViewById<TextView>(R.id.open_am_pm_txt).setOnClickListener{
            if(input_open_time_view.findViewById<TextView>(R.id.open_am_pm_txt).text.equals("AM")){
                input_open_time_view.findViewById<TextView>(R.id.open_am_pm_txt).text = "PM"
                input_open_time_view.findViewById<TextView>(R.id.open_am_pm_txt).setTextColor(resources.getColor(R.color.colorPM))
            }
            else{
                input_open_time_view.findViewById<TextView>(R.id.open_am_pm_txt).text = "AM"
                input_open_time_view.findViewById<TextView>(R.id.open_am_pm_txt).setTextColor(resources.getColor(R.color.colorAM))
            }
        }
        input_closed_time_view.findViewById<TextView>(R.id.closed_am_pm_txt).setOnClickListener{
            if(input_closed_time_view.findViewById<TextView>(R.id.closed_am_pm_txt).text.equals("AM")){
                input_closed_time_view.findViewById<TextView>(R.id.closed_am_pm_txt).text = "PM"
                input_closed_time_view.findViewById<TextView>(R.id.closed_am_pm_txt).setTextColor(resources.getColor(R.color.colorPM))
            }
            else{
                input_closed_time_view.findViewById<TextView>(R.id.closed_am_pm_txt).text = "AM"
                input_closed_time_view.findViewById<TextView>(R.id.closed_am_pm_txt).setTextColor(resources.getColor(R.color.colorAM))
            }
        }
        input_closed_time_view.findViewById<AppCompatEditText>(R.id.closed_time_input_edt).doOnTextChanged { text, start, count, after ->
            if(text.isNullOrEmpty()){

            }
            else if((0>text.toString().toInt()) || (12 < text.toString().toInt())){
                Toast.makeText(this,"0 ~ 12 까지 입력하세요 !",Toast.LENGTH_LONG).show()
                input_closed_time_view.findViewById<AppCompatEditText>(R.id.closed_time_input_edt).setText(null)
            }
        }
        consent_img.setOnClickListener{
            consent_img.setColorFilter(resources.getColor(R.color.transparency))
            consent_fin_view.visibility = View.VISIBLE
            stickerData.powerPlugAvailable = true

            dragView.performClick()
        }
        park_img.setOnClickListener{
            park_img.setColorFilter(resources.getColor(R.color.transparency))
            park_fin_view.visibility = View.VISIBLE
            stickerData.parkingAvailable = true
            dragView.performClick()
        }
        work_24_img.setOnClickListener{
            work_24_img.setColorFilter(resources.getColor(R.color.transparency))
            h24_fin_view.visibility = View.VISIBLE
            stickerData.allDayAvailable = true
            dragView.performClick()
        }
        best_menu_img.setOnClickListener{
            mainlayout.visibility = View.INVISIBLE
            sticker_input_layout.visibility = View.VISIBLE
            input_best_menu_view.visibility = View.VISIBLE
            stateBestInput = 1
            dragView.performClick()
            dragView.isClickable = false
        }
        open_time_img.setOnClickListener{
            mainlayout.visibility = View.INVISIBLE
            sticker_input_layout.visibility = View.VISIBLE
            input_open_time_view.visibility = View.VISIBLE
            input_closed_time_view.visibility = View.VISIBLE
            stateBestInput = 3
            dragView.performClick()
            dragView.isClickable = false
        }
        gallery_img.setOnClickListener{
            selectAlbum()
        }


    }
    private fun setStickerPosition(){
        consent_fin_view.x = mainlayout.width*0.1f
        consent_fin_view.y = mainlayout.height*0.45f

        park_fin_view.x = mainlayout.width*0.35f
        park_fin_view.y = mainlayout.height*0.62f

        h24_fin_view.x = mainlayout.width*0.65f
        h24_fin_view.y = mainlayout.height*0.3f

        best_fin_view.x = mainlayout.width*0.6f
        best_fin_view.y = mainlayout.height*0.5f

        work_time_fin_view.x = mainlayout.width*0.03f
        work_time_fin_view.y = mainlayout.height*0.27f

        photo_fin_view.x = mainlayout.width*0.1f
        photo_fin_view.y = mainlayout.height*0.62f
    }


    private fun setViewTouchEvent(){
        //등록버튼 **********************
        txt_sticker_regist.setOnClickListener{

            intent = Intent()
            intent.putExtra("StickerData", stickerData)
            setResult(1,intent)
            finish()

        }
        //스티커 액티비티 종료 버튼 *****************
        sticker_finish_btn.setOnClickListener{
            showFinishDialog()//정말 돌아가시겠습니가?
        }
        // 베스트 메뉴 추가 버튼 리스너   ************ //
        input_best_menu_view.findViewById<ImageView>(R.id.best_plus_img).setOnClickListener{
            stateBestInput = 2
            input_best_menu_view.visibility = View.GONE
            input_best_menu_view2.findViewById<TextView>(R.id.up1).text =
                input_best_menu_view.findViewById<AppCompatEditText>(R.id.up1).text.toString()
            input_best_menu_view2.visibility = View.VISIBLE
        }
        // 스티커 입력 완료 리스너 ******************* //
        sticker_complete_txt.setOnClickListener{
            when(stateBestInput){
                1 ->{
                    //입력값 저장
                    stickerData.bestMenu.clear()
                    stickerData.bestMenu.add(input_best_menu_view.findViewById<AppCompatEditText>(R.id.up1).text.toString())
                    //상태 리셋
                    input_best_menu_view.findViewById<AppCompatEditText>(R.id.up1).setText("")
                    input_best_menu_view2.findViewById<AppCompatEditText>(R.id.up1).setText("")
                    input_best_menu_view2.findViewById<AppCompatEditText>(R.id.up2).setText("")
                    mainlayout.visibility = View.VISIBLE
                    sticker_input_layout.visibility = View.INVISIBLE
                    input_best_menu_view.visibility = View.INVISIBLE
                    stateBestInput = 0
                    dragView.isClickable = true
                    //키보드 내리기
                    var imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(input_best_menu_view.findViewById<AppCompatEditText>(R.id.up1).windowToken,0)
                    //완료 스티커 띄우기
                    if(!stickerData.bestMenu.get(0).isNullOrEmpty()) {
                        best_fin_view.findViewById<TextView>(R.id.up1).text = ""
                        best_fin_view.findViewById<TextView>(R.id.up2).text = ""
                        best_fin_view.findViewById<TextView>(R.id.up1).text = stickerData.bestMenu.get(0)
                        best_fin_view.visibility = View.VISIBLE
                        best_menu_img.setColorFilter(resources.getColor(R.color.transparency))
                    }
                    else{
                        best_fin_view.visibility = View.INVISIBLE
                    }
                }
                2 ->{
                    //입력값 저장
                    stickerData.bestMenu.clear()
                    stickerData.bestMenu.add(input_best_menu_view2.findViewById<AppCompatEditText>(R.id.up1).text.toString())
                    stickerData.bestMenu.add(input_best_menu_view2.findViewById<AppCompatEditText>(R.id.up2).text.toString())
                    //상태 리셋
                    input_best_menu_view.findViewById<AppCompatEditText>(R.id.up1).setText("")
                    input_best_menu_view2.findViewById<AppCompatEditText>(R.id.up1).setText("")
                    input_best_menu_view2.findViewById<AppCompatEditText>(R.id.up2).setText("")
                    mainlayout.visibility = View.VISIBLE
                    sticker_input_layout.visibility = View.INVISIBLE
                    input_best_menu_view.visibility = View.INVISIBLE
                    input_best_menu_view2.visibility = View.INVISIBLE
                    stateBestInput = 0
                    dragView.isClickable = true
                    //키보드 내리기
                    var imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(input_best_menu_view.findViewById<AppCompatEditText>(R.id.up1).windowToken,0)
                    //완료 스티커 띄우기
                    if(!stickerData.bestMenu.get(0).isNullOrEmpty()||!stickerData.bestMenu.get(1).isNullOrEmpty()) {
                        best_fin_view.findViewById<TextView>(R.id.up1).text = ""
                        best_fin_view.findViewById<TextView>(R.id.up2).text = ""
                        best_fin_view.findViewById<TextView>(R.id.up1).text = stickerData.bestMenu.get(0)
                        best_fin_view.findViewById<TextView>(R.id.up2).text = stickerData.bestMenu.get(1)
                        best_fin_view.visibility = View.VISIBLE
                        best_menu_img.setColorFilter(resources.getColor(R.color.transparency))
                    }
                    else{
                        best_fin_view.visibility = View.INVISIBLE
                    }
                }
                3 ->{
                    if(input_open_time_view.findViewById<AppCompatEditText>(R.id.open_time_input_edt).text.isNullOrEmpty()
                        ||input_closed_time_view.findViewById<AppCompatEditText>(R.id.closed_time_input_edt).text.isNullOrEmpty()){
                        Toast.makeText(this,"시간을 입력해 주세요!!",Toast.LENGTH_LONG).show()
                    }
                    else{
                        //입력값 저장
                        stickerData.open = input_open_time_view.findViewById<AppCompatEditText>(R.id.open_time_input_edt).text.toString()
                        stickerData.close = input_closed_time_view.findViewById<AppCompatEditText>(R.id.closed_time_input_edt).text.toString()
                        if(open_am_pm_txt.text.equals("PM")){
                            stickerData.open = (stickerData.open.toInt() + 12).toString()
                        }
                        if(closed_am_pm_txt.text.equals("PM")){
                            stickerData.close = (stickerData.close.toInt() + 12).toString()
                        }
                        //상태 리셋
                        input_open_time_view.findViewById<AppCompatEditText>(R.id.open_time_input_edt).setText(null)
                        input_closed_time_view.findViewById<AppCompatEditText>(R.id.closed_time_input_edt).setText(null)
                        mainlayout.visibility = View.VISIBLE
                        sticker_input_layout.visibility = View.INVISIBLE
                        input_open_time_view.visibility = View.INVISIBLE
                        input_closed_time_view.visibility = View.INVISIBLE
                        stateBestInput = 0
                        dragView.isClickable = true
                        //키보드 내리기
                        var imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(input_open_time_view.findViewById<AppCompatEditText>(R.id.open_time_input_edt).windowToken,0)
                        //완료 스티커 띄우기
                        if(stickerData.open.toInt()<=12) {
                            work_time_fin_view.findViewById<TextView>(R.id.work_fin_open_txt).text =
                                stickerData.open + "AM"
                        }
                        else{
                            work_time_fin_view.findViewById<TextView>(R.id.work_fin_open_txt).text =
                                (stickerData.open.toInt() -12).toString() + "PM"
                        }
                        if(stickerData.close.toInt()<=12) {
                            work_time_fin_view.findViewById<TextView>(R.id.work_fin_closed_txt).text =
                                stickerData.close + "AM"
                        }
                        else{
                            work_time_fin_view.findViewById<TextView>(R.id.work_fin_closed_txt).text =
                                (stickerData.close.toInt() -12).toString() + "PM"
                        }
                        work_time_fin_view.visibility = View.VISIBLE
                        open_time_img.setColorFilter(resources.getColor(R.color.transparency))

                    }
                }
            }

        }
        // 스티커 입력창에서 백버튼 ******************//
        sticer_input_backbtn.setOnClickListener{
            when(stateBestInput){
                1 ->{
                    //상태 리셋
                    input_best_menu_view.findViewById<AppCompatEditText>(R.id.up1).setText("")
                    input_best_menu_view2.findViewById<AppCompatEditText>(R.id.up1).setText("")
                    input_best_menu_view2.findViewById<AppCompatEditText>(R.id.up2).setText("")
                    mainlayout.visibility = View.VISIBLE
                    sticker_input_layout.visibility = View.INVISIBLE
                    input_best_menu_view.visibility = View.INVISIBLE
                    stateBestInput = 0
                    dragView.isClickable = true
                    //키보드 내리기
                    var imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(input_best_menu_view.findViewById<AppCompatEditText>(R.id.up1).windowToken,0)

                }
                2 ->{
                    stateBestInput = 1
                    input_best_menu_view2.visibility = View.GONE
                    input_best_menu_view2.findViewById<AppCompatEditText>(R.id.up1).setText("")
                    input_best_menu_view2.findViewById<AppCompatEditText>(R.id.up2).setText("")
                    input_best_menu_view.visibility = View.VISIBLE
                }
                3 ->{
                    //상태 리셋
                    input_open_time_view.findViewById<AppCompatEditText>(R.id.open_time_input_edt).setText(null)
                    input_closed_time_view.findViewById<AppCompatEditText>(R.id.closed_time_input_edt).setText(null)
                    mainlayout.visibility = View.VISIBLE
                    sticker_input_layout.visibility = View.INVISIBLE
                    input_open_time_view.visibility = View.INVISIBLE
                    input_closed_time_view.visibility = View.INVISIBLE
                    stateBestInput = 0
                    dragView.isClickable = true
                    //키보드 내리기
                    var imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(input_open_time_view.findViewById<AppCompatEditText>(R.id.open_time_input_edt).windowToken,0)

                }
            }
        }
    }

    private fun selectAlbum(){
        //앨범 열기


        TedPermission.with(this)
            .setPermissionListener(object : PermissionListener {
                override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                    Toast.makeText(this@StickerRegistActivity,"Permission Denied", Toast.LENGTH_LONG).show()
                }

                override fun onPermissionGranted() {
                    var photoPicker = TedBottomPicker.Builder(this@StickerRegistActivity)
                        .setOnMultiImageSelectedListener {
                            txt_sticker_regist.isClickable = false
                            sticker_finish_btn.visibility = View.INVISIBLE
                            txt_sticker_regist.text = "업로드 중"
                            // it 에 선택한 사진 uri 담김
                            if(it.size == 0){//사진을 선택 안했다면
                                txt_sticker_regist.isClickable = true
                                sticker_finish_btn.visibility = View.VISIBLE
                                txt_sticker_regist.text = "완료"
                            }
                            else{
                                stickerData.photoUriList!!.clear()
                                for(i in 0..it.size-1){

                                    stickerData.photoUriList!!.add(it.get(i).toString())
                                }
                                photo_fin_view.findViewById<TextView>(R.id.photo_count_txt).text = stickerData.photoUriList!!.size.toString()

                                //이미지 바로 업로드 *********************
                                //cloudinary init config
                                val config = HashMap<String,String>()
                                config.put("cloud_name", "hotspot-team")
                                if(!isMediaManagerInit) {
                                    MediaManager.init(this@StickerRegistActivity, config)
                                    isMediaManagerInit = true
                                }
                                for(i in 0 .. (stickerData.photoUriList!!.size-1)) {
                                    val requestId = MediaManager.get()
                                        .upload(stickerData.photoUriList!!.get(i).toUri())
                                        .unsigned("hotspot-dev")
                                        .callback(object : UploadCallback {
                                            override fun onError(
                                                requestId: String?,
                                                error: ErrorInfo?
                                            ) {
                                                Log.d("Cloudinary", "error : ${error.toString()}")
                                                Toast.makeText(this@StickerRegistActivity,"이미지 업로드에 실패했습니다. 네트워크 확인 바랍니다."
                                                    ,Toast.LENGTH_LONG).show()
                                                uploadisSuccess = false
                                                if(i==(it.size-1)){
                                                    txt_sticker_regist.isClickable = true
                                                    sticker_finish_btn.visibility = View.VISIBLE
                                                    txt_sticker_regist.text = "완료"
                                                    gallery_img.isClickable = true
                                                    uploadisSuccess = true
                                                }

                                            }

                                            override fun onProgress(
                                                requestId: String?,
                                                bytes: Long,
                                                totalBytes: Long
                                            ) {
                                            }

                                            override fun onReschedule(
                                                requestId: String?,
                                                error: ErrorInfo?
                                            ) {
                                            }

                                            override fun onStart(requestId: String?) {
                                                gallery_img.isClickable = false
                                            }

                                            override fun onSuccess(
                                                requestId: String?,
                                                resultData: MutableMap<Any?, Any?>?
                                            ) {
                                                Log.d(
                                                    "Cloudinary",
                                                    "resultData : ${resultData.toString()}"
                                                )
                                                if(!resultData.isNullOrEmpty()) {
                                                    stickerData.cloudinaryIdList!!.add(resultData.get("public_id").toString())
                                                    stickerData.cloudinaryUrlList!!.add(resultData.get("url").toString())
                                                }
                                                if(i==(it.size-1)){
                                                    if(uploadisSuccess) {
                                                        txt_sticker_regist.isClickable = true
                                                        sticker_finish_btn.visibility = View.VISIBLE
                                                        txt_sticker_regist.text = "완료"
                                                        gallery_img.isClickable = true
                                                        gallery_img.setColorFilter(
                                                            resources.getColor(
                                                                R.color.transparency
                                                            )
                                                        )
                                                        photo_fin_view.visibility = View.VISIBLE
                                                    }
                                                    else uploadisSuccess = true
                                                }

                                            }
                                        }).dispatch()
                                }
                                dragView.performClick()
                            }
                        }
                        .showCameraTile(false)
                        .showGalleryTile(false)
                        .setGalleryTile(R.drawable.img_sticker_list_gallery)
                        .setCompleteButtonText("Upload")
                        .setEmptySelectionText("최대 5장까지 추가 가능합니다")
                        .setSelectMaxCount(5)
                        .setPreviewMaxCount(25)//최근 사진 몇개까지 불러올거냐
                        .setSpacing(5)
                        .create()
                    photoPicker.show(supportFragmentManager)
                }
            })
            .setPermissions(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .check()

    }


    override fun onBackPressed() {
        //팝업창
        showFinishDialog()
    }
    private fun showFinishDialog(){
        mainlayout.visibility = View.INVISIBLE
        sticker_input_layout.visibility = View.INVISIBLE
        sticker_popup_layout.visibility = View.VISIBLE
        sticker_popup_layout.findViewById<TextView>(R.id.stk_quit_ok_txt).setOnClickListener{
            //setResult
            intent = Intent()
            var emptyStickerData = StickerData()
            intent.putExtra("StickerData",emptyStickerData)
            setResult(1,intent)
            finish()
        }
        sticker_popup_layout.findViewById<TextView>(R.id.stk_quit_no_txt).setOnClickListener{
            //원상태복구
            sticker_popup_layout.visibility = View.INVISIBLE
            mainlayout.visibility = View.VISIBLE
        }
    }

}
//work_24_img,best_menu_img,consent_img,park_img,open_time_img,gallery_img
class dragListener(var alldayView: ImageView,var bestmenuView: ImageView,var consentView: ImageView,
                   var parkView: ImageView,
                   var worktimeView: ImageView,var photoView: ImageView,var stickerData: StickerData) : View.OnDragListener{

    override fun onDrag(v: View?, event: DragEvent?): Boolean {

        if(event != null) {
            when(event.action){
                DragEvent.ACTION_DRAG_STARTED -> {
                    if (event.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                        // As an example of what your application might do,
                        // applies a blue color tint to the View to indicate that it can accept
                        // data.

                        // Invalidate the view to force a redraw in the new tint

                        // returns true to indicate that the View can accept the dragged data.
                        return true
                    } else {
                        // Returns false. During the current drag and drop operation, this View will
                        // not receive events again until ACTION_DRAG_ENDED is sent.
                        return false
                    }

                }
                DragEvent.ACTION_DRAG_ENTERED -> {
                    (v as ImageView).setColorFilter(Color.RED)
                    v.invalidate()
                    return true
                }
                DragEvent.ACTION_DRAG_EXITED -> {
                    (v as ImageView).clearColorFilter()
                    v.invalidate()
                    return true

                }
                DragEvent.ACTION_DROP ->{
                    val item : ClipData.Item = event.clipData.getItemAt(0)
                    val data = item.text.toString()
                    v!!.visibility = View.INVISIBLE
                    var view =  event.localState as View
                    view.visibility = View.INVISIBLE

                    when(data){
                        "ConsentView" ->{
                            consentView.clearColorFilter()
                            stickerData.powerPlugAvailable = false
                        }
                        "AlldayView" ->{
                            alldayView.clearColorFilter()
                            stickerData.allDayAvailable = false
                        }
                        "ParkView" ->{
                            parkView.clearColorFilter()
                            stickerData.parkingAvailable = false
                        }
                        "WorktimeView" ->{
                            worktimeView.clearColorFilter()
                            stickerData.open = ""
                            stickerData.close = ""
                        }
                        "BestView" ->{
                            bestmenuView.clearColorFilter()
                            stickerData.bestMenu.clear()
                        }
                        "PhotoView" ->{
                            photoView.clearColorFilter()
                            stickerData.photoUriList!!.clear()
                            stickerData.cloudinaryIdList!!.clear()
                            stickerData.cloudinaryUrlList!!.clear()
                        }
                    }


                    (v as ImageView).clearColorFilter()
                    v.invalidate()
                    return true

                }
                DragEvent.ACTION_DRAG_ENDED -> {
                    (v as ImageView).clearColorFilter()
                    if(v.isVisible){
                        v.visibility = View.INVISIBLE
                    }
                    v.invalidate()
                    when(event.result) {
                        true ->{

                        }
                        else ->{

                        }
                    }

                }
                else -> {
                    // An unknown action type was received.
                    Log.e("DragDrop Example", "Unknown action type received by OnDragListener.")
                    return false
                }

            }
        }
        return true
    }
}