package com.rwawrzyniak.flighthelper.presentation.flights.state

import com.rwawrzyniak.flighthelper.presentation.flights.CheckAvailabilityQuery

sealed class FlightsAvailabilityIntent {
    data class Search(val query: CheckAvailabilityQuery) : FlightsAvailabilityIntent()
}
