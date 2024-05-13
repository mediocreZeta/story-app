package com.example.storyapp

import com.example.storyapp.data.remote.StoryApi
import com.example.storyapp.data.remote.response.GetStoriesResponse
import com.example.storyapp.data.remote.response.GeneralResponse
import com.example.storyapp.data.remote.response.GetDetailStoryResponse
import com.example.storyapp.data.remote.response.LoginResponse
import com.example.storyapp.data.remote.response.Story
import okhttp3.MultipartBody
import okhttp3.RequestBody

class FakeApiServices : StoryApi {


    override suspend fun register(list: String, email: String, password: String): GeneralResponse {
        TODO("Not yet implemented")
    }

    override suspend fun login(email: String, password: String): LoginResponse {
        TODO("Not yet implemented")
    }

    override suspend fun addStory(
        token: String,
        file: MultipartBody.Part,
        description: RequestBody,
        lat: Float?,
        lon: Float?
    ): GeneralResponse {
        TODO("Not yet implemented")
    }

    override suspend fun addStoryAsGuest(
        file: MultipartBody.Part,
        description: RequestBody,
        lat: Float?,
        lon: Float?
    ): GeneralResponse {
        TODO("Not yet implemented")
    }

    override suspend fun getStories(
        token: String,
        page: Int,
        size: Int,
        location: Int?
    ): GetStoriesResponse {
        val items: MutableList<Story> = arrayListOf()
        for (i in 0..100) {
            val quote = Story(
                id = i.toString(),
                name = "author + $i",
                description = "quote $i",
            )
            items.add(quote)
        }
        val list = items.subList((page - 1) * size, (page - 1) * size + size)
        val response = GetStoriesResponse(
            error = false,
            listStory = list,
            message = "No error"
        )
        return response
    }

    override suspend fun getDetailStory(token: String, id: String): GetDetailStoryResponse {
        TODO("Not yet implemented")
    }
}
