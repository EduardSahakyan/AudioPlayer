package com.example.domain.usecases

import com.example.data.models.AudioModel
import com.example.data.repositories.AudioRepository
import kotlinx.coroutines.flow.Flow

internal class GetAudioListUseCaseImpl(private val audioRepository: AudioRepository): GetAudioListUseCase {

    override fun invoke(): Flow<AudioModel> {
        return audioRepository.getAudioList()
    }

}