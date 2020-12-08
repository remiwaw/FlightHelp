package com.rwawrzyniak.flighthelper.business.data.network.util

import com.rwawrzyniak.flighthelper.business.data.network.RyanairApi
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import javax.inject.Inject

class DateUtils @Inject constructor(){
	// Input: 2020-12-30T00:00:00.000  Output: 2020-12-30
	fun formatApiResponseOutputFormatToApiInput(dateString: String): String {
		val inputFormatter =
			DateTimeFormatter.ofPattern(RyanairApi.API_RETURNED_DATE_OUTPUT_FORMAT, Locale.ENGLISH)
		val outputFormatter = DateTimeFormatter.ofPattern(
			RyanairApi.API_DESIRED_DATE_INPUT_FORMAT,
			Locale.ENGLISH
		)
		val date = LocalDate.parse(dateString, inputFormatter)
		return outputFormatter.format(date)
	}
}
