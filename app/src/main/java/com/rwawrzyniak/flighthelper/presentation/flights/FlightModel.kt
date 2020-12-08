package com.rwawrzyniak.flighthelper.presentation.flights

data class FlightModel(
    val flightDate: String,
    val flightNumber: String,
    val duration: String,
    val priceWithCurrency: Double,
    val currency: String = "BTC",
)