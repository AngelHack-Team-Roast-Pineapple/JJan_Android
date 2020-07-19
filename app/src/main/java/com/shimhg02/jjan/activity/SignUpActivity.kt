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
class SignUpActivity : BaseActivity() {
    val PREFERENCE = "com.shimhg02.jjan"
    override var viewId: Int = R.layout.activity_signup

    override fun onCreate() {
        val pref = getSharedPreferences(PREFERENCE, MODE_PRIVATE)
        val editor = pref.edit()
        System.out.println("token Test1 : " + pref.getString("fbToken", ""))
        System.out.println("token Test2 : " + pref.getString("ggToken", ""))
        val login_btn = findViewById<Button>(R.id.login_btn)
        val id_tv = findViewById<EditText>(R.id.id_tv)
        val pw_tv = findViewById<EditText>(R.id.pw_tv)


        login_btn.setOnClickListener {
            editor.putString("id",id_tv.text.toString())
            editor.putString("pw",pw_tv.text.toString())
            editor.apply()
            startActivity<SignUpInfoActivity>()
            finish()
        }
    }
}
