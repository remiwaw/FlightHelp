package com.rwawrzyniak.flighthelper.business.data.db.mapper

import com.rwawrzyniak.flighthelper.business.data.datasource.models.StationModel
import com.rwawrzyniak.flighthelper.business.data.db.entitiy.StationsEntity
import com.rwawrzyniak.flighthelper.business.domain.util.EntityMapper
import javax.inject.Inject


class StationMapperEntityToModel @Inject constructor(): EntityMapper<StationsEntity, StationModel> {

	fun mapListFromEntity(entities: List<StationsEntity>): List<StationModel> {
		// TODO actually calling map on big collections may be much slower than iterating over array
		return entities.map { StationModel(it.code, it.name) }
	}

	override fun mapFromEntity(entity: StationsEntity): StationModel = with(entity){
		StationModel(code, name)
	}

	override fun mapToEntity(domainModel: StationModel): StationsEntity {
		error("Not used yet")
	}
}
