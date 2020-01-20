package com.example.hotspot


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Log.d
import androidx.appcompat.app.AppCompatActivity
import com.kakao.auth.AuthType
import com.kakao.auth.ISessionCallback
import com.kakao.auth.Session
import com.kakao.network.ErrorResult
import com.kakao.usermgmt.UserManagement
import com.kakao.usermgmt.callback.MeV2ResponseCallback
import com.kakao.usermgmt.response.MeV2Response
import com.kakao.usermgmt.response.model.User
import com.kakao.util.exception.KakaoException
import kotlinx.android.synthetic.main.activity_login.*
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class LoginActivity: AppCompatActivity(){
    private var callback: SessionCallback = SessionCallback()

    private val URL : String = "http://hotspot-dev-654767138.ap-northeast-2.elb.amazonaws.com"
//    private val URL: String = "http://13.124.166.248"

    //token 변수 전역으로 선언
    var token : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        //interceptor 선언
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient.Builder().addInterceptor(interceptor).build()


        val retrofit = Retrofit.Builder()
            .baseUrl(URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        val api = retrofit.create(APIService::class.java)

        // Get hash key and print

        // 카카오 제공 버튼일 경우
        callback = SessionCallback()
        Session.getCurrentSession().addCallback(callback)
        Session.getCurrentSession().checkAndImplicitOpen()

        btnKakaoLogin.setOnClickListener {
            Session.getCurrentSession().open(AuthType.KAKAO_LOGIN_ALL, this)
        }

        d("TAG", "onCreate() : token = ${this.token}")

        try {
            val paramObject =
                JSONObject()
                .put("kakao_access_token",this.token)


            d("TAG", "paramObject : ${paramObject}")

            api.postToken(paramObject.toString()).enqueue(object : Callback<Token> {
                override fun onResponse(call: Call<Token>, response: Response<Token>) {
                    d("TAG", "onResponse() : ")
                }

                override fun onFailure(call: Call<Token>, t: Throwable) {
                    d("TAG", "onFailure() : ")
                    t.printStackTrace()
                }
            })

        } catch (e: JSONException) {
            e.printStackTrace()
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

            UserManagement.getInstance().me(object : MeV2ResponseCallback() {

                override fun onFailure(errorResult: ErrorResult?) {
                    d("DEBUG","Session Call back :: on failed ${errorResult?.errorMessage}")
                }

                override fun onSessionClosed(errorResult: ErrorResult?) {
                    Log.e("DEBUG","Session Call back :: onSessionClosed ${errorResult?.errorMessage}")

                }

                override fun onSuccess(result: MeV2Response?) {
                    checkNotNull(result) { "session response null" }

                    d("TAG token", "onSuccess() : token = ${Session.getCurrentSession().tokenInfo.accessToken}")

                    // register or login


                    //UserInfo
                    d("DEBUG","result : ${result}")

                    redirectSignupActivity()
                }

            })
        }

    }

    protected fun redirectSignupActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
