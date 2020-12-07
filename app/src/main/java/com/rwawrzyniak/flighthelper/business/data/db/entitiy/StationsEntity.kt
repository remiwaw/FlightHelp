package com.rwawrzyniak.flighthelper.business.data.db.entitiy

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "station")
data class StationsEntity(

	@PrimaryKey(autoGenerate = false)
	@ColumnInfo(name = "code")
	val code: String,
	@ColumnInfo(name = "name")
	val name: String
)
