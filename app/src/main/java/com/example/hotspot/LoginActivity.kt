package com.example.hotspot

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.util.Log.d
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.kakao.auth.AuthType
import com.kakao.auth.ISessionCallback
import com.kakao.auth.Session
import com.kakao.network.ErrorResult
import com.kakao.usermgmt.UserManagement
import com.kakao.usermgmt.callback.MeV2ResponseCallback
import com.kakao.usermgmt.response.MeV2Response
import com.kakao.util.exception.KakaoException
import kotlinx.android.synthetic.main.activity_login.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.Serializable


class LoginActivity: AppCompatActivity(){
    private var callback: SessionCallback = SessionCallback()

    //token 변수 전역으로 선언
    var token : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        login_lottie_view.setAnimation("login charac.json")
        login_lottie_view.repeatCount = 50
        login_lottie_view.playAnimation()


        // Get hash key and print

        // 카카오 제공 버튼일 경우
        callback = SessionCallback()
        Session.getCurrentSession().addCallback(callback)
        Session.getCurrentSession().checkAndImplicitOpen()

        //카카오 로그인 버튼 클릭
        btnKakaoLogin.setOnClickListener {
            Session.getCurrentSession().open(AuthType.KAKAO_LOGIN_ALL, this)
        }



    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (Session.getCurrentSession().handleActivityResult(
                requestCode,
                resultCode,
                data)) {
            d("DEBUG","session get current session")
            return
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onDestroy() {
        super.onDestroy()
        Session.getCurrentSession().removeCallback(callback)
    }

    private inner class SessionCallback : ISessionCallback {

        override fun onSessionOpenFailed(exception: KakaoException?) {
            Log.e("WARN","Session Call back :: onSessionOpenFailed: ${exception?.message}")
        }

        override fun onSessionOpened() {

            //token 받아오기
            token = Session.getCurrentSession().tokenInfo.accessToken

            //interceptor 선언
            val interceptor = HttpLoggingInterceptor()
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            val client = OkHttpClient.Builder().addInterceptor(interceptor).build()


            val retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()

            val api = retrofit.create(APIService::class.java)
            // token post 동작
            try {
                val jsonToken = Token(token)

                api.postToken(jsonToken).enqueue(object : Callback<AccessToken> {
                    override fun onResponse(call: Call<AccessToken>,
                                            response: Response<AccessToken>
                    ) {
                        if(response.isSuccessful) {
                            d("TAG", "onResponse()")
                            d("TAG", "responseBody : ${response.body()}")

                            //access_token, sign_up value
                            //new_sign_up이 true면 신규 token을 의미
                            val prefrenceInfo = AccessToken(
                                response.body()!!.access_token,
                                response.body()!!.new_sign_up
                            )


                            // SharedPreference 사용해서 앱 내부에 토큰 저장
                            GlobalApplication.prefs.savePreferences(prefrenceInfo.access_token)
                            d("TAG", "pref.getPreferences : ${GlobalApplication.prefs.getPreferences()}")

                            redirectSignupActivity()

                        }
                    }

                    override fun onFailure(call: Call<AccessToken>, t: Throwable) {
                        d("TAG", "onFailure() : ")
                        t.printStackTrace()
                    }
                })

            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }

    }

    protected fun redirectSignupActivity() {
        val intent = Intent(this, MainActivity::class.java)
        val isnewUser = true
        intent.putExtra("IsNewUser",isnewUser as Serializable)
        startActivity(intent)
        login_lottie_view.cancelAnimation()
        finish()
    }

}
