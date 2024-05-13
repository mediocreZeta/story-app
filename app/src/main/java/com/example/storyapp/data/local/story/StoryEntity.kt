package com.example.storyapp.data.local.story

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import retrofit2.http.Field

@Entity(tableName = "story")
@Parcelize
data class StoryEntity(

    @PrimaryKey
    @field:SerializedName("id")
    val id: String,

    @field:SerializedName("photo_url")
    val photoUrl: String? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("created_date")
    val createdDate: String? = null,

    @field:SerializedName("description")
    val description: String? = null,

    @field:SerializedName("latitude")
    val lat: Double? = null,

    @field:SerializedName("longitude")
    val lon: Double? = null,
) : Parcelable