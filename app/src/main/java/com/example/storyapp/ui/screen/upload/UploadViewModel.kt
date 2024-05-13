package com.example.storyapp.ui.screen.upload

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.repository.StoryRepository
import com.example.storyapp.data.Resources
import com.example.storyapp.other.SettingsPreference
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

class UploadViewModel(
    private val repository: StoryRepository,
    private val preference: SettingsPreference
) : ViewModel() {
    private val _uploadState = MutableStateFlow<Resources<String>>(Resources.Loading)
    val uploadState = _uploadState.asStateFlow()

    fun uploadAsAuthenticated(
        file: File,
        description: String,
        lat: Float? = null,
        lon: Float? = null
    ) {
        viewModelScope.launch {
            val token = preference.getTokenKey().first()
            if (token.isNotEmpty()) {
                repository.addStory(token, file, description, lat, lon).collect { result ->
                    _uploadState.update { result }
                }
            }
        }
    }


    fun uploadAsGuest(file: File, description: String, lat: Float? = null, lon: Float? = null) {
        viewModelScope.launch {
            repository.addStoryAsGuest(file, description, lat, lon).collect { result ->
                _uploadState.update { result }
            }
        }
    }

}