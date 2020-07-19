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
class SignUpInfoActivity : BaseActivity() {
    val PREFERENCE = "com.shimhg02.jjan"
    override var viewId: Int = R.layout.activity_singup_info

    override fun onCreate() {
        val pref = getSharedPreferences(PREFERENCE, MODE_PRIVATE)
        val editor = pref.edit()
        System.out.println("token Test1 : " + pref.getString("fbToken", ""))
        System.out.println("token Test2 : " + pref.getString("ggToken", ""))
        val login_btn = findViewById<Button>(R.id.login_btn)
        val nick_tv = findViewById<EditText>(R.id.nick_tv)
        val sex_tv = findViewById<EditText>(R.id.sex_tv)
        val power_tv = findViewById<EditText>(R.id.power_tv)
        val imotion_tv = findViewById<EditText>(R.id.imotion_tv)


        login_btn.setOnClickListener {
            Client.retrofitService.signUp(pref.getString("id",""),pref.getString("pw",""),sex_tv.text.toString(),imotion_tv.text.toString()).enqueue(object : Callback<LogIn> {
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
                                            startActivity<LoginActivity>()
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
                                    this@SignUpInfoActivity,
                                    "로그인 실패: PW나 ID를 다시 확인하세요.",
                                    Toast.LENGTH_LONG).show()
                        }
                        500 -> {
                            login_btn.isClickable = true
                            Toast.makeText(this@SignUpInfoActivity, "서버 점검중입니다. 잠시 후 다시 시도해 주세요.", Toast.LENGTH_LONG).show()
                        }
                    }
                }

                override fun onFailure(call: Call<LogIn>?, t: Throwable?) {

                }
            })
        }
    }


}
