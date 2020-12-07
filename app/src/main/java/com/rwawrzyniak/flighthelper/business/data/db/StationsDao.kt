package com.rwawrzyniak.flighthelper.business.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rwawrzyniak.flighthelper.business.data.db.entitiy.StationsEntity

@Dao
interface StationsDao {
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertAll(posts: List<StationsEntity>)

	@Query("SELECT * FROM station WHERE code = :code")
	fun getNameByCode(code: String) : List<StationsEntity>

	@Query("SELECT * FROM station")
	fun getAll(): List<StationsEntity>
}
