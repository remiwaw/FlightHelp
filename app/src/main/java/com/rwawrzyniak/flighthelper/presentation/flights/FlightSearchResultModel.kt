package com.rwawrzyniak.flighthelper.presentation.flights

data class FlightSearchResultModel(
    val originName: String,
    val destinationNAme: String,
    val flights: List<FlightModel> = listOf()
)