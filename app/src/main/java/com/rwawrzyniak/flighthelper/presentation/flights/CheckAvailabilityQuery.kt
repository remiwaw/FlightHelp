package com.rwawrzyniak.flighthelper.presentation.flights

data class CheckAvailabilityQuery(
	val origin: String,
	val destination: String,
	val dateout: String,
	val adult: Int,
	val teen: Int,
	val child: Int
)
