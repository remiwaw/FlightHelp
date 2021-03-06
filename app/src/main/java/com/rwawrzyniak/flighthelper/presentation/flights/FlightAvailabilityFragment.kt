package com.rwawrzyniak.flighthelper.presentation.flights

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
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
import com.rwawrzyniak.flighthelper.presentation.flights.usecase.ValidateSearchInputUseCase
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.ldralighieri.corbind.view.clicks
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class FlightAvailabilityFragment @Inject constructor(private val validateSearchInputUseCase: ValidateSearchInputUseCase) : Fragment(R.layout.fragment_flight_availability) {


	private val flightAdapter: FlightsAdapter by lazy { FlightsAdapter(mutableListOf(), ::navigateToSummary) }
	private val viewModel: FlightsAvailabilityViewModel by viewModels()

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
				val origin = originStationInput.tag
				val destination = destinationStationInput.tag
				val safeOrigin = if(origin != null) origin as StationModel else StationModel()
				val safeDestination = if(destination != null) destination as StationModel else StationModel()
				val query = CheckAvailabilityQuery(
					origin = safeOrigin,
					destination = safeDestination,
					dateout = departureDateInput.text.toString(),
					adult = adultsNumberPicker.value,
					teen = teenNumberPicker.value,
					child = childrenNumberPicker.value
				)
				quickValidation(query)
				withContext(Dispatchers.IO) {
					viewModel.sendIntent(
						FlightsAvailabilityIntent.Search(
							query
						)
					)
				}
			}
	}

	// This should be done only in viewmodel probably
	private suspend fun quickValidation(query: CheckAvailabilityQuery){
		withContext(Dispatchers.Main){
			if(validateSearchInputUseCase.validate(query).not()){
				Toast.makeText(requireContext(), "Invalidation failed, check your fields", LENGTH_SHORT).show()
			} else (
				setTitle( "${query.origin.name}-${query.destination.name}")
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
			val currentlySelectedOrigin = adapterView.adapter.getItem(pos) as StationModel
			originStationInput.tag =  currentlySelectedOrigin
		}

		destinationStationInput.setAdapter(StationsAdapter(requireContext(), R.layout.station_item_layout, stations))
			destinationStationInput.threshold = 1
		destinationStationInput.setOnItemClickListener { adapterView, _, pos, _ ->
			val currentlySelectedDestination= adapterView.adapter.getItem(pos) as StationModel
			destinationStationInput.tag = currentlySelectedDestination
		}
		// Todo user should be forced to choose from the list
		val chooseFromTheList = Toast.makeText(requireContext(), "You have to choose item from the list", LENGTH_SHORT)
		originStationInput.setOnClickListener {
			originStationInput.text.clear()
			chooseFromTheList.show()
		}
		originStationInput.setOnDismissListener {
			destinationStationInput.text.clear()
			chooseFromTheList.show()
		}
	}

	fun navigateToSummary(flightSummary : FlightModel){
		findNavController().navigate(FlightAvailabilityFragmentDirections.actionFirstFragmentToSecondFragment(flightSummary))
	}

	//TODO This should go to baseFragment
	private fun setTitle(title: String){
		val toolbarView = (activity as MainActivity).toolbar
		toolbarView?.title = title
	}

	private fun areAdaptersInitialized() = originStationInput.adapter != null && destinationStationInput.adapter != null
}
