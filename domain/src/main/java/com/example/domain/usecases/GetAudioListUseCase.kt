package com.example.domain.usecases

interface GetAudioListUseCase {

    operator fun invoke(): List<String>

}