package com.rwawrzyniak.flighthelper.presentation.flights

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.applandeo.materialcalendarview.CalendarView
import com.applandeo.materialcalendarview.DatePicker
import com.applandeo.materialcalendarview.builders.DatePickerBuilder
import com.applandeo.materialcalendarview.listeners.OnSelectDateListener
import com.rwawrzyniak.flighthelper.R
import com.rwawrzyniak.flighthelper.business.domain.model.Station
import com.rwawrzyniak.flighthelper.presentation.UIState
import com.rwawrzyniak.flighthelper.presentation.flights.adapter.FlightsAdapter
import com.rwawrzyniak.flighthelper.presentation.flights.adapter.StationsAdapter
import com.rwawrzyniak.flighthelper.presentation.flights.state.FlightAvailabilityViewState
import com.rwawrzyniak.flighthelper.presentation.flights.state.FlightsAvailabilityIntent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_flight_availability.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import ru.ldralighieri.corbind.view.clicks
import java.text.SimpleDateFormat
import java.util.*

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class FlightAvailabilityFragment : Fragment(R.layout.fragment_flight_availability) {


	private val flightAdapter: FlightsAdapter by lazy { FlightsAdapter(mutableListOf(), {}) }
	private val outgoingStationAdapter: StationsAdapter by lazy { StationsAdapter(requireContext()) }
	private val inboundStationAdapter: StationsAdapter by lazy { StationsAdapter(requireContext()) }
	private val viewModel: FlightsAvailabilityViewModel by viewModels()

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		with(flights_recyclerview){
			layoutManager = LinearLayoutManager(requireContext())
			adapter = flightAdapter
		}

		originStationInput.setAdapter(inboundStationAdapter)
		destinationStationInput.setAdapter(outgoingStationAdapter)

		setupOutDateCalendar()
		setupPassengerNumbersPicker()

		observeStateChanges()

		lifecycleScope.launch {
			searchButton
				.clicks()
				.debounce(200)
				.collectLatest {
					viewModel.sendIntent(
						FlightsAvailabilityIntent.Search(
							CheckAvailabilityQuery(
								origin = originStationInput.text.toString(),
								destination = destinationStationInput.text.toString(),
								dateout = departureDateInput.text.toString(),
								adult = adultsNumberPicker.value,
								teen = teenNumberPicker.value,
								child = childrenNumberPicker.value
							)
						)
					)
				}
		}
	}

	private fun setupPassengerNumbersPicker() {
		listOf(adultsNumberPicker, teenNumberPicker, childrenNumberPicker).forEach {
			it.minValue = 0
			it.maxValue = 99
		}
	}

	private fun setupOutDateCalendar() {
		departureDateInput.setOnClickListener { createCalendarPicker().show() }
	}

	private fun createCalendarPicker(): DatePicker {
		// TODO provide this date through dependency (for easier testing)
		val today = Calendar.getInstance()

		return DatePickerBuilder(requireContext(), object : OnSelectDateListener {
			override fun onSelect(calendar: List<Calendar>) {
				val dateFormatted: String =
					SimpleDateFormat("dd-MM-yyyy", Locale.UK).format(calendar.first().time)
				departureDateInput.setText(dateFormatted)
			}
		})
			.pickerType(CalendarView.ONE_DAY_PICKER)
			.minimumDate(today)
			.date(today)
			.build()
	}

	private fun observeStateChanges() {
		lifecycleScope.launch {
			viewModel.viewState.collectLatest {
				when (it) {
					UIState.Idle -> {
					}
					UIState.Loading -> renderLoadingState()
					is UIState.Error -> renderErrorState(it)
					is UIState.Success -> rendSuccessState(it)
				}
			}
		}
	}

	private fun renderLoadingState() {
		visibleGroup.isVisible = false
		noData.isVisible = false
		retry_button.isVisible = false
		progress_bar.isVisible = true
		errorText.isVisible = false
	}

	private fun renderErrorState(error: UIState.Error) {
		visibleGroup.isVisible = true
		noData.isVisible = false
		retry_button.isVisible = true
		progress_bar.isVisible = false
		errorText.isVisible = true
		errorText.text = error.errorMessage
	}

	private fun rendSuccessState(success: UIState.Success<FlightAvailabilityViewState>) {


		visibleGroup.isVisible = true
		flightAdapter.setData(success.data.flights)
		noData.isVisible = success.data.flights.isEmpty()
		retry_button.isVisible = false
		progress_bar.isVisible = false
		errorText.isVisible = false
	}

	private fun setupInOutFlight(stations: List<Station>){
		require(stations.isNotEmpty())
		listOf(originStationInput, destinationStationInput).forEach {
			it
		}
	}
}
