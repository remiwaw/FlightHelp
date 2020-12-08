package com.rwawrzyniak.flighthelper.presentation.flights.usecase

import javax.inject.Inject

class ValidateSearchInputUseCase @Inject constructor(){

    // This validation could be done also directly on view in xml
    fun validate(origin: String,
                 destination: String,
                 dateout: String,
                 adult: Int,
                 teen: Int,
                 child: Int): Boolean {
        // TODO just simple validation if not empty
       return origin.isNotBlank() && destination.isNotBlank() && dateout.isNotBlank() && adult >= 0 && teen >= 0 && child >= 0
    }
}