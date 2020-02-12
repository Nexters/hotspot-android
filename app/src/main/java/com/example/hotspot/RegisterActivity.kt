package com.example.hotspot

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log.d
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.register_view.*
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.Serializable

class RegisterActivity : AppCompatActivity() {

    private lateinit var myplace : MyPlace

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val isAdd = intent.getBooleanExtra("isAdd",true) // true면 등록 , false면 수정

        val fr_reg = FragmentRegister()
        val bundle = Bundle()
        bundle.putBoolean("isAdd", isAdd)

        if(!isAdd) {
            myplace = intent.getSerializableExtra("myPlace") as MyPlace
            d("TAG RegisterActivity", "isAdd : $isAdd")

            bundle.putSerializable("myPlace", myplace as Serializable)
            fr_reg.arguments = bundle

            supportFragmentManager!!.beginTransaction()
                .replace(R.id.register_activity, fr_reg)
                .commit()
        }

        else {

            fr_reg.arguments = bundle
            supportFragmentManager.beginTransaction()
                .replace(R.id.register_activity, fr_reg)
                .commit()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        BusProvider.getInstance().post(ActivityResultEvent(requestCode, resultCode, data))

    }

    override fun onBackPressed() {
        //일단 벡 버튼 막아놓음
        // 정말 돌아가시겠슴니까? 팝업창 띄워야함
    }

}
