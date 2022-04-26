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
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import com.example.data.R
import com.example.data.models.AudioModel
import java.io.File


class AudioService: Service() {

    private val binder: IBinder = LocalBinder()

    private val mediaSessionCompat: MediaSessionCompat by lazy {
        MediaSessionCompat(applicationContext, "tag")
    }
    private val intentPlay: Intent by lazy {
        Intent(applicationContext, NotificationActionService::class.java)
            .setAction(ACTION_AUDIO_PLAY)
    }
    private val pendingIntentPlay: PendingIntent by lazy {
        PendingIntent.getBroadcast(
            applicationContext, 0,
            intentPlay, PendingIntent.FLAG_UPDATE_CURRENT
        )
    }
    private val intentPrevious: Intent by lazy {
        Intent(applicationContext, NotificationActionService::class.java)
            .setAction(ACTION_AUDIO_PRV)
    }
    private val pendingIntentPrevious: PendingIntent by lazy {
        PendingIntent.getBroadcast(
            applicationContext, 0,
            intentPrevious, PendingIntent.FLAG_UPDATE_CURRENT
        )
    }
    private val intentNext: Intent by lazy {
        Intent(applicationContext, NotificationActionService::class.java)
            .setAction(ACTION_AUDIO_NEXT)
    }
    private val pendingIntentNext: PendingIntent by lazy {
        PendingIntent.getBroadcast(
            applicationContext, 0,
            intentNext, PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private val intentClose: Intent by lazy {
        Intent(applicationContext, NotificationActionService::class.java)
            .setAction(ACTION_AUDIO_CLOSE)
    }
    private val pendingIntentClose: PendingIntent by lazy {
        PendingIntent.getBroadcast(
            applicationContext, 0,
            intentClose, PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private val broadcastReceiver: BroadcastReceiver by lazy {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                when (intent.extras!!.getString(ACTION_AUDIO_KEY)) {
                    ACTION_AUDIO_PRV -> {
                        onTrackPrevious()
                    }
                    ACTION_AUDIO_PLAY -> {
                        onTrackPause()
                    }
                    ACTION_AUDIO_NEXT -> {
                        onTrackNext()
                    }
                    ACTION_AUDIO_CLOSE -> {
                        audioPlayerClose()
                    }
                }
            }
        }
    }

    private var mediaPlayer = MediaPlayer()
    private var selectedTrack = -1
    private var audioList = listOf<AudioModel>()
    private var playOrPauseDrawable = android.R.drawable.ic_media_pause
    var changePlayPauseButton: ((Boolean) -> Unit)? = null
    var stopServiceAndHideMusicBar: (() -> Unit)? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChanel()
        registerReceiver(broadcastReceiver, IntentFilter(AUDIO_INTENT_FILTER))
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(broadcastReceiver)
    }

    inner class LocalBinder : Binder() {
        fun getService(): AudioService = this@AudioService
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    fun audioPlayerClose(){
        stopServiceAndHideMusicBar?.invoke()
        mediaPlayer.stop()
        mediaPlayer = MediaPlayer()
        selectedTrack = -1
        stopForeground(true)
        stopSelf()
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

    fun setupAudioList(list: List<AudioModel>){
        audioList = list
    }

    fun onTrackPlay(position: Int) {
        if(selectedTrack != position) {
            mediaPlayer.stop()
            selectedTrack = position
            mediaPlayer = MediaPlayer.create(applicationContext, File(audioList[position].audioData).toUri())
            mediaPlayer.start()
            playOrPauseDrawable = android.R.drawable.ic_media_pause
            changePlayPauseButton?.invoke(true)
            startForeground(NOTIFICATION_ID, createNotification(audioList[position].name, audioList[position].logo))
        }else {
            onTrackPause()
        }
    }

    fun onTrackPause(){
        if(mediaPlayer.isPlaying) {
            playOrPauseDrawable = android.R.drawable.ic_media_play
            mediaPlayer.pause()
            changePlayPauseButton?.invoke(false)
        }else {
            playOrPauseDrawable = android.R.drawable.ic_media_pause
            mediaPlayer.start()
            changePlayPauseButton?.invoke(true)
        }
        startForeground(NOTIFICATION_ID, createNotification(audioList[selectedTrack].name, audioList[selectedTrack].logo))
    }

    fun onTrackNext() {
        if(selectedTrack < audioList.size - 1)
            onTrackPlay(selectedTrack + 1)
    }

    fun onTrackPrevious() {
        if(selectedTrack > 0)
            onTrackPlay(selectedTrack - 1)
    }

    private fun createNotification(name: String, bitmap: Bitmap) = NotificationCompat.Builder(this, CHANNEL_ONE)
        .setSilent(true)
        .setContentTitle(name)
        .setContentText(BLANK)
        .setLargeIcon(bitmap)
        .setSmallIcon(R.drawable.ic_mus)
        .addAction(android.R.drawable.ic_media_previous, "Previous", pendingIntentPrevious)
        .addAction(playOrPauseDrawable, "Play", pendingIntentPlay)
        .addAction(android.R.drawable.ic_media_next, "Next", pendingIntentNext)
        .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Close", pendingIntentClose)
        .setStyle(androidx.media.app.NotificationCompat.MediaStyle()
            .setShowActionsInCompactView(0,1,2,3)
            .setMediaSession(mediaSessionCompat.sessionToken)
        )
        .setPriority(NotificationManager.IMPORTANCE_DEFAULT)
        .build()

    companion object {

        private const val CHANNEL_ONE = "CHANNEL_ONE"
        private const val CHANNEL_NAME = "Music"
        private const val NOTIFICATION_ID = 215
        private const val BLANK = ""
        private const val ACTION_AUDIO_PLAY = "actionplay"
        private const val ACTION_AUDIO_PRV = "actionprevious"
        private const val ACTION_AUDIO_NEXT = "actionnext"
        private const val ACTION_AUDIO_CLOSE = "actionclose"
        const val ACTION_AUDIO_KEY = "actionname"
        const val AUDIO_INTENT_FILTER = "TRACKS_TRACKS"
    }

}