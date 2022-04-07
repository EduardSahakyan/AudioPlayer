package com.example.data.di

import com.example.data.repositories.AudioRepository
import com.example.data.repositories.AudioRepositoryImpl
import org.koin.dsl.module

val repositoryModule = module {
    single<AudioRepository> {
        AudioRepositoryImpl()
    }
}