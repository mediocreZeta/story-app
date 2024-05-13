package com.example.storyapp.ui.screen.main

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.storyapp.R
import com.example.storyapp.databinding.FragmentMainBinding
import com.example.storyapp.other.wrapEspressoIdlingResource
import com.example.storyapp.ui.MainViewModelFactory
import com.example.storyapp.ui.adapter.LoadingStateAdapter
import com.example.storyapp.ui.adapter.StoryAdapter
import com.example.storyapp.ui.screen.maps.MapsActivity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainFragment : Fragment() {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        val factory = MainViewModelFactory.getInstance(requireActivity())
        viewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAppBar()
        setupMainScreen()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupMainScreen() {
        val adapter = StoryAdapter()
        binding.rvStory.layoutManager = LinearLayoutManager(requireActivity())
        binding.rvStory.adapter = adapter
        binding.rvStory.adapter = adapter.withLoadStateFooter(
            LoadingStateAdapter(adapter::retry)
        )

        adapterObserver(adapter)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.paginationFlow.collectLatest {
                adapter.submitData(it)
            }
        }


        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_cameraFragment)
        }
    }

    private fun setupAppBar() {
        binding.mainToolbar.apply {
            inflateMenu(R.menu.main_menu)
            setTitle(R.string.app_name)
            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.btn_logout -> {
                        lifecycleScope.launch {
                            wrapEspressoIdlingResource {
                                viewModel.logout()
                                findNavController().navigate(R.id.action_mainFragment_to_loginFragment)
                            }
                        }
                        true
                    }

                    R.id.btn_language -> {
                        startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                        true
                    }

                    R.id.btn_map -> {
                        val intent = Intent(requireActivity(), MapsActivity::class.java)
                        startActivity(intent)
                        true
                    }

                    else -> false

                }
            }
        }
    }

    private fun adapterObserver(adapter: StoryAdapter) {
        adapter.addLoadStateListener { loadStates ->
            //Mediator ada karena menggunakan Remote Mediator
            when (val result = loadStates.mediator!!.refresh) {
                is LoadState.NotLoading -> {
                    val isLoading = loadStates.append is LoadState.NotLoading &&
                            loadStates.prepend is LoadState.NotLoading &&
                            loadStates.refresh is LoadState.NotLoading &&
                            loadStates.source.refresh is LoadState.NotLoading
                    if (isLoading && adapter.itemCount == 0) {
                        showErrorScreen(getString(R.string.empty_data), adapter::refresh)
                    } else {
                        showMainScreen { }
                    }
                }

                is LoadState.Loading -> {
                    if (adapter.itemCount == 0) {
                        showLoadingScreen()
                    } else {
                        showMainScreen { }
                    }
                }

                is LoadState.Error -> {
                    val errorMessage = result.error.localizedMessage ?: "Error"
                    showErrorScreen(errorMessage, adapter::retry)
                }
            }
        }

    }


    private fun showErrorScreen(message: String, retry: () -> Unit) {
        binding.rvStory.visibility = View.GONE
        binding.itemLoading.apply {
            retryButton.apply {
                visibility = View.VISIBLE
                isEnabled = true
                setOnClickListener { retry() }
            }
            errorMsg.apply {
                visibility = View.VISIBLE
                errorMsg.text = message
            }
            progressBar.visibility = View.GONE
        }
    }

    private fun showMainScreen(cancel: () -> Unit) {
        binding.itemLoading.apply {
            errorMsg.visibility = View.GONE
            progressBar.visibility = View.GONE
            retryButton.visibility = View.GONE
        }
        binding.rvStory.visibility = View.VISIBLE
        cancel()
    }

    private fun showLoadingScreen() {
        binding.itemLoading.apply {
            errorMsg.visibility = View.GONE
            retryButton.visibility = View.GONE
            progressBar.visibility = View.VISIBLE
        }
        binding.rvStory.visibility = View.GONE
    }

    companion object {
        val TAG: String = MainFragment::class.java.simpleName
    }
}