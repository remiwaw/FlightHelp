package com.rwawrzyniak.flighthelper.presentation.flights.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import com.rwawrzyniak.flighthelper.R
import com.rwawrzyniak.flighthelper.business.data.datasource.models.StationModel
import kotlinx.android.synthetic.main.station_item_layout.view.stationCode
import kotlinx.android.synthetic.main.station_item_layout.view.stationName

class StationsAdapter(context: Context,
                      resource: Int = R.layout.station_item_layout,
                      private val stations: List<StationModel>
) : ArrayAdapter<StationModel>(context, resource) {

    private val tempItems: MutableList<StationModel> = stations.toMutableList()
    private val suggestions: MutableList<StationModel> = mutableListOf()

    override fun getFilter(): Filter {
        return stationFilter
    }

    private val stationFilter: Filter

    init {
        stationFilter = object : Filter() {
            override fun convertResultToString(resultValue: Any?): CharSequence {
                return (resultValue as StationModel).name
            }

            override fun performFiltering(constraint: CharSequence?): FilterResults {
                return if (constraint != null) {
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
                val tempStations = results?.values

                if (tempStations != null && tempStations is List<*> && tempStations.isNotEmpty()) {
                    clear()
                    (tempStations as List<StationModel>).forEach {
                        add(it)
                    }
                    notifyDataSetChanged()
                } else {
                    clear()
                    notifyDataSetChanged()
                }
            }
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItem(position: Int): StationModel {
        return stations[position]
    }

    override fun getCount(): Int {
        return stations.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val retView: View

        if (convertView == null) {
            retView = LayoutInflater.from(parent.context)
                .inflate(R.layout.station_item_layout, parent, false)

            retView.stationCode.text = getItem(position).code
            retView.stationName.text = getItem(position).name

            retView
        } else {
            retView = convertView
        }

        return retView
    }
}