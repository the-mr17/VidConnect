package com.mr_17.vidconnect.ui.profile.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.mr_17.vidconnect.MainActivity
import com.mr_17.vidconnect.R
import com.mr_17.vidconnect.databinding.FragmentProfileBinding
import com.mr_17.vidconnect.ui.auth.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale


@AndroidEntryPoint
class ProfileFragment : Fragment(R.layout.fragment_profile), LocationListener {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var locationManager: LocationManager
    private lateinit var latLng: LatLng
    private val authViewModel by viewModels<AuthViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentProfileBinding.bind(view)

        (activity as MainActivity).setToolbarTitle("Profile")

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        checkAndGetPermissions()

        binding.apply {
            etFullName.setText(authViewModel.currentUser?.displayName.toString())
            etEmailAddress.setText(authViewModel.currentUser?.email.toString())
            tvVerifyEmailAddress.apply {
                val isEmailVerified = authViewModel.currentUser?.isEmailVerified!!
                setOnClickListener {
                    if(!isEmailVerified) {
                        authViewModel.sendEmailVerification()
                        showToast("Verification Link has been send to your email.")
                    }
                }
                text = if(isEmailVerified) "Verified!" else "Verify"
            }
        }
    }

    private fun checkAndGetPermissions() {
        Dexter
            .withContext(requireContext().applicationContext)
            .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse) {
                    getCurrentLocation()
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse) {
                    showToast("Location Access Permission Denied!")
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest?,
                    token: PermissionToken?
                ) {
                    token?.continuePermissionRequest()
                }
            }).check()
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        if (isLocationEnabled()) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5f, this)
            activity?.let {
                mFusedLocationClient.lastLocation.addOnCompleteListener(it) { task ->
                    val location: Location? = task.result
                    if (location != null) {
                        val geocoder = Geocoder(requireContext(), Locale.getDefault())
                        val list: MutableList<Address>? =
                            geocoder.getFromLocation(location.latitude, location.longitude, 1)
                        latLng = LatLng(location.latitude, location.longitude)

                        val markerOptions =
                            MarkerOptions().position(latLng).title("Your location")

                        binding.apply {
                            etAddress.setText(list?.get(0)!!.getAddressLine(0).toString())
                        }

                        val mapFragment = childFragmentManager
                            .findFragmentById(R.id.fragment_google_maps) as SupportMapFragment

                        mapFragment.getMapAsync {
                            it.addMarker(markerOptions)
                            it.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                        }
                    }
                }
            }
        } else {
            showToast("Please turn on location")
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        }
    }

    private fun isLocationEnabled(): Boolean {
        locationManager =
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    override fun onLocationChanged(location: Location) {
        latLng = LatLng(location.latitude, location.longitude)
        getCurrentLocation()
    }
}