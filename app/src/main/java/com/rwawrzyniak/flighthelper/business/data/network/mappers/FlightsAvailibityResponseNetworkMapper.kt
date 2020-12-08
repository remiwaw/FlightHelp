package com.rwawrzyniak.flighthelper.business.data.network.mappers

import com.rwawrzyniak.flighthelper.business.domain.model.AvailabilityResponse
import com.rwawrzyniak.flighthelper.business.domain.util.EntityMapper
import com.rwawrzyniak.flighthelper.presentation.flights.FlightModel
import javax.inject.Inject

class FlightsAvailibityResponseNetworkMapper @Inject constructor(): EntityMapper<AvailabilityResponse, List<FlightModel>> {

	override fun mapFromEntity(response: AvailabilityResponse): List<FlightModel> {
		val trip = response.trips.first()
		val firstDate = trip.dates.first()
		val flightDateOut = firstDate.dateOut
		val flights = firstDate.flights
		return flights.map { FlightModel(flightDateOut, it.flightNumber, it.duration, it.regularFare.fares.first().amount) }
	}

	override fun mapToEntity(domainModel: List<FlightModel>): AvailabilityResponse {
		error("Not used")
	}
}
