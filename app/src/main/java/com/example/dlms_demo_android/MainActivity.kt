package com.example.dlms_demo_android

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.nativelib.TrackingEventClient

class MainActivity : AppCompatActivity() {
    private lateinit var tracker: TrackingEventClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        tracker = TrackingEventClient.getInstance();

        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    fun trackAction(view: View) {
        tracker.trackActionEvent()
    }

    fun trackActionStream(view: View) {
        repeat(100) {
            tracker.trackActionEvent()
        }
    }
    fun trackImpression(view: View) {
        tracker.trackImpressionEvent()
    }
    fun trackImpressionStream(view: View) {
        repeat(100) {
            tracker.trackImpressionEvent()
        }
    }
}