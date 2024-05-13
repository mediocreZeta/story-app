package com.example.storyapp.ui.screen.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.repository.StoryRepository
import com.example.storyapp.data.Resources
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val repository: StoryRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<Resources<String>>(Resources.Loading)
    val uiState = _uiState.asStateFlow()

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            repository.register(name, email, password).collect { result ->
                _uiState.value = result
            }
        }
    }
}