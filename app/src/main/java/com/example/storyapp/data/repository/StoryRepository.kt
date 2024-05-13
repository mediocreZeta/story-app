package com.example.storyapp.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.storyapp.data.StoryRemoteMediator
import com.example.storyapp.data.local.story.StoryDatabase
import com.example.storyapp.data.local.story.StoryEntity
import com.example.storyapp.data.remote.StoryApi
import com.example.storyapp.data.remote.response.LoginResult
import com.example.storyapp.data.remote.response.Story
import com.example.storyapp.other.Constants
import com.example.storyapp.other.RepositoryUtils
import com.example.storyapp.data.Resources
import com.example.storyapp.other.SettingsPreference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.File

interface StoryRepository {
    fun register(name: String, email: String, password: String): Flow<Resources<String>>

    fun login(email: String, password: String): Flow<Resources<LoginResult>>

    suspend fun addStory(
        token: String,
        file: File,
        description: String,
        lat: Float? = null,
        lon: Float? = null
    ): Flow<Resources<String>>

    suspend fun addStoryAsGuest(
        file: File,
        description: String,
        lat: Float? = null,
        lon: Float? = null
    ): Flow<Resources<String>>

    suspend fun getStories(
        token: String,
        size: Int? = 10,
        page: Int? = null,
        location: Int? = null
    ): Resources<List<Story>>

    suspend fun getStoryDetail(
        token: String,
        id: String
    ): Flow<Resources<Story>>

    fun getPagingStories(): Flow<PagingData<StoryEntity>>

}

class StoryRepositoryImpl(
    private val apiServices: StoryApi,
    private val database: StoryDatabase,
    private val preference: SettingsPreference
) :
    StoryRepository {
    override fun register(
        name: String,
        email: String,
        password: String
    ): Flow<Resources<String>> {
        val x = Result.success("something")
        return flow {
            emit(Resources.Loading)
            try {
                val response = apiServices.register(name, email, password).message
                if (response != null) {
                    emit(Resources.Success(response))
                } else {
                    emit(Resources.Error(Constants.EMPTY_OR_NULL_MESSAGE))
                }
            } catch (e: HttpException) {
                emit(Resources.Error(RepositoryUtils.parseHttpExceptionErrorMessage(e)))
            }
        }


    }

    override fun login(email: String, password: String): Flow<Resources<LoginResult>> {

        return RepositoryUtils.repositoryHelper {
            apiServices.login(email, password).loginResult
        }
    }

    override suspend fun addStory(
        token: String,
        file: File,
        description: String,
        lat: Float?,
        lon: Float?
    ): Flow<Resources<String>> {
        return RepositoryUtils.repositoryHelper {
            val parsedFile = RepositoryUtils.convertPhotoIntoMultipart(file)
            val parsedToken = RepositoryUtils.convertStringIntoTokenFormat(token)
            val parsedDescription =
                RepositoryUtils.convertDescriptionIntoRequestBody(description)
            apiServices.addStory(parsedToken, parsedFile, parsedDescription, lat, lon).message
        }

    }

    override suspend fun addStoryAsGuest(
        file: File,
        description: String,
        lat: Float?,
        lon: Float?
    ): Flow<Resources<String>> {
        return RepositoryUtils.repositoryHelper {
            val parsedFile = RepositoryUtils.convertPhotoIntoMultipart(file)
            val parsedDescription =
                RepositoryUtils.convertDescriptionIntoRequestBody(description)
            apiServices.addStoryAsGuest(parsedFile, parsedDescription, lat, lon).message
        }
    }

    override suspend fun getStories(
        token: String,
        size: Int?,
        page: Int?,
        location: Int?
    ): Resources<List<Story>> {
        return try {
            val validToken = RepositoryUtils.convertStringIntoTokenFormat(token)
            val list = apiServices.getStories(
                token = validToken,
                location = location
            )
            val currentData = list.listStory
            if (currentData != null) {
                Resources.Success(RepositoryUtils.toValidList(currentData))
            } else {
                Resources.Error(Constants.EMPTY_OR_NULL_MESSAGE)
            }
        } catch (e: HttpException) {
            Resources.Error(RepositoryUtils.parseHttpExceptionErrorMessage(e))
        } catch (e: Exception) {
            Resources.Error(RepositoryUtils.errorMessageFormatter(e))
        }
    }

    override suspend fun getStoryDetail(token: String, id: String): Flow<Resources<Story>> {
        return RepositoryUtils.repositoryHelper {
            apiServices.getDetailStory(token, id).story
        }
    }

    @OptIn(ExperimentalPagingApi::class)
    override fun getPagingStories(): Flow<PagingData<StoryEntity>> {
        return Pager(
            config = PagingConfig(
                pageSize = Constants.PAGE_SIZE
            ),
            remoteMediator = StoryRemoteMediator(database, apiServices, preference),
            pagingSourceFactory = {
                database.storyDao().getAllStory()
            }
        ).flow
    }

    companion object {
        @Volatile
        private var instance: StoryRepository? = null
        fun getInstance(
            apiService: StoryApi,
            database: StoryDatabase,
            preference: SettingsPreference
        ): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepositoryImpl(apiService, database, preference)
            }.also { instance = it }
    }
}
