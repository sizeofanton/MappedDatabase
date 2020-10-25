package com.sizeofanton.mappeddatabaseandroid

import android.app.Application
import com.sizeofanton.mappeddatabaseandroid.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(appModule)
        }
    }

    companion object {
        private lateinit var instance: App
        fun getInstance(): App = instance
    }
}
