package com.example.storyapp.data.remote.response

data class LoginResponse(
    val error: Boolean?,
    val loginResult: LoginResult?,
    val message: String?
)