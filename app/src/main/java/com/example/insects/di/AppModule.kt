package com.example.insects.di

import com.example.insects.AppDatabase
import com.example.insects.GameDao
import com.example.insects.GameRepository
import com.example.insects.data.local.GoldDataStore
import com.example.insects.data.repository.GoldRepository
import com.example.insects.ui.viewmodel.GameViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val appModule = module {

    single { AppDatabase.getDatabase(androidContext()) }

    single { get<AppDatabase>().gameDao() }

    single { GoldDataStore(androidContext()) }

    singleOf(::GameRepository)

    singleOf(::GoldRepository)

    viewModelOf(::GameViewModel)
}