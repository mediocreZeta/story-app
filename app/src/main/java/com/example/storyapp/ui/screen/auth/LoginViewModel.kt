package com.example.storyapp.ui.screen.auth

import androidx.lifecycle.ViewModel
import com.example.storyapp.data.repository.StoryRepository
import com.example.storyapp.data.Resources
import com.example.storyapp.other.SettingsPreference
import com.example.storyapp.data.remote.response.LoginResult
import kotlinx.coroutines.flow.Flow

class LoginViewModel(
    private val repository: StoryRepository,
    private val pref: SettingsPreference
) : ViewModel() {


    fun login(email: String, password: String): Flow<Resources<LoginResult>> {
        return repository.login(email, password)
    }

    suspend fun updateTokenAndSession(tokenKey: String?) {
        if (tokenKey != null) {
            pref.saveTokenKey(tokenKey)
            pref.updateSessionStatusTo(true)
        }
    }
}