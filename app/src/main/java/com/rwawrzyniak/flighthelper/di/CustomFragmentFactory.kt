package com.rwawrzyniak.flighthelper.di

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.rwawrzyniak.flighthelper.presentation.flights.FlightAvailabilityFragment
import com.rwawrzyniak.flighthelper.presentation.flights.usecase.ValidateSearchInputUseCase
import javax.inject.Inject

class CustomFragmentFactory @Inject constructor(private val validateSearchInputUseCase: ValidateSearchInputUseCase) : FragmentFactory() {
	override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
		return when (className) {
			FlightAvailabilityFragment::class.java.name -> FlightAvailabilityFragment(validateSearchInputUseCase)
			else -> super.instantiate(classLoader, className)
		}
	}
}
