package com.mr_17.vidconnect.ui.home.fragments

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.mr_17.vidconnect.MainActivity
import com.mr_17.vidconnect.R
import com.mr_17.vidconnect.data.Resource
import com.mr_17.vidconnect.databinding.FragmentHomeBinding
import com.mr_17.vidconnect.enums.LatestEventType
import com.mr_17.vidconnect.service.ServiceRepository
import com.mr_17.vidconnect.ui.auth.AuthViewModel
import com.mr_17.vidconnect.ui.home.HomeViewModel
import com.mr_17.vidconnect.ui.home.adapters.UsersRecyclerViewAdapter
import com.mr_17.vidconnect.ui.home.models.LatestEvent
import com.mr_17.vidconnect.enums.LatestEventType.*
import com.mr_17.vidconnect.ui.home.models.User
import com.mr_17.vidconnect.ui.home.models.isValid
import com.mr_17.vidconnect.utils.Constants
import com.mr_17.vidconnect.utils.Constants.DATABASE_REF_USERS
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home),
UsersRecyclerViewAdapter.OnClickListener {

    private lateinit var binding: FragmentHomeBinding
    private val homeViewModel by viewModels<HomeViewModel>()
    private val authViewModel by viewModels<AuthViewModel>()

    private var allPermissionsGranted = false
    private lateinit var userList : List<User>

    @Inject lateinit var serviceRepository: ServiceRepository

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)

        (activity as MainActivity).setToolbarTitle("Home")

        homeViewModel.getAllUserData()
        homeViewModel.subscribeForLatestEvent()

        binding.apply {
            btnProfile.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_profileFragment)
            }
        }

        startService()
        initObservers()
    }

    private fun initObservers() {
        lifecycleScope.launch {
            homeViewModel.allUserDataFlow.collectLatest {
                when(it) {
                    is Resource.Failure -> {
                    }
                    is Resource.Success -> {
                        userList = it.result
                        binding.rvUsers.apply {
                            adapter = UsersRecyclerViewAdapter(
                                it.result,
                                requireContext(),
                                this@HomeFragment
                            )
                            layoutManager = LinearLayoutManager(context)
                        }
                    }
                    is Resource.Loading -> {
                    }
                    else -> {}
                }
            }
        }

        lifecycleScope.launch {
            homeViewModel.latestEventFlow.collectLatest {
                when(it) {
                    is Resource.Failure -> {
                    }
                    is Resource.Success -> {
                        binding.apply {
                            if(it.result.isValid()) {
                                when (it.result.type) {
                                    START_AUDIO_CALL -> {
                                        incomingCallLayout.isVisible = true
                                        tvIncomingCallTitle.text =
                                            "${it.result.senderId} is audio calling you"
                                        btnAccept.setOnClickListener {
                                            findNavController().navigate(
                                                HomeFragmentDirections.actionHomeFragmentToCallFragment(
                                                    isVideoCall = false,
                                                    isCaller = false
                                                )
                                            )
                                        }
                                    }

                                    START_VIDEO_CALL -> {
                                        incomingCallLayout.isVisible = true
                                        tvIncomingCallTitle.text =
                                            "${it.result.senderId} is video calling you"
                                        btnAccept.setOnClickListener {
                                            findNavController().navigate(
                                                HomeFragmentDirections.actionHomeFragmentToCallFragment(
                                                    isVideoCall = true,
                                                    isCaller = false
                                                )
                                            )
                                        }
                                    }

                                    else -> {}
                                }
                            }
                        }
                    }
                    is Resource.Loading -> {
                    }
                    else -> {}
                }
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    /*private fun handleIncomingEvent(latestEvent: LatestEvent) {
        binding.apply {
            when(latestEvent.type) {
                is
            }
        }
    }*/

    override fun onCallButtonClick(v: View?, position: Int) {
        if(allPermissionsGranted) {
            homeViewModel.sendConnectionRequest(
                userList[position].uId,
                false
            )
            findNavController().navigate(
                HomeFragmentDirections.actionHomeFragmentToCallFragment(
                    targetId = userList[position].uId,
                    isVideoCall = false,
                    isCaller = true
                )
            )
        } else {
            checkAndGetPermissions()
        }
    }

    override fun onVideoCallButtonClick(v: View?, position: Int) {
        if(allPermissionsGranted) {
            homeViewModel.sendConnectionRequest(
                userList[position].uId,
                true
            )
            findNavController().navigate(
                HomeFragmentDirections.actionHomeFragmentToCallFragment(
                    targetId = userList[position].uId,
                    isVideoCall = true,
                    isCaller = true
                )
            )
        } else {
            checkAndGetPermissions()
        }
    }

    private fun startService() {
        serviceRepository.startService(authViewModel.currentUser!!.uid)
    }

    private fun checkAndGetPermissions() {
        Dexter
            .withContext(requireContext().applicationContext)
            .withPermissions(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            )
            .withListener(object: MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    if(report!!.areAllPermissionsGranted()) {
                        allPermissionsGranted = true
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    request: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    token?.continuePermissionRequest();
                }

            })
            .check();
    }
}