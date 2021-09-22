package com.udacity.asteroidradar.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.api.AsteroidsApiFilter
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.repository.AsteroidsApiStatus
import com.udacity.asteroidradar.repository.AsteroidsApiStatus.*
import com.udacity.asteroidradar.repository.AsteroidsRepository
import kotlinx.coroutines.launch


class MainViewModel( application: Application) : ViewModel() {

    /**
     * Database initialization
     */
    private val database = getDatabase(application)

    private val asteroidsRepository = AsteroidsRepository(database)

    private val asteroidsFilter = MutableLiveData<AsteroidsApiFilter>()


    val picture = asteroidsRepository.picture
    val pictureExtra = asteroidsRepository.pictureExtra

    private val _navigateToSelectedAsteroid = MutableLiveData<Asteroid>()
    val navigateToSelectedAsteroid: LiveData<Asteroid>
        get() = _navigateToSelectedAsteroid



    /**
     * Call getAsteroidProperties() on init so we can display status immediately.
     */
    init {
        asteroidsFilter.value = AsteroidsApiFilter.SHOW_SAVED
        try {
            viewModelScope.launch {
                asteroidsRepository.refreshAsteroids()
                asteroidsRepository.refreshPicture()

            }
        } catch (e: Exception) {
            Log.w("ERROR", e.message.toString())
        }
    }


    /**
     * Navigation
     */
    fun displayAsteroidDetails(asteroid: Asteroid) {
        _navigateToSelectedAsteroid.value = asteroid
    }

    fun displayAsteroidDetailsComplete() {
        _navigateToSelectedAsteroid.value = null
    }


    /**
     * Filter
     */
    fun updateFilter(filter: AsteroidsApiFilter) {
        asteroidsFilter.value = filter

    }

    val asteroids = Transformations.switchMap(asteroidsFilter){
        asteroidsRepository.getAsteroidsSelected(it)
    }

}