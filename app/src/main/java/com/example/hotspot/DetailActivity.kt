package com.example.hotspot

import android.os.Bundle
import android.util.Log.d
import androidx.appcompat.app.AppCompatActivity
import java.io.Serializable

class DetailActivity : AppCompatActivity(){


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val myplace = intent.getSerializableExtra("myPlace") as MyPlace

        val fr_detail = FragmentDetailView()
        val bundle = Bundle()
        bundle.putSerializable("myPlace", myplace as Serializable)
        fr_detail.arguments = bundle

        d("TAG", "DetailActivity(): commit()")
        supportFragmentManager.beginTransaction()
            .replace(R.id.detail_activity, fr_detail)
            .commit()
    }
}