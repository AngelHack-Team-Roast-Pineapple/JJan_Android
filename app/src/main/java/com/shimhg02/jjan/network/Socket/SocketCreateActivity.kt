package com.shimhg02.jjan.network.Socket


import android.R.string
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.enablex.jjan.R
import com.shimhg02.jjan.GameActivity
import com.shimhg02.jjan.activity.DashboardActivity
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.jetbrains.anko.startActivity
import org.json.JSONArray
import org.json.JSONObject
import java.net.URISyntaxException
import java.util.*
import kotlin.collections.ArrayList


@Suppress("DEPRECATION")
class SocketCreateActivity : AppCompatActivity() {

    lateinit var mSocket: Socket;
    val PREFERENCE = "com.shimhg02.jjan"
    val socketURI = "https://jjan.andy0414.com"
    var roomIdName = getRandomString(30)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_socket)
        try {
            mSocket = IO.socket(socketURI)
            Log.d("success", mSocket.id())

        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("fail", "Failed to connect")
        }
        var match_btn = findViewById<Button>(R.id.match_btn)
        mSocket.connect()
        mSocket.on(Socket.EVENT_CONNECT, onConnect)
        match_btn.setOnClickListener {
            startMeeting()
        }
    }

    var onConnect = Emitter.Listener {
        mSocket.emit("createRoom",JSONObject("{roomName: \""+ roomIdName+"\",userToken: \"Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VySUQiOiJzaGltaGcwMiIsInBhc3N3b3JkIjoiRXVWTHlxY0wySGpOdXpRVXdKdGxMQk9DZVVTdEE4VS9HN0E2bnpXYWwreHBkWG5mc09vbWVISGRwQy9CdG1DUkRqM1pFVG9uSXVkUkNkaEtwYmpNVWc9PSIsImxhc3RMb2dpblRpbWUiOiIyMDIwLTA3LTE4VDAzOjM5OjU1LjYzN1oifQ.rQ8DYUZyE_Vguf7b84SGQ6RqX7YprG0q_s3Ccom4cZg\"}"))
        mSocket.on("createRoom", onCreateRoom)
    }

    private fun startMeeting() {
        mSocket.emit("matchingMeeting", JSONObject("{roomName:\""+roomIdName+"\"}"))
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
                Log.d("asdasd", roomId)
                Toast.makeText(this@SocketCreateActivity,"매칭 성공 입니다.",Toast.LENGTH_SHORT).show()
                startActivity<DashboardActivity>()
            } catch (e: Exception) {
                return@Runnable
            }
        })
    }

    internal var onCreateRoom: Emitter.Listener = Emitter.Listener { args ->
        runOnUiThread(Runnable {
            val pref = getSharedPreferences(PREFERENCE, MODE_PRIVATE)
            val data = args[0] as JSONArray
            try {
                Log.d("asdasd", data.toString())
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