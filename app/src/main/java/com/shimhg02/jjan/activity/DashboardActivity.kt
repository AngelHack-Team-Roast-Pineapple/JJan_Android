package com.shimhg02.jjan.activity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.MenuBuilder
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import com.enablex.jjan.R
import com.shimhg02.jjan.ApplicationController
import com.shimhg02.jjan.web_communication.WebCall
import com.shimhg02.jjan.web_communication.WebConstants
import com.shimhg02.jjan.web_communication.WebResponse
import org.json.JSONException
import org.json.JSONObject

class DashboardActivity : AppCompatActivity(), View.OnClickListener, WebResponse {
    private var name: EditText? = null
    private var roomId: EditText? = null
    private var joinRoom: Button? = null
    private var createRoom: Button? = null
    private var token: String? = null
    private var sharedPreferences: SharedPreferences? = null
    private var room_Id: String? = null
    private var radioGroup: RadioGroup? = null
    private var role = "moderator"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dashboard)
        supportActionBar!!.title = "QuickApp"
        sharedPreferences = ApplicationController.sharedPrefs
        setView()
        setClickListener()
        supportActionBar!!.title = "Quick App"
        setSharedPreference()
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.createRoom -> WebCall(this, this, null, WebConstants.getRoomId, WebConstants.getRoomIdCode, false, true).execute()
            R.id.joinRoom -> {
                room_Id = roomId!!.text.toString()
                if (validations()) {
                    validateRoomIDWebCall()
                }
            }
        }
    }

    private fun validations(): Boolean {
        if (name!!.text.toString().isEmpty()) {
            Toast.makeText(this, "Please Enter name", Toast.LENGTH_SHORT).show()
            return false
        } else if (roomId!!.text.toString().isEmpty()) {
            Toast.makeText(this, "Please create Room Id.", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun validateRoomIDWebCall() {
        WebCall(this, this, null, WebConstants.validateRoomId + room_Id, WebConstants.validateRoomIdCode, true, false).execute()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.actions, menu)
        if (menu is MenuBuilder) {
            val menuBuilder = menu
            //            menuBuilder.setOptionalIconsVisible(true);
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_share -> if (!name!!.text.toString().equals("", ignoreCase = true) && !roomId!!.text.toString().equals("", ignoreCase = true)) {
                val shareBody = """Hi,
${name!!.text} has invited you to join room with Room Id ${roomId!!.text}"""
                val sharingIntent = Intent(Intent.ACTION_SEND)
                sharingIntent.type = "text/plain"
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here")
                sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody)
                startActivity(sharingIntent)
            } else {
                Toast.makeText(this, "Please create Room first.", Toast.LENGTH_SHORT).show()
            }
        }
        return super.onOptionsItemSelected(item)
    }


    private fun onVaidateRoomIdSuccess(response: String) {
        Log.e("responsevalidate", response)
        try {
            val jsonObject = JSONObject(response)
            if (jsonObject.optString("result").trim { it <= ' ' }.equals("40001", ignoreCase = true)) {
                Toast.makeText(this, jsonObject.optString("error"), Toast.LENGTH_SHORT).show()
            } else {
                savePreferences()
                roomTokenWebCall
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun onGetTokenSuccess(response: String) {
        Log.e("responseToken", response)
        try {
            val jsonObject = JSONObject(response)
            if (jsonObject.optString("result").equals("0", ignoreCase = true)) {
                token = jsonObject.optString("token")
                Log.e("token", token)
                val intent = Intent(this@DashboardActivity, VideoConferenceActivity::class.java)
                intent.putExtra("token", token)
                intent.putExtra("name", name!!.text.toString())
                startActivity(intent)
            } else {
                Toast.makeText(this, jsonObject.optString("error"), Toast.LENGTH_SHORT).show()
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun onGetRoomIdSuccess(response: String) {
        Log.e("responseDashboard", response)
        try {
            val jsonObject = JSONObject(response)
            room_Id = jsonObject.optJSONObject("room").optString("room_id")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        runOnUiThread { roomId!!.setText(room_Id) }
    }



    private fun setSharedPreference() {
        if (sharedPreferences != null) {
            if (!sharedPreferences!!.getString("name", "").isEmpty()) {
                name!!.setText(sharedPreferences!!.getString("name", ""))
            }
            if (!sharedPreferences!!.getString("room_id", "").isEmpty()) {
                roomId!!.setText(sharedPreferences!!.getString("room_id", ""))
            }
        }
    }

    private fun setClickListener() {
        createRoom!!.setOnClickListener(this)
        joinRoom!!.setOnClickListener(this)
        radioGroup!!.setOnCheckedChangeListener { radioGroup, i ->
            role = if (i == 0) {
                "moderator"
            } else {
                "participant"
            }
        }
    }

    private fun setView() {
        name = findViewById<View>(R.id.name) as EditText
        roomId = findViewById<View>(R.id.roomId) as EditText
        createRoom = findViewById<View>(R.id.createRoom) as Button
        joinRoom = findViewById<View>(R.id.joinRoom) as Button
        radioGroup = findViewById<View>(R.id.radioButtonGroup) as RadioGroup
    }

    private fun jsonObjectToSend(): JSONObject {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("name", "Test Dev Room")
            jsonObject.put("settings", settingsObject)
            jsonObject.put("data", dataObject)
            jsonObject.put("sip", sIPObject)
            jsonObject.put("owner_ref", "fadaADADAAee")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return jsonObject
    }

    private val sIPObject: JSONObject
        private get() = JSONObject()

    private val dataObject: JSONObject
        private get() {
            val jsonObject = JSONObject()
            try {
                jsonObject.put("name", name!!.text.toString())
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            return jsonObject
        }

    private val settingsObject: JSONObject
        private get() {
            val jsonObject = JSONObject()
            try {
                jsonObject.put("description", "Testing")
                jsonObject.put("scheduled", false)
                jsonObject.put("scheduled_time", "")
                jsonObject.put("duration", 50)
                jsonObject.put("participants", 10)
                jsonObject.put("billing_code", 1234)
                jsonObject.put("auto_recording", false)
                jsonObject.put("active_talker", true)
                jsonObject.put("quality", "HD")
                jsonObject.put("wait_moderator", false)
                jsonObject.put("adhoc", false)
                jsonObject.put("mode", "group")
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            return jsonObject
        }

    private val roomTokenWebCall: Unit
        private get() {
            val jsonObject = JSONObject()
            try {
                jsonObject.put("name", name!!.text.toString())
                jsonObject.put("role", role)
                jsonObject.put("user_ref", "2236")
                jsonObject.put("roomId", room_Id)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            if (!name!!.text.toString().isEmpty() && !roomId!!.text.toString().isEmpty()) {
                WebCall(this, this, jsonObject, WebConstants.getTokenURL, WebConstants.getTokenURLCode, false, false).execute()
            }
        }

    private fun savePreferences() {
        val editor = sharedPreferences!!.edit()
        editor.putString("name", name!!.text.toString())
        editor.putString("room_id", room_Id)
        editor.commit()
    }

    override fun onWebResponse(response: String?, callCode: Int) {
        when (callCode) {
            WebConstants.getRoomIdCode -> response?.let { onGetRoomIdSuccess(it) }
            WebConstants.getTokenURLCode -> response?.let { onGetTokenSuccess(it) }
            WebConstants.validateRoomIdCode -> response?.let { onVaidateRoomIdSuccess(it) }
        }
    }
    override fun onWebResponseError(error: String?, callCode: Int) {
        Log.e("errorDashboard", error)
    }
}