package com.zedxer.testproject.services

import android.R
import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import android.util.Log
import android.widget.Toast
import com.google.android.gms.maps.model.LatLng

class GPSservices : Service() {
    private lateinit var mylocation: Location
    private var circleLat  :Double? = 0.0
    private var circleLong :Double?= 0.0
    private var circleRadiusMeters :Double?= 0.0
    lateinit var context: Context

    @SuppressLint("MissingPermission")
    override fun onBind(intent: Intent?): IBinder? {
      circleLat  = intent?.getDoubleExtra("LAT", 0.0)
      circleLong = intent?.getDoubleExtra("LONG",0.0)
      circleRadiusMeters = intent?.getDoubleExtra("RADIUS",0.0)
        Log.v("SERVICE_INSIDE" , "CIRCLE DATA COLLECTED $circleLat  $circleLong + $circleRadiusMeters")

        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000,
            5f, object : LocationListener {
                override fun onLocationChanged(location: Location) {

                    //Place current location marker
                    val latLng = LatLng(location.latitude, location.longitude)
                    Log.v("SERVICE_INSIDE" , latLng.toString())
                    sendBroadcast(intent)
                    sendNotification(context)
                    setupNotificationChannel(context!!)

                }

                override fun onStatusChanged(s: String, i: Int, bundle: Bundle) {

                }

                override fun onProviderEnabled(s: String) {

                }

                override fun onProviderDisabled(s: String) {

                }
            })


        return LocationServices()
    }

    inner class LocationServices : Binder() {
        fun getLocation() = mylocation
    }

    @SuppressLint("MissingPermission")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        circleLat  = intent?.getDoubleExtra("LAT", 0.0)
        circleLong = intent?.getDoubleExtra("LONG",0.0)
        circleRadiusMeters = intent?.getDoubleExtra("LONG",0.0)
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000,
            5f, object : LocationListener {
                override fun onLocationChanged(location: Location) {

                    val latLng = LatLng(location.latitude, location.longitude)
                    Log.v("SERVICE_INSIDE" , latLng.toString())
                    sendBroadcast(intent)
                    sendNotification(context)
                    setupNotificationChannel(context!!)
                }

                override fun onStatusChanged(s: String, i: Int, bundle: Bundle) {

                }

                override fun onProviderEnabled(s: String) {

                }

                override fun onProviderDisabled(s: String) {

                }
            })

        return START_NOT_STICKY
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

    override fun onCreate() {
        super.onCreate()
        context = this

    }
}