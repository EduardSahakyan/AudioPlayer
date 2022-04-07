package com.example.domain.di

import com.example.domain.usecases.GetAudioListUseCase
import com.example.domain.usecases.GetAudioListUseCaseImpl
import org.koin.dsl.module

val useCaseModule = module {
    factory<GetAudioListUseCase> {
        GetAudioListUseCaseImpl(audioRepository = get())
    }
}