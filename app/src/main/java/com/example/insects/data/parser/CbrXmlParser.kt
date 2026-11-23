package com.example.insects.data.parser

import android.util.Log
import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import java.io.StringReader

data class MetallRecord(
    val date: String,
    val code: String,
    val buy: String?,
    val sell: String?
)

object CbrXmlParser {

    private const val TAG = "CbrXmlParser"

    fun parse(xml: String): List<MetallRecord> {
        val records = mutableListOf<MetallRecord>()

        try {
            val parser = Xml.newPullParser()
            parser.setInput(StringReader(xml))

            var eventType = parser.eventType
            var currentRecord: MetallRecord? = null

            while (eventType != XmlPullParser.END_DOCUMENT) {
                when (eventType) {
                    XmlPullParser.START_TAG -> {
                        if (parser.name.equals("Record", ignoreCase = true)) {
                            val date = parser.getAttributeValue(null, "Date")
                                ?: parser.getAttributeValue(null, "date")
                                ?: ""

                            val code = parser.getAttributeValue(null, "Code")
                                ?: parser.getAttributeValue(null, "code")
                                ?: ""

                            currentRecord = MetallRecord(
                                date = date,
                                code = code,
                                buy = null,
                                sell = null
                            )
                        } else if (currentRecord != null) {
                            when (parser.name) {
                                "Buy", "buy" -> {
                                    val value = parser.nextText()
                                    currentRecord = currentRecord.copy(buy = value)
                                }
                                "Sell", "sell" -> {
                                    val value = parser.nextText()
                                    currentRecord = currentRecord.copy(sell = value)
                                }
                            }
                        }
                    }
                    XmlPullParser.END_TAG -> {
                        if (parser.name.equals("Record", ignoreCase = true) && currentRecord != null) {
                            records.add(currentRecord)
                            currentRecord = null
                        }
                    }
                }
                eventType = parser.next()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка парсинга XML", e)
        }

        return records
    }
}