package com.rwawrzyniak.flighthelper.presentation

sealed class UIState<out T: Any> {
	object Idle: UIState<Nothing>()
	object Loading: UIState<Nothing>()
   	data class Error(val errorMessage: String): UIState<Nothing>()
	data class Success<out T: Any>(val data: T): UIState<T>()

	// TODO check for NPE in databinding
	fun toData(): T? = if(this is Success) this.data else null
}
