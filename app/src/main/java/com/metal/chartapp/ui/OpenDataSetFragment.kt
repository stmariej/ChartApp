package com.metal.chartapp.ui

import android.app.Activity
import android.app.PendingIntent.getActivity
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.net.Uri
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.metal.chartapp.R
import com.metal.chartapp.helpers.ReportReader
import com.tempifi.app.realm.RStoredReading
import com.tempifi.app.realm.RStoredSensor
import com.tempifi.app.ui.home.HistoryFragment
import io.realm.Realm
import io.realm.RealmList
import kotlinx.android.synthetic.main.fragment_open_data_set.*
import kotlinx.android.synthetic.main.screen_title.*
import okhttp3.*
import org.jetbrains.anko.textColor
import org.json.JSONArray
import org.json.JSONObject
import java.io.*
import java.lang.Exception

class OpenDataSetFragment : BaseFragment() {

    private val OPEN_REQUEST_CODE = 41
    private lateinit var status: TextView
    private lateinit var realm: Realm
    private var sensor:RStoredSensor? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val btn_click_me = view.findViewById(com.metal.chartapp.R.id.openbutton) as Button
        btn_click_me.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "text/*"
            startActivityForResult(intent, OPEN_REQUEST_CODE)
        }
        status = view.findViewById(com.metal.chartapp.R.id.status) as TextView
        setStatusText("", false)

        titleText.text = "Load Data Set"
        buttonLeft.visibility = View.INVISIBLE
        buttonRight.visibility = View.INVISIBLE

        // Create sensor if not there
        realm = Realm.getDefaultInstance()
        val mac_address = getResources().getString(R.string.default_mac_address)
        sensor = realm.where(RStoredSensor::class.java).equalTo("mac_address", mac_address).findFirst()

        viewList.setOnClickListener {
            val history = HistoryFragment()
            forwardToFragment(history)
        }

        realm.executeTransaction { _ ->
            if (sensor == null) {
                sensor = RStoredSensor()
                sensor!!.mac_address = mac_address
                realm.copyToRealmOrUpdate(sensor!!)
            }

            if (sensor!!.readings == null) {
                sensor!!.readings = RealmList<RStoredReading>()
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == OPEN_REQUEST_CODE) {
            if (data != null) {
                deleteOldReadings()
                readReport(data.data)
            }
        }
    }

    fun setStatusText(text: String, error: Boolean) {
        getActivity()?.runOnUiThread {
            status.text = text
            if (error) {
                status.textColor = Color.RED
            } else {
                status.textColor = Color.WHITE
            }
        }
    }

    fun deleteOldReadings(){

        // SET STATUS INFO
        setStatusText("Deleting Previous Readings", false)

        // DELETE EXISTING READINGS
        if (sensor == null) {
            setStatusText("Problem with database", true)
            return
        }

        realm.executeTransaction { _ ->
            val sensor_history = sensor!!.readings!!.where().findAll()
            sensor_history.deleteAllFromRealm()
        }
    }

    fun readReport(uri: Uri?):Boolean{

        if (context == null){
            setStatusText("Application Error", true)
            return false
        }

        // OPEN REPORT
        val report_reader = ReportReader(context!!)
        if (uri == null || report_reader.open(uri) == false){
            setStatusText("Error Opening File", true)
            return false
        }

        // READ HEADER
        if (report_reader.readHeader() == false){
            setStatusText("Error Reading Report Header", true)
            return false
        }

        // READ DATA
        val readings_read = report_reader.readData()
        setStatusText("Readings Read (" + readings_read + ")", false)
        if (readings_read > 0){
            storeReadings(report_reader)
        }

        return true
    }

    fun storeReadings(report_reader:ReportReader){
        var i = 0
        realm.executeTransaction { _ ->
            for (reading in report_reader.readings) {
                println(reading)
                val new_reading = RStoredReading()
                new_reading.timestamp = reading.timestamp.toLong()
                new_reading.reading = reading.reading
                sensor!!.readings!!.add(new_reading)
                i++
            }
        }
        setStatusText("SUCCESS! (" + i + " readings)", false)
    }

    override fun backEnabled(): Boolean {
        return false
    }

    /*override fun getBackFragmentClass(): Class<*> {
        return ReportListFragment::class.java
    }*/

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            com.metal.chartapp.R.layout.fragment_open_data_set,
            container,
            false
        )
    }
}