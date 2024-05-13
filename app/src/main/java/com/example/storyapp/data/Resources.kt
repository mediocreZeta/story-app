package com.example.storyapp.data

sealed class Resources<out T> {
    data class Success<T>(val data: T) : Resources<T>()
    data object Loading : Resources<Nothing>()
    data class Error(val errorMessage: String) : Resources<Nothing>()
}