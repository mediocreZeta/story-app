package com.example.storyapp.ui.screen.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.storyapp.data.repository.StoryRepository
import com.example.storyapp.other.SettingsPreference

class MainViewModel(
    repository: StoryRepository,
    private val preference: SettingsPreference
) : ViewModel() {

    val paginationFlow = repository.getPagingStories().cachedIn(viewModelScope)

    suspend fun logout() {
        preference.resetAllValue()
    }

}