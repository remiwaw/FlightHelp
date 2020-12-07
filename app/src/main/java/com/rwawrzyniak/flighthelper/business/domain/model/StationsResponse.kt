package com.rwawrzyniak.flighthelper.business.domain.model

data class StationsResponse(
    val stations: List<Station>
)

data class Station(
    val airportShopping: Boolean,
    val alias: List<String>,
    val alternateName: Any,
    val code: String,
    val countryAlias: Any,
    val countryCode: String,
    val countryGroupCode: String,
    val countryGroupName: String,
    val countryName: String,
    val ecoFriendly: Boolean,
    val latitude: String,
    val longitude: String,
    val markets: List<Market>,
    val mobileBoardingPass: Boolean,
    val name: String,
    val notices: Any,
    val timeZoneCode: String,
    val tripCardImageUrl: String
)

data class Market(
    val code: String,
    val group: Any,
    val onlyConnecting: Boolean,
    val onlyPrintedBoardingPass: Boolean,
    val pendingApproval: Boolean,
    val stops: List<Stop>
)

data class Stop(
    val code: String
)
