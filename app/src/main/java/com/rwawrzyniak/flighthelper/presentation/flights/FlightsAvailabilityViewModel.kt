package com.rwawrzyniak.flighthelper.presentation.flights

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rwawrzyniak.flighthelper.business.data.datasource.AvailabilityRepository
import com.rwawrzyniak.flighthelper.business.data.datasource.StationsRepository
import com.rwawrzyniak.flighthelper.business.data.network.ApiResult
import com.rwawrzyniak.flighthelper.business.data.network.mappers.FlightsAvailibityResponseNetworkMapper
import com.rwawrzyniak.flighthelper.business.domain.model.AvailabilityResponse
import com.rwawrzyniak.flighthelper.presentation.UIState
import com.rwawrzyniak.flighthelper.presentation.flights.state.FlightAvailabilityViewState
import com.rwawrzyniak.flighthelper.presentation.flights.state.FlightsAvailabilityIntent
import com.rwawrzyniak.flighthelper.presentation.flights.usecase.ValidateSearchInputUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class FlightsAvailabilityViewModel
@ViewModelInject constructor(
	private val availabilityRepository: AvailabilityRepository,
	private val flightsAvailibityResponseNetworkMapper: FlightsAvailibityResponseNetworkMapper,
	private val useCase: ValidateSearchInputUseCase,
	private val stationsRepository: StationsRepository,
	@Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {


	private val flightsAvailabilityIntent = Channel<FlightsAvailabilityIntent>(Channel.UNLIMITED)
	private val _state = ConflatedBroadcastChannel<UIState<FlightAvailabilityViewState>>(UIState.Idle)
	val viewState: Flow<UIState<FlightAvailabilityViewState>>
		get() = _state.asFlow()

	init {
		handleIntent()
	}

	suspend fun sendIntent(intent: FlightsAvailabilityIntent){
		flightsAvailabilityIntent.send(intent)
	}

	private fun handleIntent() {
		viewModelScope.launch {
			flightsAvailabilityIntent.consumeAsFlow().collectLatest { intent ->
				when(intent){
					is FlightsAvailabilityIntent.Search -> actionOnSearch(intent)
					is FlightsAvailabilityIntent.Initialize -> publishStateWithStations()
				}
			}
		}
	}

	private suspend fun actionOnSearch(intent: FlightsAvailabilityIntent.Search) {
		// Validate
		val isValid = with(intent){
			 useCase.validate(query)
		}

		if(isValid) publishStateWithFlights(intent)
		// Search
	}

	private suspend fun publishStateWithStations() {
		_state.send(UIState.Loading)

		val result = stationsRepository.getStations()
		val viewState: UIState<FlightAvailabilityViewState> =
			when (result) {
				is ApiResult.Success ->
					UIState.Success(
						_state.value.toData()?.copy(stations = result.value)
							?: FlightAvailabilityViewState(stations = result.value, flights = listOf())
					)
				is ApiResult.GenericError -> UIState.Error(result.error?.message ?: "Unknown error")
				ApiResult.NetworkError -> UIState.Error("Network error")
			}

		_state.send(viewState)
	}

	private suspend fun publishStateWithFlights(intent: FlightsAvailabilityIntent.Search) {
		_state.send(UIState.Loading)

		with(intent.query){
			val result: ApiResult<AvailabilityResponse> = availabilityRepository.checkAvailability(
				origin = origin.code,
				destination = destination.code,
				dateout = dateout,
				adult = adult,
				teen = teen,
				child = child
			)

			val viewState = when(result){
				is ApiResult.Success -> {
					val flightSearchResultModel = flightsAvailibityResponseNetworkMapper.mapFromEntity(result.value)


					// We shouldnt pass stations every time view is updated, one time on start is enough (this list is pretty long)
					val updatedViewState = FlightAvailabilityViewState(flights = flightSearchResultModel.flights)
					UIState.Success(updatedViewState)
				}
				is ApiResult.GenericError -> UIState.Error(result.error?.message ?: "Unknown error")
				ApiResult.NetworkError -> UIState.Error("Network error")
			}

			_state.send(viewState)
		}
	}
}
