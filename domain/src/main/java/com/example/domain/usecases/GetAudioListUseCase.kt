package com.example.domain.usecases

import com.example.data.models.AudioModel
import kotlinx.coroutines.flow.Flow

interface GetAudioListUseCase {

    operator fun invoke(): Flow<AudioModel>

}