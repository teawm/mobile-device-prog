package com.example.insects.domain

data class GoldRateSnapshot(
    val priceInKopecks: Long,
    val quoteDate: String,
    val fetchTime: Long
) {
    val hasData: Boolean
        get() = priceInKopecks > 0L && fetchTime > 0L

    companion object {
        val EMPTY = GoldRateSnapshot(
            priceInKopecks = 0L,
            quoteDate = "Нет данных",
            fetchTime = 0L
        )
    }
}