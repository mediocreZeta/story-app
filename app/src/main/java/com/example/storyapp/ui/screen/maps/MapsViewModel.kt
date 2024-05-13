package com.example.storyapp.ui.screen.maps

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.repository.StoryRepository
import com.example.storyapp.data.Resources
import com.example.storyapp.other.SettingsPreference
import com.example.storyapp.data.remote.response.Story
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MapsViewModel(
    private val repository: StoryRepository,
    private val preference: SettingsPreference,
) : ViewModel() {

    private val _uiState = MutableStateFlow<Resources<List<Story>>>(Resources.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        getLocation()
    }

    private fun getLocation() {
        viewModelScope.launch {
            _uiState.update { Resources.Loading }
            val token = preference.getTokenKey().first()
            _uiState.update {
                repository.getStories(
                    token = token,
                    location = 1
                )
            }
        }
    }
}