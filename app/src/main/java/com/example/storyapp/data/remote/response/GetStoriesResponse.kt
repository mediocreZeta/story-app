package com.example.storyapp.data.remote.response

data class GetStoriesResponse(
    val error: Boolean?,
    val listStory: List<Story?>?,
    val message: String?
)