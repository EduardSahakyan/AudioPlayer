package com.example.data.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.data.services.AudioService.Companion.ACTION_AUDIO_KEY
import com.example.data.services.AudioService.Companion.AUDIO_INTENT_FILTER


class NotificationActionService : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        context.sendBroadcast(
            Intent(AUDIO_INTENT_FILTER)
                .putExtra(ACTION_AUDIO_KEY, intent.action)
        )
    }
}