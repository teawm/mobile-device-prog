package com.example.insects.data.repository

import android.util.Log
import com.example.insects.data.api.CbrRetrofitClient
import com.example.insects.data.local.GoldDataStore
import com.example.insects.data.mapper.toGoldRateSnapshot
import com.example.insects.data.parser.CbrXmlParser
import com.example.insects.data.parser.MetallRecord
import com.example.insects.domain.GoldRateSnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class GoldRepository(private val dataStore: GoldDataStore) {

    companion object {
        private const val TAG = "GoldRepository"

        private const val CACHE_FRESHNESS_MILLIS = 4L * 60L * 60L * 1000L

        private const val MAX_RETRY_ATTEMPTS = 3
        private const val INITIAL_RETRY_DELAY_MILLIS = 1000L

        private const val LOOKBACK_DAYS = 14
    }

    val goldRateFlow: Flow<GoldRateSnapshot> = dataStore.goldRateFlow

    suspend fun refreshGoldRate(force: Boolean = false): Boolean = withContext(Dispatchers.IO) {
        val currentSnapshot = goldRateFlow.first()

        if (!force && isCacheFresh(currentSnapshot)) {
            Log.d(TAG, "Кэш актуален, сетевой запрос не нужен")
            return@withContext false
        }

        var lastException: Exception? = null

        repeat(MAX_RETRY_ATTEMPTS) { index ->
            val attempt = index + 1

            try {
                val (dateReq1, dateReq2) = buildRequestPeriod()
                Log.d(TAG, "Запрос котировок металлов: $dateReq1 - $dateReq2, попытка $attempt")

                val rawXml = CbrRetrofitClient.apiService.getMetallRatesRaw(
                    dateReq1 = dateReq1,
                    dateReq2 = dateReq2
                )

                val records: List<MetallRecord> = CbrXmlParser.parse(rawXml)

                val latestGoldRecord = findLatestGoldRecord(records)

                if (latestGoldRecord == null) {
                    Log.w(TAG, "RateNotFound: запись золота не найдена")
                    return@withContext false
                }

                val newSnapshot = latestGoldRecord.toGoldRateSnapshot(
                    fetchTime = System.currentTimeMillis()
                )

                if (newSnapshot == null) {
                    Log.w(TAG, "InvalidData: цену золота не удалось безопасно разобрать")
                    return@withContext false
                }

                if (isOlderThanCache(newSnapshot, currentSnapshot)) {
                    Log.w(TAG, "Получена более старая котировка, кэш не перезаписываем")
                    return@withContext false
                }

                dataStore.saveGoldRate(newSnapshot)

                Log.d(
                    TAG,
                    "Курс золота сохранён: ${newSnapshot.priceInKopecks} коп., дата ${newSnapshot.quoteDate}"
                )

                return@withContext true

            } catch (e: IOException) {
                lastException = e
                Log.w(TAG, "Транспортная ошибка, попытка $attempt", e)

                if (attempt < MAX_RETRY_ATTEMPTS) {
                    delay(INITIAL_RETRY_DELAY_MILLIS * attempt)
                }

            } catch (e: HttpException) {
                Log.e(TAG, "HTTP ошибка: ${e.code()} ${e.message()}", e)
                return@withContext false

            } catch (e: Exception) {

                Log.e(TAG, "Ошибка обработки ответа", e)
                return@withContext false
            }
        }

        Log.e(TAG, "Все попытки обновления исчерпаны", lastException)
        return@withContext false
    }

    private fun isCacheFresh(snapshot: GoldRateSnapshot): Boolean {
        if (!snapshot.hasData) return false

        val age = System.currentTimeMillis() - snapshot.fetchTime
        return age in 0 until CACHE_FRESHNESS_MILLIS
    }

    private fun buildRequestPeriod(): Pair<String, String> {
        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.US)

        val endCalendar = Calendar.getInstance()
        val endDate = endCalendar.time

        val startCalendar = Calendar.getInstance()
        startCalendar.add(Calendar.DAY_OF_YEAR, -LOOKBACK_DAYS)
        val startDate = startCalendar.time

        return dateFormat.format(startDate) to dateFormat.format(endDate)
    }

    private fun findLatestGoldRecord(records: List<MetallRecord>?): MetallRecord? {
        if (records.isNullOrEmpty()) {
            Log.w(TAG, "Список записей пуст")
            return null
        }

        Log.d(TAG, "Получено записей: ${records.size}")
        records.forEachIndexed { index, record ->
            Log.d(TAG, "Запись #$index: Date=${record.date}, Code=${record.code}, Buy=${record.buy}, Sell=${record.sell}")
        }

        val goldRecords = records.filter { it.code.trim() == "1" }

        if (goldRecords.isEmpty()) {
            Log.w(TAG, "RateNotFound: записи с Code=1 не найдены. Доступные Code: ${records.map { it.code }.toSet()}")
            return null
        }

        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.US)

        return goldRecords.maxByOrNull { record ->
            try {
                dateFormat.parse(record.date)?.time ?: 0L
            } catch (e: Exception) {
                Log.w(TAG, "Не удалось распарсить дату: ${record.date}", e)
                0L
            }
        }
    }

    private fun isOlderThanCache(
        newSnapshot: GoldRateSnapshot,
        cachedSnapshot: GoldRateSnapshot
    ): Boolean {
        val newDate = parseCbrDate(newSnapshot.quoteDate) ?: return false
        val cachedDate = parseCbrDate(cachedSnapshot.quoteDate) ?: return false

        return newDate < cachedDate
    }

    private fun parseCbrDate(date: String?): Long? {
        if (date.isNullOrBlank()) return null

        return try {
            val format = SimpleDateFormat("dd.MM.yyyy", Locale.US)
            format.isLenient = false
            format.parse(date)?.time
        } catch (e: Exception) {
            null
        }
    }
}