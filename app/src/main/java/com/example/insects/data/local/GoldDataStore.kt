package com.example.insects.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.insects.domain.GoldRateSnapshot
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "gold_rate_prefs")

class GoldDataStore(private val context: Context) {

    private object PreferencesKeys {
        val PRICE_IN_KOPECKS = longPreferencesKey("price_in_kopecks")
        val QUOTE_DATE = stringPreferencesKey("quote_date")
        val FETCH_TIME = longPreferencesKey("fetch_time")
    }

    val goldRateFlow: Flow<GoldRateSnapshot> = context.dataStore.data.map { preferences ->
        val price = preferences[PreferencesKeys.PRICE_IN_KOPECKS] ?: 0L
        val date = preferences[PreferencesKeys.QUOTE_DATE] ?: "Нет данных"
        val fetchTime = preferences[PreferencesKeys.FETCH_TIME] ?: 0L

        if (fetchTime == 0L) {
            GoldRateSnapshot.EMPTY
        } else {
            GoldRateSnapshot(
                priceInKopecks = price,
                quoteDate = date,
                fetchTime = fetchTime
            )
        }
    }

    suspend fun saveGoldRate(snapshot: GoldRateSnapshot) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.PRICE_IN_KOPECKS] = snapshot.priceInKopecks
            preferences[PreferencesKeys.QUOTE_DATE] = snapshot.quoteDate
            preferences[PreferencesKeys.FETCH_TIME] = snapshot.fetchTime
        }
    }

    suspend fun clearCache() {
        context.dataStore.edit { it.clear() }
    }
}