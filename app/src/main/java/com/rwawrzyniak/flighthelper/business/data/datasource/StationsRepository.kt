package com.rwawrzyniak.flighthelper.business.data.datasource

import com.rwawrzyniak.flighthelper.business.data.db.StationsDao
import com.rwawrzyniak.flighthelper.business.data.network.ApiResult
import com.rwawrzyniak.flighthelper.business.data.network.RyanairStaticResourcesApi
import com.rwawrzyniak.flighthelper.business.data.network.util.safeApiCall
import com.rwawrzyniak.flighthelper.business.domain.model.AvailabilityResponse
import com.rwawrzyniak.flighthelper.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class StationsRepository @Inject constructor (
	private val ryanairStaticResourcesApi: RyanairStaticResourcesApi,
	private val dao: StationsDao,
	@IoDispatcher private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {

	suspend fun getStations(){
		// TODO if there entries in DB get them first
		val apiResult: ApiResult<AvailabilityResponse> =
			safeApiCall(dispatcher) {
				ryanairStaticResourcesApi.getStations()
			}
	}
}
