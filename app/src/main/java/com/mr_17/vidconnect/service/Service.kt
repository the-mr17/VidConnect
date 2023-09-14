package com.mr_17.vidconnect.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.mr_17.vidconnect.R
import com.mr_17.vidconnect.service.ServiceActions.*
import com.mr_17.vidconnect.utils.Constants.NODE_UID
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class Service : Service(){
    private var isServiceRunning = false
    private var uId: String? = null

    private lateinit var notificationManager: NotificationManager

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(NotificationManager::class.java)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {incomingIntent ->
            when(incomingIntent.action) {
                START_SERVICE.name -> handleStartService(incomingIntent)
                else -> Unit
            }
        }

        return START_STICKY
    }

    private fun handleStartService(incomingIntent: Intent) {
        if(!isServiceRunning) {
            isServiceRunning = true
            uId = incomingIntent.getStringExtra(NODE_UID)
            startServiceWithNotification()

            // setup my clients
        }
    }

    private fun startServiceWithNotification() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                "channel1",
                "foreground",
                NotificationManager.IMPORTANCE_HIGH
            )

            notificationManager.createNotificationChannel(notificationChannel)

            val notification = NotificationCompat.Builder(
                this,
                "channel1"
            ).setSmallIcon(R.mipmap.ic_launcher)

            startForeground(1, notification.build())
        }
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
}