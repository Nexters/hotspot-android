package com.example.hotspot

import android.Manifest
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.Image
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.provider.MediaStore
import android.view.ActionMode
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.view.isInvisible
import com.google.gson.annotations.SerializedName
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.widget.LocationButtonView
import gun0912.tedbottompicker.TedBottomPicker
import kotlinx.android.synthetic.main.activity_sticker_regist.*
import kotlinx.android.synthetic.main.register_view.*
import java.io.Serializable

class StickerRegistActivity : AppCompatActivity() {
    private lateinit var photoUriList : ArrayList<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sticker_regist)
        photoUriList = arrayListOf()
        //네이버 맵 옵션 설정
        var naverMapOptions = NaverMapOptions()
        naverMapOptions.allGesturesEnabled(false)
        naverMapOptions.zoomControlEnabled(false)


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

        setGridImgListener()

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

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        setStickerPosition()
        setSticKersListener()
    }
    private fun setSticKersListener(){
        consent_img.setOnClickListener{
            consent_fin_view.visibility = View.VISIBLE
            dragView.performClick()
        }
        park_img.setOnClickListener{
            park_fin_view.visibility = View.VISIBLE
            dragView.performClick()
        }
        work_24_img.setOnClickListener{
            h24_fin_view.visibility = View.VISIBLE
            dragView.performClick()
        }
        best_menu_img.setOnClickListener{
            mainlayout.visibility = View.INVISIBLE
            sticker_input_layout.visibility = View.generateViewId()
            dragView.performClick()
            dragView.isClickable = false
        }
    }
    private fun setStickerPosition(){
        consent_fin_view.x = mainlayout.width*0.1f
        consent_fin_view.y = mainlayout.height*0.45f

        park_fin_view.x = mainlayout.width*0.35f
        park_fin_view.y = mainlayout.height*0.62f

        h24_fin_view.x = mainlayout.width*0.6f
        h24_fin_view.y = mainlayout.height*0.3f
    }


    private fun setViewTouchEvent(){
        txt_sticker_regist.setOnClickListener{
            if(photoUriList.size == 0) {
                setResult(2)
                finish()
            }
            else{
                var stickerData = StickerData()
                stickerData.photoUriList = photoUriList
                intent = Intent()
                intent.putExtra("StickerData", stickerData)
                setResult(1,intent)
                finish()
            }
        }
        input_best_menu_view.findViewById<ImageView>(R.id.best_plus_img).setOnClickListener{
            input_best_menu_view.visibility = View.GONE
            input_best_menu_view2.findViewById<TextView>(R.id.up1).text =
                input_best_menu_view.findViewById<AppCompatEditText>(R.id.up1).text.toString()
            input_best_menu_view2.visibility = View.VISIBLE
        }
    }
    //Grid panel의 이미지뷰들의 리스너 정의
    private fun setGridImgListener(){
        gallery_img.setOnClickListener{
            selectAlbum()
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

                            // it 에 선택한 사진 uri 담김
                            if(it.size == 0){//사진을 선택 안했다면
                            }
                            else{
                                /*
                                var bitmap = MediaStore.Images.Media.getBitmap(contentResolver,it.get(0))
                                imgView.setImageBitmap(bitmap)*/
                                for(i in 0..it.size-1){
                                    photoUriList.add(it.get(i).toString())
                                }
                                dragView.performClick()
                            }
                        }
                        .showCameraTile(false)
                        .showGalleryTile(false)
                        .setGalleryTile(R.drawable.star_marker)
                        .setCompleteButtonText("Done")
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

}