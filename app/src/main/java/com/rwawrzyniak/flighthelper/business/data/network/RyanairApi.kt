package com.rwawrzyniak.flighthelper.business.data.network

import com.rwawrzyniak.flighthelper.business.domain.model.AvailabilityResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface RyanairApi{

	// https://www.ryanair.com/api/booking/v4/en-gb/Availability?roundtrip=false&origin=WRO&destination=DUB&chd=0&dateout=2020-12-30&inf=0&ToUs=AGREED&adt=1
	@GET("/api/booking/v4/en-gb/Availability")
	suspend fun getFlightAvailability(
		@Query("origin") origin: String, // wro
		@Query("destination") destination: String, // DUB
		@Query("dateout") dateout: String , // 2020-12-30
		@Query("adt") adult: Int ,
		@Query("teen") teen: Int ,
		@Query("chd") child: Int ,
		@Query("roundtrip") roundtrip: Boolean = false,
		@Query("ToUs") termsOfService: String = "AGREED"
	): AvailabilityResponse
}
