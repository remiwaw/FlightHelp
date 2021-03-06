package com.rwawrzyniak.flighthelper.business.data.network.mappers

import com.rwawrzyniak.flighthelper.business.data.network.util.DateUtils
import com.rwawrzyniak.flighthelper.business.domain.model.AvailabilityResponse
import com.rwawrzyniak.flighthelper.business.domain.util.EntityMapper
import com.rwawrzyniak.flighthelper.presentation.flights.FlightModel
import com.rwawrzyniak.flighthelper.presentation.flights.FlightSearchResultModel
import javax.inject.Inject

class FlightsAvailibityResponseNetworkMapper @Inject constructor(private val dateUtils: DateUtils): EntityMapper<AvailabilityResponse, FlightSearchResultModel> {

	// As far as i get this api first() is ok to call here
	override fun mapFromEntity(response: AvailabilityResponse): FlightSearchResultModel {
		val trip = response.trips.first()
		val firstDate = trip.dates.first()
		val flightDateOut = firstDate.dateOut
		val flights = firstDate.flights

		val flightsModel = flights.map {
			FlightModel(
				flightDate = dateUtils.formatDateFromApi(flightDateOut),
				flightNumber = it.flightNumber,
				duration = it.duration,
				priceWithCurrency = it.regularFare.fares.first().amount.toString() + ""+ response.currency,
				infantsLeft = it.infantsLeft,
				fareClass = it.regularFare.fareClass,
				discountPercent = it.regularFare.fares.first().discountInPercent,
				originName = trip.originName,
				destinationName = trip.destinationName
			)
		}

		return FlightSearchResultModel(
			originName = trip.originName,
			destinationNAme = trip.destinationName,
			flights = flightsModel
		)
	}

	override fun mapToEntity(domainModel: FlightSearchResultModel): AvailabilityResponse {
		error("Not used")
	}
}
