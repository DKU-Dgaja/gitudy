package com.takseha.presentation.firebase

import android.content.Intent
import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
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
        super.onMessageReceived(remoteMessage)

        if (remoteMessage.data.isNotEmpty()) {
            // 새로운 알림 수신 시 Broadcast 전송
            Log.d("MyFirebaseMessagingService", remoteMessage.data.toString())
            val intent = Intent("com.takseha.NEW_NOTIFICATION")
            intent.putExtra("data", remoteMessage.data.toString())
            sendBroadcast(intent)
        }
    }
}