package com.example.hotspot

import android.Manifest
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.provider.MediaStore
import android.view.ActionMode
import android.view.MotionEvent
import android.widget.Toast
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.NaverMapOptions
import com.naver.maps.map.OnMapReadyCallback
import gun0912.tedbottompicker.TedBottomPicker
import kotlinx.android.synthetic.main.activity_sticker_regist.*

class StickerRegistActivity : AppCompatActivity() {
    private lateinit var photoUriList : ArrayList<Uri>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sticker_regist)

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
                    val cameraUpdate = CameraUpdate.scrollTo(LatLng(37.527180, 126.932794)) //해당 위치로 카메라 시점 이동(위치 넘겨받기)
                    it.moveCamera(cameraUpdate)
                    var nmo = NaverMapOptions()
                    nmo.scrollGesturesEnabled(false)
                })
                fm.beginTransaction().add(R.id.mapframe, it).commit()
            }


        setGridImgListener()


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