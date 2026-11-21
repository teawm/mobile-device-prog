package com.example.insects

import android.app.Application
import com.example.insects.data.local.GoldDataStore
import com.example.insects.data.repository.GoldRepository

class InsectsApp : Application() {

    lateinit var goldRepository: GoldRepository
        private set

    override fun onCreate() {
        super.onCreate()
        val dataStore = GoldDataStore(this)
        goldRepository = GoldRepository(dataStore)
    }
}