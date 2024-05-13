package com.example.storyapp.ui.screen.maps

import android.content.pm.PackageManager
import android.content.res.Resources.*
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.storyapp.R
import com.example.storyapp.databinding.ActivityMapsBinding
import com.example.storyapp.data.Resources
import com.example.storyapp.ui.MapsViewModelFactory
import com.example.storyapp.data.remote.response.Story
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.launch

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var viewmodel: MapsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        val factory = MapsViewModelFactory.getInstance(applicationContext)
        viewmodel = ViewModelProvider(this, factory)[MapsViewModel::class.java]
        setContentView(binding.root)
        setToolbar()
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isMyLocationButtonEnabled = true
        mMap.uiSettings.isCompassEnabled = true

        getMyLocation()
        setMapStyle()
        setupUsersLocation()
    }

    private fun setupUsersLocation() {
        lifecycleScope.launch {
            viewmodel.uiState.collect { result ->
                when (result) {
                    is Resources.Error -> {
                        Log.d(TAG, "Error")
                    }

                    is Resources.Loading -> {
                        Log.d(TAG, "Loading")
                    }

                    is Resources.Success -> {
                        Log.d(TAG, "Success")
                        addManyMarker(result.data)
                    }
                }
            }
        }
    }

    private fun setToolbar() {
        binding.mapsToolbar.apply {
            inflateMenu(R.menu.main_menu)
            menu.clear()
            setNavigationIcon(R.drawable.baseline_arrow_back_black_24dp)
            setNavigationContentDescription(R.string.navigate_up)
            setNavigationOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
        }
    }

    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (exception: NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", exception)
        }
    }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun addManyMarker(stories: List<Story>) {
        stories.forEach { story ->
            val latitude = story.lat
            val longitude = story.lon
            if (latitude != null && longitude != null) {
                Log.d(TAG, latitude.toString())
                val latLng = LatLng(latitude, longitude)
                val markerOption = MarkerOptions().apply {
                    position(latLng)
                    title(story.name)
                    snippet(story.description)
                }
                mMap.addMarker(markerOption)
            }
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            getMyLocation()
        }
    }
    companion object {
        const val TAG = "MapsActivity"
    }
}