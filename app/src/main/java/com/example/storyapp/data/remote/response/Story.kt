package com.example.storyapp.data.remote.response

import android.os.Parcelable
import com.example.storyapp.data.local.story.StoryEntity
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Parcelize
data class Story(
    val createdAt: String? = null,
    val description: String? = null,
    val id: String? = null,
    val lat: Double? = null,
    val lon: Double? = null,
    val name: String? = null,
    val photoUrl: String? = null
) : Parcelable

fun Story.toEntityStory(): StoryEntity {
    val parsedDate = LocalDateTime.parse(createdAt, DateTimeFormatter.ISO_DATE_TIME)
    val formattedDate = parsedDate.format(DateTimeFormatter.ofPattern("dd MMMM yyyy"))
    return StoryEntity(
        id!!, photoUrl, name, formattedDate, description, lat, lon
    )
}