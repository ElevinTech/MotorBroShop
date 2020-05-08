package com.elevintech.motorbroshop

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.elevintech.motorbroshop.Chat.ChatListActivity
import com.elevintech.motorbroshop.Chat.ChatLogActivity
import com.elevintech.motorbroshop.Database.MotorBroDatabase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FirebaseMessagingService : FirebaseMessagingService(){

    var channelId = "com.elevintech.motorbro.userchat"

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        MotorBroDatabase().checkIfLoggedIn { isLoggedIn ->

            if(isLoggedIn)
                MotorBroDatabase().updateFcmToken(token)
        }

    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        // Check if message contains a data payload.
        remoteMessage.data.isNotEmpty().let {

            if (remoteMessage.data.containsKey("notificationType")){

                val notificationData = remoteMessage.data

                if (notificationData["notificationType"] == "chat"){
                    val intent = Intent(this, ChatLogActivity::class.java)
                    intent.putExtra("chatRoomId", notificationData["chatRoom"])
                    intent.putExtra("shopId", notificationData["shop"])
                    intent.putExtra("customerId", notificationData["user"])
                    val pendingIntent = PendingIntent.getActivity(this,0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

                    buildNotification( notificationData, pendingIntent )
                }

            }
        }

    }

    private fun buildNotification(notificationData: Map<String, String>, pendingIntent: PendingIntent) {

        createNotificationChannel()

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.motorbroinicon)
            .setContentTitle( notificationData["title"] )
            .setContentText( notificationData["body"] )
            .setDefaults(Notification.DEFAULT_ALL) //Important for heads-up notification
            .setPriority(NotificationCompat.PRIORITY_MAX) //Important for heads-up notification
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setOngoing(false) // set to false to allow dismissing by swipe

        val manager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        manager.notify(0, builder.build())
    }


    private fun createNotificationChannel() {

        val notificationName = "Motor Bro Chats"
        val notificationDescription = "Chats between shops and user"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, notificationName, importance).apply {
                description = notificationDescription
            }

            channel.description = notificationDescription
            channel.setShowBadge(true)
            channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC

            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

}