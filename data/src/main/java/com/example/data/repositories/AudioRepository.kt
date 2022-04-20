package com.example.data.repositories

import com.example.data.models.AudioModel
import kotlinx.coroutines.flow.Flow

interface AudioRepository {

    fun getAudioList(): Flow<AudioModel>

}