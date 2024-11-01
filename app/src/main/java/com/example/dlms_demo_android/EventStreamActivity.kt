package com.example.dlms_demo_android

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.nativelib.EventTracker
import org.dlms.services.ActionEvent
import org.dlms.services.ImpressionEvent

class EventStreamActivity : AppCompatActivity() {
    private lateinit var tracker: EventTracker
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_event_stream)
        tracker = EventTracker.getInstance()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    fun trackAction(view: View) {
        val event = ActionEvent
            .newBuilder()
            .setEventId(System.currentTimeMillis().toString())
            .setActionType("click")
            .setTimestamp(System.currentTimeMillis())
            .setUserId("ankit1")
            .build()
        tracker.trackActionEvent(event)
    }

    fun trackActionStream(view: View) {
        repeat(100) {
            trackAction(view)
        }
    }

    fun trackImpression(view: View) {
        val event = ImpressionEvent
            .newBuilder()
            .setTimestamp(System.currentTimeMillis())
            .setImpressionId("")
            .setUserId("ankit1")
            .setTimestamp(System.currentTimeMillis())
            .build()
        tracker.trackImpressionEvent(event)
    }

    fun trackImpressionStream(view: View) {
        repeat(100) {
            trackImpression(view)
        }
    }
}