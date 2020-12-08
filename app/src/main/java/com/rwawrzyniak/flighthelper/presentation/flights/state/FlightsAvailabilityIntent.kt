package com.rwawrzyniak.flighthelper.presentation.flights.state

sealed class FlightsAvailabilityIntent {
    data class Search(val origin: String,
                      val destination: String,
                      val dateout: String,
                      val adult: Int,
                      val teen: Int,
                      val child: Int) : FlightsAvailabilityIntent()
}
