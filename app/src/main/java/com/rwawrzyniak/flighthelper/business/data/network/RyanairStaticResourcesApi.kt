package com.rwawrzyniak.flighthelper.business.data.network

import com.rwawrzyniak.flighthelper.business.domain.model.AvailabilityResponse
import com.rwawrzyniak.flighthelper.business.domain.model.StationsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface RyanairStaticResourcesApi{

	@GET("/static/stations.json")
	suspend fun getStations(): StationsResponse
}
