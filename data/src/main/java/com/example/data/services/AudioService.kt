package com.example.data.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.data.R
import java.io.ByteArrayInputStream


class AudioService: Service() {

    private val mediaSessionCompat: MediaSessionCompat by lazy {
        MediaSessionCompat(applicationContext, "tag")
    }
    private val intentPlay: Intent by lazy {
        Intent(applicationContext, NotificationActionService::class.java)
            .setAction("actionplay")
    }
    private val pendingIntentPlay: PendingIntent by lazy {
        PendingIntent.getBroadcast(
            applicationContext, 0,
            intentPlay, PendingIntent.FLAG_UPDATE_CURRENT
        )
    }
    private val intentPrevious: Intent by lazy {
        Intent(applicationContext, NotificationActionService::class.java)
            .setAction("actionprevious")
    }
    private val pendingIntentPrevious: PendingIntent by lazy {
        PendingIntent.getBroadcast(
            applicationContext, 0,
            intentPrevious, PendingIntent.FLAG_UPDATE_CURRENT
        )
    }
    private val intentNext: Intent by lazy {
        Intent(applicationContext, NotificationActionService::class.java)
            .setAction("actionnext")
    }
    private val pendingIntentNext: PendingIntent by lazy {
        PendingIntent.getBroadcast(
            applicationContext, 0,
            intentNext, PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private val broadcastReceiver: BroadcastReceiver by lazy {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                when (intent.extras!!.getString("actionname")) {
                    "actionprevious" -> {
                    }//onTrackPrevious()
                    "actionplay" -> {
                    }/*if (isPlaying) {
                            onTrackPause()
                        } else {
                            onTrackPlay()
                        }*/
                    "actionnext" -> {
                    } //onTrackNext()
                }
            }
        }
    }


    override fun onCreate() {
        super.onCreate()
        createNotificationChanel()
        registerReceiver(broadcastReceiver, IntentFilter("TRACKS_TRACKS"))
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            val data = it.getStringExtra(DATA_EXTRA)
            val name = it.getStringExtra(NAME_EXTRA) ?: "Unknown"
            val bitmapByteArray = it.getByteArrayExtra(BITMAP_EXTRA) ?: byteArrayOf()
            val byteArrayInputStream = ByteArrayInputStream(bitmapByteArray)
            val bitmap = BitmapFactory.decodeStream(byteArrayInputStream)
            Log.d("t12", bitmapByteArray.toString())
            startForeground(NOTIFICATION_ID, createNotification(name, bitmap))
        } ?: stopSelf()
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(broadcastReceiver)
    }

    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }


    private fun createNotificationChanel(){
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                CHANNEL_ONE, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH,
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun createNotification(name: String, bitmap: Bitmap) = NotificationCompat.Builder(this, CHANNEL_ONE)
        .setContentTitle(name)
        .setContentText(BLANK)
        .setLargeIcon(bitmap)
        .setSmallIcon(R.drawable.ic_mus)
        .addAction(android.R.drawable.ic_media_previous, "Previous", pendingIntentPrevious)
        .addAction(android.R.drawable.ic_media_play, "Play", pendingIntentPlay)
        .addAction(android.R.drawable.ic_media_next, "Next", pendingIntentNext)
        .setStyle(androidx.media.app.NotificationCompat.MediaStyle()
            .setShowActionsInCompactView(0,1,2)
            .setMediaSession(mediaSessionCompat.sessionToken))
        .build()

    companion object {

        private const val CHANNEL_ONE = "CHANNEL_ONE"
        private const val CHANNEL_NAME = "Music"
        private const val NOTIFICATION_ID = 215
        private const val DATA_EXTRA = "extra_data"
        private const val NAME_EXTRA = "extra_name"
        private const val BITMAP_EXTRA = "extra_bitmap"
        private const val BLANK = ""

        fun newIntent(context: Context, data: String, name: String, byteArray: ByteArray): Intent {
            return Intent(context, AudioService::class.java).apply {
                putExtra(DATA_EXTRA, data)
                putExtra(NAME_EXTRA, name)
                putExtra(BITMAP_EXTRA, byteArray)
            }
        }

    }

}