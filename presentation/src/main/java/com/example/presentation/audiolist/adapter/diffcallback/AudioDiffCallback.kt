package com.example.presentation.audiolist.adapter.diffcallback

import androidx.recyclerview.widget.DiffUtil
import com.example.data.models.AudioModel

class AudioDiffCallback(private val oldList: List<AudioModel>, private val newList: List<AudioModel>): DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size
    override fun getNewListSize(): Int = newList.size
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].name == newList[newItemPosition].name
    }
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}