package com.dionbalerr.ocrtest

import android.app.Activity
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat

class ScreenCaptureService : Service() {
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int
    {
        Log.i("DEBUGGING TOOL", "ScreenCaptureService onStartCommand")
        val resultCode = intent?.getIntExtra("resultCode", Activity.RESULT_CANCELED) ?: 0
        val data = intent?.getParcelableExtra<Intent>("data")

        Log.i("DEBUGGING TOOL", "resultcode: $resultCode")
        Log.i("DEBUGGING TOOL", "data: $data")

        if (resultCode == Activity.RESULT_OK && data != null)
        {
            val notification = createNotification()
            startForeground(1, notification)

            val projectionManager =
                getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager

            val mediaProjection = projectionManager.getMediaProjection(resultCode, data)

            return START_STICKY
        }

        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotification(): Notification
    {
        val channelId = "screen_capture_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            val chan = NotificationChannel(
                channelId,
                "Screen Capture",
                NotificationManager.IMPORTANCE_LOW
            )
            getSystemService(NotificationManager::class.java)
                .createNotificationChannel(chan)
        }
        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Screen Capture Running")
            .setContentText("Your screen is being captured")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()
    }


}