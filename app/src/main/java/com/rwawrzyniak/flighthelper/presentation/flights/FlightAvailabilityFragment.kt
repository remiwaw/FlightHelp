package com.rwawrzyniak.flighthelper.presentation.flights

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.widget.Button
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.applandeo.materialcalendarview.CalendarView
import com.applandeo.materialcalendarview.DatePicker
import com.applandeo.materialcalendarview.builders.DatePickerBuilder
import com.applandeo.materialcalendarview.listeners.OnSelectDateListener
import com.rwawrzyniak.flighthelper.R
import com.rwawrzyniak.flighthelper.business.data.datasource.models.StationModel
import com.rwawrzyniak.flighthelper.presentation.MainActivity
import com.rwawrzyniak.flighthelper.presentation.UIState
import com.rwawrzyniak.flighthelper.presentation.flights.adapter.FlightsAdapter
import com.rwawrzyniak.flighthelper.presentation.flights.adapter.StationsAdapter
import com.rwawrzyniak.flighthelper.presentation.flights.state.FlightAvailabilityViewState
import com.rwawrzyniak.flighthelper.presentation.flights.state.FlightsAvailabilityIntent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.toolbar
import kotlinx.android.synthetic.main.fragment_flight_availability.adultsNumberPicker
import kotlinx.android.synthetic.main.fragment_flight_availability.childrenNumberPicker
import kotlinx.android.synthetic.main.fragment_flight_availability.departureDateInput
import kotlinx.android.synthetic.main.fragment_flight_availability.destinationStationInput
import kotlinx.android.synthetic.main.fragment_flight_availability.errorText
import kotlinx.android.synthetic.main.fragment_flight_availability.flights_recyclerview
import kotlinx.android.synthetic.main.fragment_flight_availability.noData
import kotlinx.android.synthetic.main.fragment_flight_availability.originStationInput
import kotlinx.android.synthetic.main.fragment_flight_availability.progress_bar
import kotlinx.android.synthetic.main.fragment_flight_availability.retry_button
import kotlinx.android.synthetic.main.fragment_flight_availability.searchButton
import kotlinx.android.synthetic.main.fragment_flight_availability.teenNumberPicker
import kotlinx.android.synthetic.main.fragment_flight_availability.visibleGroup
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import ru.ldralighieri.corbind.view.clicks
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class FlightAvailabilityFragment : Fragment(R.layout.fragment_flight_availability) {


	private val flightAdapter: FlightsAdapter by lazy { FlightsAdapter(mutableListOf(), ::navigateToSummary) }
	private val viewModel: FlightsAvailabilityViewModel by viewModels()
	private var currentlySelectedOrigin: StationModel = StationModel()
	private var currentlySelectedDestination: StationModel = StationModel()

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		with(flights_recyclerview) {
			layoutManager = LinearLayoutManager(requireContext())
			adapter = flightAdapter
		}

		setupOutDateCalendar()
		setupPassengerNumbersPicker()

		observeStateChanges()

		lifecycleScope.launch {
			askForFlights(searchButton)
			askForFlights(retry_button)
		}

		lifecycleScope.launch {
			viewModel.sendIntent(FlightsAvailabilityIntent.Initialize)
		}
	}

	private suspend fun askForFlights(button: Button) {
		button.clicks()
			.debounce(200)
			.collectLatest {
				viewModel.sendIntent(
					FlightsAvailabilityIntent.Search(
						CheckAvailabilityQuery(
							origin = currentlySelectedOrigin,
							destination = currentlySelectedDestination,
							dateout = departureDateInput.text.toString(),
							adult = adultsNumberPicker.value,
							teen = teenNumberPicker.value,
							child = childrenNumberPicker.value
						)
					)
				)
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
				// TODO this is a expected API Format, it should be transformend in ViewModel and presented here as dd-MM-yyyy oder any other desired format
				val dateFormatted: String =
					SimpleDateFormat("yyyy-MM-dd", Locale.UK).format(calendar.first().time)
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
		setupInOutFlightStations(success.data.stations)
		noData.isVisible = success.data.flights.isEmpty()
		retry_button.isVisible = false
		progress_bar.isVisible = false
		errorText.isVisible = false
	}

	private fun setupInOutFlightStations(stations: List<StationModel>){
		// TODO there the case where cities are the same in both pickers
		// TODO Also could i share this adapter somehow?? Instead of creating two
		if(areAdaptersInitialized()) return

		originStationInput.setAdapter(StationsAdapter(requireContext(), R.layout.station_item_layout, stations))
		originStationInput.threshold = 1
		originStationInput.setOnItemClickListener { adapterView, _, pos, _ ->
			currentlySelectedOrigin = adapterView.adapter.getItem(pos) as StationModel
			setTitle()
		}

		destinationStationInput.setAdapter(StationsAdapter(requireContext(), R.layout.station_item_layout, stations))
			destinationStationInput.threshold = 1
		destinationStationInput.setOnItemClickListener { adapterView, _, pos, _ ->
			currentlySelectedDestination= adapterView.adapter.getItem(pos) as StationModel
			setTitle()
		}
	}

	fun navigateToSummary(flightSummary : FlightModel){
		findNavController().navigate(FlightAvailabilityFragmentDirections.actionFirstFragmentToSecondFragment(flightSummary))
	}

	//TODO This should go to baseFragment
	private fun setTitle(){
		val toolbarView = (activity as MainActivity).toolbar

		if(currentlySelectedOrigin.name.isNotBlank() && currentlySelectedDestination.name.isNotBlank())
			toolbarView?.title = "${currentlySelectedOrigin.name}-${currentlySelectedDestination.name}"
	}

	private fun areAdaptersInitialized() = originStationInput.adapter != null && destinationStationInput.adapter != null
}
