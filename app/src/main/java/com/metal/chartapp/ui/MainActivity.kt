package com.metal.chartapp.ui

import android.content.Intent
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import com.metal.chartapp.R
import com.nes.transfragment.BaseTransActivity
import com.tempifi.app.realm.RStoredSensor
import io.realm.Realm
import io.realm.RealmConfiguration

class MainActivity : BaseTransActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(com.metal.chartapp.R.id.fragmentContainer, OpenDataSetFragment()).commit()
        }
        initDB()
    }

    fun initDB() {
        Realm.init(this)
        //Realm.deleteRealm(Realm.getDefaultConfiguration())
        val realmConfiguration = RealmConfiguration.Builder()
            .deleteRealmIfMigrationNeeded()
            .build()
        Realm.setDefaultConfiguration(realmConfiguration)

        val realm = Realm.getDefaultInstance()
        val res: Resources = getResources()
        var sensor = realm.where(RStoredSensor::class.java)
            .equalTo("mac_address", res.getString(R.string.default_mac_address)).findFirst()
        if (sensor == null) {
            realm.executeTransaction { _ ->
                sensor = RStoredSensor()
                sensor!!.mac_address = res.getString(R.string.default_mac_address)
                realm.copyToRealmOrUpdate(sensor!!)
            }
        }
    }
}