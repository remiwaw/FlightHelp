package com.rwawrzyniak.flighthelper.presentation.flights.state

import com.rwawrzyniak.flighthelper.business.data.datasource.models.StationModel
import com.rwawrzyniak.flighthelper.presentation.flights.FlightModel

data class FlightAvailabilityViewState(
	val inBoundFlightName: String = "TODO later",
	val outBoundFlightName: String = "Todo later",
	val stations: List<StationModel> = listOf(),
	val flights: List<FlightModel>
)
