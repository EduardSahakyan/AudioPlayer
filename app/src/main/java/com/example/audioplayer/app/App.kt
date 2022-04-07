package com.example.audioplayer.app

import android.app.Application
import com.example.data.di.repositoryModule
import com.example.domain.di.useCaseModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(listOf(
                repositoryModule,
                useCaseModule
                ))
        }
    }
}