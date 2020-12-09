package com.rwawrzyniak.flighthelper.business.data.network

import com.rwawrzyniak.flighthelper.business.domain.model.StationsResponse
import retrofit2.http.GET

interface RyanairStaticResourcesApi{

	@GET("/static/stations.json")
	suspend fun getStations(): StationsResponse
}
