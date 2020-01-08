package com.example.hotspot

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle


import com.kakao.auth.ISessionCallback
import com.kakao.auth.Session
import com.kakao.network.ErrorResult
import com.kakao.usermgmt.UserManagement

import com.kakao.usermgmt.callback.MeV2ResponseCallback
import com.kakao.usermgmt.response.MeV2Response
import com.kakao.util.exception.KakaoException

import android.util.Log



class LoginActivity: AppCompatActivity() {
    private var callback: SessionCallback = SessionCallback()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        // Get hash key and print


        // 카카오 제공 버튼일 경우
        Session.getCurrentSession().addCallback(callback)

    }

    override fun onDestroy() {
        super.onDestroy()
        Session.getCurrentSession().removeCallback(callback)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            Log.d("DEBUG","session get current session")
            return
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    private class SessionCallback : ISessionCallback {
        override fun onSessionOpenFailed(exception: KakaoException?) {
            Log.e("WARN","Session Call back :: onSessionOpenFailed: ${exception?.message}")
        }

        override fun onSessionOpened() {
            Session.getCurrentSession().tokenInfo.accessToken
            UserManagement.getInstance().me(object : MeV2ResponseCallback() {

                override fun onFailure(errorResult: ErrorResult?) {
                    Log.d("DEBUG","Session Call back :: on failed ${errorResult?.errorMessage}")
                }

                override fun onSessionClosed(errorResult: ErrorResult?) {
                    Log.e("DEBUG","Session Call back :: onSessionClosed ${errorResult?.errorMessage}")

                }

                override fun onSuccess(result: MeV2Response?) {
                    checkNotNull(result) { "session response null" }
                    // register or login
                    Log.d("DEBUG","result : ${result}")

                }

            })
        }

    }
}
