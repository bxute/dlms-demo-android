package com.example.dlms_demo_android

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.nativelib.GrpcClient
import com.example.nativelib.streamobservers.LiveScoreObserver
import com.example.nativelib.streamobservers.LiveScoreObserver.ScoreCallback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.dlms.services.ScoreRequest
import org.dlms.services.ScoreResponse

class ScoreStreamingActivity : AppCompatActivity() {
    private val grpcClient: GrpcClient by lazy { GrpcClient.getInstance() }
    private lateinit var scoreBoard: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_score_streaming)
        scoreBoard = findViewById(R.id.scoreBoard)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private val scores = mutableListOf<ScoreResponse>()
    fun onNewScoreAvailable(score: ScoreResponse) {
        scores.add(score)
        if (scores.size > 10) {
            scores.removeAt(0)
        }
        lifecycleScope.launch {
            withContext(Dispatchers.Main) {
                scoreBoard.text = scores.joinToString("\n") { it.scoreDetail }
            }
        }
    }

    fun onStatusChanged(msg: String) {
        lifecycleScope.launch {
            withContext(Dispatchers.Main) {
                scoreBoard.text = msg
            }
        }
    }

    fun streamLiveScores(view: View) {
        lifecycleScope.launch {
            startStreaming()
        }
    }

    suspend fun startStreaming() {
        withContext(Dispatchers.IO) {
            onStatusChanged("Stream starting soon...")
            grpcClient.requestLiveScores(
                ScoreRequest
                    .newBuilder()
                    .setMatchId(1)
                    .build(),
                LiveScoreObserver(object : ScoreCallback {
                    override fun onScoreUpdate(score: ScoreResponse?) {
                        if (score != null) {
                            onNewScoreAvailable(score)
                        }
                    }

                    override fun onError(t: Throwable?) {
                        t?.printStackTrace()
                        onStatusChanged("Error: ${t?.message}")
                    }

                    override fun onCompleted() {
                        onStatusChanged("Stream completed!")
                    }
                })
            );
        }
    }
}