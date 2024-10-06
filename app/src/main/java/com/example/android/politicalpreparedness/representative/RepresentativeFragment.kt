package com.example.android.politicalpreparedness.representative

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.data.RepresentativeNetworkRepository
import com.example.android.politicalpreparedness.databinding.FragmentRepresentativeBinding
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListAdapter
import com.example.android.politicalpreparedness.util.showToast
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.material.snackbar.Snackbar
import java.util.Locale

class RepresentativeFragment : Fragment() {
    companion object {
        private const val MOTION_LAYOUT_STATE = "motion_layout_state"
    }

    private var _binding: FragmentRepresentativeBinding? = null
    private val binding get() = _binding!!

    private val representativesAdapter = RepresentativeListAdapter()

    private lateinit var viewModel: RepresentativeViewModel

    private lateinit var resolutionForResultLauncher: ActivityResultLauncher<IntentSenderRequest>

    private lateinit var locationPermissionLauncher: ActivityResultLauncher<String>

    private lateinit var fusedLocationClient: FusedLocationProviderClient


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        resolutionForResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // Location was enabled
                getAddressFromDeviceLocation()
            } else {
                showToast(R.string.enable_location_error)
            }
        }
        locationPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                checkLocationEnabled()
            } else {
                showToast(R.string.location_required_error)
            }
        }

        val viewModelFactory = RepresentativeViewModelFactory(
            RepresentativeNetworkRepository(
                CivicsApi.retrofitService
            ),
        )

        viewModel = ViewModelProvider(this, viewModelFactory)[RepresentativeViewModel::class.java]
        _binding = FragmentRepresentativeBinding.inflate(inflater, container, false)

        binding.representativeViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        binding.apply {
            recyclerRepresentatives.apply {
                adapter = representativesAdapter
                layoutManager = LinearLayoutManager(requireContext()).apply {
                    orientation = LinearLayoutManager.VERTICAL
                }
            }

            val statesArray = resources.getStringArray(R.array.states)

            val adapter =
                ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, statesArray)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            state.adapter = adapter

            state.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    pos: Int,
                    id: Long
                ) {
                    viewModel.setSelectedState(statesArray[pos])
                }

                override fun onNothingSelected(parent: AdapterView<*>) = Unit
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.representatives.observe(viewLifecycleOwner) { representatives ->
            representativesAdapter.submitList(representatives)
        }

        viewModel.showLoading.observe(viewLifecycleOwner) { showLoading ->
            if (showLoading) {
                binding.representativeLoading.visibility = View.VISIBLE
            } else {
                binding.representativeLoading.visibility = View.GONE
            }
        }

        binding.buttonSearch.setOnClickListener {
            viewModel.getRepresentatives()
        }

        binding.buttonLocation.setOnClickListener {
            checkLocationPermission()
        }

        savedInstanceState?.let {
            val savedState = it.getInt(MOTION_LAYOUT_STATE)
            binding.representativeMotionLayout.post {
                binding.representativeMotionLayout.transitionToState(savedState)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val currentState = binding.representativeMotionLayout.currentState
        outState.putInt(MOTION_LAYOUT_STATE, currentState)
    }


    private fun checkLocationPermission() {
        val foregroundLocationApproved = (
                PackageManager.PERMISSION_GRANTED ==
                        ActivityCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                )

        if (foregroundLocationApproved) {
            checkLocationEnabled()
        } else {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun checkLocationEnabled() {
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_LOW_POWER
        }

        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val settingsClient = LocationServices.getSettingsClient(requireActivity())
        val locationSettingsResponseTask =
            settingsClient.checkLocationSettings(builder.build())

        locationSettingsResponseTask.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                val intentSenderRequest = IntentSenderRequest.Builder(exception.resolution).build()
                resolutionForResultLauncher.launch(intentSenderRequest)  // Launch
            } else {
                Snackbar.make(
                    binding.root,
                    R.string.location_required_error, Snackbar.LENGTH_INDEFINITE
                ).setAction(android.R.string.ok) {
                    checkLocationEnabled()
                }.show()
            }
        }

        locationSettingsResponseTask.addOnCompleteListener {
            if (it.isSuccessful) {
                println("prueba, location is enabled")
                getAddressFromDeviceLocation()
            }
        }
    }

    private fun geoCodeLocation(location: Location): Address? {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        return geocoder.getFromLocation(location.latitude, location.longitude, 1)
            ?.map { address ->
                Address(
                    address.thoroughfare,
                    address.subThoroughfare,
                    address.locality,
                    address.adminArea,
                    address.postalCode
                )
            }
            ?.first()
    }

    private fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

    private fun getAddressFromDeviceLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            binding.representativeLoading.visibility = View.VISIBLE

            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        geoCodeLocation(location = location)?.let { address ->
                            viewModel.getRepresentatives(address = address)
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    showToast(message = getString(R.string.location_error, exception.message))
                    // Handle the failure to get location
                    exception.printStackTrace()
                    binding.representativeLoading.visibility = View.GONE
                }
            return
        }
    }
}