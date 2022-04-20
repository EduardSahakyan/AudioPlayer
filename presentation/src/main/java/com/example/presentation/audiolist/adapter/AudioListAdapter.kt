package com.example.presentation.audiolist.adapter

import android.graphics.Bitmap
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.data.models.AudioModel
import com.example.presentation.audiolist.adapter.diffcallback.AudioDiffCallback
import com.example.presentation.audiolist.adapter.viewholders.AudioViewHolder
import com.example.presentation.databinding.AudioItemBinding
import java.io.ByteArrayOutputStream

class AudioListAdapter: RecyclerView.Adapter<AudioViewHolder>() {

    var audioList = listOf<AudioModel>()

    var onPlayClickListener: ((String, String, ByteArray) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AudioViewHolder {
        val binding = AudioItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AudioViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AudioViewHolder, position: Int) {
        holder.bind(audioList[position])
        holder.itemView.setOnClickListener {
            Log.d("t12", audioList[position].audioData)
            val byteArrayOutputStream = ByteArrayOutputStream()
            audioList[position].logo.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()
            onPlayClickListener?.invoke(audioList[position].audioData, audioList[position].name, byteArray)
        }
    }

    override fun getItemCount(): Int {
        return audioList.size
    }

    fun updateData(newList: List<AudioModel>){
        val diffCallback = AudioDiffCallback(audioList, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        audioList = newList
        diffResult.dispatchUpdatesTo(this)
    }

}