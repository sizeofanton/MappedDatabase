package com.sizeofanton.mappeddatabaseandroid.data.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.sizeofanton.mappeddatabaseandroid.R
import com.sizeofanton.mappeddatabaseandroid.data.local.CurrentUser
import com.sizeofanton.mappeddatabaseandroid.data.local.EncryptedPreferences
import com.sizeofanton.mappeddatabaseandroid.data.remote.NetworkService
import org.koin.core.KoinComponent
import org.koin.core.inject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val CHANNEL_ID = "firebaseChannel"
class MessagingService: FirebaseMessagingService(), KoinComponent {

    private var channelCreated = false
    private var notificationId = -1

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        EncryptedPreferences.getInstance(this).putString("fb_token", token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        if (!enableNotification) return
        if (remoteMessage.notification != null) {
            if (!channelCreated) createNotificationChannel()
            val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(remoteMessage.notification?.title)
                .setContentText(remoteMessage.notification?.body)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build()

            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                .notify(notificationId, notification)
            notificationId++
        }

    }

    companion object {
        fun getToken(context: Context): String {
            return EncryptedPreferences.getInstance(context).getString("fb_token")
        }

        var enableNotification: Boolean = true
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "defaultChannel"
            val descriptionText = "Default channel for app notifications"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }

            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).apply {
                createNotificationChannel(channel)
            }
        }
        channelCreated = true
    }



}