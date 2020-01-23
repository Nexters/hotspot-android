package com.example.hotspot

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_list.*

class RVActivity :AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        // 리사이클러뷰 테스트 리스트
        var list = mutableListOf(
            VOClass("민병찬", 26),
            VOClass("최정헌", 26),
            VOClass("서희수", 24),
            VOClass("김세준", 28)
        )

        recycler_view.setHasFixedSize(true)
        recycler_view.layoutManager = LinearLayoutManager(MainActivity())
        recycler_view.adapter = RecyclerAdapter(list)

        btn_esc.setOnClickListener {
            finish()
        }

    }
}