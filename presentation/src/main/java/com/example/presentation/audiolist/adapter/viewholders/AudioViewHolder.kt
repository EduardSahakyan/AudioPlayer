package com.example.presentation.audiolist.adapter.viewholders

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.data.models.AudioModel
import com.example.presentation.databinding.AudioItemBinding

class AudioViewHolder(private val binding: AudioItemBinding): RecyclerView.ViewHolder(binding.root) {

    fun bind(audioModel: AudioModel) = with(binding){
        songName.text = audioModel.name
        val minutes: Int = (audioModel.duration/1000) / 60
        val seconds: Int = (audioModel.duration/1000) % 60
        var strTemp = if (minutes < 10) "0$minutes:" else "$minutes:"
        strTemp = if (seconds < 10) strTemp + "0" + seconds.toString() else strTemp + seconds.toString()
        duration.text = strTemp
        songLogo.setImageBitmap(audioModel.logo)
    }

}