package com.tempifi.app.realm

import io.realm.RealmObject
import java.util.*

open class RStoredReading : RealmObject() {
    var timestamp: Long = 0
    var reading: Int = 0

    val humidity: Double
        get() {
            val bytes = intToBytes(reading)
            val h1 = bytes[1].toUByte()
            val h0 = bytes[0].toUByte()
            val raw_humidity = (h1.toUInt() shl 8) or h0.toUInt()
            return convertHumidity(raw_humidity.toInt())
        }

    fun getTemperatureInC(): Double {
        val bytes = intToBytes(reading)
        val temp1 = bytes[3].toUByte()
        val temp0 = bytes[2].toUByte()
        val raw_temp = (temp1.toUInt() shl 8) or temp0.toUInt()
        val calc_temp = convertTemperature(raw_temp.toInt())
        return calc_temp
    }

    fun intToBytes(value: Int): ByteArray {
        val bytes = ByteArray(4)
        bytes[0] = (value and 0xFF).toByte()
        bytes[1] = ((value ushr 8) and 0xFF).toByte()
        bytes[2] = ((value ushr 16) and 0xFF).toByte()
        bytes[3] = ((value ushr 24) and 0xFF).toByte()
        return bytes
    }

    fun convertTemperature(temperature: Int): Double {
        val tc = -46.85 + 175.72 * temperature / 65536.00
        return Math.round(10.0 * tc) / 10.0
    }

    fun convertHumidity(humidity: Int): Double {
        val hc = -6.0 + (125.0 * humidity) / 65536.0
        return Math.round(hc * 10.0) / 10.0
    }
}

