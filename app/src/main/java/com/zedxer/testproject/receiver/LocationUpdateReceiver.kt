package com.zedxer.testproject.receiver

import android.R
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.widget.Toast


class LocationUpdateReceiver :BroadcastReceiver(){
    override fun onReceive(context: Context?, intent: Intent?) {
        sendNotification(context)
        setupNotificationChannel(context!!)
        Toast.makeText(context, "Broadcast Received", Toast.LENGTH_LONG).show()
    }
    private fun sendNotification (context: Context?) {

        val mBuilder = NotificationCompat.Builder(context)

        //Create the intent that’ll fire when the user taps the notification//

        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.androidauthority.com/"))
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

        mBuilder.setContentIntent(pendingIntent)

        mBuilder.setSmallIcon(R.drawable.alert_dark_frame)
        mBuilder.setContentTitle("My notification")
        mBuilder.setContentText("Hello World!")

        val mNotificationManager =
            context?.applicationContext?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mNotificationManager.notify(0, mBuilder.build())
    }


    private fun setupNotificationChannel(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                val mChannel = NotificationChannel(
                    "101",
                    "notification-channel",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                mChannel.description = "Something"
                mChannel.setShowBadge(false)
                mChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                mNotificationManager.createNotificationChannel(mChannel)
            }
        }
//
}