package com.mr_17.vidconnect.service

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.mr_17.vidconnect.utils.Constants.NODE_UID
import javax.inject.Inject

class ServiceRepository @Inject constructor(
    private val context: Context
) {
    fun startService(uId: String) {
        Thread {
            val intent = Intent(context, Service::class.java)
            intent.putExtra(NODE_UID, uId)
            intent.action = ServiceActions.START_SERVICE.name
            startServiceIntent(intent)
        }.start()
    }

    private fun startServiceIntent(intent: Intent) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }

    fun setupViews(isVideoCall: Boolean, isCaller: Boolean, targetId: String) {
        val intent = Intent(context, Service::class.java)
        intent.apply {
            action = ServiceActions.SETUP_VIEWS.name
            putExtra("isVideoCall", isVideoCall)
            putExtra("isCaller", isCaller)
            putExtra("targetId", targetId)
        }
        startServiceIntent(intent)
    }
}