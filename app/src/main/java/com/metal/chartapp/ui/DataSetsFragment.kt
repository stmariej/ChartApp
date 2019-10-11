package com.metal.chartapp.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.metal.chartapp.R
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.lang.Exception

class DataSetsFragment : BaseFragment() {

    lateinit var listView: ListView
    var data_sets: ArrayList<DataSet> = ArrayList();
    val json_url = "http://vitalnomad.com/reports/list.json"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listView = view.findViewById<ListView>(com.metal.chartapp.R.id.listView) as ListView
        loadDataSets(json_url)
    }

    fun loadDataSets(url: String) {

        data_sets = ArrayList();
        val request = Request.Builder().url(url).build()

        //  GET JSON FROM SERVER
        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, e.toString())
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    val str_response = response.body()!!.string()
                    val json_contact: JSONObject = JSONObject(str_response)
                    val jsonarray_info: JSONArray = json_contact.getJSONArray("info")
                    for (i in 0..jsonarray_info.length() - 1) {
                        val json_objectdetail: JSONObject = jsonarray_info.getJSONObject(i)
                        val report_info: DataSet = DataSet();
                        report_info.name = json_objectdetail.getString("name")
                        report_info.url = json_objectdetail.getString("url")
                        data_sets.add(report_info)
                    }
                } catch (e: Exception) {
                    Log.e(TAG, e.toString())
                }

                getActivity()?.runOnUiThread {
                    listView.adapter = DataSetAdapter(context!!.applicationContext, data_sets)
                }
            }
        })
    }

    public class DataSet {
        lateinit var name: String
        lateinit var url: String

        constructor(name: String, url: String) {
            this.name = name; this.url = url
        }

        constructor()
    }

    class DataSetAdapter(context: Context, arrayListDetails: ArrayList<DataSet>) : BaseAdapter() {
        private val layoutInflater: LayoutInflater
        private val arrayListDetails: ArrayList<DataSet>

        init {
            this.layoutInflater = LayoutInflater.from(context)
            this.arrayListDetails = arrayListDetails
        }

        override fun getCount(): Int {
            return arrayListDetails.size
        }

        override fun getItem(position: Int): Any {
            return arrayListDetails.get(position)
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {

            val view: View?
            val dataSetViewHolder: DataSetViewHolder

            if (convertView == null) {
                view = this.layoutInflater.inflate(R.layout.data_set_row, parent, false)
                dataSetViewHolder = DataSetViewHolder(view)
                view.tag = dataSetViewHolder
            } else {
                view = convertView
                dataSetViewHolder = view.tag as DataSetViewHolder
            }

            dataSetViewHolder.rlname.text = arrayListDetails.get(position).name
            //dataSetViewHolder.rlurl.text = arrayListDetails.get(position).url
            return view
        }
    }

    private class DataSetViewHolder(row: View?) {
        val rlname: TextView
        //val rlurl: TextView
        val linearLayout: LinearLayout

        init {
            this.rlname = row?.findViewById<TextView>(R.id.rlname) as TextView
            //this.rlurl = row.findViewById<TextView>(R.id.rlurl) as TextView
            this.linearLayout = row.findViewById<LinearLayout>(R.id.linearLayout) as LinearLayout
        }
    }

    override fun backEnabled(): Boolean {
        return true
    }

    /*override fun getBackFragmentClass(): Class<*> {
        return ReportListFragment::class.java
    }*/

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(com.metal.chartapp.R.layout.fragment_data_sets, container, false)
    }
}