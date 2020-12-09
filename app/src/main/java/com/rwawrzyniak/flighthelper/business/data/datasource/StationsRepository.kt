package com.rwawrzyniak.flighthelper.business.data.datasource

import com.rwawrzyniak.flighthelper.business.data.datasource.models.StationModel
import com.rwawrzyniak.flighthelper.business.data.db.StationsDao
import com.rwawrzyniak.flighthelper.business.data.db.mapper.StationMapperEntityToModel
import com.rwawrzyniak.flighthelper.business.data.network.ApiResult
import com.rwawrzyniak.flighthelper.business.data.network.RyanairStaticResourcesApi
import com.rwawrzyniak.flighthelper.business.data.network.mappers.StationsResponseNetworkMapper
import com.rwawrzyniak.flighthelper.business.data.network.util.safeApiCall
import com.rwawrzyniak.flighthelper.business.domain.model.StationsResponse
import com.rwawrzyniak.flighthelper.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class StationsRepository @Inject constructor(
	private val ryanairStaticResourcesApi: RyanairStaticResourcesApi,
	private val dao: StationsDao,
	private val stationsResponseNetworkMapper: StationsResponseNetworkMapper,
	private val stationMapperEntityToModel: StationMapperEntityToModel,
	@IoDispatcher private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    // TODO usually mapping to Model should be done one layer up so in UseCase or directly inViewModel
    // But to avoid saving the whole response to DB its also ok here.
    suspend fun getStations(): ApiResult<List<StationModel>> {

        withContext(dispatcher) {
            val entities = dao.getAll()

            // TODO i guess station list is not updated very frequently, if it is we should make some extra checks here, on trigger
            if (entities.isNotEmpty()) {
                val models = stationMapperEntityToModel.mapListFromEntity(entities)
                ApiResult.Success(models)
            }
        }

        val apiResult: ApiResult<StationsResponse> =
            safeApiCall(dispatcher) {
                ryanairStaticResourcesApi.getStations()
            }

        val mappedApiResult: ApiResult<List<StationModel>> = when (apiResult) {
			is ApiResult.Success -> {
				try {
					val entities = stationsResponseNetworkMapper.mapFromEntity(apiResult.value)
					dao.insertAll(entities)
					ApiResult.Success(stationMapperEntityToModel.mapListFromEntity(entities))
				} catch (e: Exception) {
					// TODO we could make here more specific error, i.e defining a new errorCode
					ApiResult.GenericError()
				}
			}
			is ApiResult.GenericError -> apiResult
			ApiResult.NetworkError -> ApiResult.NetworkError
        }

        return mappedApiResult
    }
}
