package com.tempifi.app.realm

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

open class RStoredSensor : RealmObject() {
    @PrimaryKey
    var mac_address: String = "Sensor"
    var readings: RealmList<RStoredReading>? = null
}
