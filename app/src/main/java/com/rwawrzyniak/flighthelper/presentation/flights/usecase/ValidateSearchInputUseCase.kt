package com.rwawrzyniak.flighthelper.presentation.flights.usecase

import com.rwawrzyniak.flighthelper.presentation.flights.CheckAvailabilityQuery
import javax.inject.Inject

class ValidateSearchInputUseCase @Inject constructor(){

    // This validation could be done also directly on view in xml
    fun validate(query: CheckAvailabilityQuery): Boolean {
        // TODO just simple validation if not empty / could be extended
		with(query){
			return origin.code.isNotBlank() &&
                origin.name.isNotBlank() &&
                destination.code.isNotBlank() &&
                destination.name.isNotBlank() &&
                dateout.isNotBlank() &&
                adult >= 0 && teen >= 0 && child >= 0
		}
    }
}
