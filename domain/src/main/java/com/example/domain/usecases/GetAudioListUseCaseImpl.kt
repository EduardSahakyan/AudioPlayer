package com.example.domain.usecases

import com.example.data.repositories.AudioRepository

internal class GetAudioListUseCaseImpl(private val audioRepository: AudioRepository): GetAudioListUseCase {

    override fun invoke(): List<String> {
        TODO("Not yet implemented")
    }

}