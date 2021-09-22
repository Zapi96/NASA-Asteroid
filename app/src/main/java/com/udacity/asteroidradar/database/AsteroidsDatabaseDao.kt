package com.udacity.asteroidradar.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.*

/**
 * Defines methods for using the Asteroids class with Room.
 */
@Dao
interface AsteroidsDatabaseDao {
    @Query("SELECT * FROM asteroids_table ORDER BY closeApproachDate DESC")
    fun getAllAsteroids(): LiveData<List<AsteroidsData>>

    @Query("SELECT * FROM asteroids_table WHERE closeApproachDate == :today ORDER BY closeApproachDate DESC")
    fun getTodayAsteroids(today: String): LiveData<List<AsteroidsData>>

    @Query("SELECT * FROM asteroids_table WHERE closeApproachDate BETWEEN :firstDay AND :lastDay ORDER BY closeApproachDate DESC")
    fun getSavedAsteroids(firstDay: String,lastDay: String): LiveData<List<AsteroidsData>>

    @Query("SELECT * FROM asteroids_table WHERE closeApproachDate BETWEEN :monday AND :sunday ORDER BY closeApproachDate DESC")
    fun getWeekAsteroids(monday: String,sunday: String): LiveData<List<AsteroidsData>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll( asteroids: Array<AsteroidsData>)
}

/**
 * Defines methods for using the Pictures class with Room.
 */
@Dao
interface PicturesDatabaseDao {
    @Query("SELECT * FROM pictures_table ORDER BY id DESC LIMIT 1")
    fun getLastPicture(): LiveData<PicturesData>

    @Query("SELECT * FROM pictures_table WHERE mediaType == 'image' ORDER BY id DESC LIMIT 1")
    fun getPreviousPicture(): LiveData<PicturesData>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll( picture: PicturesData)
}

