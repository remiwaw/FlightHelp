package com.rwawrzyniak.flighthelper.business.data.datasource

import com.rwawrzyniak.flighthelper.business.data.network.ApiResult
import com.rwawrzyniak.flighthelper.business.data.network.RyanairApi
import com.rwawrzyniak.flighthelper.business.data.network.util.safeApiCall
import com.rwawrzyniak.flighthelper.business.domain.model.AvailabilityResponse
import com.rwawrzyniak.flighthelper.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class AvailabilityRepository @Inject constructor (
	private val ryanairApi: RyanairApi,
	@IoDispatcher private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {

	suspend fun checkAvailability(
		origin: String,
		destination: String,
		dateout: String,
		adult: Int,
		teen: Int,
		child: Int
	): ApiResult<AvailabilityResponse> =
			safeApiCall(dispatcher) {
				ryanairApi.getFlightAvailability(origin, destination, dateout, adult, teen, child)
			}
	}
