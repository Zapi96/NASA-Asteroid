package com.udacity.asteroidradar.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.udacity.asteroidradar.PictureOfDay

@Entity(tableName = "pictures_table")
data class PicturesData (
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val mediaType: String,
    val title: String,
    val url: String
)

fun PicturesData.asDomainModel(): PictureOfDay {
    return PictureOfDay (
            mediaType = this.mediaType,
            title = this.title,
            url = this.url
        )
}


fun PictureOfDay.asDatabaseModel(): PicturesData {
    return PicturesData(
        mediaType = this.mediaType,
        title = this.title,
        url = this.url
        )
}

