package com.rwawrzyniak.flighthelper.presentation.flights.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rwawrzyniak.flighthelper.R
import com.rwawrzyniak.flighthelper.business.data.datasource.models.StationModel
import kotlinx.android.synthetic.main.station_item_layout.view.stationCode
import kotlinx.android.synthetic.main.station_item_layout.view.stationName

class StationsAdapter(context: Context,
                      resource: Int = R.layout.station_item_layout,
                      private val stations: MutableList<StationModel> = mutableListOf(),
                      private val tempItems: MutableList<StationModel> = mutableListOf(),
                      private val suggestions: MutableList<StationModel> = mutableListOf()
) : ArrayAdapter<StationModel>(context, resource) {

    override fun getFilter(): Filter {
        return stationFilter
    }

    private val stationFilter: Filter

    init {
        stationFilter = object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                return if(constraint != null) {
                    suggestions.clear()
                    tempItems.forEach {
                        if (it.name.toLowerCase().startsWith(constraint.toString(), ignoreCase = true)) {
                            suggestions.add(it)
                        }
                    }

                    val filterResults = FilterResults()
                    filterResults.values = suggestions
                    filterResults.count = suggestions.size

                    filterResults
                } else {
                    FilterResults()
                }
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                val tempStations = results?.values as List<StationModel>
                if(tempStations.isNotEmpty()){

                stations.clear()
                tempStations.forEach {
                    stations.add(it)
                }
                notifyDataSetChanged()
                } else{
                    stations.clear()
                    notifyDataSetChanged()
                }
            }
        }
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val holder: RecyclerView.ViewHolder
        val retView: View

        if(convertView == null){
            retView = LayoutInflater.from(parent.context)
                .inflate(R.layout.station_item_layout, parent, false)

            retView.stationCode.text =  stations[position].code
            retView.stationName.text =  stations[position].name

            retView
        } else {
            retView = convertView
        }

        return retView
    }

    fun setData(data: List<StationModel>){
        stations.clear()
        stations.addAll(data)
        notifyDataSetChanged()
    }

    class StationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val stationCode = view.stationCode as TextView
        val stationName = view.stationName as TextView
    }

    override fun getCount(): Int {
        return stations.size
    }
}