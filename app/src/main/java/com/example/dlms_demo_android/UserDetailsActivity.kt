package com.example.dlms_demo_android

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.nativelib.GrpcClient
import io.grpc.stub.StreamObserver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.dlms.services.UserRequest
import org.dlms.services.UserResponse

class UserDetailsActivity : AppCompatActivity() {
    private lateinit var userDetailsTv: TextView
    private val grpcClient: GrpcClient by lazy { GrpcClient.getInstance() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_user_details)
        userDetailsTv = findViewById(R.id.userDetailsTv)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    fun updateUserDetails(userResponse: UserResponse) {
        Log.d("UserDetailsActivity", "updateUserDetails: on Thread ${Thread.currentThread().name}")
        lifecycleScope.launch {
            withContext(Dispatchers.Main) {
                Log.d("UserDetailsActivity", "updating view : on Thread ${Thread.currentThread().name}")
                userDetailsTv.text = """
                ${userResponse.userId}
                ${userResponse.name}
                ${userResponse.email}
        """.trimIndent()
            }
        }
    }

    fun fetchUserDetails(view: View) {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                grpcClient.fetchUserDetails(
                    UserRequest.newBuilder().setUserId("userId2").build(),
                    object :
                        StreamObserver<UserResponse> {
                        override fun onNext(value: UserResponse?) {
                            Log.d("UserDetailsActivity", "onNext: ${value.toString()}")
                            value?.let { updateUserDetails(userResponse = it) }
                        }

                        override fun onError(t: Throwable?) {
                            t?.printStackTrace()
                            if (t != null) {
                                Log.d("UserDetailsActivity", "onError: ${t.localizedMessage}")
                            }
                        }

                        override fun onCompleted() {
                            Log.d("UserDetailsActivity", "onCompleted: ")
                        }
                    })
            }
        }
    }
}