package com.rwawrzyniak.flighthelper.presentation.flights.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.TextView
import com.rwawrzyniak.flighthelper.R
import com.rwawrzyniak.flighthelper.business.data.datasource.models.StationModel

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


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view: View? = convertView
        var holder: ViewHolder

        if(view == null){
            view = LayoutInflater.from(parent.context).inflate(R.layout.station_item_layout, parent, false)

            val stationNameView = view.findViewById<TextView>(R.id.stationName)
            val stationCodeView = view.findViewById<TextView>(R.id.stationCode)

            holder = ViewHolder(stationCodeView, stationNameView)
            view.tag = holder

        } else {
            holder = view.tag as ViewHolder
        }
        // TODO change it to use viewHolder

        holder.codeView.text = requireNotNull(getItem(position)?.code)
        holder.nameView.text = requireNotNull(getItem(position)?.name)


        return requireNotNull(view)
    }

    private data class ViewHolder(val codeView: TextView, val nameView: TextView)
}