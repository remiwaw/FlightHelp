package com.rwawrzyniak.flighthelper.presentation.flights

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FlightModel(
    val flightDate: String,
    val flightNumber: String,
    val duration: String,
    val priceWithCurrency: String,
    val infantsLeft: Int,
    val fareClass: String,
    val discountPercent: Int,
    val originName: String,
    val destinationName: String
) : Parcelable