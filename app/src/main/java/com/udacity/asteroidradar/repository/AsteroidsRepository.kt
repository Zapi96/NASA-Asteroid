package com.udacity.asteroidradar.repository

import android.annotation.SuppressLint
import android.net.Network
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.BuildConfig
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.api.*
import com.udacity.asteroidradar.database.AsteroidsDatabase
import com.udacity.asteroidradar.database.asDatabaseModel
import com.udacity.asteroidradar.database.asDomainModel
import com.udacity.asteroidradar.main.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

enum class AsteroidsApiStatus { LOADING, DONE }


class AsteroidsRepository(private val database: AsteroidsDatabase) {

    private val APIKey = BuildConfig.NASA_API_KEY


    @RequiresApi(Build.VERSION_CODES.O)
    val currentDay = currentDate()
    @RequiresApi(Build.VERSION_CODES.O)
    val finalDay = finalDate()
    @RequiresApi(Build.VERSION_CODES.O)
    val sunday = sundayDate()


    val picture: LiveData<PictureOfDay> = Transformations.map(database.picturesDatabaseDao.getLastPicture()) {
        it?.asDomainModel()
    }

    val pictureExtra: LiveData<PictureOfDay> = Transformations.map(database.picturesDatabaseDao.getPreviousPicture()) {
        it?.asDomainModel()
    }


    suspend fun refreshAsteroids() {
        withContext(Dispatchers.IO) {

            try {
                val asteroids = AsteoridApi.retrofitService.getAsteroids(currentDay, finalDay, APIKey)
                val parsedAsteroids = parseAsteroidsJsonResult(JSONObject(asteroids))
                database.asteroidsDatabaseDao.insertAll(parsedAsteroids.asDatabaseModel())
            } catch (e: Exception) {
                Log.w("ERROR", e.message.toString())
            }

        }
    }

    suspend fun refreshPicture() {
        withContext(Dispatchers.IO) {
            try {
                val picture = PictureApi.retrofitService.getPicture( APIKey)
                database.picturesDatabaseDao.insertAll(picture.asDatabaseModel())
            } catch (e: Exception) {
                Log.w("ERROR", e.message.toString())
            }

        }
    }

    fun getAsteroidsSelected(filter: AsteroidsApiFilter): LiveData<List<Asteroid>> {
        return Transformations.map(when (filter) {
            AsteroidsApiFilter.SHOW_WEEK ->  database.asteroidsDatabaseDao.getWeekAsteroids(currentDay,sunday)
            AsteroidsApiFilter.SHOW_TODAY -> database.asteroidsDatabaseDao.getTodayAsteroids(currentDay)
            else -> database.asteroidsDatabaseDao.getSavedAsteroids(currentDay,finalDay)}) {
                    it.asDomainModel()
                }


    }
}