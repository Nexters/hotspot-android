package com.example.hotspot

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log.d
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.map_view.*
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
        val isNewUser = intent.getBooleanExtra("IsNewUser",false)
        val fr_reg = FragmentRegister()
        val bundle = Bundle()
        bundle.putBoolean("isAdd", isAdd)


        if(!isAdd) {
            myplace = intent.getSerializableExtra("myPlace") as MyPlace
            val requestCode = intent.getIntExtra("RequestCode",0)
            d("TAG RegisterActivity", "isAdd : $isAdd")

            bundle.putSerializable("myPlace", myplace as Serializable)
            bundle.putSerializable("RequestCode", requestCode as Serializable)
            fr_reg.arguments = bundle

            supportFragmentManager!!.beginTransaction()
                .replace(R.id.register_activity, fr_reg)
                .commit()
        }
        else {
            bundle.putSerializable("IsNewUser" ,isNewUser as Serializable)
            fr_reg.arguments = bundle
            supportFragmentManager.beginTransaction()
                .replace(R.id.register_activity, fr_reg)
                .commit()
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == 1) {
            BusProvider.getInstance().post(ActivityResultEvent(requestCode, resultCode, data))
        }
    }

    override fun onBackPressed() {
        var currentFragment = getVisibleFragment()
        if(currentFragment is FragmentSearch){
            supportFragmentManager.popBackStack()
        }
        else if(currentFragment is FragmentRegister){
            if(regist_category_layout.isVisible){
                edtTxt_memo.visibility = View.VISIBLE
                edtTxt_memo.isFocusableInTouchMode = true
                edtTxt_memo.isFocusable = true
                txt_address.visibility = View.VISIBLE
                txt_visited.visibility = View.VISIBLE
                txt_place_name.isClickable = true
                btn_regist.visibility = View.VISIBLE

                stickerBt.visibility = View.VISIBLE
                regist_category_layout.visibility = View.GONE
                return
            }
            else {
                btn_esc3.performClick()
                return
            }
        }


    }
    private fun getVisibleFragment() : Fragment? {
        for(fragment in supportFragmentManager.fragments){
            if(fragment.isVisible){
                return fragment
            }
        }
        return null
    }

}
