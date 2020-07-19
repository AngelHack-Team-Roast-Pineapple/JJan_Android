package com.shimhg02.jjan.activity



import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.enablex.jjan.R
import com.shimhg02.jjan.Util.Bases.BaseActivity
import com.shimhg02.jjan.network.Data.LogIn
import com.shimhg02.jjan.network.Data.Users
import com.shimhg02.jjan.network.Retrofit.Client
import org.jetbrains.anko.startActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * @description 로그인 액티비티
 */


@Suppress("DEPRECATION")
class LoginActivity : BaseActivity() {
    val PREFERENCE = "com.shimhg02.jjan"
    override var viewId: Int = R.layout.activity_login

    override fun onCreate() {
        val pref = getSharedPreferences(PREFERENCE, MODE_PRIVATE)
        System.out.println("token Test1 : " + pref.getString("fbToken", ""))
        System.out.println("token Test2 : " + pref.getString("ggToken", ""))
        val login_btn = findViewById<Button>(R.id.login_btn)
        val signup_go = findViewById<TextView>(R.id.signup_go)
        login_btn.setOnClickListener {
            login()
        }
        signup_go.setOnClickListener {
            startActivity<SignUpActivity>()
        }
    }

    private fun login() {
        val login_btn = findViewById<Button>(R.id.login_btn)
        val id_tv = findViewById<EditText>(R.id.id_tv)
        val pw_tv = findViewById<EditText>(R.id.pw_tv)
        val pref = getSharedPreferences(PREFERENCE, MODE_PRIVATE)
        val editor = pref.edit()
        Client.retrofitService.logIn(id_tv.text.toString(), pw_tv.text.toString()).enqueue(object : Callback<LogIn> {
            override fun onResponse(call: Call<LogIn>?, response: Response<LogIn>?) {
                when (response!!.code()) {
                    200 -> {
                        editor.putString("userToken", response.body()!!.data.toString())
                        System.out.println("LOGD: " +  response.body()!!.data.toString())
                        editor.apply()
                        Client.retrofitService.getLogIn(response.body()!!.data.toString()).enqueue(object : Callback<Users> {
                            override fun onResponse(call: Call<Users>?, response: Response<Users>?) {
                                when (response!!.code()) {
                                    200 -> {
                                        editor.putString("sex", response.body()!!.data.sex.toString())
                                        editor.putString("name", response.body()!!.data.username.toString())
                                        System.out.println("LOGD: " +  response.body()!!.data.sex.toString())
                                        System.out.println("LOGD: " +  response.body()!!.data.username.toString())
                                        editor.apply()
                                        startActivity<SelectPartyActivity>()
                                        finish()
                                    }
                                }
                            }
                            override fun onFailure(call: Call<Users>?, t: Throwable?) {

                            }
                        })
                    }
                    404 -> {
                        login_btn.isClickable = true
                        Toast.makeText(
                                this@LoginActivity,
                                "로그인 실패: PW나 ID를 다시 확인하세요.",
                                Toast.LENGTH_LONG).show()
                    }
                    500 -> {
                        login_btn.isClickable = true
                        Toast.makeText(this@LoginActivity, "서버 점검중입니다. 잠시 후 다시 시도해 주세요.", Toast.LENGTH_LONG).show()
                    }
                }
            }

            override fun onFailure(call: Call<LogIn>?, t: Throwable?) {

            }
        })
    }
}
