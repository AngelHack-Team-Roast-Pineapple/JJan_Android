package com.shimhg02.jjan.network.Socket


import android.os.Bundle
import android.util.Log
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.enablex.jjan.R
import com.shimhg02.jjan.activity.DashboardActivity
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.jetbrains.anko.startActivity
import org.json.JSONArray
import org.json.JSONObject


@Suppress("DEPRECATION")
class SocketJoinActivity : AppCompatActivity() {

    lateinit var mSocket: Socket;
    val PREFERENCE = "com.shimhg02.jjan"
    val socketURI = "https://jjan.andy0414.com"
    var roomIdName = getRandomString(30)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var actionbar = supportActionBar
        actionbar?.hide()
        setContentView(R.layout.activity_join)
        try {
            mSocket = IO.socket(socketURI)
            Log.d("success", mSocket.id())

        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("fail", "Failed to connect")
        }
        var join_on = findViewById<EditText>(R.id.matchin_etv)
        var join_btn = findViewById<Button>(R.id.join_btn)
        join_btn.setOnClickListener {
            mSocket.connect()
            mSocket.on(Socket.EVENT_CONNECT, onConnect)
            startMeeting()
        }
    }

    var onConnect = Emitter.Listener {
        val pref = getSharedPreferences(PREFERENCE, MODE_PRIVATE)
        val data = JSONObject()
        var join_on = findViewById<EditText>(R.id.matchin_etv)
        data.put("invitationCode", join_on.text.toString());
        data.put("userToken", pref.getString("userToken",""));
        mSocket.emit("joinRoom",data)
        mSocket.on("joinRoom", onRoomCreate)
    }

    private fun startMeeting() {
        mSocket.on("matchingMeeting", onMatched)
    }

    internal var onMatched: Emitter.Listener = Emitter.Listener { args ->
        runOnUiThread(Runnable {
            val pref = getSharedPreferences(PREFERENCE, MODE_PRIVATE)
            val editor = pref.edit()
            val data = args[0] as JSONArray
            val dataString = data.toString().split("[")
            val dataName = dataString[1].toString().split("]")

            System.out.println("LOGD:" + dataString[1])
            System.out.println("LOGD2:" + dataName[0])
            val splitJson = dataName[0].split(",")
            val roomIDJson =  splitJson[1].split(":")
            val roomIDString = roomIDJson[1].split("\"")
            val roomId = roomIDString[1]
            val meetRoomData = splitJson[0] +"}"
            editor.putString("roomID",roomId)
            editor.apply()
            try {
                mSocket.emit("joinMeeting", JSONObject(dataName[0]))
                Log.d("asdasd", data.toString())
                Toast.makeText(this@SocketJoinActivity,"매칭 성공 입니다.",Toast.LENGTH_SHORT).show()
                startActivity<DashboardActivity>()
            } catch (e: Exception) {
                return@Runnable
            }
        })
    }

    internal var onRoomCreate: Emitter.Listener = Emitter.Listener { args ->
        runOnUiThread(Runnable {
            val pref = getSharedPreferences(PREFERENCE, MODE_PRIVATE)
            val data = args[0] as JSONArray
            try {
                Log.d("asdasd room", data.toString())
                Toast.makeText(this@SocketJoinActivity,"환영합니다.",Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                return@Runnable
            }
        })
    }
    fun getRandomString(length: Int) : String {
        val allowedChars = ('A'..'Z') + ('a'..'z')
        return (1..length)
                .map { allowedChars.random() }
                .joinToString("")
    }

}