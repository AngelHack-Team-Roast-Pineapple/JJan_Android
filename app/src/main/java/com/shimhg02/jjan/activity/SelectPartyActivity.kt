package com.shimhg02.jjan.activity

import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.enablex.jjan.R
import com.shimhg02.jjan.network.Socket.SocketCreateActivity
import com.shimhg02.jjan.network.Socket.SocketJoinActivity
import org.jetbrains.anko.startActivity


class SelectPartyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var actionbar = supportActionBar
        actionbar?.hide()
        setContentView(R.layout.activity_select_party)
        val party_start = findViewById<Button>(R.id.party_start)
        val join_start = findViewById<Button>(R.id.join_start)
        party_start.setOnClickListener {
            startActivity<SocketCreateActivity>()
        }
        join_start.setOnClickListener {
            startActivity<SocketJoinActivity>()
        }
    }

}