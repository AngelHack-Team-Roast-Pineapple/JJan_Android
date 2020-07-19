package com.shimhg02.jjan.network.Socket


import android.R.string
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.enablex.jjan.R
import com.hanks.htextview.fade.FadeTextView
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
    var arrMessages: ArrayList<String> = ArrayList()
    var position = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        var actionbar = supportActionBar
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        actionbar?.hide()
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
        var lottie_layer = findViewById<RelativeLayout>(R.id.lottie_layer)
        var invite_btn = findViewById<Button>(R.id.invite_btn)
        var text_tv = findViewById<TextView>(R.id.text_tv)

        mSocket.connect()
        mSocket.on(Socket.EVENT_CONNECT, onConnect)
        match_btn.setOnClickListener {
            lottie_layer.visibility = View.VISIBLE
            setTextData()
            setTextAnimation()
            setupLottie()
            startMeeting()
            match_btn.visibility = View.INVISIBLE
            invite_btn.visibility = View.INVISIBLE
        }
        val pref = getSharedPreferences(PREFERENCE, MODE_PRIVATE)
        invite_btn.setOnClickListener {
            text_tv.text = pref.getString("invitationCode","")
        }
        text_tv.setOnClickListener {
            val textToCopy = text_tv.text
            val clip = ClipData.newPlainText("RANDOM UUID",textToCopy)
            clipboard.primaryClip = clip
            Toast.makeText(this,"클립보드에 초대코드가 복사되었습니다! 친구에게 보내세요!", Toast.LENGTH_SHORT).show()
        }
    }

    var onConnect = Emitter.Listener {
        val pref = getSharedPreferences(PREFERENCE, MODE_PRIVATE)
        mSocket.emit("createRoom",JSONObject("{roomName: \""+ roomIdName+"\",userToken:\""+ pref.getString("userToken","") + "\"}"))
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
            val meetingName = dataName[0].split(",")
            val meetingNameString = dataName[0].split("\"")
            System.out.println("LOGD MEET ROOM : " + meetingName[0] + "}")
            editor.putString("roomID",roomId)
            editor.putString("meetID",meetingName[0] + "}")
            editor.putString("meetIdStr",meetingNameString[3])
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

    fun setupLottie(){
        var animation_view = findViewById<LottieAnimationView>(R.id.animation_view)
        animation_view.setAnimation("beer.json")
        animation_view.playAnimation()
        animation_view.loop(true)
    }

    fun setTextData(){
        arrMessages.add("처음보는 사람에겐 예의를 지켜주세요!")
        arrMessages.add("두근두근 매칭... 과연 누구랑 될까요?")
        arrMessages.add("개인정보 유출에 조심하세요!")
        arrMessages.add("아 쓸내용이 없다")
        arrMessages.add("옹기잇")
    }

    fun setTextAnimation(){
        var anime_text = findViewById<FadeTextView>(R.id.anime_text)
        anime_text.animateText(arrMessages[position])
        position++
        var handler = Handler()
        handler.postDelayed(object : Runnable {
            override fun run() {
                handler.postDelayed(this, 5000)
                if (position >= arrMessages.size) position = 0
                anime_text.animateText(arrMessages[position])
                position++
            }
        }, 5000)
    }



    internal var onCreateRoom: Emitter.Listener = Emitter.Listener { args ->
        runOnUiThread(Runnable {
            val pref = getSharedPreferences(PREFERENCE, MODE_PRIVATE)
            val editor = pref.edit()
            val data = args[0] as JSONArray
            try {
                Log.d("asdasd TEST", data.toString())
                val inviteNum =  data.toString().split("\"")
                editor.putString("invitationCode", inviteNum[3].toString())
                editor.apply()
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