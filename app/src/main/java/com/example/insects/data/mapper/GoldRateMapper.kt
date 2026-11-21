package com.example.insects.data.mapper

import com.example.insects.data.parser.MetallRecord
import com.example.insects.domain.GoldRateSnapshot
import java.math.BigDecimal
import java.math.RoundingMode

fun MetallRecord.toGoldRateSnapshot(fetchTime: Long): GoldRateSnapshot? {
    val rawDate = date?.trim().orEmpty()
    if (rawDate.isEmpty()) return null

    val rawPrice = sell?.trim()?.takeIf { it.isNotEmpty() }
        ?: buy?.trim()?.takeIf { it.isNotEmpty() }
        ?: return null

    if (!rawPrice.matches(Regex("""\d+([,.]\d+)?"""))) {
        return null
    }

    val normalizedPrice = rawPrice.replace(",", ".")

    val priceInKopecks = try {
        BigDecimal(normalizedPrice)
            .movePointRight(2)
            .setScale(0, RoundingMode.HALF_UP)
            .toLong()
    } catch (e: NumberFormatException) {
        return null
    } catch (e: ArithmeticException) {
        return null
    }

    if (priceInKopecks <= 0L) return null

    return GoldRateSnapshot(
        priceInKopecks = priceInKopecks,
        quoteDate = rawDate,
        fetchTime = fetchTime
    )
}