package com.rwawrzyniak.flighthelper.presentation.flights

import com.rwawrzyniak.flighthelper.business.data.datasource.models.StationModel

data class CheckAvailabilityQuery(
    val origin: StationModel,
    val destination: StationModel,
    val dateout: String,
    val adult: Int,
    val teen: Int,
    val child: Int
)
