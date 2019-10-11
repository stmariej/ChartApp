package com.metal.chartapp.helpers

import android.content.Context
import android.content.res.Resources
import android.net.Uri
import android.util.Log
import android.view.View
import com.metal.chartapp.R
import com.metal.chartapp.ui.OpenDataSetFragment
import com.tempifi.app.realm.RStoredReading
import com.tempifi.app.realm.RStoredSensor
import io.realm.Realm
import io.realm.RealmList
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.Exception

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

import java.text.SimpleDateFormat
import kotlin.experimental.and
import android.R.attr.x

class ReportReader(val context:Context) {

    val TAG = "ReportReader"

    public class Reading {

        val TAG = "Reading"
        val dateString: String
        val tempString: String
        val humidityString: String
        val timestamp:Long
        val reading:Int

        constructor(date: String, temp: String, humidity: String) {
            this.dateString = date; this.tempString = temp; this.humidityString = humidity
            timestamp = dateStringToTimeStamp(dateString)
            reading = tempAndHumidityToReading(tempString, humidityString)
        }

        override fun toString(): String {
            val s = """timestamp: $timestamp reading: $reading dateString: $dateString tempString: $tempString humidityString: $humidityString"""
            return s
        }

        fun dateStringToTimeStamp(date_string: String):Long{
            try{
                return ((SimpleDateFormat("MM/dd/yy h:mm a", Locale.US).parse(date_string).time))
            } catch (e:Exception) {
                try {
                    return ((SimpleDateFormat("MM/dd/yy kk:mm", Locale.US).parse(date_string).time))
                } catch (e:Exception){

                }
                Log.e(TAG, e.toString())
                return 0
            }
        }

        @ExperimentalUnsignedTypes
        fun tempAndHumidityToReading(temp_string: String, humidity_String:String):Int{

            val t:UInt = convertTemperature(temp_string).toUShort().toUInt()
            val h:UInt = convertHumidity(humidity_String).toUShort().toUInt()

            //val t:UInt = convertTemperature("100").toUShort().toUInt()
            //val h:UInt = convertHumidity("0").toUShort().toUInt()

            val r:UInt = h + (t shl 16);
            val ri = r.toInt()
            return ri
        }

        fun convertTemperature(temperature: String): Short {
            try{
                var t = temperature.toDouble()
                t = ((t+46.85)*65536.0)/175.72
                return t.toShort()
            } catch (e:Exception){
                Log.e(TAG, "Error converting temp (" + temperature+ "):" +e.toString())
                return 0
            }
        }

        fun convertHumidity(humidity: String): Short {
            try{
                var t = humidity.toDouble()
                t = ((t+6.0)*65536.0)/125.0
                return t.toShort()
            } catch (e:Exception){
                Log.e(TAG, "Error converting humidity (" + humidity+ "):" +e.toString())
                return 0
            }
        }
    }

    var fileReader: BufferedReader? = null
    var readings: ArrayList<Reading> = ArrayList<Reading>()

    fun open(uri: Uri):Boolean{
        fileReader = null
        var inputStream: InputStream? = null
        try {
            inputStream = context.getContentResolver().openInputStream(uri)
            fileReader = BufferedReader(InputStreamReader(inputStream))
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
        return true
    }

    fun close() {
        try {
            fileReader?.close()
        } catch (e: IOException) {
            println("Closing fileReader Error!")
            e.printStackTrace()
        }
    }

    fun readHeader():Boolean{

        try {
            readings = ArrayList<Reading>()
            fileReader?.readLine()
            fileReader?.readLine()
            fileReader?.readLine()
            fileReader?.readLine()
            fileReader?.readLine()
        } catch (e: Exception) {
            close()
            e.printStackTrace()
            return false
        }
        return true
    }

    fun readData():Int{
        var i: Int = 0
        var line: String?
        try {
            line = fileReader?.readLine()
            while (line != null) {
                val tokens = line.split(",")
                if (tokens.size > 0) {
                    readings.add(Reading(tokens[0], tokens[1], tokens[3]))
                }
                line = fileReader?.readLine()
                i++
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        close()
        return i
    }

}