package com.takseha.presentation.firebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.takseha.presentation.ui.auth.SocialLoginCompleteActivity
import kotlinx.coroutines.tasks.await

class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("MyFirebaseMessagingService", "onNewToken: $token")
        // 서버로 새 token 보내는 api 호출
    }

    companion object {
        suspend fun getFirebaseToken(): String? {
            return try {
                FirebaseMessaging.getInstance().token.await()
            } catch (e: Exception) {
                Log.w("MyFirebaseMessagingService", "FCM registration token 발급 실패", e)
                null
            }
        }
    }

    // 포그라운드 메시지 수신
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d("MyFirebaseMessagingService", "From: " + remoteMessage!!.from)
        Log.d("MyFirebaseMessagingService", "Message data: ${remoteMessage.data}")
        Log.d("MyFirebaseMessagingService", "Message noti: ${remoteMessage.notification}")

        if (remoteMessage.data.isNotEmpty()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                sendNotification(remoteMessage)
            }
        } else {
            Log.e("MyFirebaseMessagingService", "data가 비어있습니다. 메시지를 수신하지 못했습니다.")
        }
    }

    private fun sendNotification(remoteMessage: RemoteMessage) {
        Log.d("MyFirebaseMessagingService", "onMessageReceived: $remoteMessage")
        remoteMessage.notification?.apply {
            val intent = Intent(
                this@MyFirebaseMessagingService,
                SocialLoginCompleteActivity::class.java
            ).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent = PendingIntent.getActivity(
                this@MyFirebaseMessagingService,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val defaultChannel = createDefaultChannel()
                val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(defaultChannel)

                val notificationBuilder =
                    NotificationCompat.Builder(this@MyFirebaseMessagingService, defaultChannel.id)

                val notification = notificationBuilder.apply {
                    setSmallIcon(android.R.drawable.ic_dialog_info)
                    setContentTitle(title)
                    setContentText(body)
                    setContentIntent(pendingIntent)
                    setAutoCancel(true)
                }.build()

                notificationManager.notify(9999, notification)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createDefaultChannel(): NotificationChannel {
        val channelId = "DefaultAlarm"
        return NotificationChannel(
            "default_channel_id",
            channelId,
            NotificationManager.IMPORTANCE_DEFAULT
        )
    }

    // TODO : 추후 백그라운드 메시지 수신 로직 구현
}