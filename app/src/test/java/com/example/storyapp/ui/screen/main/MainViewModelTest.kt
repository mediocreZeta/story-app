package com.example.storyapp.ui.screen.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.testing.asSnapshot
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import com.example.storyapp.DataDummy
import com.example.storyapp.MainDispatcherRule
import com.example.storyapp.data.local.story.StoryEntity
import com.example.storyapp.data.repository.StoryRepository
import com.example.storyapp.other.SettingsPreference
import com.example.storyapp.ui.adapter.StoryAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    @Mock
    private lateinit var repository: StoryRepository

    @Mock
    //Tidak akan digunakan dalam test, hanya untuk parameter viewmodel
    private lateinit var settingsPreference: SettingsPreference

    @Test
    fun `when Get story have data return not null`() = runTest {
        val dummyQuote = DataDummy.generateDummyStoryResponse()
        val data: PagingData<StoryEntity> = PagingData.from(dummyQuote)
        val expectedQuote = flow { emit(data) }
        Mockito.`when`(repository.getPagingStories()).thenReturn(expectedQuote)

        val mainViewModel = MainViewModel(repository, settingsPreference)

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = listUpdateCallback,
            mainDispatcher = Dispatchers.Main
        )
        val actualQuote: PagingData<StoryEntity> = mainViewModel.paginationFlow.first()
        differ.submitData(actualQuote)

        assertNotNull(differ.snapshot())
        assertEquals(dummyQuote.size, differ.snapshot().size)
        assertEquals(dummyQuote[0], differ.snapshot()[0])
    }

    @Test
    fun `when Get story Empty Should Return No Data`() = runTest {
        val data: PagingData<StoryEntity> = PagingData.from(emptyList())
        val expectedQuote = flow { emit(data) }
        Mockito.`when`(repository.getPagingStories()).thenReturn(expectedQuote)

        val mainViewModel = MainViewModel(repository, settingsPreference)
        val actualQuote: PagingData<StoryEntity> = mainViewModel.paginationFlow.first()
        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = listUpdateCallback,
            mainDispatcher = Dispatchers.Main
        )
        differ.submitData(actualQuote)
        assertEquals(0, differ.snapshot().size)
    }
}

val listUpdateCallback = object : ListUpdateCallback {
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
}





