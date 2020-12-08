package com.rwawrzyniak.flighthelper.presentation.flights

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rwawrzyniak.flighthelper.business.data.datasource.AvailabilityRepository
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
	private val repository: AvailabilityRepository,
	private val flightsAvailibityResponseNetworkMapper: FlightsAvailibityResponseNetworkMapper,
	private val useCase: ValidateSearchInputUseCase,
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
				}
			}
		}
	}

	private suspend fun actionOnSearch(intent: FlightsAvailabilityIntent.Search) {
		// Validate
		val isValid = with(intent){
			 useCase.validate(origin = origin, destination = destination, dateout = dateout, adult = adult, teen = teen, child = child)
		}

		if(isValid) fetchFlightsFromRepo(intent)
		// Search
	}

	private suspend fun fetchFlightsFromRepo(intent: FlightsAvailabilityIntent.Search) {
		_state.send(UIState.Loading)

		with(intent){
			val result: ApiResult<AvailabilityResponse> = repository.checkAvailability(
				origin = origin,
				destination = destination,
				dateout = dateout,
				adult = adult,
				teen = teen,
				child = child
			)

			val viewState = when(result){
				is ApiResult.Success -> {
					val flights: List<FlightModel> = flightsAvailibityResponseNetworkMapper.mapFromEntity(result.value)
					UIState.Success(FlightAvailabilityViewState(flights))
				}
				is ApiResult.GenericError -> UIState.Error(result.error?.message ?: "Unknown error")
				ApiResult.NetworkError -> UIState.Error("Network error")
			}

			_state.send(viewState)
		}
	}
}
