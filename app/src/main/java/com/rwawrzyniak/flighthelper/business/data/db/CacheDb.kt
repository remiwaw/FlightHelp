package com.rwawrzyniak.flighthelper.business.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.rwawrzyniak.flighthelper.business.data.db.entitiy.StationsEntity

@Database(
	entities = [StationsEntity::class],
	version = 1
)
abstract class CacheDb : RoomDatabase() {
	abstract fun getStationsDao(): StationsDao

	companion object {
		const val DB_NAME = "cacheDb"
	}
}


