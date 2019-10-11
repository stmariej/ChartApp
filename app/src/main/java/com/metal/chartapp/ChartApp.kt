package com.metal.chartapp

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import com.jakewharton.threetenabp.AndroidThreeTen
import com.tempifi.app.realm.RStoredSensor
import net.danlew.android.joda.JodaTimeAndroid
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.RealmResults

class ChartApp : Application() {

    companion object {
        lateinit var app: ChartApp
    }

    override fun onCreate() {
        super.onCreate()
        app = this
        AndroidThreeTen.init(this)
        JodaTimeAndroid.init(this)
    }


}