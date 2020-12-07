package com.rwawrzyniak.flighthelper.business.data.network.util

import com.rwawrzyniak.flighthelper.business.data.network.ErrorResponse
import com.rwawrzyniak.flighthelper.business.data.network.ApiResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException


// https://medium.com/@douglas.iacovelli/how-to-handle-errors-with-retrofit-and-coroutines-33e7492a912«
suspend fun <T> safeApiCall(dispatcher: CoroutineDispatcher, apiCall: suspend () -> T): ApiResult<T> {
    return withContext(dispatcher) {
        try {
            ApiResult.Success(apiCall.invoke())
        } catch (throwable: Throwable) {
            when (throwable) {
				is IOException -> ApiResult.NetworkError
				is HttpException -> {
					val code = throwable.code()
					val errorResponse = convertErrorBody(throwable)
					ApiResult.GenericError(code, errorResponse)
				}
                else -> {
                    ApiResult.GenericError(null, null)
                }
            }
        }
    }
}

private fun convertErrorBody(throwable: HttpException): ErrorResponse? {
    return try {
    	throwable.response()?.errorBody().toString().let {
			val jObjError = JSONObject(it)
			ErrorResponse(jObjError.getJSONObject("error").getString("message"))
        }
    } catch (exception: Exception) {
        null
    }
}
