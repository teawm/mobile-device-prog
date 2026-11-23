package com.example.insects

import android.app.Application
import com.example.insects.data.local.GoldDataStore
import com.example.insects.data.repository.GoldRepository
import com.example.insects.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class InsectsApp : Application() {

    lateinit var goldRepository: GoldRepository
        private set

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@InsectsApp)
            modules(appModule)
        }

        val dataStore = GoldDataStore(this)
        goldRepository = GoldRepository(dataStore)
    }
}