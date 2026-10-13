package com.example.foodchecker

import android.app.Application
import com.example.foodchecker.di.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class FoodCheckerApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@FoodCheckerApplication)
            modules(appModules)
        }
    }
}
