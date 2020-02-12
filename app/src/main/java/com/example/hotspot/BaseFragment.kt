package com.example.hotspot

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

open class BaseFragment : Fragment() {
    private var stateRegister = false
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(stateRegister == false) {
            BusProvider.getInstance().register(this)
            stateRegister = true
        }
    }

    override fun onDestroy() {
        //Fragment에서 startActivityForresult 하고 결과 받기 위해
        //ActivityResult 이벤트 감지 하기 위한 작업  register/unregister
        if(stateRegister == true) {
            BusProvider.getInstance().unregister(this)
            stateRegister = false
        }
        super.onDestroy()
    }
}