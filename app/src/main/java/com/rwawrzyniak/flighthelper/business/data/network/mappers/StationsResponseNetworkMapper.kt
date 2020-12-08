package com.rwawrzyniak.flighthelper.business.data.network.mappers

import com.rwawrzyniak.flighthelper.business.data.db.entitiy.StationsEntity
import com.rwawrzyniak.flighthelper.business.domain.model.AvailabilityResponse
import com.rwawrzyniak.flighthelper.business.domain.model.StationsResponse
import com.rwawrzyniak.flighthelper.business.domain.util.EntityMapper
import com.rwawrzyniak.flighthelper.presentation.flights.FlightModel
import com.rwawrzyniak.flighthelper.presentation.flights.FlightSearchResultModel
import javax.inject.Inject

class StationsResponseNetworkMapper @Inject constructor(): EntityMapper<StationsResponse, List<StationsEntity>> {

	override fun mapFromEntity(response: StationsResponse): List<StationsEntity> =
		response.stations.map { StationsEntity(it.code, it.name) }


	override fun mapToEntity(domainModel: List<StationsEntity>): StationsResponse {
		error("Not used")
	}
}
