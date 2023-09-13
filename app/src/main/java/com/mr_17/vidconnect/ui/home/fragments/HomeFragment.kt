package com.mr_17.vidconnect.ui.home.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.mr_17.vidconnect.MainActivity
import com.mr_17.vidconnect.R
import com.mr_17.vidconnect.data.Resource
import com.mr_17.vidconnect.databinding.FragmentHomeBinding
import com.mr_17.vidconnect.ui.home.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {
    private lateinit var binding: FragmentHomeBinding
    private val homeViewModel by viewModels<HomeViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)

        (activity as MainActivity).setToolbarTitle("Home")

        homeViewModel.getAllUserData()

        binding.apply {
            btnProfile.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_profileFragment)
            }
        }

        initObservers()
    }

    private fun initObservers() {
        lifecycleScope.launch {
            homeViewModel.allUserDataFlow.collectLatest {
                when(it) {
                    is Resource.Failure -> {
                    }
                    is Resource.Success -> {
                        Log.d("users", it.toString())
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
}