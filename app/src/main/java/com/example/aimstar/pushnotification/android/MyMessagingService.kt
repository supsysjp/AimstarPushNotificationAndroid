package com.example.aimstar.pushnotification.android

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import jp.co.aimstar.push.android.Aimstar

class MyMessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Aimstar.registerToken(
            context = applicationContext,
            aimstarId = AIMSTAR_ID,
            fcmToken = token,
        )
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        remoteMessage.notification?.let {
            val notificationId = remoteMessage.data["notification_id"]
            val targetGroupId = remoteMessage.data["target_group_id"]
            if (notificationId != null && targetGroupId != null) {
                sendNotification(
                    notification = it,
                    notificationId = notificationId,
                    targetGroupId = targetGroupId,
                )
            }
        }

    }

    private fun sendNotification(
        notification: RemoteMessage.Notification,
        notificationId: String,
        targetGroupId: String,
    ) {
        val intent = Intent(this, MainActivity::class.java)
            .apply {
                putExtra("notification_id", notificationId)
                putExtra("target_group_id", targetGroupId)
            }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        val notificationBuilder = NotificationCompat.Builder(this, NotificationChannelId)
            .setContentTitle(notification.title)
            .setContentText(notification.body)
            .setAutoCancel(true)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)

        createNotificationChannel(this)
        NotificationManagerCompat.from(this)
            .notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(
                NotificationChannel(
                    NotificationChannelId,
                    NotificationChannelName,
                    NotificationManager.IMPORTANCE_DEFAULT,
                )
            )
        }
    }

    companion object {
        private const val NotificationChannelId = "my_notification_channel"
        private const val NotificationChannelName = "通知"
    }
}