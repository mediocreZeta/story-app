package com.example.storyapp.ui.screen.upload

import android.Manifest
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.storyapp.R
import com.example.storyapp.data.Resources
import com.example.storyapp.databinding.FragmentUploadBinding
import com.example.storyapp.other.getImageUri
import com.example.storyapp.other.reduceFileImage
import com.example.storyapp.other.uriToFile
import com.example.storyapp.ui.UploadViewModelFactory
import com.example.storyapp.ui.customviews.CustomAlertDialog
import com.example.storyapp.ui.customviews.CustomLoadingDialog
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.google.android.gms.location.SettingsClient
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class UploadFragment : Fragment() {
    private lateinit var viewModel: UploadViewModel
    private var _binding: FragmentUploadBinding? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private val binding get() = _binding!!
    private var currentImageUri: Uri? = null

    private lateinit var loadingDialog: CustomLoadingDialog
    private lateinit var successDialog: CustomAlertDialog
    private lateinit var errorDialog: CustomAlertDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUploadBinding.inflate(inflater, container, false)
        val factory = UploadViewModelFactory.getInstance(requireActivity())
        viewModel = ViewModelProvider(this, factory)[UploadViewModel::class.java]
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        loadingDialog = CustomLoadingDialog(requireActivity())
        errorDialog = CustomAlertDialog(requireActivity())
        successDialog = CustomAlertDialog(requireActivity())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAppbar()
        setupButton()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupAppbar() {
        binding.cameraToolbar.apply {
            inflateMenu(R.menu.main_menu)
            menu.clear()
            title = resources.getString(R.string.upload_story)
            setNavigationContentDescription(R.string.navigate_up)
            setNavigationIcon(R.drawable.baseline_arrow_back_black_24dp)
            setNavigationOnClickListener { findNavController().navigateUp() }
        }
    }

    private fun setupLocationData(latitude: String = "", longitude: String = "", visibility: Int) {
        with(binding) {
            tvLatitudeValue.text = latitude
            tvLongitudeValue.text = longitude
            tvLongitudeValue.visibility = visibility
            tvLatitudeValue.visibility = visibility
            tvLatitude.visibility = visibility
            tvLongitude.visibility = visibility
        }
    }

    private fun setupButton() {
        with(binding) {
            btnIncludeLocation.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    startPermissionRequest()
                    return@setOnCheckedChangeListener
                }
                setupLocationData(visibility = View.GONE)
            }
            btnCamera.setOnClickListener { startCamera() }
            btnGallery.setOnClickListener { startGallery() }
            btnUpload.setOnClickListener {
                val previousId = findNavController().previousBackStackEntry?.destination?.id
                if (previousId == null) {
                    findNavController().popBackStack()
                    return@setOnClickListener
                }
                uploadImage(previousId)
            }
        }


        binding.btnUpload.setOnClickListener {
            val previousId = findNavController().previousBackStackEntry?.destination?.id
            if (previousId != null) {
                uploadImage(previousId)
            } else {
                findNavController().popBackStack()
            }
        }
    }

    private fun startCamera() {
        currentImageUri = getImageUri(requireContext())
        launcherCamera.launch(currentImageUri)
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun uploadImage(previousId: Int) {
        currentImageUri?.let { uri ->
            val locationLatitude = binding.tvLatitudeValue.text.toString().toFloatOrNull()
            val locationLongitude = binding.tvLongitudeValue.text.toString().toFloatOrNull()
            val imageFile = uriToFile(uri, requireContext()).reduceFileImage()
            val description = binding.editText.text.toString()
            val isUserAuthenticated = previousId == R.id.mainFragment
            if (isUserAuthenticated) {
                viewModel.uploadAsAuthenticated(
                    imageFile,
                    description,
                    locationLatitude,
                    locationLongitude
                )
            } else {
                viewModel.uploadAsGuest(
                    imageFile,
                    description,
                    locationLatitude,
                    locationLongitude
                )
            }
            uiStateObserver(isUserAuthenticated)
        } ?: Toast.makeText(
            requireContext(),
            "No file",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun uiStateObserver(isUserAuthenticated: Boolean) {
        lifecycleScope.launch {
            viewModel.uploadState.collect { result ->
                when (result) {
                    is Resources.Loading -> {
                        loadingDialog.startLoadingDialog()
                    }

                    is Resources.Error -> {
                        loadingDialog.closeLoadingDialog()
                        errorDialog.startDialog(result.errorMessage, "Ok")
                        cancel()
                    }

                    is Resources.Success -> {
                        loadingDialog.closeLoadingDialog()
                        Toast.makeText(
                            requireActivity(),
                            getString(R.string.stories_added_successfully),
                            Toast.LENGTH_SHORT
                        ).show()
                        if (isUserAuthenticated) {
                            findNavController().navigate(R.id.action_cameraFragment_to_mainFragment)
                        } else {
                            findNavController().navigate(R.id.action_cameraFragment_to_loginFragment)
                        }
                    }
                }
            }
        }
    }


private val launcherGallery = registerForActivityResult(
    ActivityResultContracts.PickVisualMedia()
) { uri ->
    if (uri != null) {
        currentImageUri = uri
        showImage()
    } else {
        Toast.makeText(requireContext(), "No image selected", Toast.LENGTH_SHORT).show()
    }
}

private val launcherCamera = registerForActivityResult(
    ActivityResultContracts.TakePicture()
) { isSuccess ->
    if (isSuccess) {
        showImage()
    }
}

private fun showImage() {
    currentImageUri?.let {
        binding.imgPreview.setImageURI(it)
    }
}

private fun startPermissionRequest() {
    locationPermissionRequest.launch(
        arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )
}

private fun getMyLastLocation() {
    if (!checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
        !checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
    ) {
        println("Not granted")
        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
        return
    }

    loadingDialog.startLoadingDialog()
    fusedLocationClient.getCurrentLocation(
        Priority.PRIORITY_HIGH_ACCURACY,
        null
    ).addOnSuccessListener { location ->

        if (location != null) {
            val latitude = location.latitude.toString()
            val longitude = location.longitude.toString()
            val visibility = View.VISIBLE
            setupLocationData(latitude, longitude, visibility)
        } else {
            Toast.makeText(
                requireActivity(),
                getString(R.string.null_location),
                Toast.LENGTH_SHORT
            ).show()
            binding.btnIncludeLocation.isChecked = false
        }
        loadingDialog.closeLoadingDialog()
    }
}


private val locationPermissionRequest =
    registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permission ->
        when {
            permission.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                createLocationRequest()
            }

            permission.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                createLocationRequest()
            }

            else -> {
                binding.btnIncludeLocation.isChecked = false
                val snackBar = Snackbar.make(
                    binding.btnIncludeLocation,
                    getString(R.string.location_permission_denied),
                    Toast.LENGTH_SHORT
                )

                val intent = Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.fromParts("package", requireActivity().packageName, null)
                )
                snackBar.setAction(
                    "Settings"
                ) {
                    startActivity(intent)
                }.show()
                Log.d(TAG, "No permission given")
            }
        }
    }

private fun checkPermission(permission: String): Boolean {
    return ContextCompat.checkSelfPermission(
        requireActivity(),
        permission
    ) == PackageManager.PERMISSION_GRANTED
}

private fun createLocationRequest() {
    locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 500L)
        .setDurationMillis(3000L)
        .build()
    val builder = LocationSettingsRequest.Builder()
        .addLocationRequest(locationRequest)
    val client: SettingsClient = LocationServices.getSettingsClient(requireActivity())
    client.checkLocationSettings(builder.build())
        .addOnSuccessListener {
            getMyLastLocation()
        }
        .addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                try {
                    resolutionLauncher.launch(
                        IntentSenderRequest.Builder(exception.resolution).build()
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    Toast.makeText(requireActivity(), sendEx.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
}

private val resolutionLauncher =
    registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        when (result.resultCode) {
            AppCompatActivity.RESULT_OK -> {
                getMyLastLocation()
            }

            AppCompatActivity.RESULT_CANCELED -> {
                binding.btnIncludeLocation.isChecked = false
                Toast.makeText(
                    requireActivity(),
                    "Need GPS to use this features!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


companion object {
    val TAG: String = UploadFragment::class.java.simpleName
}

}