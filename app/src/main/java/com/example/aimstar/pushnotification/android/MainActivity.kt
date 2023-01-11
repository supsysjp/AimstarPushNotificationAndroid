package com.example.aimstar.pushnotification.android

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.messaging.FirebaseMessaging
import jp.co.aimstar.push.android.Aimstar


internal const val CUSTOMER_ID = "my_sample_customer_id"
private const val API_KEY = ""

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Aimstar.init(
            context = this@MainActivity,
            apiKey = API_KEY,
            tenantId = "tenantId"
        )

        findViewById<TextView>(R.id.aimstarId).text = "customer id: $CUSTOMER_ID"
        handleIntent(intent)
        findViewById<Button>(R.id.registerButton).apply {
            setOnClickListener {
                getFcmToken()
            }
        }
        findViewById<Button>(R.id.logoutButton).apply {
            setOnClickListener {
                Aimstar.logout(this@MainActivity)
                FirebaseMessaging.getInstance().deleteToken().addOnCompleteListener {
                }
            }
        }
        getFcmToken()
    }

    private fun getFcmToken() {
        FirebaseMessaging.getInstance()
            .token
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val token = it.result
                    Aimstar.registerToken(
                        context = this@MainActivity,
                        customerId = CUSTOMER_ID,
                        fcmToken = token,
                    )
                }
            }
    }

    private fun handleIntent(intent: Intent) {
        val notificationId = intent.extras?.getString("notification_id")
        val targetGroupId = intent.extras?.getString("target_group_id")
        if (notificationId != null && targetGroupId != null) {
            Aimstar.sendLog(
                context = this,
                notificationId = notificationId,
                targetGroupId = targetGroupId,
            )
        }
    }
}
