package com.tempifi.app.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.metal.chartapp.R
import com.metal.chartapp.ui.BaseFragment
import com.metal.chartapp.ui.MainActivity
import com.metal.chartapp.ui.OpenDataSetFragment
import com.tempifi.app.realm.RStoredReading
import com.tempifi.app.realm.RStoredSensor
import io.realm.Realm
import io.realm.RealmChangeListener
import kotlinx.android.synthetic.main.fragment_history.*
import kotlinx.android.synthetic.main.screen_title.*
import org.joda.time.DateTime
import java.text.SimpleDateFormat
import java.util.*


@ExperimentalUnsignedTypes
class HistoryFragment : BaseFragment() {

    private lateinit var sensor: RStoredSensor
    private lateinit var mac_address: String

    ////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Life Cycle
    ////////////////////////////////////////////////////////////////////////////////////////////////////////
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mac_address = getResources().getString(R.string.default_mac_address)
        sensor = Realm.getDefaultInstance().where(RStoredSensor::class.java).equalTo(
            "mac_address",
            mac_address
        ).findFirst()!!

        titleText.text = "Data - List View"

        buttonLeft.visibility = View.VISIBLE
        buttonRight.visibility = View.INVISIBLE
        buttonRight.text = ""

        historyRecyclerView.setHasFixedSize(true)
        historyRecyclerView.layoutManager = LinearLayoutManager(context)
        historyRecyclerView.adapter = MyAdapter(this)

        sensor.addChangeListener(object : RealmChangeListener<RStoredSensor> {
            override fun onChange(results: RStoredSensor) {
                updateUI()
            }
        })
        updateUI()
    }

    override fun onDestroyView() {
        sensor.removeAllChangeListeners()
        super.onDestroyView()
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////
    // UpdateUI
    ////////////////////////////////////////////////////////////////////////////////////////////////////////
    private fun updateUI() {
        historyRecyclerView.adapter?.notifyDataSetChanged()
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////
    // MyAdapter
    ////////////////////////////////////////////////////////////////////////////////////////////////////////
    inner class MyAdapter(val dashboard: HistoryFragment) : RecyclerView.Adapter<MyAdapter.SensorViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SensorViewHolder {
            val textView = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_sensor_reading, parent, false)
            return SensorViewHolder(textView)
        }

        override fun getItemCount(): Int {
            return dashboard.sensor.readings!!.size
        }

        override fun onBindViewHolder(holder: SensorViewHolder, position: Int) {
            if (getItemCount() > position) {
                //holder.bindSensor(dashboard.sensor.readings!!.get((dashboard.sensor.readings!!.size - 1) - position)!!)
                holder.bindSensor(dashboard.sensor.readings!!.get(position)!!)
            }
        }

        inner class SensorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            val temperature: TextView = itemView.findViewById(R.id.temperature) as TextView
            val humidity: TextView = itemView.findViewById(R.id.humidity) as TextView
            val timestamp: TextView = itemView.findViewById(R.id.timestamp) as TextView

            fun bindSensor(reading: RStoredReading) {

                timestamp.setText(getLastUpdateString2(reading.timestamp))
                temperature.setText(String.format("${reading.getTemperatureInC()}"))
                humidity.setText(String.format("${reading.humidity}"))
            }
        }
    }

    fun getLastUpdateString2(lastOnTime: Long): String {

//        val lastOnTime:Long = 1570797575

        var result = "getting info..."
        try {
            val date = DateTime(lastOnTime)

            val format: SimpleDateFormat

            format = SimpleDateFormat("MM/dd/yy kk:mm a", Locale.getDefault())
            result = String.format("%s", format.format(date.toDate()))

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return result
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Boiler Plate
    ////////////////////////////////////////////////////////////////////////////////////////////////////////

    override fun backEnabled(): Boolean {
        return true
    }

    override fun getBackFragmentClass(): Class<*> {
        return OpenDataSetFragment::class.java
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

}
