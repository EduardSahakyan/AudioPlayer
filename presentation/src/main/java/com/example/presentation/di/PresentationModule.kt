package com.example.presentation.di

import com.example.presentation.audiolist.AudioListViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val presentationModule = module {
    viewModel<AudioListViewModel>{
        AudioListViewModel(get())
    }
}