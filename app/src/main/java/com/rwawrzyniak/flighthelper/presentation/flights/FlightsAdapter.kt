package com.rwawrzyniak.flighthelper.presentation.flights

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rwawrzyniak.flighthelper.R
import kotlinx.android.synthetic.main.flight_item_layout.view.duration
import kotlinx.android.synthetic.main.flight_item_layout.view.flightDate
import kotlinx.android.synthetic.main.flight_item_layout.view.flightNumber
import kotlinx.android.synthetic.main.flight_item_layout.view.price

class FlightsAdapter(private val flights: MutableList<FlightModel>, private val onClickListener: (FlightModel) -> Unit) : RecyclerView.Adapter<FlightsAdapter.FlightViewHolder>() {

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlightViewHolder =
		FlightViewHolder(
			LayoutInflater.from(parent.context)
				.inflate(R.layout.flight_item_layout, parent, false)
		)

	override fun onBindViewHolder(holder: FlightViewHolder, position: Int) {
		val flightModel: FlightModel = flights[position]

		holder.itemView.setOnClickListener{ onClickListener(flightModel) }
		holder.flightDate.text = flightModel.flightDate
		holder.flightNumber.text = flightModel.flightNumber
		holder.duration.text = flightModel.duration
		holder.price.text = flightModel.priceWithCurrency.toString()
	}

	// Could be also done using livedata
	fun setData(data: List<FlightModel>){
		flights.clear()
		flights.addAll(data)
		notifyDataSetChanged()
	}

	class FlightViewHolder(view: View) : RecyclerView.ViewHolder(view) {
		val flightDate = view.flightDate as TextView
		val flightNumber = view.flightNumber as TextView
		val duration = view.duration as TextView
		val price = view.price as TextView

	}

	override fun getItemCount(): Int = flights.size
}