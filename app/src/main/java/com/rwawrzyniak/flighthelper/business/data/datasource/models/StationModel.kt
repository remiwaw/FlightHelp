package com.rwawrzyniak.flighthelper.business.data.datasource.models

data class StationModel(
	val code: String,
	val name: String
){
	override fun toString(): String = "$name [$code]"
}
