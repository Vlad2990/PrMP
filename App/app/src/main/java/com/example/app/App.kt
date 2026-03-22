package com.example.app // Ваш пакет

import android.app.Application
import com.example.app.di.appModule // Импорт вашего модуля
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(appModule)
        }
    }
}