package com.example.storyapp.other

import com.example.storyapp.data.Resources
import com.example.storyapp.data.remote.response.GeneralResponse
import com.example.storyapp.data.remote.response.Story
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File

object RepositoryUtils {
    fun <T> repositoryHelper(block: suspend () -> T?): Flow<Resources<T>> {
        return flow {
            emit(Resources.Loading)
            try {
                val response = block()
                if (response != null) {
                    emit(Resources.Success(response))
                } else {
                    emit(Resources.Error(Constants.EMPTY_OR_NULL_MESSAGE))
                }
            } catch (e: HttpException) {
                println(e)
                emit(Resources.Error(parseHttpExceptionErrorMessage(e)))
            } catch (e: Exception) {
                println(e)
                emit(Resources.Error(errorMessageFormatter(e)))
            }
        }
    }

    fun toValidList(stories: List<Story?>): List<Story> {
        val output = ArrayList<Story>()
        if (stories.isNullOrEmpty()) return listOf()
        for (story in stories) {
            if (story != null) output.add(story)
        }
        return output
    }

    fun convertPhotoIntoMultipart(file: File): MultipartBody.Part {
        return MultipartBody.Part.createFormData(
            "photo",
            file.name,
            file.asRequestBody("image/jpeg".toMediaType())
        )
    }

    fun convertDescriptionIntoRequestBody(description: String): RequestBody {
        return description.toRequestBody("text/plain".toMediaType())
    }

    fun convertStringIntoTokenFormat(token: String): String {
        return "Bearer $token"
    }

    fun errorMessageFormatter(exception: Exception): String {
        return "Error: $exception"
    }

    fun parseHttpExceptionErrorMessage(e: HttpException): String {
        val jsonInString = e.response()?.errorBody()?.string()
        val errorBody = Gson().fromJson(jsonInString, GeneralResponse::class.java)
        return errorBody.message.toString()
    }
}