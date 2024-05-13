package com.example.storyapp.di

import android.content.Context
import com.example.storyapp.data.local.story.StoryDatabase
import com.example.storyapp.data.remote.StoryApi
import com.example.storyapp.data.repository.StoryRepository
import com.example.storyapp.data.repository.StoryRepositoryImpl
import com.example.storyapp.other.SettingsPreference
import com.example.storyapp.other.dataStore

object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val apiService = StoryApi.apiServices
        val database = provideDatabase(context)
        val preference = provideSettingPreferences(context)
        return StoryRepositoryImpl.getInstance(apiService, database, preference)
    }

    fun provideSettingPreferences(context: Context): SettingsPreference {
        return SettingsPreference.getInstance(context.dataStore)
    }

    private fun provideDatabase(context: Context): StoryDatabase {
        return StoryDatabase.getDatabase(context)
    }
}