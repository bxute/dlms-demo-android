package com.example.dlms_demo_android

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.nativelib.EventTracker
import org.dlms.services.ActionEvent
import org.dlms.services.ImpressionEvent

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    fun openEventStreaming(view: View) {
        startActivity(Intent(this, EventStreamActivity::class.java))
    }
    fun openScoreStreaming(view: View) {
        startActivity(Intent(this, ScoreStreamingActivity::class.java))
    }
    fun openChatStreaming(view: View) {
        startActivity(Intent(this, ChatStreamingActivity::class.java))
    }
    fun fetchUserDetails(view: View) {
        startActivity(Intent(this, UserDetailsActivity::class.java))
    }
}