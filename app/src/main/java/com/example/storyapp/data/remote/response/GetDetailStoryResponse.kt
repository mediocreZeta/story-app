package com.example.storyapp.data.remote.response

data class GetDetailStoryResponse(
    val error: Boolean?,
    val message: String?,
    val story: Story?
)