package com.example.storyapp.data

import android.content.Context
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingConfig
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.storyapp.FakeApiServices
import com.example.storyapp.data.local.story.StoryDatabase
import com.example.storyapp.data.local.story.StoryEntity
import com.example.storyapp.other.SettingsPreference
import com.example.storyapp.other.dataStore
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalPagingApi
@RunWith(AndroidJUnit4::class)
class StoryRemoteMediatorTest {
    private lateinit var database: StoryDatabase
    private lateinit var apiServices: FakeApiServices
    private lateinit var preference: SettingsPreference

    @Before
    fun initParams() {
        val context: Context = ApplicationProvider.getApplicationContext()

        database = Room.inMemoryDatabaseBuilder(
            context,
            StoryDatabase::class.java
        ).allowMainThreadQueries().build()
        apiServices = FakeApiServices()
        preference = SettingsPreference.getInstance(context.dataStore)
    }

    @After
    fun clearParams() {
        database.close()
    }

    @Test
    fun refreshLoadReturnsSuccessResultWhenMoreDataIsPresent() = runBlocking {
        val remoteMediator = StoryRemoteMediator(database, apiServices, preference)
        val pagingState = PagingState<Int, StoryEntity>(
            listOf(),
            null,
            PagingConfig(10),
            10
        )
        val result = remoteMediator.load(LoadType.REFRESH, pagingState)
        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertFalse((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
    }
}